import static java.lang.System.out;
import java.util.stream.*;
import java.util.*;
import java.io.*;

/**
 * An adaptation to Merge* algorithms
 * 
 * A horrid failure, it only works well on grid-based cities and even then has faults.
 * 
 * @author Jeffrey McAteer
 */
public class BlindSpot extends TSAlgo {
  public String getAlgoName() { return "Blind Spot"; }
  
  public static void main(String... args) throws Exception {
    test(new BlindSpot(), args);
  }
  
  public int[] solve() {
    int[] path = getDefaultPath();
    int[][] depth = split(path);
    while (containsSplittablePath(depth)) {
      depth = nextDepth(depth);
    }
    while (depth.length > 1) {
      depth = mergeDepth(depth);
    }
    moveFrom(depth[0], path);
    return path;
  }
  
  /* Path traversal functions */
  
  public static boolean containsSplittablePath(int[][] paths) {
    for (int[] path : paths) {
      if (path.length > 2) return true;
    }
    return false;
  }
  
  // walk down the next depth of our tree
  public int[][] nextDepth(int[][] tree) {
    int[][] next = new int[tree.length * 5][];
    for (int i=0; i<tree.length; i++) {
      int[][] quads = split(tree[i]);
      int j = i*5;
      next[j] = quads[0];
      next[j+1] = quads[1];
      next[j+2] = quads[2];
      next[j+3] = quads[3];
      next[j+4] = quads[4]; // the middle quad
    }
    return next;
  }
  
  // walk up the next depth of our tree
  public int[][] mergeDepth(int[][] tree) {
    int[][] previous = new int[tree.length / 5][];
    for (int i=0; i<tree.length; i += 5) {
      previous[i/5] = merge(tree[i], tree[i+1], tree[i+2], tree[i+3], tree[i+4]);
    }
    return previous;
  }
  
  /* Merge & Split functions */
  
  // Dear fellow programmers: This is the best thing in the universe
  // Sincerely, a recently saved thread of sanity.
  static final int UPPER_RIGHT = 0;
  static final int UPPER_LEFT = 1;
  static final int LOWER_LEFT = 2;
  static final int LOWER_RIGHT = 3;
  
  // merge the paths in quadrants together into a single path
  public static int[] merge(int[]... paths) {
    /*
    out.println("Merging: ");
    for (int[] path : paths) {
      for (int i : path) out.printf("(%d, %d), ", locationCoords[i][0], locationCoords[i][1]);
      out.println();
    }/**/
    
    switch (paths.length) {
      case 2:
        if (paths[0].length < 1) return paths[1];
        else if (paths[1].length < 1) return paths[0];
        else if (paths[0].length == 1 && paths[1].length == 1) return concatenate(paths[0], paths[1]);
        else return getBestUnclosedPath(
          concatenate(reverse(paths[0]), paths[1]),
          concatenate(reverse(paths[0]), reverse(paths[1])),
          concatenate(paths[0], paths[1]),
          concatenate(paths[0], reverse(paths[1]))
        );
      
      case 4:
        // note to future optimizers: this is as optimized as possible. Seriously, don't try.
        return merge(merge(paths[0], paths[1]), merge(paths[2], paths[3]));
      
      case 5:
        int[][] quads = new int[4][];
        System.arraycopy(paths, 0, quads, 0, 4);
        int[] blindSpot = paths[4];
        return merge(blindSpot, merge(quads));
      
      default:
        assert false : "We can't merge paths of length "+paths.length;
        return null;
    }
  }
  
  // indexes 0-3 are quads, 4 is the center of the graph
  int[][] split(int[] path) {
    int[][] quads = new int[5][path.length];
    setIntsToNullish(quads);
    int[] quad_i = new int[]{0, 0, 0, 0, 0}; // indexes for quads & center (index 4)
    double[] center = center(path);
    double[][] quadCenters = quadCenters(center, path);
    for (int i : path) {
      // highest quadrant
      int q = quadrant(center, locationCoords[i]);
      // quadrant inside quadrant
      int qq = quadrant(quadCenters[q], locationCoords[i]);
      if ((q == UPPER_RIGHT && qq == LOWER_LEFT) ||
          (q == UPPER_LEFT && qq == LOWER_RIGHT) ||
          (q == LOWER_LEFT && qq == UPPER_RIGHT) ||
          (q == LOWER_RIGHT && qq == UPPER_LEFT) ) {
        // we have a point to put in the blind spot
        q = 4;
        
      }
      
      quads[q][quad_i[q]] = i;
      quad_i[q]++;
      
    }
    // remove nulls from quads
    for (int i=0; i<quads.length; i++) quads[i] = removeNulls(quads[i]);
    return quads;
  }
  
  /* Mostly point functions */
  
  double[] center(int[] path) {
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
  
  // index 0 is upper right, index 1 is upper left, 2 lower left, 3 lower right. 
  double[][] quadCenters(double[] center, int[] path) {
    if (path.length < 1) return new double[][] {
      center, center, center, center
    };
    
    boolean use_weighted_center = false;
    double weight = 0.25; // % away from borders
    assert weight <= 0.50;
    
    if (use_weighted_center) {
      int[][] quads = new int[4][path.length];
      setIntsToNullish(quads);
      int[] quad_i = new int[]{0, 0, 0, 0}; // indexes for quads
      for (int i : path) {
        int q = quadrant(center, locationCoords[i]);
        quads[q][quad_i[q]] = i;
        quad_i[q]++;
      }
      // remove nulls from quads
      for (int i=0; i<quads.length; i++) quads[i] = removeNulls(quads[i]);
      return new double[][] {
        center(quads[0]),
        center(quads[1]),
        center(quads[2]),
        center(quads[3])
      };
      
    } else {
      int left_most = path[0];
      for (int i : path) {
        if (locationCoords[i][0] < locationCoords[left_most][0]) {
          left_most = i;
        }
      }
      
      int right_most = path[0];
      for (int i : path) {
        if (locationCoords[i][0] > locationCoords[right_most][0]) {
          right_most = i;
        }
      }
      
      int top_most = path[0];
      for (int i : path) {
        if (locationCoords[i][1] > locationCoords[top_most][1]) {
          top_most = i;
        }
      }
      
      int bot_most = path[0];
      for (int i : path) {
        if (locationCoords[i][1] < locationCoords[bot_most][1]) {
          bot_most = i;
        }
      }
      
      int left  = locationCoords[left_most][0];
      int top   = locationCoords[top_most][1];
      int right = locationCoords[right_most][0];
      int bot   = locationCoords[bot_most][1];
      
      double left_x = ((right - left) * weight) + left;
      double right_x = ((right - left) * (1.0 - weight)) + left;
      double top_y = ((top - bot) * (1.0 - weight)) + bot;
      double bot_y = ((top - bot) * weight) + bot;
      
      return new double[][] { // index 0 is upper right, index 1 is upper left, 2 lower left, 3 lower right. 
        {right_x, top_y},
        {left_x, top_y},
        {left_x, bot_y},
        {right_x, bot_y}
      };
      
    }
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
  
}
