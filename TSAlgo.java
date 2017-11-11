import java.io.*;
import java.util.*;
import java.util.stream.*;
import java.util.function.*;

import javax.swing.*;
import java.awt.*;

/**
 * A superclass for different traveling salesman algorithms
 * Contains useful functions which should be standard for every algorithm
 * @author Jeffrey McAteer
 */
public class TSAlgo {
  
  // Dumpt details about every step
  public static final boolean DUMP = System.getenv("DUMP") == null? false : System.getenv("DUMP").equalsIgnoreCase("true");
  
  // Controls animation of progress
  public static final boolean PROGRESS = System.getenv("PROGRESS") == null? true : System.getenv("PROGRESS").equalsIgnoreCase("true");;
  
  public static final boolean NUMBERS = false;
  
  // Clears a line of text for PROGRESS printouts
  public static final String CLEAR = "                                        \r";
  
  public static double[][] coordinates;
  
  public static double[][] weights;
  
  public TSAlgo(String tspFile) {
    if (tspFile == null) {
      return;
    }
    try {
      String fileContents = new Scanner(new File(tspFile)).useDelimiter("\\Z").next();
      String[] lines = fileContents.split("\n");
      int vertices = Integer.parseInt(lines[3].split(": ")[1]);
      
      coordinates = new double[vertices][2];
      
      IntStream.range(0, vertices).parallel().forEach(
        i -> {
          if (PROGRESS) {
            System.err.print(CLEAR);
            System.err.printf("Parsing vertices %,d/%,d\r", i, vertices);
          }
          String[] rows = lines[i+6].trim().replaceAll("  ", " ").split(" ");
          
          coordinates[i][0] = Double.parseDouble(rows[1]);
          coordinates[i][1] = Double.parseDouble(rows[2]);
      });
      
      // good for testing
      if (coordinates.length < 100) {
        int r = new Random().nextInt(coordinates.length);
        for (int i=0; i<r; i++) {
          shuffle(coordinates);
        }
      }
      
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
      
      if (DUMP && coordinates.length < 50) {
        for (int row=0; row<weights.length; row++) {
          for (int col=0; col<weights[0].length; col++) {
            if (weights[row][col] == Double.POSITIVE_INFINITY) {
              System.out.print("Inf  ");
            } else {
              System.out.printf("%.2f ", weights[row][col]);
            }
          }
          System.out.println();
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
  
  public static int[] concatinate(int[]... ints) {
    int totalLen = 0;
    for (int[] arr : ints) totalLen += arr.length;
    int[] combined = new int[totalLen];
    int i=0;
    for (int[] arr : ints) {
      for (int num : arr) {
        combined[i] = num;
        i++;
      }
    }
    return combined;
  }
  
  public static void debugPath(int[] path) {
    if (DUMP && weights.length < 20) {
      System.out.print("Path: ");
      for (int num : path) System.out.printf("%d ", num);
      System.out.println();
      
      System.out.print("Path: ");
      for (int p : path) {
        System.out.printf("(%s %s) ", coordinates[p][0], coordinates[p][1]);
      }
      System.out.println();
    }
  }
  
  public static void displayAsync(int[] path, String title) {
    new Thread() {
      public void run() {
        display(path, title);
      }
    }.start();
  }
  
  public static void display(int[] path, String title) {
    display(path, title, (double[] d, Graphics g) -> {});
  }
  
  public static void display(int[] path, String title, BiConsumer<double[], Graphics> painter) {
    
    final int size = 600;
    final int offset = 50;
    
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
        boolean points = path.length < 100;
        g.drawString(title, 5, 5);
        for (int i=0; i<path.length; i++) {
          int a = path[i];
          int b = path[(i+1) % path.length];
          double[] a_coords = convert(coordinates[a]);
          double[] b_coords = convert(coordinates[b]);
          
          g.drawLine((int) a_coords[0], (int) a_coords[1],
                     (int) b_coords[0], (int) b_coords[1]);
          
          if (points) {
            int oval_size = 5;
            g.fillOval((int) a_coords[0]-oval_size,
                       (int) a_coords[1]-oval_size,
                       oval_size*2, oval_size*2
            );
            
            if (NUMBERS) {
              g.drawString(""+a, (int) (a_coords[0]+(oval_size*1.4)),
                                 (int) (a_coords[1]+(oval_size*1.4)));
            }
          }
        }
        painter.accept(new double[] {size, width, height, offset}, g);
      }
    };
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setSize(size+(offset*2), size+(offset*2));
    f.setLocationRelativeTo(null);
    f.setVisible(true);
    
  }
  
}
