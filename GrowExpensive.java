import static java.lang.System.out;
import java.util.stream.*;
import java.util.*;
import java.io.*;

/**
 * Picks 3 random coordinates and joins them.
 * For each remaining coordinate, join it with every selected edge
 * and choose to join at the egde that gives the smallest TSP size.
 * 
 * c2 gives this one trouble...
 * 
 * @author Jeffrey McAteer
 */
public class GrowExpensive extends TSAlgo {
  public String getAlgoName() { return "GrowExpensive"; }
  
  public static void main(String... args) throws Exception {
    test(new GrowExpensive(), args);
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
      Integer point = unselected.remove(0);
      int insert = 0; // index to insert point at in selected
      double insert_len_test = lengthTest(selected, insert, point);
      // for all edges in selected points
      for (int i=0; i<selected.size()+1; i++) {
        if (lengthTest(selected, i, point) < insert_len_test) {
          insert = i;
          insert_len_test = lengthTest(selected, insert, point);
        }
      }
      selected.add(insert, point);
    }
    
    int[] path = new int[locationCoords.length];
    for (int i=0; i<path.length; i++) {
      path[i] = selected.get(i);
    }
    return path;
  }
  
  double lengthTest(ArrayList<Integer> selected, int insert, Integer point) {
    selected.add(insert, point);
    int[] pth = new int[selected.size()];
    for (int i=0; i<pth.length; i++) {
      pth[i] = selected.get(i);
    }
    double len = pathLength(pth);
    selected.remove(point);
    return len;
  }
  
}
