

package grv.distributed.workload.runnable;

import com.hazelcast.core.HazelcastInstance;
import grv.distributed.RunningState;
import grv.distributed.workload.Workload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * A base implementation of {@link WorkloadRunnable} that provides most of the functionality
 * of how a concrete implementation should behave, besides the actual logic of running the workload.
 */
public abstract class AbstractWorkloadRunnable<T extends Workload> implements WorkloadRunnable<T> {

  @Autowired
  transient HazelcastInstance hazelcastInstance;

  /**
   * Workload.
   */
  private final T workload;

  /**
   * Running state.
   */
  protected RunningState runningState = RunningState.NOT_STARTED;

  /**
   * Exception thrown during the course of running the workload.
   */
  private Throwable throwable = null;

  /**
   * Constructor.
   *
   * @param workload Workload that this runnable services.
   */
  protected AbstractWorkloadRunnable(T workload) {
    Assert.notNull(workload, "workload must not be null");
    this.workload = workload;
  }

  public abstract void execute() throws InterruptedException;


  /**
   * {@inheritDoc}
   */
  @Override
  public T getWorkload() {
    return workload;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RunningState getRunningState() {
    return runningState;
  }

  /**
   * Sets the running state of the runnable.
   *
   * @param runningState Running state of the runnable.
   */
  protected void setRunningState(RunningState runningState) {
    this.runningState = runningState;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void terminate() {
    runningState = RunningState.STOPPED;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void fail() {
    runningState = RunningState.ERROR;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Throwable getException() {
    return throwable;
  }

  /**
   * Sets the exception thrown during the course of running the workload.
   *
   * @param throwable the exception thrown during the course of running the workload.
   */
  protected void setException(Throwable throwable) {
    this.throwable = throwable;
  }

}
