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
import grv.distributed.workload.runnable.MasterTask;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Slf4j
public class NumberPrinterMasterWorkloadRunnable extends MasterTask<NumberPrinterWorkload,NumberPrinterWorkload> {

  /**
   * Constructor.
   *
   * @param workload Workload that this runnable services.
   */
  public NumberPrinterMasterWorkloadRunnable(NumberPrinterWorkload workload) {
    super(workload);
  }

  @Override
  protected List<NumberPrinterWorkload> breakDown(NumberPrinterWorkload workload) {
    return LongStream.range(workload.getBegin(), workload.getEnd())
        .boxed()
        .map(a -> new NumberPrinterWorkload(a, a))
        .collect(Collectors.toList());
  }
}
