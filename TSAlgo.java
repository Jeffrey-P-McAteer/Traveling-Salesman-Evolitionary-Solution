import java.io.*;
import java.util.*;

import javax.swing.*;
import java.awt.*;

/**
 * A superclass for different traveling salesman algorithms
 * Contains useful functions which should be standard for every algorithm
 * @author Jeffrey McAteer
 */
public class TSAlgo {
  public static final boolean PROGRESS = true;
  public static final String CLEAR = "                                                \r";
  
  public static double[][] coordinates;
  
  public static double[][] weights;
  
  public TSAlgo(String tspFile) {
    try {
      String fileContents = new Scanner(new File(tspFile)).useDelimiter("\\Z").next();
      String[] lines = fileContents.split("\n");
      int vertices = Integer.parseInt(lines[3].split(": ")[1]);
      
      coordinates = new double[vertices][2];
      
      for (int i=0; i<vertices; i++) {
        if (PROGRESS) {
          System.err.print(CLEAR);
          System.err.printf("Parsing vertices %,d/%,d\r", i, vertices);
        }
        String[] rows = lines[i+6].split(" ");
        coordinates[i][0] = Double.parseDouble(rows[1]);
        coordinates[i][1] = Double.parseDouble(rows[2]);
      }
      // good for testing
      shuffle(coordinates);
      
      weights = new double[vertices][vertices];
      
      for (int i=0; i < vertices; i++) {
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
  
  public static void display(int[] path, String title) {
    final int size = 600;
    final int offset = 30;
    
    int leftmost = 0;
    int rightmost = 0;
    int topmost = 0;
    int botmost = 0;
    
    for (int point: path) {
      if (coordinates[point][0] < coordinates[leftmost][0]) {
        leftmost = point;
      }
      if (coordinates[point][0] > coordinates[rightmost][0]) {
        rightmost = point;
      }
      if (coordinates[point][1] > coordinates[topmost][1]) {
        topmost = point;
      }
      if (coordinates[point][1] < coordinates[botmost][1]) {
        botmost = point;
      }
    }
    double width = coordinates[rightmost][0] - coordinates[leftmost][0];
    double height = coordinates[topmost][1] - coordinates[botmost][1];
    
    JFrame f = new JFrame(title) {
      public double[] convert(double[] original) {
        return new double[] {
          ((original[0]/width)*size)+offset,
          ((original[1]/height)*size)+offset,
        };
      }
      public void paint(Graphics g) {
        for (int i=0; i<path.length; i++) {
          int a = path[i];
          int b = path[(i+1) % path.length];
          double[] a_coords = convert(coordinates[a]);
          double[] b_coords = convert(coordinates[b]);
          
          g.drawLine((int) a_coords[0], (int) a_coords[1],
                     (int) b_coords[0], (int) b_coords[1]);
          
          int oval_size = 5;
          g.fillOval((int) a_coords[0]-oval_size,
                     (int) a_coords[1]-oval_size,
                     oval_size*2, oval_size*2
          );
          
          g.drawString(""+a, (int) (a_coords[0]+(oval_size*1.4)),
                             (int) (a_coords[1]+(oval_size*1.4)));
          
        }
      }
    };
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setSize(size+(offset*2), size+(offset*2));
    f.setLocationRelativeTo(null);
    f.setVisible(true);
    
  }
  
}
