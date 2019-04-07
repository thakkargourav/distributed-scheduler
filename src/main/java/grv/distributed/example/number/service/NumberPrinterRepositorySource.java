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
import grv.distributed.workload.repository.source.WorkloadRepositorySource;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * For demonstration purposes, simply returns the static list of workloads to run.
 * In non-trivial implementations, workload repository sources might load workloads
 * from a database or some other service.
 */
@Service
public class NumberPrinterRepositorySource implements WorkloadRepositorySource<NumberPrinterWorkload> {

  private final Set<NumberPrinterWorkload> workloads;

  public NumberPrinterRepositorySource() {
    this.workloads = new HashSet<>();
  }

  @Override
  public void addWorkLoads(List<NumberPrinterWorkload> workloads) {
    this.workloads.addAll(workloads);
  }

  @Override
  public final Set<NumberPrinterWorkload> queryWorkloads() {
    Set<NumberPrinterWorkload> workloads = new HashSet<>(this.workloads);
    this.workloads.clear();
    return workloads;
  }
}
