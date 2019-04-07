

package grv.distributed.workload.repository.source;

import grv.distributed.workload.Workload;
import grv.distributed.workload.repository.CachingWorkloadRepository;
import grv.distributed.workload.repository.WorkloadRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * A workload repository source is responsible for providing sets of workloads that
 * the schedule should act upon. Any implementations of this interface will be used
 * by the {@link WorkloadRepository} to aggregate a set of workloads while will be
 * scheduled for execution by the application cluster.
 * <p>
 * There is no assumption that implementation will cache the results of the
 * implementation-specific query. For example, if an implementation must query a
 * database or some other web service, it is left to the implementation to determine
 * whether those results should be cached for a time.
 * <p>
 * As a note, the default {@link WorkloadRepository} registered in Spring is a
 * {@link CachingWorkloadRepository}, which caches the results from all repository
 * source implementations at an aggregated level.
 */
public interface WorkloadRepositorySource<T extends Workload> {

  Set<T> queryWorkloads();

  void addWorkLoads(List<T> workloads);

  default void addWorkLoads(T... workloads) {
    List<T> ts = Arrays.asList(workloads);
    addWorkLoads(ts);
  }



}
