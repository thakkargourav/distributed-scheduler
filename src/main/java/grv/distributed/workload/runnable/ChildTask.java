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
    setRunningState(RunningState.RUNNING);
    try {
      execute();
      setRunningState(RunningState.STOPPED);
    } catch (InterruptedException ignored) {
      setRunningState(RunningState.STOPPED);
    } catch (Exception e) {
      setException(e);
      addError();
      setRunningState(RunningState.ERROR);
    }
    countDown();
  }

  private synchronized void addError() {
    String masterUrn = getWorkload().getMasterUrn();
    String childUrn = getWorkload().getUrn();
    hazelcastInstance.getMultiMap("ERROR").put(masterUrn, childUrn);
    log.info("Error occurred while processing child: {} for master: {}", childUrn, masterUrn);
  }

  private synchronized void countDown() {
    String masterUrn = getWorkload().getMasterUrn();
    hazelcastInstance.getCountDownLatch(masterUrn).countDown();
    String urn = getWorkload().getUrn();
    log.info("Decreasing count of master: {} for child: {}", masterUrn, urn);
  }
}
