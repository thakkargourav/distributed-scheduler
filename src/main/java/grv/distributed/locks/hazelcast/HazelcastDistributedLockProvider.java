/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package grv.distributed.locks.hazelcast;

import grv.distributed.locks.DistributedLock;
import grv.distributed.locks.DistributedLockProvider;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.stereotype.Service;

@Service
public class HazelcastDistributedLockProvider implements DistributedLockProvider {
  /**
   * Hazelcast instance.
   */
  private final HazelcastInstance hazelcastInstance;

  /**
   * Constructor.
   *
   * @param hazelcastInstance Hazelcast instance that backs the distributed locks.
   */
  public HazelcastDistributedLockProvider(HazelcastInstance hazelcastInstance) {
    this.hazelcastInstance = hazelcastInstance;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DistributedLock getDistributedLock(String key) {
    return new HazelcastDistributedLock(hazelcastInstance.getLock(key));
  }
}