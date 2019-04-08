package grv.distributed.config;

import com.bikeemotion.quartz.jobstore.hazelcast.HazelcastJobStore;
import com.hazelcast.core.HazelcastInstance;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import java.util.Properties;

@Configuration
public class QuartzConfiguration {

  @Bean
  @Primary
  public Scheduler scheduler(HazelcastInstance hazelcastInstance, SchedulerFactoryBean schedulerFactoryBean) throws SchedulerException {
    HazelcastJobStore.setHazelcastClient(hazelcastInstance);
    Properties props = new Properties();
    props.setProperty(StdSchedulerFactory.PROP_JOB_STORE_CLASS, HazelcastJobStore.class.getName());
    props.setProperty(StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME, "sc");
    props.setProperty(StdSchedulerFactory.PROP_SCHED_JMX_EXPORT, "true");
    props.setProperty(StdSchedulerFactory.PROP_THREAD_POOL_PREFIX + ".threadCount", "10");
    props.setProperty(StdSchedulerFactory.PROP_THREAD_POOL_PREFIX + ".threadPriority", "5");
    Scheduler scheduler = schedulerFactoryBean.getScheduler();
    scheduler.start();
    return scheduler;
  }


  @Bean
  public JobFactory jobFactory(ApplicationContext applicationContext) {
    AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
    jobFactory.setApplicationContext(applicationContext);
    return jobFactory;
  }

  @Bean
  public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory) {
    SchedulerFactoryBean factory = new SchedulerFactoryBean();
    factory.setSchedulerName("evive-quartz-scheduler");
    factory.setOverwriteExistingJobs(true);
    factory.setJobFactory(jobFactory);
    return factory;
  }


  private final class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {

    private transient AutowireCapableBeanFactory beanFactory;
    private transient ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) {
      this.applicationContext = context;
      beanFactory = context.getAutowireCapableBeanFactory();
    }

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
      final Object job = super.createJobInstance(bundle);
      String jobBeanName = applicationContext.getBeanNamesForType(job.getClass())[0];
      beanFactory.configureBean(job, jobBeanName);
      return job;
    }


  }

}
