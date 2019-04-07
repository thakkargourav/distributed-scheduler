

package grv.distributed.example.number.master.service;

import grv.distributed.example.number.master.NumberPrinterMasterWorkload;
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
public class NumberPrinterMasterRepositorySource implements WorkloadRepositorySource<NumberPrinterMasterWorkload> {

  private final Set<NumberPrinterMasterWorkload> workloads;

  public NumberPrinterMasterRepositorySource() {
    this.workloads = new HashSet<>();
  }

  @Override
  public void addWorkLoads(List<NumberPrinterMasterWorkload> workloads) {
    this.workloads.addAll(workloads);
  }

  @Override
  public final Set<NumberPrinterMasterWorkload> queryWorkloads() {
    Set<NumberPrinterMasterWorkload> workloads = new HashSet<>(this.workloads);
    this.workloads.clear();
    return workloads;
  }
}
