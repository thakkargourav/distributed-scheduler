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

package grv.distributed.example.number.service;

import grv.distributed.example.number.NumberPrinterWorkload;
import grv.distributed.workload.context.SingleThreadedWorkloadContext;
import grv.distributed.workload.Workload;
import grv.distributed.workload.context.WorkloadContext;
import grv.distributed.workload.context.WorkloadContextFactory;
import org.springframework.stereotype.Service;

@Service
public class NumberPrinterWorkloadContextFactory implements WorkloadContextFactory<NumberPrinterWorkload> {

  @Override
  public Class<NumberPrinterWorkload> klass() {
    return NumberPrinterWorkload.class;
  }

  @Override
  public WorkloadContext<NumberPrinterWorkload> createContext(Workload workload) {
    return new SingleThreadedWorkloadContext(new NumberPrinterWorkloadRunnable((NumberPrinterWorkload) workload));
  }

}
