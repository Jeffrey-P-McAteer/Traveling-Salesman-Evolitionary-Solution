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
    
    List<List<Integer>> allPermutations = new ArrayList<List<Integer>>();
    permutation(allPermutations, Arrays.<Integer>asList(), Arrays.asList(intToInteger(path)));
    
    return integerToInt( getBestPath(allPermutations).toArray(new Integer[0]) );
  }
  
  // stolen from janos at stackoverflow.com/a/25704865
  // many thanks, permutations are always difficult for me
  // and I'm only interested in a brute force algorithm for comparison purposes
  private static void permutation(List<List<Integer>> accum, List<Integer> prefix, List<Integer> nums) {
      int n = nums.size();
      if (n == 0) {
          accum.add(prefix);
      } else {
          for (int i = 0; i < n; ++i) {
              List<Integer> newPrefix = new ArrayList<Integer>();
              newPrefix.addAll(prefix);
              newPrefix.add(nums.get(i));
              List<Integer> numsLeft = new ArrayList<Integer>();
              numsLeft.addAll(nums);
              numsLeft.remove(i);
              permutation(accum, newPrefix, numsLeft);
          }
      }
  }

    
}
