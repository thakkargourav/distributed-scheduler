

package grv.distributed.instruction;

import grv.distributed.workload.WorkloadReport;
import grv.distributed.workload.context.manager.WorkloadContextManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * An {@link Instruction} that requests a {@link WorkloadReport} from
 * a cluster member.
 */
@Slf4j
public class ReportInstruction implements Instruction<WorkloadReport> {

  private static final long serialVersionUID = -4957030174170441321L;

  /**
   * Workload context manager.
   */
  @Autowired
  private transient WorkloadContextManager workloadContextManager;

  /**
   * {@inheritDoc}
   */
  @Override
  public WorkloadReport call() {
    try {
      return workloadContextManager.getWorkloadReport();
    } catch (Exception e) {
      log.error("Unhandled exception encountered while retrieving report.", e);
      return null;
    }
  }
}
