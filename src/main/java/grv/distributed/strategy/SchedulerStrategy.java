

package grv.distributed.strategy;

import grv.distributed.cluster.ClusterMember;
import grv.distributed.instruction.WorkloadActionsInstruction;
import grv.distributed.workload.Workload;
import grv.distributed.workload.WorkloadReport;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implements the actual logic of creating and updating workload assignments to cluster members.
 */
public interface SchedulerStrategy {
  /**
   * Creates a series of workload actions mapped to cluster members.
   *
   * @param registeredWorkloads A set of workloads that should be scheduled by the cluster.
   * @param reports             A mapping of cluster members to their current work loads.
   * @return a series of change set mappings for cluster members and workloads.
   */
  List<Map<ClusterMember, WorkloadActionsInstruction>> schedule(Set<? extends Workload> registeredWorkloads,
                                                                Map<? extends ClusterMember, WorkloadReport> reports);
}
