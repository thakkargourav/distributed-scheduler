/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package grv.distributed;

import grv.distributed.cluster.ClusterManager;
import grv.distributed.cluster.ClusterMember;
import grv.distributed.locks.DistributedLock;
import grv.distributed.locks.DistributedLockProvider;
import grv.distributed.instruction.ReportInstruction;
import grv.distributed.instruction.ShutdownInstruction;
import grv.distributed.instruction.WorkloadActionsInstruction;
import grv.distributed.strategy.SchedulerStrategy;
import grv.distributed.workload.Workload;
import grv.distributed.workload.WorkloadReport;
import grv.distributed.workload.repository.WorkloadRepository;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * The distributed scheduler is responsible for managing load balance scheduling and
 * instruction submission to cluster members. The actual load balancing logic is provided
 * by a {@link SchedulerStrategy} implementation.
 */
@Service
@Slf4j
public class DistributedScheduler implements DisposableBean {
  /**
   * Name of the distributed property holding the time the next schedule should occur.
   */
  private final static String SCHEDULE_TIME_KEY = "distributed-schedule-time";

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
  private final SchedulerStrategy schedulerStrategy;

  /**
   * Workload repository.
   */
  private final WorkloadRepository workloadRepository;

  /**
   * Constructor.
   *
   * @param distributedLockProvider Distributed lock provider.
   * @param clusterManager          Cluster manager.
   * @param schedulerProperties     Scheduler configuration properties.
   * @param schedulerStrategy       Strategy implementation used by the scheduler to load balancer the cluster.
   * @param workloadRepository      Workload repository.
   */
  public DistributedScheduler(
      DistributedLockProvider distributedLockProvider,
      ClusterManager clusterManager,
      SchedulerProperties schedulerProperties,
      SchedulerStrategy schedulerStrategy,
      WorkloadRepository workloadRepository
  ) {
    this.distributedLockProvider = distributedLockProvider;
    this.clusterManager = clusterManager;
    this.schedulerProperties = schedulerProperties;
    this.schedulerStrategy = schedulerStrategy;
    this.workloadRepository = workloadRepository;
  }

  /**
   * Attempt to conduct a non-forceful scheduling.
   */
  @Scheduled(fixedDelayString = "${scheduler.rebalance-poll-interval:PT3s}",
             initialDelayString = "${scheduler.rebalance-poll-delay:PT3s}")
  public void schedule() {
    schedule(false);
  }

  /**
   * Conducts a workload scheduling.
   *
   * @param force If true, will disregard the time check and schedule now.
   */
  public void schedule(boolean force) {
    /* TODO: lock name configurable? */
    DistributedLock lock = distributedLockProvider.getDistributedLock("distributed-scheduler-lock" );

    // TODO: configurable?
    try {
      if (lock.supportsLeases()) {
        if (!lock.tryLock(0, TimeUnit.MILLISECONDS, 10, TimeUnit.MINUTES)) {
          return;
        }
      } else {
        if (!lock.tryLock(0, TimeUnit.MILLISECONDS)) {
          return;
        }
      }

      if (!force && !isTimeToSchedule()) {
        lock.unlock();
        return;
      }
    } catch (InterruptedException ignored) {
      Thread.currentThread().interrupt();
      if (!lock.supportsLeases() || (lock.supportsLeases() && lock.isLocked())) {
        lock.unlock();
      }
      return;
    }

    try {
      Set<Workload> registeredWorkloads = workloadRepository.getWorkloads();

      Map<ClusterMember, WorkloadReport> reports = clusterManager.submitInstruction(new ReportInstruction());
      if (reports.size() == 0) {
        throw new IllegalStateException("received no workload reports from any cluster member nodes");
      }

      List<Map<ClusterMember, WorkloadActionsInstruction>> instructions = schedulerStrategy.schedule(
          registeredWorkloads,
          reports);

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
   * Determines if it's time to schedule workloads.
   *
   * @return whether it's time to schedule workloads.
   */
  private boolean isTimeToSchedule() {
    return System.currentTimeMillis() >= getScheduleTime();
  }

  /**
   * Returns the time the next schedule should occur.
   *
   * @return The time the next schedule should occur.
   */
  private long getScheduleTime() {
    Long time = clusterManager.getProperty(SCHEDULE_TIME_KEY, Long.class);

    if (time == null) {
      return 0;
    }

    return time;
  }

  /**
   * Sets the time the next schedule should occur.
   *
   * @param time The time the next schedule should occur.
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
