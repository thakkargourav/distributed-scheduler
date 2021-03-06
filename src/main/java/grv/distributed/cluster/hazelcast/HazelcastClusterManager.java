package grv.distributed.cluster.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberSelector;
import grv.distributed.cluster.AbstractClusterManager;
import grv.distributed.cluster.ClusterConfigurationProperties;
import grv.distributed.cluster.ClusterManager;
import grv.distributed.cluster.ClusterMember;
import grv.distributed.instruction.Instruction;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * A {@link ClusterManager} implementation backed by Hazelcast.
 */
@Service
public class HazelcastClusterManager extends AbstractClusterManager implements InitializingBean {
  /**
   * Name of the com.budjb.spring.lock.distributed map to store time markers.
   */
  private static final String HAZELCAST_MAP_NAME = "distributed-properties";

  /**
   * Name of the executor service.
   */
  private static final String EXECUTOR_NAME = "cluster-management";

  /**
   * Hazelcast instance.
   */
  private final HazelcastInstance hazelcastInstance;

  /**
   * Hazelcast executor service.
   */
  private IExecutorService executorService;

  /**
   * Constructor.
   *
   * @param hazelcastInstance              Hazelcast instance backing the cluster manager.
   * @param clusterConfigurationProperties Cluster configuration properties.
   */
  public HazelcastClusterManager(HazelcastInstance hazelcastInstance,
                                 ClusterConfigurationProperties clusterConfigurationProperties) {
    super(clusterConfigurationProperties);
    this.hazelcastInstance = hazelcastInstance;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<ClusterMember> getClusterMembers() {
    return hazelcastInstance.getCluster().getMembers().stream()
        .map(HazelcastClusterMember::new)
        .collect(Collectors.toList());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected <T> Future<T> submitInstruction(ClusterMember clusterMember,
                                            Instruction<? extends T> instruction) {
    return executorService.submit(new AutowiringCallableWrapper<>(instruction),
                                  new SingleMemberSelector(((HazelcastClusterMember) clusterMember).getMember())
    );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Map<String, Object> getProperties() {
    return hazelcastInstance.getMap(HAZELCAST_MAP_NAME);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void afterPropertiesSet() {
    executorService = hazelcastInstance.getExecutorService(EXECUTOR_NAME);
  }

  /**
   * A Hazelcast selector that identifies a singular member.
   */
  private static class SingleMemberSelector implements MemberSelector {
    /**
     * Hazelcast cluster member.
     */
    final Member member;

    /**
     * Constructor.
     *
     * @param member Cluster member to select.
     */
    public SingleMemberSelector(Member member) {
      this.member = member;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean select(Member member) {
      return this.member == member;
    }
  }
}
