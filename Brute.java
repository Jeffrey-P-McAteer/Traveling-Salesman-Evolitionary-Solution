import static java.lang.System.out;
import java.util.stream.*;
import java.util.*;
import java.io.*;


/**
 * 
 * @author Jeffrey McAteer
 */
public class Brute extends TSAlgo {
  public String getAlgoName() { return "Brute Force"; }
  
  public static void main(String... args) throws Exception {
    test(new Brute(), args);
  }
  
  public int[] solve() {
    int[] path = getDefaultPath();
    int[][] permutations = new int[fact(path.length)][];
    
    permutations[0] = new int[path.length];
    moveFrom(path, permutations[0]);
    
    boolean inverse = false;
    for (int i=1; i<permutations.length; i++) {
      permutations[i] = new int[path.length];
      moveFrom(permutations[i-1], permutations[i]);
      
      int toSwap = i % (path.length-1);
      if (toSwap == 0) {
        inverse = !inverse;
      }
      if (inverse) {
        int tmp = permutations[i][path.length-toSwap];
        permutations[i][path.length-toSwap] = permutations[i][path.length-toSwap+1];
        permutations[i][path.length-toSwap+1] = tmp;
        
      } else {
        int tmp = permutations[i][toSwap];
        permutations[i][toSwap] = permutations[i][toSwap+1];
        permutations[i][toSwap+1] = tmp;
      }
      
      //print(permutations[i]);
    }
    return getBestPath(permutations);
  }
   
}
