package grv.distributed.workload.runnable;

import grv.distributed.RunningState;
import grv.distributed.workload.ChildWorkload;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ChildTask<T extends ChildWorkload> extends AbstractWorkloadRunnable<T> {

  /**
   * Constructor.
   *
   * @param workload Workload that this runnable services.
   */
  protected ChildTask(T workload) {
    super(workload);
  }

  @Override
  public void run() {
    log.info("Child task {} has started", getWorkload().getUrn());
    setRunningState(RunningState.RUNNING);
    try {
      execute();
      setRunningState(RunningState.STOPPED);
      log.info("Child task {} marked completed", getWorkload().getUrn());
    } catch (Exception e) {
      setException(e);
      addError(e);
      setRunningState(RunningState.ERROR);
    } finally {
      countDown();
    }
  }

  private void addError(Exception e) {
    String masterUrn = getWorkload().getMasterUrn();
    String childUrn = getWorkload().getUrn();
    hazelcastInstance.getMultiMap("ERROR").put(masterUrn, childUrn);
    log.error("Error occurred while processing child: {} for master: {}", childUrn, masterUrn, e);
  }

  private void countDown() {
    String masterUrn = getWorkload().getMasterUrn();
    hazelcastInstance.getCountDownLatch(masterUrn).countDown();
    String urn = getWorkload().getUrn();
    log.debug("Decreasing count of master: {} for child: {}", masterUrn, urn);
  }
}
