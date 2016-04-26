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
  
  // merge the paths in quadrants together into a single path
  public static int[] merge(int[][] paths) {
    if (paths.length == 1) {
      return paths[0];
      
    } else if (paths.length == 2) {
      // conn is a number telling us which of the 4 possible orientations
      // is the closest to connect two graphs.
      int conn = closestPointsConnectingNum(paths[0], paths[1]);
      switch (conn) {
        case 0:
          // stick the beginning of the first path to the beginning of the second path
          return concatenate(reverse(paths[0]), paths[1]);
        case 1:
          // stick the beginning of the first path to the end of the second path
          return concatenate(reverse(paths[0]), reverse(paths[1]));
        case 2:
          // stick the end of the first path to the beginning of the second path
          return concatenate(paths[0], paths[1]);
        case 3:
          // stick the end of the first path to the end of the second path
          return concatenate(paths[0], reverse(paths[1]));
      }
      assert false:"something borked";
      return null;
    } else {
      // if more than 2, merge the first and the merged result of the remaining ones.
      return merge(new int[][]{
        paths[0],
        merge(Arrays.copyOfRange(paths, 1, paths.length))
      });
    }
  }
  
  // split the path into 4 quadrants of paths
  public static int[][] split(int[] path) {
    if (path.length < 1) return null;
    int[][] quads = new int[4][path.length]; // originally had path.length/4, but we remove nulls anyway
    setIntsToNullish(quads);
    int[] quad_i = new int[]{0, 0, 0, 0}; // indexes for quads
    int[] center = center(path);
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
  
  // returns 0-3, depending upon the comparison of 4 distance operations
  public static int closestPointsConnectingNum(int[] pathA, int[] pathB) {
    if (pathA.length < 1 || pathB.length < 1) {
      return 0;
    }
    double[] dists = new double[4];
    dists[0] = fastDistance(locationCoords[pathA[0]], locationCoords[pathB[0]]);
    dists[1] = fastDistance(locationCoords[pathA[0]], locationCoords[pathB[pathB.length-1]]);
    dists[2] = fastDistance(locationCoords[pathA[pathA.length-1]], locationCoords[pathB[0]]);
    dists[3] = fastDistance(locationCoords[pathA[pathA.length-1]], locationCoords[pathB[pathB.length-1]]);
    int closestDistIndex = 0;
    for (int i=0; i<dists.length; i++) {
      if (dists[i] > dists[closestDistIndex]) {
        closestDistIndex = i;
      }
    }
    return closestDistIndex;
  }
  
  // quad of 0, 1, 2, or 3. 0 is in upper right, 1 is in upper left, 2 lower left, 3 lower right
  // NB: 0,0 is in quad 1. Basically, values of 0 round upwards towards 1
  // quadrants are relative to the origin coordinates
  public static int quadrant(int[] origin, int[] coord) {
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
  public static int[] center(int[] path) {
    assert path.length > 0;
    double weight = 1.0/path.length;
    double[] center = new double[] {0.0, 0.0};
    for (int i : path) {
      center[0] += weight * locationCoords[i][0];
      center[1] += weight * locationCoords[i][1];
    }
    return new int[] {(int) center[0], (int) center[1]};
  }
  
}
  
