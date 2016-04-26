import static java.lang.System.out;
import java.util.stream.*;
import java.util.*;
import java.io.*;

/**
 * @author Jeffrey McAteer
 */
public class MergeRecursive extends Merge {
  public String getAlgoName() { return "Merge Recursive"; }
  
  public static void main(String... args) throws Exception {
    test(new MergeRecursive(), args);
  }
  
  public int[] solve() {
    int[] path = getDefaultPath();
    
    int ignoreLevel = 3;
    while (true) {
      try {
        solve(path, ignoreLevel);
        if (ignoreLevel > 3) {
          System.out.println("Warning: had to set an ignoreLevel of "+ignoreLevel);
        }
        break;
      } catch (StackOverflowError e) {
        ignoreLevel++;
      }
    }
    return path;
  }
  
  public static void solve(int[] path, int ignoreLevel) {
    if (path.length < ignoreLevel) return; // can we avoid a stack overflow for a partial solution?
    if (path.length < 3) return; // paths of size 2 or smaller are already 'solved'
    // split the path into 4 paths on each quadrant
    int[][] quads = split(path);
    for (int[] quadPath : quads) {
      // solve that quadrant's path
      solve(quadPath, ignoreLevel);
    }
    // this only works because we have a _refrence_ which we operate on.
    // After everything has been split up, we merge them back together
    moveFrom(merge(quads), path);
  }
  
}
