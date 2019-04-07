

package grv.distributed.instruction;

import grv.distributed.strategy.Action;
import grv.distributed.workload.Workload;
import grv.distributed.workload.context.manager.WorkloadContextManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * An instruction created by the scheduler system that conducts changes to workload
 * assignments for a specific cluster member.
 */
@Slf4j
public class WorkloadActionsInstruction implements Instruction<Boolean> {
  /**
   * Scheduler actions to perform.
   */
  private final List<Action> actions;

  /**
   * Workload context manager.
   */
  private transient WorkloadContextManager workloadContextManager;

  /**
   * Constructor.
   *
   * @param actions the scheduler actions to perform.
   */
  public WorkloadActionsInstruction(List<Action> actions) {
    this.actions = actions;
  }

  /**
   * Sets the workload context manager.
   *
   * @param workloadContextManager Workload context manager.
   */
  @Autowired
  public void setWorkloadContextManager(WorkloadContextManager workloadContextManager) {
    this.workloadContextManager = workloadContextManager;
  }

  /**
   * Returns the scheduler actions to perform.
   *
   * @return the scheduler actions to perform.
   */
  public List<Action> getActions() {
    return actions;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Boolean call() {
    List<Future<?>> futures = new ArrayList<>();

    actions.removeIf(a -> {
      if (a.getActionType() == ActionType.ADD) {
        return false;
      }
      if (!workloadContextManager.isServicing(a.getWorkload())) {
        log.warn("Discarding "
                     + a.getActionType()
                     + " instruction for workload "
                     + a.getWorkload()
                     + " because the node is not servicing it");
      }
      return !workloadContextManager.isServicing(a.getWorkload());
    });

    for (Action action : actions) {
      Workload workload = action.getWorkload();

      try {
        switch (action.getActionType()) {
          case ADD:
            log.info("Adding workload " + workload + " to node");
            workloadContextManager.start(workload);
            break;

          case STOP:
            log.info("Stopping workload " + workload + " on node");
            futures.add(workloadContextManager.stop(workload));
            break;

          case REMOVE:
            log.info("Removing workload " + workload + " from node");
            futures.add(workloadContextManager.remove(workload));
            break;

          case RESTART:
            log.info("Restarting workload " + workload + " on node");
            futures.add(workloadContextManager.restart(workload));
            break;

          case FAIL:
            log.info("Failing workload " + workload + " on node");
            workloadContextManager.fail(workload);
            break;

          default:
            log.error("Action type " + action.getActionType() + " is unsupported");
        }
      } catch (Exception e) {
        log.error("Unhandled exception encountered while processing instructions for workload "
                      + workload, e);
      }
    }

    try {
      while (futures.size() > 0) {
        futures.removeIf(Future::isDone);
        Thread.sleep(250);
      }
    } catch (InterruptedException ignored) {
      Thread.currentThread().interrupt();
      log.info("Interrupted while waiting for instructions to complete");
    }

    return true;
  }
}
