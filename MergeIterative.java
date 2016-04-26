import static java.lang.System.out;
import java.util.stream.*;
import java.util.*;
import java.io.*;

/**
 * @author Jeffrey McAteer
 */
public class MergeIterative extends Merge {
  public String getAlgoName() { return "Merge Iterative"; }
  
  public static void main(String... args) throws Exception {
    test(new MergeIterative(), args);
  }
  
  public int[] solve() {
    int[] path = getDefaultPath();
    
    ArrayList<Integer[]> toSolve = new ArrayList<Integer[]>();
    int[][] quads = split(path);
    int nodeIndexForThisDepth = 0;
    int max = 4;
    // only break when quads is null and we are beginning a new depth
    while (quads != null || nodeIndexForThisDepth != 0) {
      if (quads != null) {
        toSolve.add(intToInteger(quads[0]));
        toSolve.add(intToInteger(quads[1]));
        toSolve.add(intToInteger(quads[2]));
        toSolve.add(intToInteger(quads[3]));
      }
      nodeIndexForThisDepth += 4;
      if (nodeIndexForThisDepth >= max) {
        max *= 4;
        nodeIndexForThisDepth = 0;
      }
      quads = split(integerToInt(toSolve.get(0)));
      if (quads != null) toSolve.remove(0);
    }
    while (toSolve.size() > 1) {
      quads = new int[][] {
        integerToInt(toSolve.remove(toSolve.size()-1)),
        integerToInt(toSolve.remove(toSolve.size()-1)),
        integerToInt(toSolve.remove(toSolve.size()-1)),
        integerToInt(toSolve.remove(toSolve.size()-1))
      };
      int[] single = merge(quads);
      toSolve.add(0, intToInteger(single));
    }
    
    moveFrom(integerToInt(toSolve.get(0)), path);
    
    
    return path;
  }
  
  public static int[] integerToInt(Integer[] items) {
    int[] its = new int[items.length];
    for (int i=0; i<items.length; i++) {
      its[i] = (int) items[i];
    }
    return its;
  }
  
  public static Integer[] intToInteger(int[] items) {
    Integer[] its = new Integer[items.length];
    for (int i=0; i<items.length; i++) {
      its[i] = (Integer) items[i];
    }
    return its;
  }
  
}
