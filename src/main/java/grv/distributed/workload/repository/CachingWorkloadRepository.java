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

package grv.distributed.workload.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import grv.distributed.workload.Workload;
import grv.distributed.workload.repository.source.WorkloadRepositorySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * An implementation of {@link WorkloadRepository} that queries {@link WorkloadRepositorySource} beans for workloads,
 * and caches the results for a configured time period.
 */
@Service
public class CachingWorkloadRepository extends SimpleWorkloadRepository implements WorkloadRepository {
  /**
   * Lock object used to synchronize cache updates.
   */
  private final Object cacheLock = new Object();

  /**
   * Cached workloads.
   */
  private Set<Workload> workloads = new HashSet<>();

  /**
   * When the cache expires.
   */
  private long cacheExpiry;

  /**
   * Constructor.
   *
   * @param sources List of {@link WorkloadRepositorySource workload repository sources} to use.
   */
  @Autowired
  public CachingWorkloadRepository(List<WorkloadRepositorySource> sources) {
    super(sources);
  }

  /**
   * {@inheritDoc}
   */
  public Set<Workload> getWorkloads() {
    if (System.currentTimeMillis() > cacheExpiry) {
      synchronized (cacheLock) {
        if (System.currentTimeMillis() > cacheExpiry) {
          workloads = super.getWorkloads();
          cacheExpiry = System.currentTimeMillis() + 60; // TODO: configurable!
        }
      }
    }
    return workloads;
  }
}
