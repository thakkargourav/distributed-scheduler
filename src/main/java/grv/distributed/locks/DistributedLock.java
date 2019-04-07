package grv.distributed.locks;

import java.util.concurrent.locks.Lock;

public interface DistributedLock extends Lock {

  /**
   * Returns whether the lock instance believes is currently owns the lock.
   *
   * @return Whether the lock instance believes is currently owns the lock.
   */
  boolean isLocked();
}
