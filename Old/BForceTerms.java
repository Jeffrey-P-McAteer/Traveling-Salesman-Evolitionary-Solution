import static java.lang.System.out;
import java.util.stream.*;
import java.util.*;
import java.io.*;

public class BForceTerms extends TSAlgo {
  public String getAlgoName() { return "BForceTerms"; }
  
  public static void main(String... args) throws Exception {
    test(new BForceTerms(), args);
  }
  
  public int[] solve() {
    Grow g = new Grow();
    RocketBoostedEvolution e = new RocketBoostedEvolution();
    double best_path_len = pathLength(e.solve());
    
    int one = 0;
    int zero = 0;
    
    for (int i=0; i<1000; i++) {
      if (pathLength(g.solve()) == best_path_len) {
        //out.printf("Correct %f\n", Grow.edge_length_coefficient);
        out.print(1);
        one++;
      } else {
        out.print(0);
        zero++;
        //out.printf("Incorrect %f\n", Grow.edge_length_coefficient);
      }
      Grow.edge_length_coefficient += 0.001;
    }
    out.println();
    
    out.printf("Ones: %d\nZeroes: %d\n", one, zero);
    
    return null;
  }
}
