

package grv.distributed.example;

import grv.distributed.workload.Workload;
import grv.distributed.workload.context.SingleThreadedWorkloadContext;
import grv.distributed.workload.context.WorkloadContext;
import grv.distributed.workload.context.WorkloadContextFactory;
import org.springframework.stereotype.Service;

@Service
public class ExampleWorkloadContextFactory implements WorkloadContextFactory<ExampleWorkload> {

  @Override
  public Class<ExampleWorkload> klass() {
    return ExampleWorkload.class;
  }

  @Override
  public WorkloadContext<ExampleWorkload> createContext(Workload workload) {
    return new SingleThreadedWorkloadContext<ExampleWorkload>(new ExampleWorkloadRunnable((ExampleWorkload) workload));
  }

}
