import java.util.stream.IntStream;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.stream.*;

/**
 * Finally implementing this algorithm, not my own but the result of research by Dantzig, Fulkerson and Johnson.
 * source urls:
 *    http://axon.cs.byu.edu/~martinez/classes/312/Projects/TSP/TSP.html
 *    http://www.intelligence.tuc.gr/~petrakis/courses/datastructures/algorithms.pdf
 *    https://people.eecs.berkeley.edu/~demmel/cs267/assignment4.html
 *    
 * Currently bug-a-liscious, in addition to using way more memory than necessary & doing poor copying of arrays,
 * there is also a reoccurring bug in fat_enelope.tsp when the algorithm decides to cut accross the center of the graph.
 */
public class BranchAndBound extends TSAlgo {
	public BranchAndBound(String s) {
    super(s);
  }
  
  double lower_bound = Double.POSITIVE_INFINITY;
  int[] best_path;
  
  public int[] solve() {
    best_path = new int[weights.length];
    for (int i=0; i<best_path.length; i++) {
      best_path[i] = i;
    }
    ArrayList<Integer> remaining_nums = new ArrayList<>();
    for (int i=0; i<best_path.length; i++) {
      remaining_nums.add(i);
    }
    recurse(remaining_nums, null);
    
    return best_path;
  }
  
  public void recurse(ArrayList<Integer> remaining_numbers, ArrayList<Integer> path) {
    if (path == null) path = new ArrayList<>();
    
    if (remaining_numbers.size() < 1) {
      handlePermutation(path);
    }
    else {
      for (int i=0; i<remaining_numbers.size(); i++) {
        // Clone remaining numbers
        ArrayList<Integer> nums = new ArrayList<>();
        for (Integer in : remaining_numbers) {
          if (in == remaining_numbers.get(i)) continue;
          nums.add((int) in);
        }
        // Clone path
        ArrayList<Integer> p = new ArrayList<>();
        for (Integer in : path) {
          p.add((int) in);
        }
        // Add number to path
        p.add((int) remaining_numbers.get(i));
        assert nums.size() < remaining_numbers.size() && p.size() > path.size();
        // Recurse!
        recurse(nums, p);
      }
    }
  }
  
  public void handlePermutation(ArrayList<Integer> path) {
    double this_sum = 0.0;
    for (int i = 1; i<path.size(); i++) {
      this_sum += weights[path.get(i-1)][path.get(i)];
      if (this_sum > lower_bound) {
        //System.err.println("skipping " + (path.size()-1) + " steps");
        return; // Don't waste processing power, we're done
      }
    }
    lower_bound = this_sum;
    //System.err.println("new lower_bound = "+lower_bound);
    for (int i=0; i<path.size(); i++) {
      best_path[i] = path.get(i);
    }
  }
  
}
