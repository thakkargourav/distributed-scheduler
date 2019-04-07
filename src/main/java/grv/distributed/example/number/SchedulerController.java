package grv.distributed.example.number;

import grv.distributed.example.number.master.NumberPrinterMasterWorkload;
import grv.distributed.example.number.master.service.NumberPrinterMasterRepositorySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SchedulerController {

  @Autowired
  private NumberPrinterMasterRepositorySource numberPrinterMasterRepositorySource;


  @GetMapping("schedule")
  public void test(long begin, long end) {
    numberPrinterMasterRepositorySource.addWorkLoads(new NumberPrinterMasterWorkload(begin, end));
  }

}
