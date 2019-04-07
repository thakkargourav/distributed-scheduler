package grv.distributed.example.number.child;

import grv.distributed.workload.ChildWorkload;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class NumberPrinterChildWorkload extends ChildWorkload {

  private long begin;
  private long end;

  public NumberPrinterChildWorkload(long begin, long end) {
    super(String.format("%s-%s", begin, end));
    this.begin = begin;
    this.end = end;
  }

}
