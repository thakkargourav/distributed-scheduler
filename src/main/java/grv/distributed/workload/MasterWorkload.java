

package grv.distributed.workload;

/**
 * Provides the basis for representing a a unit of work that can be considered a workload.
 */
public abstract class MasterWorkload extends Workload {

  /**
   * Constructor.
   *
   * @param id ID of the workload.
   */
  protected MasterWorkload(String id) {
    super(id);
  }
}