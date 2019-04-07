package grv.distributed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = "grv")
@EnableAutoConfiguration
@EnableScheduling
public class DistributedSchedulerApplication {

  public static void main(String[] args) {
    SpringApplication.run(DistributedSchedulerApplication.class, args);
  }

}
