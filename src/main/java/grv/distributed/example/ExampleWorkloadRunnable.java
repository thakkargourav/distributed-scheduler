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

package grv.distributed.example;

import grv.distributed.RunningState;
import grv.distributed.workload.runnable.AbstractWorkloadRunnable;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  public void run() {
    setRunningState(RunningState.RUNNING);

    try {
      while (!Thread.currentThread().isInterrupted()) {
        log.info("Workload " + getWorkload().getUrn() + " ticked.");
        try {
          Thread.sleep(1000);
        } catch (InterruptedException ignored) {
          Thread.currentThread().interrupt();
          setRunningState(RunningState.STOPPED);
        }
      }

      setRunningState(RunningState.STOPPED);
    } catch (Exception e) {
      setRunningState(RunningState.ERROR);
    }
  }
}
