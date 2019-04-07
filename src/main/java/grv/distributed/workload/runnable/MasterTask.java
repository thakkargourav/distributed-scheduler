package grv.distributed.workload.runnable;

import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.core.MultiMap;
import grv.distributed.DistributedScheduler;
import grv.distributed.RunningState;
import grv.distributed.workload.ChildWorkload;
import grv.distributed.workload.MasterWorkload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public abstract class MasterTask<M extends MasterWorkload, C extends ChildWorkload>
    extends AbstractWorkloadRunnable<M> {

  public static final String ERROR_KEY_NAME = "ERROR";
  @Autowired
  private transient DistributedScheduler distributedScheduler;

  /**
   * Constructor.
   *
   * @param workload Workload that this runnable services.
   */
  protected MasterTask(M workload) {
    super(workload);
  }

  @Override
  public void run() {
    setRunningState(RunningState.RUNNING);
    try {
      String urn = getWorkload().getUrn();
      ICountDownLatch countDownLatch = hazelcastInstance.getCountDownLatch(urn);
      List<C> breakDowns = breakDown(getWorkload(), urn);
      boolean setCount = countDownLatch.trySetCount(breakDowns.size());
      if (!setCount) {
        setRunningState(RunningState.ERROR);
        return;
      }
      distributedScheduler.add(breakDowns);
    } catch (Exception e) {
      setException(e);
      setRunningState(RunningState.ERROR);
    }
  }

  protected abstract List<C> breakDown(M workload, String urn);

  @Override
  public void execute() {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RunningState getRunningState() {
    if (RunningState.ERROR == runningState || RunningState.STOPPED == runningState) {
      return runningState;
    }

    String masterUrn = getWorkload().getUrn();

    ICountDownLatch countDownLatch = hazelcastInstance.getCountDownLatch(masterUrn);
    int pendingTasks = countDownLatch.getCount();

    MultiMap<Object, Object> errorMultimap = hazelcastInstance.getMultiMap(ERROR_KEY_NAME);
    List<String> errorChildUrns = errorMultimap.get(masterUrn)
        .stream()
        .filter(a -> a instanceof String)
        .map(a -> (String) a)
        .collect(Collectors.toList());

    boolean errorsPresent = !errorChildUrns.isEmpty();

    if (errorsPresent) {
      log.error("Marking master: {} FAILED since some of children failed. Failed children: {}",
                masterUrn,
                errorChildUrns);
    }

    if (0 == pendingTasks) {
      countDownLatch.destroy();
      errorMultimap.remove(masterUrn);
      runningState = errorsPresent ? RunningState.ERROR : RunningState.STOPPED;
    }

    return runningState;
  }

}
