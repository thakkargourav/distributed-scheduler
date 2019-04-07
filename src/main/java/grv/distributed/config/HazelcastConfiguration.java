package grv.distributed.config;

import com.hazelcast.config.Config;
import com.hazelcast.spring.context.SpringManagedContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfiguration {

  @Bean
  public Config hazelCastConfig() {
    Config config = new Config();
    config.setInstanceName("hazelcast-instance");
//    config.getGroupConfig().setName("hazelcast-instance-test");
    config.setManagedContext(managedContext());
    return config;
  }

  @Bean
  public SpringManagedContext managedContext() {
    return new SpringManagedContext();
  }

}