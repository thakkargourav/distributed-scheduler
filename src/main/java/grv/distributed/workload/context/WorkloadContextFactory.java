

package grv.distributed.workload.context;

import grv.distributed.workload.Workload;
import grv.distributed.workload.runnable.WorkloadRunnable;

/**
 * Describes a factory for workload contexts.
 * <p>
 * Implementations of this interface provide the glue between a workload (a unit of work)
 * and some logic that acts on the workload. The factory is responsible for creating
 * a workload context that is specific to some executor that knows how to handle the workload
 * type.
 * <p>
 * There is no assumption that a workload type
 * maps to only one context factory; in fact, more that one factory may support a single
 * workload type.
 */
public interface WorkloadContextFactory<T extends Workload> {
  /**
   * Returns whether the workload context factory supports the given workload.
   *
   * @return the type of workload.
   */
  Class<T> klass();

  /**
   * Creates a new {@link WorkloadContext} for the given workload.
   *
   * @param workload Workload to create a context for.
   * @return a new {@link WorkloadRunnable} for the given workload.
   */
  WorkloadContext createContext(Workload workload);
}
