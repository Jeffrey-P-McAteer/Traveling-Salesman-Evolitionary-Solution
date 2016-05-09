import static java.lang.System.out;
import java.util.stream.*;
import java.util.*;
import java.io.*;

/**
 * Superclass with methods for Merge and Split algorithms
 * @author Jeffrey McAteer
 */
public class Merge extends TSAlgo {
  public String getAlgoName() { return "Merge"; }
  
  public static void main(String... args) throws Exception {
    test(new Merge(), args);
  }
  
  // we do not implement travelling salesman
  public int[] solve() { return null; }
  
  // walk down the next depth of our tree
  public int[][] nextDepth(int[][] tree) {
    int[][] next = new int[tree.length * 4][];
    for (int i=0; i<tree.length; i++) {
      int[][] quads = split(tree[i]);
      int j = i*4;
      next[j] = quads[0];
      next[j+1] = quads[1];
      next[j+2] = quads[2];
      next[j+3] = quads[3];
    }
    return next;
  }
  
  // walk up the next depth of our tree
  public int[][] mergeDepth(int[][] tree) {
    /*out.println("=== Tree ===");
    for (int[] is : tree) {
      for (int i : is) {
        out.print("("+locationCoords[i][0]+", "+locationCoords[i][1]+"), ");
      }
      out.println();
    }*/
    
    int[][] previous = new int[tree.length / 4][];
    for (int i=0; i<tree.length; i += 4) {
      int[] merged = merge(tree[i], tree[i+1], tree[i+2], tree[i+3]);
      int j = i/4;
      previous[j] = merged;
    }
    return previous;
  }
  
  // check if tree of paths contains any path with more than 2 cities in it
  public static boolean containsSplittablePath(int[][] paths) {
    for (int[] path : paths) {
      if (path.length > 2) return true;
    }
    return false;
  }
  
  // merge the paths in quadrants together into a single path
  public static int[] merge(int[]... paths) {
    if (paths.length == 1) {
      return paths[0];
      
    } else if (paths.length == 2) {
      
      return getBestUnclosedPath(
        concatenate(reverse(paths[0]), paths[1]),
        concatenate(reverse(paths[0]), reverse(paths[1])),
        concatenate(paths[0], paths[1]),
        concatenate(paths[0], reverse(paths[1]))
      );
      
    } else if (paths.length == 3) {
      return getBestUnclosedPath( // this 'best' may be redundant
        merge(paths[0], merge(paths[1], paths[2])),
        merge(paths[2], merge(paths[0], paths[1])),
        merge(paths[1], merge(paths[2], paths[0]))
      );
      
    } else if (paths.length == 4) {
      return getBestUnclosedPath(
        merge(merge(paths[0], paths[1]), merge(paths[2], paths[3])),
        merge(merge(paths[3], paths[0]), merge(paths[1], paths[2])),
        merge(merge(paths[2], paths[3]), merge(paths[0], paths[1])),
        merge(merge(paths[1], paths[2]), merge(paths[3], paths[0]))
      );
      
    } else {
      assert false: "I didn't know this could happen.";
      return null;
    }
  }
  
  // split the path into 4 quadrants of paths
  public static int[][] split(int[] path) {
    int[][] quads = new int[4][path.length]; // originally had path.length/4, but we remove nulls anyway
    setIntsToNullish(quads);
    int[] quad_i = new int[]{0, 0, 0, 0}; // indexes for quads
    double[] center = center(path);
    for (int i : path) {
      int quad = quadrant(center, locationCoords[i]);
      quads[quad][quad_i[quad]] = i;
      quad_i[quad]++;
    }
    // quadrants may have nulls on the end
    for (int i=0; i<quads.length; i++) {
      quads[i] = removeNulls(quads[i]);
    }
    
    return quads;
  }
  
  // quad of 0, 1, 2, or 3. 0 is in upper right, 1 is in upper left, 2 lower left, 3 lower right
  // NB: 0,0 is in quad 1. Basically, values of 0 round upwards towards 1
  // quadrants are relative to the origin coordinates
  public static int quadrant(double[] origin, int[] coord) {
    if (coord[0] >= origin[0] && coord[1] >= origin[1]) {
      // x is 'positive' and y is 'positive'
      return 0;
    } else if (coord[0] >= origin[0] && coord[1] < origin[1]) {
      // x is 'positive' and y is 'negative'
      return 3;
    } else if (coord[0] < origin[0] && coord[1] >= origin[1]) {
      // x is 'negative' and y is 'positive'
      return 1;
    } else { // neg and neg
      return 2;
    }
  }
  
  // get coordinates of the center of this path (or sub-path)
  public static double[] center(int[] path) {
    double[] center = new double[] {0.0, 0.0};
    for (int i : path) {
      center[0] += locationCoords[i][0];
      center[1] += locationCoords[i][1];
    }
    if (path.length > 1) {
      center[0] /= path.length;
      center[1] /= path.length;
    }
    return center;
  }
  
}
  
