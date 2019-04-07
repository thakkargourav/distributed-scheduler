package grv.distributed.workload.runnable;

import grv.distributed.workload.ChildWorkload;

public abstract class ChildTask<T extends ChildWorkload> extends AbstractWorkloadRunnable<T> {
  /**
   * Constructor.
   *
   * @param workload Workload that this runnable services.
   */
  protected ChildTask(T workload) {
    super(workload);
  }
}
