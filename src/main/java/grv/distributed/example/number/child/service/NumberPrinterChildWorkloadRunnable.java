

package grv.distributed.example.number.child.service;

import grv.distributed.example.number.child.NumberPrinterChildWorkload;
import grv.distributed.workload.runnable.ChildTask;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.LongStream;

@Slf4j
public class NumberPrinterChildWorkloadRunnable extends ChildTask<NumberPrinterChildWorkload> {

  /**
   * Constructor.
   *
   * @param workload Workload that this runnable services.
   */
  public NumberPrinterChildWorkloadRunnable(NumberPrinterChildWorkload workload) {
    super(workload);
  }

  @Override
  public void execute() {
    NumberPrinterChildWorkload workload = getWorkload();
    LongStream.range(workload.getBegin(), workload.getEnd() + 1)
        .forEach(number -> log.info("Number: {}", number));
  }
}
