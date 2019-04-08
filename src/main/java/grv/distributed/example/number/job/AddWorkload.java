package grv.distributed.example.number.job;

import grv.distributed.DistributedScheduler;
import grv.distributed.example.number.master.NumberPrinterMasterWorkload;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Slf4j
@Service
public class AddWorkload implements Job, Serializable {

    @Autowired
    public DistributedScheduler distributedScheduler;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap mergedJobDataMap = jobExecutionContext.getMergedJobDataMap();
        long begin = mergedJobDataMap.getLong("begin");
        long end = mergedJobDataMap.getLong("end");
        distributedScheduler.add(new NumberPrinterMasterWorkload(begin, end));
        log.info("Job: {} added NumberPrinterMasterWorkload with begin: {} and end: {}", jobExecutionContext.getJobDetail().getKey(), begin, end);
    }
}
