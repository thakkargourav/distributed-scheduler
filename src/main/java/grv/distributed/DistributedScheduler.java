package grv.distributed;

import grv.distributed.cluster.ClusterManager;
import grv.distributed.cluster.ClusterMember;
import grv.distributed.instruction.ReportInstruction;
import grv.distributed.instruction.ShutdownInstruction;
import grv.distributed.instruction.WorkloadActionsInstruction;
import grv.distributed.locks.DistributedLock;
import grv.distributed.locks.DistributedLockProvider;
import grv.distributed.strategy.Strategy;
import grv.distributed.workload.Workload;
import grv.distributed.workload.WorkloadReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * The distributed scheduler is responsible for managing load balance scheduling and
 * instruction submission to cluster members. The actual load balancing logic is provided
 * by a {@link Strategy} implementation.
 */
@Service
@Slf4j
public class DistributedScheduler implements DisposableBean {
  /**
   * Name of the distributed property holding the time the next rebalance should occur.
   */
  private final static String SCHEDULE_TIME_KEY = "distributed-rebalance-time";

  /**
   * Distributed lock provider.
   */
  private final DistributedLockProvider distributedLockProvider;

  /**
   * Scheduler configuration properties.
   */
  private final SchedulerProperties schedulerProperties;

  /**
   * Cluster manager.
   */
  private final ClusterManager clusterManager;

  /**
   * Workload scheduler strategy.
   */
  private final Strategy strategy;


  /**
   * Constructor.
   *
   * @param distributedLockProvider Distributed lock provider.
   * @param clusterManager          Cluster manager.
   * @param schedulerProperties     Scheduler configuration properties.
   * @param strategy                Strategy implementation used by the scheduler to load balancer the cluster.
   */
  public DistributedScheduler(
      DistributedLockProvider distributedLockProvider,
      ClusterManager clusterManager,
      SchedulerProperties schedulerProperties,
      Strategy strategy
  ) {
    this.distributedLockProvider = distributedLockProvider;
    this.clusterManager = clusterManager;
    this.schedulerProperties = schedulerProperties;
    this.strategy = strategy;
  }


  public <T extends Workload> void add(T... workloads) {
    add(Arrays.asList(workloads));
  }

  public void add(List<? extends Workload> workloads) {
    DistributedLock lock = distributedLockProvider.getDistributedLock("distributed-scheduler-lock");
    try {
      lock.lockInterruptibly();
    } catch (InterruptedException ignored) {
      Thread.currentThread().interrupt();
      lock.unlock();
      return;
    }

    try {
      Map<ClusterMember, WorkloadReport> reports = clusterManager.submitInstruction(new ReportInstruction());
      if (reports.size() == 0) {
        throw new IllegalStateException("received no workload reports from any cluster member nodes");
      }

      List<Map<ClusterMember, WorkloadActionsInstruction>> instructions = strategy.add(reports, workloads);

      for (Map<ClusterMember, WorkloadActionsInstruction> instructionSet : instructions) {
        clusterManager.submitInstructions(instructionSet);
      }

    } catch (Exception e) {
      log.error("unexpected exception encountered while scheduling workloads", e);
    } finally {
      lock.unlock();
    }
  }

  /**
   * Attempt to conduct a non-forceful scheduling.
   */
  @Scheduled(fixedDelayString = "${scheduler.rebalance-poll-interval:PT3s}",
      initialDelayString = "${scheduler.rebalance-poll-delay:PT3s}")
  public void rebalance() {
    rebalance(false);
  }

  /**
   * Conducts a workload scheduling.
   *
   * @param force If true, will disregard the time check and rebalance now.
   */
  public void rebalance(boolean force) {
    /* TODO: lock name configurable? */
    DistributedLock lock = distributedLockProvider.getDistributedLock("distributed-scheduler-lock");

    // TODO: configurable?
    try {
      if (!lock.tryLock(0, TimeUnit.MILLISECONDS)) {
        return;
      }

      if (!force && !isTimeToSchedule()) {
        lock.unlock();
        return;
      }
    } catch (InterruptedException ignored) {
      Thread.currentThread().interrupt();
      lock.unlock();
      return;
    }

    try {
      Map<ClusterMember, WorkloadReport> reports = clusterManager.submitInstruction(new ReportInstruction());
      if (reports.size() == 0) {
        throw new IllegalStateException("received no workload reports from any cluster member nodes");
      }

      List<Map<ClusterMember, WorkloadActionsInstruction>> instructions = strategy.rebalance(reports);

      for (Map<ClusterMember, WorkloadActionsInstruction> instructionSet : instructions) {
        clusterManager.submitInstructions(instructionSet);
      }

      setScheduleTime(System.currentTimeMillis() + schedulerProperties.getRebalanceInterval()
          .toMillis());
    } catch (Exception e) {
      log.error("unexpected exception encountered while scheduling workloads", e);
    } finally {
      lock.unlock();
    }
  }

  /**
   * Determines if it's time to rebalance workloads.
   *
   * @return whether it's time to rebalance workloads.
   */
  private boolean isTimeToSchedule() {
    return System.currentTimeMillis() >= getScheduleTime();
  }

  /**
   * Returns the time the next rebalance should occur.
   *
   * @return The time the next rebalance should occur.
   */
  private long getScheduleTime() {
    Long time = clusterManager.getProperty(SCHEDULE_TIME_KEY, Long.class);

    if (time == null) {
      return 0;
    }

    return time;
  }

  /**
   * Sets the time the next rebalance should occur.
   *
   * @param time The time the next rebalance should occur.
   */
  private void setScheduleTime(long time) {
    clusterManager.setProperty(SCHEDULE_TIME_KEY, time);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void destroy() {
    shutdown();
  }

  /**
   * Shuts down all workloads.
   */
  public void shutdown() {
    try {
      clusterManager.submitInstruction(new ShutdownInstruction());
    } catch (Exception e) {
      log.error("unable to shut down workloads", e);
    }
  }
}
