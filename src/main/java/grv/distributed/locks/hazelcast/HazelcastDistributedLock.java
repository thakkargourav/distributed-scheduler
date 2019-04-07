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

import grv.distributed.locks.AbstractDistributedLock;
import grv.distributed.locks.DistributedLock;
import com.hazelcast.core.ILock;

import java.util.concurrent.TimeUnit;

public class HazelcastDistributedLock extends AbstractDistributedLock implements DistributedLock {
  /**
   * Constructor.
   *
   * @param lock Underlying Hazelcast {@link ILock} implementation.
   */
  public HazelcastDistributedLock(ILock lock) {
    super(lock);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void lock(long leaseTime, TimeUnit timeUnit) {
    ((ILock) getLock()).lock(leaseTime, timeUnit);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean tryLock(long waitTime,
                         TimeUnit waitTimeUnit,
                         long leaseTime,
                         TimeUnit leaseTimeUnit) throws InterruptedException {
    return ((ILock) getLock()).tryLock(waitTime, waitTimeUnit, leaseTime, leaseTimeUnit);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isLocked() {
    return ((ILock) getLock()).isLocked();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean supportsLeases() {
    return true;
  }
}
