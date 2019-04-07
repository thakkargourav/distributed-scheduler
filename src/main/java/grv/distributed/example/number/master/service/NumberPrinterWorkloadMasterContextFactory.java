

package grv.distributed.example.number.master.service;

import grv.distributed.example.number.master.NumberPrinterMasterWorkload;
import grv.distributed.workload.Workload;
import grv.distributed.workload.context.SingleThreadedWorkloadContext;
import grv.distributed.workload.context.WorkloadContext;
import grv.distributed.workload.context.WorkloadContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class NumberPrinterWorkloadMasterContextFactory implements WorkloadContextFactory<NumberPrinterMasterWorkload> {

  @Autowired
  private ApplicationContext applicationContext;

  @Override
  public Class<NumberPrinterMasterWorkload> klass() {
    return NumberPrinterMasterWorkload.class;
  }

  @Override
  public WorkloadContext<NumberPrinterMasterWorkload> createContext(Workload workload) {
    NumberPrinterMasterWorkloadRunnable numberPrinterMasterWorkloadRunnable = new NumberPrinterMasterWorkloadRunnable((NumberPrinterMasterWorkload) workload);
    AutowireCapableBeanFactory factory = applicationContext.getAutowireCapableBeanFactory();
    factory.autowireBean( numberPrinterMasterWorkloadRunnable );
    factory.initializeBean( numberPrinterMasterWorkloadRunnable, numberPrinterMasterWorkloadRunnable.getClass().getName() );
    return new SingleThreadedWorkloadContext(numberPrinterMasterWorkloadRunnable);
  }

}
