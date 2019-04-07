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
import grv.distributed.workload.repository.source.MasterWorkloadRepositorySource;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

/**
 * For demonstration purposes, simply returns the static list of workloads to run.
 * In non-trivial implementations, workload repository sources might load workloads
 * from a database or some other service.
 */
@Service
public class NumberPrinterRepositorySource extends MasterWorkloadRepositorySource<NumberPrinterWorkload> {

  private final Set<NumberPrinterWorkload> workloads;

  public NumberPrinterRepositorySource() {
    this.workloads = new HashSet<>();
  }

  public void add(NumberPrinterWorkload workload) {
    this.workloads.add(workload);
  }

  @Override
  public Stream<NumberPrinterWorkload> queryMasterWorkloads() {
    Set<NumberPrinterWorkload> workloads = new HashSet<>(this.workloads);
    this.workloads.clear();
    return workloads.stream();
  }

  @Override
  protected Stream<NumberPrinterWorkload> breakDown(NumberPrinterWorkload request) {
    return LongStream.range(request.getBegin(), request.getEnd())
        .boxed()
        .map(a -> new NumberPrinterWorkload(a, a));
  }
}
