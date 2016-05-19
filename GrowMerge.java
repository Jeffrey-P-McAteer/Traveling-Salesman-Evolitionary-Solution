import static java.lang.System.out;
import java.util.stream.*;
import java.util.*;
import java.io.*;

/**
 * Splits points into non-overlaping triangles.
 * Note that these triangles are all optimum TSPs for their verticies.
 * 
 * For each pair of triangles, the closest & longest edge is selected.
 * These edges are removed and the two graphs are joined
 * in the shortest path.
 * 
 * This continues with the merged triangles until only one merged
 * graph remains. This graph is the optimum path.
 * 
 * @author Jeffrey McAteer
 */
public class GrowMerge extends TSAlgo {
  public String getAlgoName() { return "GrowMerge"; }
  
  public static void main(String... args) throws Exception {
    test(new GrowMerge(), args);
  }
  
  public static class OptimumTSP {
    ArrayList<Integer> coords;
    
    public OptimumTSP(ArrayList<Integer> coords) {
      this.coords = coords;
    }
    
    public OptimumTSP mergeWith(OptimumTSP o) {
      return null;
    }
  }
  
  public int[] solve() {
    ArrayList<Integer> unselected = new ArrayList<>();
    ArrayList<Integer> selected = new ArrayList<>();
    
    // add all points to unselected
    for (int i=0; i<locationCoords.length; i++) {
      unselected.add(new Integer(i));
    }
    // unnecessary to shuffle, but good for testing
    Collections.shuffle(unselected, new Random());
    
    ArrayList<OptimumTSP> graphs = new ArrayList<>();
    
    // move and 3 numbers from unselected to selected
    for (int i=0; i<locationCoords.length; i++) {
      
    }
    
    // for the remaining unselected numbers
    while (graphs.size() > 1) {
      
      
    }
    System.err.println();
    
    int[] path = new int[locationCoords.length];
    for (int i=0; i<path.length; i++) {
      path[i] = selected.get(i);
    }
    return path;
  }
  
  /**
   * Describes the weight of an edge between a and b, and point p.
   */
  double weight(Integer a, Integer b, Integer p) {
    double[] point = locationCoords[p];
    double[] midpoint = new double[] {
      (locationCoords[a][0] + locationCoords[b][0]) / 2.0,
      (locationCoords[a][1] + locationCoords[b][1]) / 2.0,
    };
    double distance /* from p */ = Math.sqrt(
      Math.pow(midpoint[0] - point[0], 2) + 
      Math.pow(midpoint[1] - point[1], 2)
    );
    
    double edge_length = Math.sqrt(
      Math.pow(locationCoords[a][0] - locationCoords[b][0], 2) + 
      Math.pow(locationCoords[a][1] - locationCoords[b][1], 2)
    );
    
    edge_length *= distance/100.0;
    
    return distance + edge_length;
  }
  
  // optimized for large-scale test
  // double weight(Integer a, Integer b, Integer p) {
  //   double[] point = locationCoords[p];
  //   double[] midpoint = new double[] {
  //     (locationCoords[a][0] + locationCoords[b][0]) / 2.0,
  //     (locationCoords[a][1] + locationCoords[b][1]) / 2.0,
  //   };
  //   double distance = Math.pow(midpoint[0] - point[0], 2) + Math.pow(midpoint[1] - point[1], 2);
    
  //   return distance;
  // }
  
}
