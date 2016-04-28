import static java.lang.System.out;
import java.util.stream.*;
import java.util.*;
import java.io.*;

/**
 * The original Split-And-Merge algorithm I wrote.
 * Runs into stack overflow errors with inputs larger than ~250 cities
 * @author Jeffrey McAteer
 */
public class MergeRecursive extends Merge {
  public String getAlgoName() { return "Merge Recursive"; }
  
  public static void main(String... args) throws Exception {
    test(new MergeRecursive(), args);
  }
  
  public int[] solve() {
    int[] path = getDefaultPath();
    solve(path, 0);
    return path;
  }
  
  public static void solve(int[] path, int depth) {
    depth++;
    // paths of size 2 or smaller are already 'solved'
    // a depth > ~5000 means we're about to blow the 512kb stack.
    if (path.length < 3 || depth > 5000) return;
    
    // split the path into 4 paths on each quadrant
    int[][] quads = split(path);
    for (int[] quadPath : quads) {
      // solve that quadrant's path
      solve(quadPath, depth);
    }
    // After everything has been split up, we merge them back together
    // this only works because we have a refrence to path, which we operate on.
    moveFrom(merge(quads), path);
  }
  
}
