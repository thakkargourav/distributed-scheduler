
package grv.distributed.locks.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import grv.distributed.locks.DistributedLock;
import grv.distributed.locks.DistributedLockProvider;
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
