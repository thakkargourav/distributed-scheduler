package grv.distributed.workload.runnable;

import grv.distributed.workload.Workload;

public abstract class ChildTask<T extends Workload> extends AbstractWorkloadRunnable<T>{
  /**
   * Constructor.
   *
   * @param workload Workload that this runnable services.
   */
  protected ChildTask(T workload) {
    super(workload);
  }
}
