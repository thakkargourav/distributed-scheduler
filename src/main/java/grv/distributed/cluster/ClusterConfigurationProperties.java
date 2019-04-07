package grv.distributed.cluster;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("cluster")
@Component
public class ClusterConfigurationProperties {
  /**
   * How long the cluster manager should wait for instructions to finish before giving up.
   */
  private long instructionTimeout = 120000L;

  public long getInstructionTimeout() {
    return instructionTimeout;
  }

  public void setInstructionTimeout(long instructionTimeout) {
    this.instructionTimeout = instructionTimeout;
  }
}
