package grv.distributed.example.number.master;

import grv.distributed.workload.MasterWorkload;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class NumberPrinterMasterWorkload extends MasterWorkload {

  private long begin;
  private long end;

  public NumberPrinterMasterWorkload(long begin, long end) {
    super(String.format("%s-%s", begin, end));
    this.begin = begin;
    this.end = end;
  }

}
