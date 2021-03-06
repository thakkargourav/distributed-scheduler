

package grv.distributed.instruction;

import grv.distributed.workload.context.manager.WorkloadContextManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.Future;

/**
 * Instructs a cluster to stop all running workloads.
 */
public class ShutdownInstruction implements Instruction<Void> {
  @Autowired
  transient WorkloadContextManager workloadContextManager;

  @Override
  public Void call() throws Exception {
    Future future = workloadContextManager.shutdown();
    future.get();
    return null;
  }
}
