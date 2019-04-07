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

package grv.distributed.instruction;

import grv.distributed.workload.context.manager.WorkloadContextManager;
import grv.distributed.workload.WorkloadReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * An {@link Instruction} that requests a {@link WorkloadReport} from
 * a cluster member.
 */
@Slf4j
public class ReportInstruction implements Instruction<WorkloadReport> {

  private static final long serialVersionUID = -4957030174170441321L;

  /**
   * Workload context manager.
   */
  @Autowired
  private transient WorkloadContextManager workloadContextManager;

  /**
   * {@inheritDoc}
   */
  @Override
  public WorkloadReport call() {
    try {
      return workloadContextManager.getWorkloadReport();
    } catch (Exception e) {
      log.error("Unhandled exception encountered while retrieving report.", e);
      return null;
    }
  }
}
