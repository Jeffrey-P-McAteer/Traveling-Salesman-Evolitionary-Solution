import java.io.*;
import java.util.*;

/**
 * A superclass for different traveling salesman algorithms
 * Contains useful functions which should be standard for every algorithm
 * @author Jeffrey McAteer
 */
public class TSAlgo {
  
  public static double[][] weights;
  
  public TSAlgo(String tspFile) {
    try {
      String fileContents = new Scanner(new File(tspFile)).useDelimiter("\\Z").next();
      String[] lines = fileContents.split("\n");
      int vertices = Integer.parseInt(lines[3].split(": ")[1]);
      
      double[][] coordinates = new double[vertices][2];
      
      for (int i=0; i<vertices; i++) {
        String[] rows = lines[i+6].split(" ");
        coordinates[i][0] = Double.parseDouble(rows[1]);
        coordinates[i][1] = Double.parseDouble(rows[2]);
      }
      //shuffle(coordinates);
      
      double[][] weights = new double[vertices][vertices];
      
      for (int i=0; i < vertices; i++) {
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
      
      this.weights = weights;
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(0);
    }
  }
  
  public int[] solve() { return null; }
  
  public static double pathLength(int[] path) {
    double totalLen = 0.0;
    for (int i=0; i<path.length; i++) {
      int firstCity = path[i];
      int secondCity = path[(i+1) % path.length];
      totalLen += weights[firstCity][secondCity];
    }
    return totalLen;
  }
  
  /**
   * From StackOverflow.
   * Fisher–Yates shuffle.
   */
  public static void shuffle(double[][] ar) {
    Random rnd = new Random();
    for (int i = ar.length - 1; i >= 0; i--) {
      int index = rnd.nextInt(i + 1);
      double[] a = ar[index];
      ar[index] = ar[i];
      ar[i] = a;
    }
  }
  
}
