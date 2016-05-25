import java.util.stream.*;
import java.util.Arrays;

public class GeneralTSP extends TSAlgo {
  public static final boolean PROGRESS = true;
  
  public GeneralTSP(String s) {
    super(s);
  }
  
  public int[] solve() {
    if (weights.length < 3) {
      int[] path = new int[weights.length];
      IntStream.range(0, path.length).forEach(i -> path[i] = i);
      return path;
    }
    int[] path = new int[3];
    for (int i=0; i<3; i++) path[i] = i;
    
    while (path.length < weights.length) {
      if (PROGRESS) {
        System.err.printf("%,d/%,d                 \r", path.length, weights.length);
      }
      // System.out.printf("Path: ");
      // for (int i : path) System.out.printf("%d, ", i);
      // System.out.println();
      
      int new_point = path.length;
      
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
      // insert point (path.length) between points at (best) and (best+1)
      path = concatinate(
        Arrays.copyOfRange(path, 0, best+1), // includes path[best]
        new int[] { new_point },
        Arrays.copyOfRange(path, best+1, path.length)); // begins at path[best+1]
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
