package grv.distributed.example.number;

import grv.distributed.example.number.service.NumberPrinterRepositorySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SchedulerController {

  @Autowired
  private NumberPrinterRepositorySource numberPrinterRepositorySource;


  @GetMapping("schedule")
  public void test(long begin, long end) {
    numberPrinterRepositorySource.add(new NumberPrinterWorkload(begin, end));
  }

}
