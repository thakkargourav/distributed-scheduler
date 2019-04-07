

package grv.distributed;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

/**
 * A set of configuration properties used to manage the cluster.
 */
@Data
@Validated
@Component
@ConfigurationProperties("scheduler")
public class SchedulerProperties {
  /**
   * The amount of time that should pass between cluster re-balancing.
   */
  private Duration rebalanceInterval = Duration.ofSeconds(3);
  /**
   * The amount of time that the application should wait until it starts checking whether
   * a re-balance should occur.
   */
  private Duration rebalancePollDelay = Duration.ofSeconds(3);
  /**
   * The amount of time that should pass between checks to determine whether a re-rebalance
   * should occur
   */
  private Duration rebalancePollInterval = Duration.ofSeconds(3);
  /**
   * The amount of time that the worker context manager should wait before polling whether
   * the instruction logic has completed.
   */
  private Duration actionPollInterval = Duration.ofMillis(250);

}
