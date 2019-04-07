package grv.distributed.strategy;

import com.hazelcast.core.HazelcastInstance;
import grv.distributed.cluster.ClusterMember;
import grv.distributed.instruction.WorkloadActionsInstruction;
import grv.distributed.workload.Workload;
import grv.distributed.workload.WorkloadReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An implementation of a scheduler strategy that attempts to spread workloads evenly
 * across cluster members using a greedy algorithm.
 */
@Slf4j
@Service
public class GreedyStrategy extends AbstractStrategy {

  @Autowired
  private HazelcastInstance hazelcastInstance;

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Map<ClusterMember, WorkloadActionsInstruction>> rebalance(Map<? extends ClusterMember, WorkloadReport> reports) {
    // Create the scheduler context.
    StrategyContext context = new StrategyContext(copyReports(reports));


    Set<Workload> completedWorkloads = reports.values()
        .stream()
        .flatMap(report -> report.getEntries().stream())
//        .filter(e -> null == e.getError())
        .filter(e -> e.getState().isTerminated())
        .map(WorkloadReport.Entry::getWorkload)
        .collect(Collectors.toSet());


    completedWorkloads.forEach(w -> removeWorkload(context, w));

    // Distribute workload from over-burdened cluster members to others with low load.
    while (true) {
      Map.Entry<? extends ClusterMember, WorkloadReport> low = findLeastBusyMember(context);
      Map.Entry<? extends ClusterMember, WorkloadReport> high = findMostBusyMember(context);

      int delta = high.getValue().getEntries().size() - low.getValue().getEntries().size();

      if (delta < 2) {
        break;
      }

      Workload candidate = high.getValue()
          .getEntries()
          .stream()
          .min(new EntryRunningStateComparator())
          .map(WorkloadReport.Entry::getWorkload)
          .orElse(null);

      removeWorkload(context, high.getKey(), candidate);
      addWorkload(context, low.getKey(), candidate);
    }

    // Restart failed workloads.
//    context.getMapping()
//        .forEach((k, v) -> v.getEntries()
//            .stream()
//            .filter(e -> null != e.getError())
//            .map(WorkloadReport.Entry::getWorkload)
//            .distinct()
//            .forEach(w -> restartWorkload(context, k, w)));

    return toInstructionMap(context);
  }

  @Override
  public List<Map<ClusterMember, WorkloadActionsInstruction>> add(Map<? extends ClusterMember, WorkloadReport> reports,
                                                                  List<? extends Workload> workloads) {
    StrategyContext context = new StrategyContext(copyReports(reports));
    workloads.forEach(w -> addWorkload(context, w));
    return toInstructionMap(context);
  }

}
