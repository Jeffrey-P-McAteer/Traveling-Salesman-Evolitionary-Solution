import java.util.Random;

public class CountTheEdges extends BestRandomInsertion {
  
  public CountTheEdges(String s) {
    super(s);
    edge_counts = new int[weights.length][weights.length];
    assert edge_counts[0][0] == 0;
  }
  
  public int[][] edge_counts;
  /**
    A B C
  A 0 2 0 // 2 means that a -> b is a good edge
  B 2 0 1
  C 0 1 0
  */
  
  public int[] solve() {
    final int STEPS = 100;
    for (int i=0; i<STEPS; i++) {
      rand = new Random();
      int[] one_solution = super.solve();
      for (int j=1; j<one_solution.length; j++) {
        edge_counts[one_solution[j-1]][one_solution[j]]++;
        edge_counts[one_solution[j]][one_solution[j-1]]++;
      }
    }
    for (int[] row : edge_counts) {
      for (int num : row) {
        System.out.print(num);
        System.out.print(", ");
      }
      System.out.println();
    }
    
    int[] best_path = new int[weights.length];
    
    return best_path;
  }
  
}