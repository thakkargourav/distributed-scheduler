

package grv.distributed.strategy;

import grv.distributed.cluster.ClusterMember;
import grv.distributed.workload.WorkloadReport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A helper class that contains the mapping of cluster members to workloads and
 * a list of scheduler actions to submit to cluster members.
 */
public class StrategyContext {
  /**
   * Mapping of cluster members to workload reports. This set of data is intended
   * to be mutable by the scheduling process, and serve as a way to define the
   * ideal state of the cluster as the scheduling algorithm is run.
   */
  private final Map<? extends ClusterMember, WorkloadReport> mapping;

  /**
   * Mapping of cluster members to actions they should perform.
   */
  private final Map<ClusterMember, List<Action>> actions = new HashMap<>();

  /**
   * Container map.
   *
   * @param mapping mapping of cluster members to their workload report.
   */
  public StrategyContext(Map<? extends ClusterMember, WorkloadReport> mapping) {
    this.mapping = mapping;
  }

  /**
   * Returns the mapping of cluster members to workload reports.
   *
   * @return the mapping of cluster members to workload reports.
   */
  public Map<? extends ClusterMember, WorkloadReport> getMapping() {
    return mapping;
  }

  /**
   * Returns the mapping of cluster members to actions they should perform.
   *
   * @return the mapping of cluster members to actions they should perform.
   */
  public Map<ClusterMember, List<Action>> getActions() {
    return actions;
  }
}
