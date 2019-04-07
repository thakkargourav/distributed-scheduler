
package grv.distributed.locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * A base implementation of a {@link DistributedLock} useful for implementation which merely
 * wrap an existing {@link Lock}.
 */
public class AbstractDistributedLock implements DistributedLock {
  /**
   * Underlying lock this class is proxying.
   */
  private final Lock lock;

  /**
   * Constructor.
   *
   * @param lock Lock this class will proxy.
   */
  public AbstractDistributedLock(Lock lock) {
    this.lock = lock;
  }

  /**
   * Returns the underlying lock instance.
   *
   * @return The underlying lock instance.
   */
  protected Lock getLock() {
    return lock;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isLocked() {
    throw new UnsupportedOperationException();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void lock() {
    lock.lock();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void lockInterruptibly() throws InterruptedException {
    lock.lockInterruptibly();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean tryLock() {
    return lock.tryLock();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
    return lock.tryLock(time, unit);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void unlock() {
    lock.unlock();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Condition newCondition() {
    return lock.newCondition();
  }
}
