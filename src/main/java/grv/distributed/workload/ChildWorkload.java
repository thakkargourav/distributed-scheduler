

package grv.distributed.workload;

/**
 * Provides the basis for representing a a unit of work that can be considered a workload.
 */
public abstract class ChildWorkload extends Workload {

  /**
   * Constructor.
   *
   * @param id ID of the workload.
   */
  protected ChildWorkload(String id) {
    super(id);
  }
}