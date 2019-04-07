

package grv.distributed.example.number.child.service;

import grv.distributed.example.number.child.NumberPrinterChildWorkload;
import grv.distributed.workload.Workload;
import grv.distributed.workload.context.SingleThreadedWorkloadContext;
import grv.distributed.workload.context.WorkloadContext;
import grv.distributed.workload.context.WorkloadContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class NumberPrinterWorkloadChildContextFactory implements WorkloadContextFactory<NumberPrinterChildWorkload> {

  @Autowired
  private ApplicationContext applicationContext;

  @Override
  public Class<NumberPrinterChildWorkload> klass() {
    return NumberPrinterChildWorkload.class;
  }

  @Override
  public WorkloadContext<NumberPrinterChildWorkload> createContext(Workload workload) {
    NumberPrinterChildWorkloadRunnable numberPrinterChildWorkloadRunnable = new NumberPrinterChildWorkloadRunnable((NumberPrinterChildWorkload) workload);
    AutowireCapableBeanFactory factory = applicationContext.getAutowireCapableBeanFactory();
    factory.autowireBean( numberPrinterChildWorkloadRunnable );
    factory.initializeBean( numberPrinterChildWorkloadRunnable, numberPrinterChildWorkloadRunnable.getClass().getName() );
    return new SingleThreadedWorkloadContext(numberPrinterChildWorkloadRunnable);
  }

}
