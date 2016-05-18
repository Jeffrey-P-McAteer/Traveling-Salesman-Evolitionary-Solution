import static java.lang.System.out;
import java.util.stream.*;
import java.util.*;
import java.io.*;

/**
 * Picks 3 random coordinates and joins them.
 * For each remaining coordinate, calculate a weight between
 * the new coordinate and each edge. Pick the smalledt weighted edge
 * and insert the point between the nodes at the end of the edge.
 * 
 * The weight function is a combination of the distance between the midpoint
 * of the edge and the length of the edge.
 * 
 * @author Jeffrey McAteer
 */
public class Grow extends TSAlgo {
  public String getAlgoName() { return "Grow"; }
  
  public static void main(String... args) throws Exception {
    test(new Grow(), args);
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
    
    // move and 3 numbers from unselected to selected
    for (int i=0; i < 3; i++) {
      selected.add( unselected.remove(0) );
    }
    
    // for the remaining unselected numbers
    while (unselected.size() > 0) {
      System.err.printf("%d remaining            \r", unselected.size());
      Integer point = unselected.remove(0);
      int insert = 1; // index to insert point at in selected
      // for all edges in selected points, except the last edge
      for (int i=1; i<selected.size(); i++) {
        // compare edge between i-1 and i
        // and edge between insert-1 and insert
        if (weight(selected.get(i-1), selected.get(i), point)
            <
            weight(selected.get(insert-1), selected.get(insert), point))
        {
          insert = i;
        }
      }
      // insert is now the lightest edge ("closest")
      // except for the last edge, which we test below
      if (weight(selected.get(0), selected.get(selected.size()-1), point)
          <
          weight(selected.get(insert-1), selected.get(insert), point))
      {
        // insert at the end of the array
        selected.add(point);
        
      } else {
        // insert in the middle of the array
        selected.add(insert, point);
      }
    }
    
    int[] path = new int[locationCoords.length];
    for (int i=0; i<path.length; i++) {
      path[i] = selected.get(i);
    }
    return path;
  }
  
  public static double edge_length_coefficient = 0.0;
  /**
   * Describes the weight of an edge between a and b, and point p.
   */
  double weight(Integer a, Integer b, Integer p) {
    int[] point = locationCoords[p];
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
    
    //edge_length *= edge_length_coefficient;
    //edge_length *= Math.sin(edge_length)/1000;
    //edge_length *= Math.sin(edge_length*edge_length_coefficient);
    
    edge_length *= distance/100.0;
    
    return distance + edge_length;
  }
  
}
