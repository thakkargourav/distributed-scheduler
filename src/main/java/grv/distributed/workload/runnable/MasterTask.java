package grv.distributed.workload.runnable;

import grv.distributed.workload.ChildWorkload;
import grv.distributed.workload.MasterWorkload;
import grv.distributed.workload.repository.source.WorkloadRepositorySource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class MasterTask<M extends MasterWorkload, C extends ChildWorkload> extends AbstractWorkloadRunnable<M> {


  @Autowired
  private transient WorkloadRepositorySource<C> childWorkLoadRepository;

  /**
   * Constructor.
   *
   * @param workload Workload that this runnable services.
   */
  protected MasterTask(M workload) {
    super(workload);
  }

  @Override
  public void execute() {
    List<C> breakDowns = breakDown(getWorkload());
    childWorkLoadRepository.addWorkLoads(breakDowns);
  }

  protected abstract List<C> breakDown(M workload);

}
