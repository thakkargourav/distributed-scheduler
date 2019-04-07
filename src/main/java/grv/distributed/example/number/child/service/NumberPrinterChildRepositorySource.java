

package grv.distributed.example.number.child.service;

import grv.distributed.example.number.child.NumberPrinterChildWorkload;
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
public class NumberPrinterChildRepositorySource implements WorkloadRepositorySource<NumberPrinterChildWorkload> {

  private final Set<NumberPrinterChildWorkload> workloads;

  public NumberPrinterChildRepositorySource() {
    this.workloads = new HashSet<>();
  }

  @Override
  public void addWorkLoads(List<NumberPrinterChildWorkload> workloads) {
    this.workloads.addAll(workloads);
  }

  @Override
  public final Set<NumberPrinterChildWorkload> queryWorkloads() {
    Set<NumberPrinterChildWorkload> workloads = new HashSet<>(this.workloads);
    this.workloads.clear();
    return workloads;
  }
}
