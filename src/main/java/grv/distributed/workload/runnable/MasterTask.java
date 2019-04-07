package grv.distributed.workload.runnable;

import grv.distributed.workload.Workload;

public abstract class MasterTask<T extends Workload> extends AbstractWorkloadRunnable<T>{
  /**
   * Constructor.
   *
   * @param workload Workload that this runnable services.
   */
  protected MasterTask(T workload) {
    super(workload);
  }
}
