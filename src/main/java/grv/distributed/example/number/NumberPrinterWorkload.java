package grv.distributed.example.number;

import grv.distributed.workload.Workload;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class NumberPrinterWorkload extends Workload {
  private static final long serialVersionUID = 2369030249819246202L;

  private long begin;
  private long end;

  public NumberPrinterWorkload(long begin, long end) {
    super(String.format("%s-%s", begin, end));
    this.begin = begin;
    this.end = end;
  }

}
