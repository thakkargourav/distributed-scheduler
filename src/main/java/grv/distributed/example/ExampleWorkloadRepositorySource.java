

package grv.distributed.example;

import grv.distributed.workload.repository.source.WorkloadRepositorySource;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * For demonstration purposes, simply returns the static list of workloads to run.
 * In non-trivial implementations, workload repository sources might load workloads
 * from a database or some other service.
 */
@Service
public class ExampleWorkloadRepositorySource implements WorkloadRepositorySource<ExampleWorkload> {
  private static final Set<ExampleWorkload> workloads;

  static {
    workloads = new HashSet<>();
//    workloads.add(new ExampleWorkload("a"));
//    workloads.add(new ExampleWorkload("b"));
//    workloads.add(new ExampleWorkload("c"));
  }

  @Override
  public Set<ExampleWorkload> queryWorkloads() {
    return workloads;
  }

  @Override
  public void addWorkLoads(List<ExampleWorkload> workloads) {
    workloads.addAll(workloads);
  }
}
