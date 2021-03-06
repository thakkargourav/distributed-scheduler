

package grv.distributed.workload.runnable;

import grv.distributed.RunningState;
import grv.distributed.workload.Workload;

/**
 * An extension of {@link Runnable} that provides additional functionality for
 * managing the process logic of a workload.
 */
public interface WorkloadRunnable<T extends Workload> extends Runnable {
  /**
   * Returns the current state of the runnable.
   *
   * @return the current state of the runnable.
   */
  RunningState getRunningState();

  /**
   * Returns the workload associated with this runnable.
   *
   * @return the workload associated with this runnable.
   */
  T getWorkload();

  /**
   * Forcibly sets the runnable to an error state. Also sets the interrupted
   * flag so that the runnable will stop after any in-flight processing.
   * <p>
   * Note that this does not stop execution of the runnable in its thread.
   * This method is intended to be called to true-up the running state in the
   * event that a monitor discovers that this runnable's thread has died.
   */
  void terminate();

  /**
   * Forces a failure state. This is useful for testing various failure scenarios.
   */
  void fail();

  /**
   * Returns an exception that was encountered during an error in execution.
   * <p>
   * This will be {@code null} if no errors have occurred.
   *
   * @return the exception thrown when an error occurs, or {@code null} if
   * either there is not one present or the runnable is not in an error state.
   */
  Throwable getException();
}
