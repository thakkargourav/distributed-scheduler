package grv.distributed.example.number;

import grv.distributed.DistributedScheduler;
import grv.distributed.example.number.job.AddWorkload;
import grv.distributed.example.number.master.NumberPrinterMasterWorkload;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;

import static org.quartz.Scheduler.DEFAULT_GROUP;

@RestController
@Slf4j
public class SchedulerController {

  @Autowired
  private DistributedScheduler distributedScheduler;

  @Autowired
  private Scheduler scheduler;


  @GetMapping("execute")
  public void test(long begin, long end) {
    distributedScheduler.add(new NumberPrinterMasterWorkload(begin, end));
  }

  //Example cron: "* * 23 * 4 ?" indicates 23rd hour of all days in april
  @GetMapping("schedule")
  public void schedule(long begin, long end, String cron) throws SchedulerException, ParseException {
    CronExpression cronExpression = new CronExpression(cron);
    long time = cronExpression.getNextValidTimeAfter(Date.from(Instant.now())).getTime();
    JobDetail job = buildJob("addWork" + time + begin + end, DEFAULT_GROUP, AddWorkload.class, begin, end);
    Trigger trigger = buildTrigger("key" + time + begin + end, DEFAULT_GROUP, job, time);
    scheduler.scheduleJob(job, trigger);
    log.info("scheduled: {}, with trigger: {}", job, trigger);
  }

  private JobDetail buildJob(String jobName, String grouName, Class<? extends Job> jobClass, long begin, long end) {
    JobDataMap newJobDataMap = new JobDataMap();
    newJobDataMap.put("begin", begin);
    newJobDataMap.put("end", end);
    return JobBuilder.newJob(jobClass).withIdentity(jobName, grouName).setJobData(newJobDataMap).build();
  }

  private Trigger buildTrigger(String triggerName,
                               String triggerGroup,
                               JobDetail job,
                               Long startAt) {

    return TriggerBuilder
        .newTrigger()
        .withIdentity(triggerName, triggerGroup)
        .forJob(job)
        .startAt(startAt != null ? new Date(startAt) : null)
        .build();
  }

}
