/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package grv.distributed.workload.runnable;

import com.hazelcast.spring.context.SpringAware;
import grv.distributed.RunningState;
import grv.distributed.workload.Workload;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * A base implementation of {@link WorkloadRunnable} that provides most of the functionality
 * of how a concrete implementation should behave, besides the actual logic of running the workload.
 */
@SpringAware
public abstract class AbstractWorkloadRunnable<T extends Workload> implements WorkloadRunnable<T>,
    ApplicationContextAware, Serializable {
  /**
   * Workload.
   */
  private final T workload;

  /**
   * Running state.
   */
  private RunningState runningState = RunningState.NOT_STARTED;

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
      setRunningState(RunningState.ERROR);
    }
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

  /**
   * {@inheritDoc}
   */
  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
    beanFactory.autowireBean(this);
    beanFactory.initializeBean(this, this.getClass().getName());
  }
}
