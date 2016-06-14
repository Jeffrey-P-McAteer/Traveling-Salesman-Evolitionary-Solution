import java.util.stream.IntStream;
import java.util.Arrays;
import java.util.Random;

/**
 * This began as a general solution to TSP
 */
public class PointByPoint extends TSAlgo {
  // PROGRESS flag is in TSAlgo
  public static final boolean SHOW_STEP_GRAPHS = false;
  
  public PointByPoint(String s) {
    super(s);
  }
  
  public int[] solve() {
    // handle all the easy cases
    if (weights == null) {
      return null;
    }
    if (weights.length < 3) {
      int[] path = new int[weights.length];
      IntStream.range(0, path.length).forEach(i -> path[i] = i);
      return path;
    }
    
    int[] path = new int[3];
    // We must choose the 3 points furthest away.
    // First, find the furthest 2 points in the patrix.
    for (int row=0; row<weights.length; row++) {
      for (int col=0; col<weights[0].length; col++) {
        if (row == col) continue;
        double best = weights[path[0]][path[1]];
        double current = weights[row][col];
        if (current > best) {
          path[0] = row;
          path[1] = col;
        }
      }
    }
    // point[0] and point[1] are the furthest points
    for (int row=0; row<weights.length; row++) {
      if (row == path[0] || row == path[1]) continue;
      double best = weights[path[0]][path[2]] + weights[path[1]][path[2]];
      double current = weights[path[0]][row] + weights[path[1]][row];
      if (current > best) {
        path[2] = row;
      }
    }
    // point[0], [1], and [2] now make the largest
    // triangle possible out of all points
    
    Random rand = new Random();
    
    // while we have not added every point
    while (path.length < weights.length) {
      if (PROGRESS) {
        System.err.print(CLEAR);
        System.err.printf("Solving %,d/%,d\r", path.length, weights.length);
      }
      
      int new_point;
      a: while (true) {
        new_point = rand.nextInt(weights.length);
        for (int i=0;i<path.length; i++) {
          if (path[i] == new_point) {
            continue a;
          }
        }
        break;
      }
      
      double[] deltas = new double[path.length];
      for (int i=0; i<deltas.length; i++) {
        // readability, a and b are the points between which we would add new_point
        int a = path[i];
        int b = path[(i+1)%path.length];
        // Just for funsies
        deltas[i] = 0.0;
        // subtract off edge we would remove
        deltas[i] -= weights[a][b];
        // add the two edges we would add
        deltas[i] += weights[new_point][a];
        deltas[i] += weights[new_point][b];
      }
      
      int best = 0;
      for (int i=0; i<deltas.length; i++) {
        if (deltas[i] < deltas[best]) {
          best = i;
        }
      }
      
      int[] old_path = path;
      // insert point (path.length) between points at (best) and (best+1)
      path = concatinate(
        Arrays.copyOfRange(path, 0, best+1), // includes path[best]
        new int[] { new_point },
        Arrays.copyOfRange(path, best+1, path.length)); // begins at path[best+1]
      
      if (weights.length < 20) {
        System.out.print("Path: ");
        for (int p : path) {
          System.out.printf("(%s %s) ", coordinates[p][0], coordinates[p][1]);
        }
        System.out.println();
      }
      
      if (SHOW_STEP_GRAPHS && weights.length < 20) {
        displayAsync(path, ""+path.length);
      }
    }
    if (PROGRESS) System.err.println();
    
    return path;
  }
  
  public static int[] concatinate(int[]... ints) {
    int totalLen = 0;
    for (int[] arr : ints) totalLen += arr.length;
    int[] combined = new int[totalLen];
    int i=0;
    for (int[] arr : ints) {
      for (int num : arr) {
        combined[i] = num;
        i++;
      }
    }
    return combined;
  }
  
}
