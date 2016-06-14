import static java.lang.System.out;
import java.util.stream.*;
import java.util.*;
import java.io.*;

/**
 * 
 * @author Jeffrey McAteer
 */
public class CutLongestClosest extends TSAlgo {
  public String getAlgoName() { return "CutLongestClosest"; }
  
  public static void main(String... args) throws Exception {
    test(new CutLongestClosest(), args);
  }
  
  public int[] solve() {
    int[] path = getDefaultPath();
    double 2nd_last_dist = -1.0;
    double last_dist = pathLength(path);
    
    while (2nd_last_dist != last_dist) {
      double largest_weight = -1.0;
      int[] largest_edges = new int[4];
      
      for (int i=0; i<path.length; i++) {
        int[] edge_a = new int[] { i, (i+1)%path.length };
        for (int j=0; j<path.length; j++) {
          int[] edge_b new int[] { j, (j+1)%path.length };
          double this_weight = weight(edge_a[0], edge_a[1], edge_b[0], edge_b[1]);
          if (this_weight > largest_weight) {
            largest_weight = this_weight;
            largest_edges = new int[] {
              edge_a[0], edge_a[1], edge_b[0], edge_b[1]
            };
          }
        }
      }
      // now cut & paste largest_edges
      
      
      2nd_last_dist = last_dist;
      last_dist = pathLength(path);
    }
    
    return path;
  }
  
  // weight between two edges, described by their endpoints
  double weight(int a, int b, int c, int d) {
    double[] point = new double[] {
      (locationCoords[c][0] + locationCoords[d][0]) / 2.0,
      (locationCoords[c][1] + locationCoords[d][1]) / 2.0,
    };
    double[] midpoint = new double[] {
      (locationCoords[a][0] + locationCoords[b][0]) / 2.0,
      (locationCoords[a][1] + locationCoords[b][1]) / 2.0,
    };
    double distance /* from p */ = Math.sqrt(
      Math.pow(midpoint[0] - point[0], 2) + 
      Math.pow(midpoint[1] - point[1], 2)
    );
    
    double point_edge_length = Math.sqrt(
      Math.pow(locationCoords[c][0] - locationCoords[d][0], 2) + 
      Math.pow(locationCoords[c][1] - locationCoords[d][1], 2)
    );
    
    double edge_length = Math.sqrt(
      Math.pow(locationCoords[a][0] - locationCoords[b][0], 2) + 
      Math.pow(locationCoords[a][1] - locationCoords[b][1], 2)
    );
    
    // eh, idk. Guessing.
    edge_length *= distance/100.0;
    point_edge_length *= distance/100.0;
    
    return distance + edge_length + point_edge_length;
  }
  
}
