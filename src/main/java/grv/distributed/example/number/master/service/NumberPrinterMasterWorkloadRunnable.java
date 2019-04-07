

package grv.distributed.example.number.master.service;

import grv.distributed.example.number.child.NumberPrinterChildWorkload;
import grv.distributed.example.number.master.NumberPrinterMasterWorkload;
import grv.distributed.workload.runnable.MasterTask;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Slf4j
public class NumberPrinterMasterWorkloadRunnable extends MasterTask<NumberPrinterMasterWorkload, NumberPrinterChildWorkload> {

  /**
   * Constructor.
   *
   * @param workload Workload that this runnable services.
   */
  public NumberPrinterMasterWorkloadRunnable(NumberPrinterMasterWorkload workload) {
    super(workload);
  }

  @Override
  protected List<NumberPrinterChildWorkload> breakDown(NumberPrinterMasterWorkload workload,
                                                       String urn) {
    return LongStream.range(workload.getBegin(), workload.getEnd())
        .boxed()
        .map(a -> new NumberPrinterChildWorkload(a, a, urn))
        .collect(Collectors.toList());
  }
}
