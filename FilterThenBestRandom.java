import java.util.stream.IntStream;
import java.util.Arrays;
import java.util.Random;

// Junk from TSAlgo
import java.io.*;
import java.util.*;
import java.util.stream.*;
import javax.swing.*;
import java.awt.*;

public class FilterThenBestRandom extends TSAlgo {
  public static class Edge {
    public int pt1, pt2;
    public double weight;
    public Edge(int pt1, int pt2) {
      this.pt1 = pt1; 
      this.pt2 = pt2;
      this.weight = Math.sqrt(
        Math.pow(coordinates[pt1][0] - coordinates[pt2][0], 2) + 
        Math.pow(coordinates[pt1][1] - coordinates[pt2][1], 2)
      );
    }
    public boolean equals(Object o) {
      if (o instanceof Edge) {
        return ((Edge) o).hashCode() == this.hashCode();
      }
      return false;
    }
    public int hashCode() {
      return (pt1 * pt2) + (int) weight;
    }
  }
  
  public static class CompareCoordIndexByX implements Comparator<Integer> {
      public int compare(Integer index_a, Integer index_b) {
          if (coordinates[index_a][0] - coordinates[index_b][0] < 0.0) return -1;
          if (coordinates[index_a][0] - coordinates[index_b][0] > 0.0) return 1;
          return 0;
      }
  }
  
  
  public FilterThenBestRandom(String tspFile) {
    super(null);
    // Copy & mod of TSAlgo (super(1))
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
      if (coordinates.length < 50) {
        int r = new Random().nextInt(coordinates.length);
        for (int i=0; i<r; i++) {
          shuffle(coordinates);
        }
      }
      
      // Now we have a list of (STATIC!) coordinates[] as [[0.2, 1.3], [5.6, 1.2]....]
      ArrayList<Integer> coords_index_arr = new ArrayList<>();
      for (int i=0; i<coordinates.length; i++) {
        coords_index_arr.add(new Integer(i));
      }
      
      ArrayList<Edge> edges = new ArrayList<>();
      
      // Sort by X num (0)
      ArrayList<Integer> coords_arr_x_sort = new ArrayList<>();
      for(Integer in : coords_index_arr) coords_arr_x_sort.add(in);
      Collections.sort(coords_arr_x_sort, new CompareCoordIndexByX());
      
      ArrayList<ArrayList<Integer>> paired_arr_x_sort = new ArrayList<>();
      paired_arr_x_sort.add(new ArrayList<Integer>());
      
      for (int i=1; i<coords_arr_x_sort.size(); i++) {
        Integer lastIndex = coords_arr_x_sort.get(i-1);
        Integer index = coords_arr_x_sort.get(i);
        if (coordinates[lastIndex][0] == coordinates[index][0]) {
          ArrayList<Integer> set = paired_arr_x_sort.get(paired_arr_x_sort.size()-1);
          set.add(lastIndex);
          set.add(index);
        }
        else {
          ArrayList<Integer> al = new ArrayList<>();
          al.add(index);
          paired_arr_x_sort.add(al);
          // todo work here
        }
      }
      
      
      
      int[][] x_points = new int[coordinates.length][];
      
      // Sort by Y num (1)
      
      
      
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
    rand = new Random();
  }
  
  public static Random rand;
  
  /*
   * This function goes through the matrix & sets "unused" paths
   * to Double.POSITIVE_INFINITY
   */
  public void filter() {
    for (int i=0; i < coordinates.length; i++) {
      
    }
    
  }
  
