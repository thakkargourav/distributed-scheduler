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

package grv.distributed.locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public interface DistributedLock extends Lock {
  /**
   * Attempts to acquire a lock with and a lock timeout iff the lock is available.
   *
   * @param leaseTime The amount of time to wait for the lock to become available.
   * @param timeUnit  The time unit of waitTime.
   * @throws InterruptedException if the current thread is
   *                              interrupted while acquiring the lock (and interruption
   *                              of lock acquisition is supported)
   */
  void lock(long leaseTime, TimeUnit timeUnit) throws InterruptedException;

  /**
   * Attempts to acquire a lock with a wait timeout and a lock timeout.
   * <p>
   * If the lock becomes available within the wait timeout, the lock will be acquired and the method
   * will return {@code true}. If the wait timeout is reached, the lock is not acquired.
   *
   * @param waitTime      The amount of time to wait for the lock to become available.
   * @param waitTimeUnit  The time unit of waitTime.
   * @param leaseTime     The amount of time that lock should be considered valid.
   * @param leaseTimeUnit The time unit of lockTime.
   * @return Whether the lock was acquired.
   * @throws InterruptedException if the current thread is
   *                              interrupted while acquiring the lock (and interruption
   *                              of lock acquisition is supported)
   */
  boolean tryLock(long waitTime, TimeUnit waitTimeUnit, long leaseTime, TimeUnit leaseTimeUnit)
      throws InterruptedException;

  /**
   * Returns whether the lock instance believes is currently owns the lock.
   *
   * @return Whether the lock instance believes is currently owns the lock.
   */
  boolean isLocked();

  /**
   * Returns whether the distributed lock implementation support leases.
   * <p>
   * If {@code false}, the behavior of the lease-aware methods is undefined.
   *
   * @return Whether leases are supported.
   */
  boolean supportsLeases();
}
