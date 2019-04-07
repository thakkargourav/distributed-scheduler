package grv.distributed.example.number.child;

import grv.distributed.workload.ChildWorkload;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NumberPrinterChildWorkload extends ChildWorkload {

  private static final long serialVersionUID = 3031125797342448160L;

  private long begin;
  private long end;

  public NumberPrinterChildWorkload(long begin, long end, String urn) {
    super(String.format("%s-%s", begin, end), urn);
    this.begin = begin;
    this.end = end;
  }

}