  public int[] solve() {
    // We choose 3 random points
    int[] path = new int[] {
      rand.nextInt(weights.length),
      rand.nextInt(weights.length),
      rand.nextInt(weights.length)
    };
    // And remove collisions
    while (path[1] == path[0]) {
      path[1] = rand.nextInt(weights.length);
    }
    while (path[2] == path[0] || path[2] == path[1]) {
      path[2] = rand.nextInt(weights.length);
    }
    assert path[0] != path[1] && path[0] != path[2] && path [1] != path[2];
    // path[0], [1], and [2] now 3 random points
    
    debugPath(path);
    
    filter();
    
    // while we have not added every point
    while (path.length < weights.length) {
      if (PROGRESS) {
        System.err.print(CLEAR);
        System.err.printf("Solving %,d/%,d\r", path.length, weights.length);
      }
      
      path = insertPoint(path, getBestRandomPoint(path));
      
      debugPath(path);
      //displayAsync(path, "step "+path.length);
    }
    if (PROGRESS) System.err.println();
    
    return path;
  }
  
  // syntactic sugar which takes the result of getBestRandomPoint() in one argument
  public int[] insertPoint(int[] path, int[] pointAndLocation) {
    int point = pointAndLocation[0];
    int insert_point = pointAndLocation[1]+1;
    return insertPoint(path, point, insert_point);
  }
  
  /**
   * inserts point into path at index location
   */
  public int[] insertPoint(int[] path, int point, int location) {
    return concatinate(
      Arrays.copyOfRange(path, 0, location), // includes path[location]
      new int[] { point },
      Arrays.copyOfRange(path, location, path.length)); // begins at path[location+1]
  }
  
  // return value [0] is the point to insert, [1] is the index at which to insert it
  public int[] getBestRandomPoint(int[] path) {
    int[] pts_not_in_graph = getAllPointsNotInGraph(path);
    double[] optimum_deltas = new double[pts_not_in_graph.length];
    int[] optimum_indexes = new int[pts_not_in_graph.length];
    
    // for every point not in the graph
    IntStream.range(0, pts_not_in_graph.length).parallel().forEach(
      i -> {
        int new_pt = pts_not_in_graph[i];
        optimum_indexes[i] = getBestInsertPoint(path, new_pt);
        optimum_deltas[i] = getDelta(path, new_pt, optimum_indexes[i]);
      });
    
    // now pick the worst optimum_delta
    int worst_best_delta = 0;
    for (int i=0; i<optimum_deltas.length; i++) {
      if (optimum_deltas[i] > optimum_deltas[worst_best_delta]) {
        worst_best_delta = i;
        
      }
    }
    worst_best_delta = rand.nextInt(optimum_deltas.length);
    
    return new int[] {
      pts_not_in_graph[worst_best_delta], // the point to insert
      optimum_indexes[worst_best_delta]   // where to insert it
    };
  }
  
  public int getBestInsertPoint(int[] path, int new_pt) {
    double[] deltas = new double[path.length];
    for (int i=0; i<deltas.length; i++) {
      deltas[i] = getDelta(path, new_pt, i);
    }
    int best = 0;
    for (int i=0; i<deltas.length; i++) {
      if (deltas[i] < deltas[best]) {
        best = i;
      }
    }
    return best;
  }
  
  public double getDelta(int[] path, int new_pt, int insert_index) {
    double delta = 0.0;
    int a = path[insert_index];
    int b = path[(insert_index+1)%path.length];
    delta -= weights[a][b];
    delta += weights[new_pt][a];
    delta += weights[new_pt][b];
    return delta;
  }

  public int[] getAllPointsNotInGraph(int[] path) {
    int[] pts_not_in_graph = new int[weights.length - path.length];
    int not_in_graph_index = 0;
    // for every vertex
    for (int vertex=0; vertex<weights.length; vertex++) {
      boolean in_path = false;
      // for every value in path (path[j])
      for (int j=0; j<path.length; j++) {
        if (path[j] == vertex) {
          in_path = true;
          break;
        }
      }
      if (!in_path) {
        //if (DUMP) System.out.printf("%d is not in path\n", vertex);
        pts_not_in_graph[not_in_graph_index] = vertex;
        not_in_graph_index++;
      }
    }
    return pts_not_in_graph;
  }

}
