import static java.lang.System.out;
import java.util.stream.*;
import java.util.*;
import java.io.*;

/**
 * Removes the recursion in MergeRecursive.
 * Is not an identical algorithm to MergeRecursive.
 * @author Jeffrey McAteer
 */
public class MergeIterative extends Merge {
  public String getAlgoName() { return "Merge Iterative"; }
  
  public static void main(String... args) throws Exception {
    test(new MergeIterative(), args);
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
  
}
