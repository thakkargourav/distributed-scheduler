
package grv.distributed.locks;

public interface DistributedLockProvider {
  /**
   * Return a {@link DistributedLock} instance for the given lock key name.
   *
   * @param key The key or name of the lock.
   * @return A {@link DistributedLock} instance for the given lock key name.
   */
  DistributedLock getDistributedLock(String key);
}
