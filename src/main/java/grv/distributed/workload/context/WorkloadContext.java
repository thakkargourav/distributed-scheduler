

package grv.distributed.workload.context;

import grv.distributed.RunningState;
import grv.distributed.workload.Workload;
import grv.distributed.workload.WorkloadReport;

/**
 * Manages the runtime of a specific workload.
 */
public interface WorkloadContext<T extends Workload> {
  /**
   * Returns the workload context acts on.
   *
   * @return the workload context acts on.
   */
  T getWorkload();

  /**
   * Generates a workload report entry for the workload being processed by this context.
   *
   * @return a workload report entry.
   */
  WorkloadReport.Entry getWorkloadReportEntry();

  /**
   * Starts the workload.
   */
  void start();

  /**
   * Signals that the workload should stop and gracefully terminate. This call is non-blocking
   * and does not wait for the workload to stop. Running status can subsequently be determined
   * through the use of {@link #getRunningState} and {@link #isStopped}.
   */
  void stop();

  /**
   * Returns whether the workload has stopped. A runnable is considered stopped when
   * its thread has died or its running state is terminated.
   *
   * @return whether the runnable has stopped.
   */
  boolean isStopped();

  /**
   * Stops execution of the workload's thread. This should only be used as
   * a last-ditch attempt to get the workload to stop collecting.
   */
  void terminate();

  /**
   * Interrupts and forces an error state on the workload. This is useful for testing.
   */
  void fail();

  /**
   * Returns the running state of the workload.
   *
   * @return the running state of the workload.
   */
  RunningState getRunningState();
}