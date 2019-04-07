

package grv.distributed.example;

import grv.distributed.workload.runnable.AbstractWorkloadRunnable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExampleWorkloadRunnable extends AbstractWorkloadRunnable<ExampleWorkload> {

  /**
   * Constructor.
   *
   * @param workload Workload that this runnable services.
   */
  public ExampleWorkloadRunnable(ExampleWorkload workload) {
    super(workload);
  }


  @Override
  public void execute() throws InterruptedException {
    while (!Thread.currentThread().isInterrupted()) {
      log.info("Workload " + getWorkload().getUrn() + " ticked.");
      Thread.sleep(1000);
    }
  }
}
