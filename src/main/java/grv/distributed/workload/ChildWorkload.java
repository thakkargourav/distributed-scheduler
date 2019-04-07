package grv.distributed.workload;

import lombok.Data;

/**
 * Provides the basis for representing a a unit of work that can be considered a workload.
 */
@Data
public abstract class ChildWorkload extends Workload {

  private static final long serialVersionUID = 8279634031366472547L;

  private final String masterUrn;

  /**
   * Constructor.
   *
   * @param id ID of the workload.
   */
  protected ChildWorkload(String id, String masterUrn) {
    super(id);
    this.masterUrn = masterUrn;
  }
}