/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package grv.distributed;

import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

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
