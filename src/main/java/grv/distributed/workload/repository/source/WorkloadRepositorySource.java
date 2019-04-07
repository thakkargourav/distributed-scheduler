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

package grv.distributed.workload.repository.source;

import grv.distributed.workload.Workload;
import grv.distributed.workload.repository.CachingWorkloadRepository;
import grv.distributed.workload.repository.WorkloadRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * A workload repository source is responsible for providing sets of workloads that
 * the schedule should act upon. Any implementations of this interface will be used
 * by the {@link WorkloadRepository} to aggregate a set of workloads while will be
 * scheduled for execution by the application cluster.
 * <p>
 * There is no assumption that implementation will cache the results of the
 * implementation-specific query. For example, if an implementation must query a
 * database or some other web service, it is left to the implementation to determine
 * whether those results should be cached for a time.
 * <p>
 * As a note, the default {@link WorkloadRepository} registered in Spring is a
 * {@link CachingWorkloadRepository}, which caches the results from all repository
 * source implementations at an aggregated level.
 */
public interface WorkloadRepositorySource<T extends Workload> {

  Set<T> queryWorkloads();

  void addWorkLoads(List<T> workloads);

  default void addWorkLoads(T... workloads) {
    List<T> ts = Arrays.asList(workloads);
    addWorkLoads(ts);
  }



}
