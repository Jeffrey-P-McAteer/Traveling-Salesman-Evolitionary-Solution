import static java.lang.System.out;
import java.util.stream.*;
import java.util.*;
import java.io.*;

/**
 * The continuation of development of MergeIterative.
 * 
 * @author Jeffrey McAteer
 */
public class MergeComplete extends Merge {
  public String getAlgoName() { return "Merge Complete"; }
  
  public static void main(String... args) throws Exception {
    test(new MergeComplete(), args);
  }
  
  public int[] solve() {
    int[] path = getDefaultPath();
    ArrayList<Integer[]> toSolve = new ArrayList<Integer[]>();
    int[][] quads = split(path);
    // only break when quads is null and we are beginning a new depth
    //while (!(quads == null && nodeIndexForThisDepth == 0)) {
    while (containsSplittablePaths(toSolve)) {
      //out.print("toSolve: ");
      //print(toSolve);
      if (quads != null) {
        for (int i=0; i<quads.length; i++) {
          toSolve.add(intToInteger(quads[i]));
        }
      }
      quads = split(integerToInt(toSolve.get(0)));
      if (quads != null) {
        toSolve.remove(0);
        for (int i=0; i<quads.length; i++) {
          toSolve.add(intToInteger(quads[i]));
        }
      }
      removeEmptyAndDuplicates(toSolve);
      //out.print("toSolve: ");
      //print(toSolve);
    }
    
    //out.print("(middle) toSolve: ");
    //print(toSolve);
    
    while (toSolve.size() > 1) {
      //out.print("toSolve: ");
      //print(toSolve);
      
      quads = new int[Math.min(4, toSolve.size())][];
      for (int i=0; i<quads.length; i++) {
        quads[i] = integerToInt(toSolve.remove(0));
      }
      
      int[] single = merge(quads);
      toSolve.add(0, intToInteger(single));
      //out.print("toSolve: ");
      //print(toSolve);
    }
    
    moveFrom(integerToInt(toSolve.get(0)), path);
    
    return path;
  }
  
  public static void removeEmptyAndDuplicates(ArrayList<Integer[]> data) {
    ArrayList<Integer[]> toRemove = new ArrayList<Integer[]>();
    for (Integer[] i : data) {
      if (i.length < 1) toRemove.add(i);
    }
    for (Integer[] i : toRemove) {
      data.remove(i);
    }
    
    for (int i=0; i<data.size(); i++) {
      for (int j=0; j<data.size(); j++) {
        if (i != j && integerArrayEquality(data.get(i), data.get(j))) {
          data.remove(i);
        }
      }
    }
    
  }
  
  public static boolean integerArrayEquality(Integer[] a, Integer[] b) {
    if (a.length != b.length) return false;
    for (int i=0; i<a.length; i++) {
      if (a[i] != b[i]) return false;
    }
    return true;
  }
  
  public static boolean containsSplittablePaths(ArrayList<Integer[]> toSolve) {
    if (toSolve.size() == 0) return true;
    for (Integer[] ints : toSolve) {
      if (ints.length > 2) return true;
    }
    return false;
  }
  
}
