

package grv.distributed.strategy;

import grv.distributed.RunningState;
import grv.distributed.cluster.ClusterMember;
import grv.distributed.instruction.ActionType;
import grv.distributed.instruction.WorkloadActionsInstruction;
import grv.distributed.workload.Workload;
import grv.distributed.workload.WorkloadReport;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A base implementation of {@link Strategy} that primarily provides a set of
 * helper methods for manipulating workload assignments between cluster members.
 */
public abstract class AbstractStrategy implements Strategy {
  /**
   * Removes the given workload regardless of what cluster member it's on.
   *
   * @param context  Scheduler strategy context.
   * @param workload Workload of the action.
   */
  protected void removeWorkload(StrategyContext context, Workload workload) {
    for (ClusterMember clusterMember : context.getMapping().keySet()) {
      if (context.getMapping()
          .get(clusterMember)
          .getEntries()
          .stream()
          .anyMatch(e -> e.getWorkload().equals(workload))) {
        removeWorkload(context, clusterMember, workload);
      }
    }
  }

  /**
   * Removes the given workload from the given cluster member.
   *
   * @param context       Scheduler strategy context.
   * @param clusterMember Cluster member to add the action to.
   * @param workload      Workload of the action.
   */
  protected void removeWorkload(StrategyContext context,
                                ClusterMember clusterMember,
                                Workload workload) {
    context.getMapping()
        .get(clusterMember)
        .getEntries()
        .removeIf(e -> e.getWorkload().equals(workload));
    addAction(context, clusterMember, workload, ActionType.REMOVE);
  }

  /**
   * Adds a workload action to a cluster member.
   *
   * @param context       Scheduler strategy context.
   * @param clusterMember Cluster member to add the action to.
   * @param workload      Workload of the action.
   * @param actionType    Action to perform.
   */
  protected void addAction(StrategyContext context,
                           ClusterMember clusterMember,
                           Workload workload,
                           ActionType actionType) {
    addAction(context, clusterMember, new Action(workload, actionType));
  }

  /**
   * Adds a workload action to a cluster member.
   *
   * @param context       Scheduler strategy context.
   * @param clusterMember Cluster member to add the action to.
   * @param action        Workload action to perform.
   */
  protected void addAction(StrategyContext context,
                           ClusterMember clusterMember,
                           Action action) {
    if (!context.getActions().containsKey(clusterMember)) {
      context.getActions().put(clusterMember, new ArrayList<>());
    }

    context.getActions().get(clusterMember).add(action);
  }

  /**
   * Restarts the given workload on the given cluster member.
   *
   * @param context       Scheduler strategy context.
   * @param clusterMember Cluster member to add the action to.
   * @param workload      Workload of the action.
   */
  protected void restartWorkload(StrategyContext context,
                                 ClusterMember clusterMember,
                                 Workload workload) {
    addAction(context, clusterMember, workload, ActionType.RESTART);
  }

  /**
   * Adds the given workload to the least busy cluster member.
   *
   * @param context  Scheduler strategy context.
   * @param workload Workload of the action.
   */
  protected void addWorkload(StrategyContext context,
                             Workload workload) {
    addWorkload(context, findLeastBusyMember(context).getKey(), workload);
  }

  /**
   * Adds the given workload to the given cluster member.
   *
   * @param context       Scheduler strategy context.
   * @param clusterMember Cluster member to add the action to.
   * @param workload      Workload of the action.
   */
  protected void addWorkload(StrategyContext context,
                             ClusterMember clusterMember,
                             Workload workload) {
    context.getMapping()
        .get(clusterMember)
        .getEntries()
        .add(new WorkloadReport.Entry(workload, RunningState.NOT_STARTED));
    addAction(context, clusterMember, workload, ActionType.ADD);
  }

  /**
   * Makes a deep copy of the given map of workload reports.
   *
   * @param reports Mapping of cluster members to workload reports.
   * @return a deep copy of the given map of workload reports.
   */
  protected Map<? extends ClusterMember, WorkloadReport> copyReports(Map<? extends ClusterMember, WorkloadReport> reports) {
    return reports.entrySet()
        .stream()
        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().copy()));
  }

  /**
   * Finds the cluster member with the least amount of load.
   *
   * @param context Scheduler strategy context.
   * @return the cluster member with the least amount of load.
   */
  protected Map.Entry<? extends ClusterMember, WorkloadReport> findLeastBusyMember(
      StrategyContext context) {
    return context.getMapping()
        .entrySet()
        .stream()
        .min(Comparator.comparing(entry -> entry.getValue().getEntries().size()))
        .orElse(null);
  }

  /**
   * Returns a collection of instruction mappings resulting from a scheduling/re-balancing operation.
   * <p>
   * This method compiles instructions into two sets: first removals, and then start/restart. This
   * prevents race conditions where more than one cluster member is executing a workload.
   *
   * @param context Scheduler strategy context.
   * @return a collection of instructing  mappings suitable for submission to cluster members.
   */
  protected List<Map<ClusterMember, WorkloadActionsInstruction>> toInstructionMap(StrategyContext context) {
    List<Map<ClusterMember, WorkloadActionsInstruction>> compiled = new ArrayList<>();

    Map<ClusterMember, WorkloadActionsInstruction> instructions = compileInstructions(context,
        ActionType.REMOVE);

    if (instructions.size() > 0) {
      compiled.add(instructions);
    }

    instructions = compileInstructions(context, ActionType.ADD, ActionType.RESTART);

    if (instructions.size() > 0) {
      compiled.add(instructions);
    }

    return compiled;
  }

  /**
   * Returns a mapping of cluster members to action instructions based on the given operation types.
   *
   * @param context Scheduler strategy context.
   * @param filter  The type of actions to include in the instruction set.
   * @return a mapping of cluster members to action instructions based on the given operation types.
   */
  private Map<ClusterMember, WorkloadActionsInstruction> compileInstructions(
      StrategyContext context,
      ActionType... filter) {
    List<ActionType> filterList = Arrays.asList(filter);

    Map<ClusterMember, WorkloadActionsInstruction> instructions = new HashMap<>();

    for (ClusterMember clusterMember : context.getActions().keySet()) {
      List<Action> actions = context.getActions().get(clusterMember);

      if (actions == null || actions.size() == 0) {
        continue;
      }

      List<Action> actionsList = new ArrayList<>();

      for (Action action : actions) {
        if (filterList.contains(action.getActionType())) {
          actionsList.add(action);
        }
      }

      if (actionsList.size() > 0) {
        instructions.put(clusterMember, new WorkloadActionsInstruction(actionsList));
      }
    }

    return instructions;
  }

  /**
   * Finds the cluster member with the most amount of load.
   *
   * @param context Scheduler strategy context.
   * @return the cluster member with the most amount of load.
   */
  protected Map.Entry<? extends ClusterMember, WorkloadReport> findMostBusyMember(
      StrategyContext context) {
    return context.getMapping()
        .entrySet()
        .stream()
        .max(Comparator.comparing(entry -> entry.getValue().getEntries().size()))
        .orElse(null);
  }

  /**
   * A comparator that orders workload report entries based on their running state. Terminated workloads
   * sort first in the list.
   */
  static class EntryRunningStateComparator implements Comparator<WorkloadReport.Entry> {
    @Override
    public int compare(WorkloadReport.Entry o1, WorkloadReport.Entry o2) {
      if (o1.getState().isTerminated() && o2.getState().isTerminated()) {
        return 0;
      } else if (o1.getState().isTerminated()) {
        return -1;
      } else {
        return 1;
      }
    }
  }
}
