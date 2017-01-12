import java.util.stream.IntStream;
import java.util.Arrays;

public class InsertionPlayground extends BestWorstInsertion {
  
  public InsertionPlayground(String s) {
    super(s);
  }
  
  // vertices is N elements long with len-2 sub arrays for x and y locations
  /*public InsertionPlayground(double[][] coordinates) {
    int vertices = coordinates.length;
    weights = new double[vertices][vertices];
    
    IntStream.range(0, vertices).parallel().forEach(
      i -> {
        if (PROGRESS) {
          System.err.print(CLEAR);
          System.err.printf("Computing edge lengths %,d/%,d\r", i, vertices);
        }
        for (int j=0; j < vertices; j++) {
          if (i == j) {
            weights[i][j] = Double.POSITIVE_INFINITY;
            continue;
          }
          // sqrt((x1-x2)^2 + (y1-y2)^2)
          weights[i][j] = Math.sqrt(
            Math.pow(coordinates[i][0] - coordinates[j][0], 2) + 
            Math.pow(coordinates[i][1] - coordinates[j][1], 2)
          );
        }
      }
    );
  }/**/
  
  public static void main(String... args) {
    
  }
}