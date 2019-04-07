

package grv.distributed.workload.context;

import grv.distributed.RunningState;
import grv.distributed.workload.Workload;
import grv.distributed.workload.WorkloadReport;
import grv.distributed.workload.runnable.WorkloadRunnable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

/**
 * An implementation of {@link WorkloadContext} that provides a framework around
 * running a workload that will only ever use one {@link WorkloadRunnable} and thread.
 */
@Slf4j
public class SingleThreadedWorkloadContext<T extends Workload> implements WorkloadContext<T> {
  /**
   * Workload runnable.
   */
  private final WorkloadRunnable<T> runnable;


  /**
   * Workload runnable thread.
   */
  private Thread thread;

  /**
   * Constructor.
   *
   * @param runnable Workload runnable.
   */
  public SingleThreadedWorkloadContext(WorkloadRunnable<T> runnable) {
    Assert.notNull(runnable, "workload runnable must not be null");
    this.runnable = runnable;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public WorkloadReport.Entry getWorkloadReportEntry() {
    return new WorkloadReport.Entry(getWorkload(),
                                    runnable.getRunningState(),
                                    runnable.getException() != null ? runnable.getException()
                                        .getMessage() : null);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public T getWorkload() {
    return runnable.getWorkload();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void start() {
    if (thread != null) {
      throw new IllegalStateException(String.format("context for workload %s has already previously been started", getWorkload().getUrn()));
    }

    thread = new Thread(runnable, "runnable-" + getWorkload().getUrn());
    thread.start();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void stop() {
    if (thread != null && thread.isAlive() && !thread.isInterrupted()) {
      thread.interrupt();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isStopped() {
    if (thread == null) {
      return true;
    }

    if (!thread.isAlive()) {
      thread = null;

      if (!runnable.getRunningState().isTerminated()) {
        runnable.terminate();
      }

      return true;
    }

    return runnable.getRunningState().isTerminated();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void terminate() {
    if (thread == null) {
      return;
    }

    try {
      thread.interrupt();
      thread.join();
    } catch (InterruptedException ignored) {
      Thread.currentThread().interrupt();
      log.error("unable to successfully terminate a workload thread due to being interrupted");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void fail() {
    runnable.fail();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RunningState getRunningState() {
    return runnable.getRunningState();
  }

  /**
   * Sets the priority of the thread.
   *
   * @param priority New priority of the thread.
   */
  public void setThreadPriority(int priority) {
    if (thread != null) {
      thread.setPriority(priority);
    }
  }
}
