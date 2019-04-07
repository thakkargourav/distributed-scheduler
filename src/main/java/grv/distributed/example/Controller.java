

package grv.distributed.example;

import grv.distributed.cluster.ClusterManager;
import grv.distributed.instruction.ReportInstruction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class Controller {

  @Autowired
  private final ClusterManager clusterManager;

  public Controller(ClusterManager clusterManager) {
    this.clusterManager = clusterManager;
  }


  @GetMapping()
  public Map status() {
    try {
      return clusterManager.submitInstruction(new ReportInstruction());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
