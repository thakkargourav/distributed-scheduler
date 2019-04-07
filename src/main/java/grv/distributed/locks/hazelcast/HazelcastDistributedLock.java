
package grv.distributed.locks.hazelcast;

import com.hazelcast.core.ILock;
import grv.distributed.locks.AbstractDistributedLock;
import grv.distributed.locks.DistributedLock;

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
  public boolean isLocked() {
    return ((ILock) getLock()).isLocked();
  }
}
