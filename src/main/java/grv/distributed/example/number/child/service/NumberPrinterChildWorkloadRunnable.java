package grv.distributed.example.number.child.service;

import grv.distributed.example.number.child.NumberPrinterChildWorkload;
import grv.distributed.workload.runnable.ChildTask;
import lombok.extern.slf4j.Slf4j;

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
    for (long i = workload.getBegin(); i <= workload.getEnd(); i++) {
      // Intentionally failing for 9 for testing purpose.
      if (9 == i) {
        throw new IllegalStateException(
            "Intentionally failing for 9 for testing purpose. 9 should not have happened");
      }
      log.info("Number: {}", i);
    }
  }
}
