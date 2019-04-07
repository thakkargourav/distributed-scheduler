

package grv.distributed.instruction;

/**
 * Scheduler instruction action types.
 */
public enum ActionType {
  /**
   * Adds and starts a workload.
   */
  ADD,

  /**
   * Stops and removes a workload from the cluster member.
   */
  REMOVE,

  /**
   * Restarts a workload on the cluster member.
   */
  RESTART,

  /**
   * Stops but does not remove the workload from the cluster member.
   */
  STOP,

  /**
   * Stops and sets an error state on the workload.
   */
  FAIL
}
