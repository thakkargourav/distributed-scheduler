
package grv.distributed.locks.hazelcast;

import com.hazelcast.core.ILock;
import grv.distributed.locks.AbstractDistributedLock;
import grv.distributed.locks.DistributedLock;

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
