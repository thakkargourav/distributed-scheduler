package grv.distributed.example.number;

import grv.distributed.DistributedScheduler;
import grv.distributed.example.number.master.NumberPrinterMasterWorkload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SchedulerController {

  @Autowired
  private DistributedScheduler distributedScheduler;


  @GetMapping("schedule")
  public void test(long begin, long end) {
    distributedScheduler.add(new NumberPrinterMasterWorkload(begin, end));
  }

}
