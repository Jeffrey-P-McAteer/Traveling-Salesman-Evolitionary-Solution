import java.util.stream.IntStream;
import java.util.Arrays;
import java.util.Random;

import javax.swing.*;
import java.awt.*;

public class ExhaustiveTriangleWorstInsert extends TSAlgo {
  public ExhaustiveTriangleWorstInsert(String s) {
    super(s);
    rand = new Random();
  }
  
  public static Random rand;
  
  // Give function path with 3 points
  public int[] originalSolve(int[] path) {
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
    
    // while we have not added every point
    while (path.length < weights.length) {
      if (false && PROGRESS) {
        System.err.print(CLEAR);
        System.err.printf("Solving %,d/%,d\r", path.length, weights.length);
      }
      
      path = insertPoint(path, getBestRandomPoint(path));
      
      debugPath(path);
      //displayAsync(path, "step "+path.length);
    }
    if (false && PROGRESS) System.err.println();
    
    return path;
  }
  
  public int[] solve() {
    // We step through every permutation of 3 triangles in the graph
    int[][] paths = new int[weights.length * weights.length * weights.length][];
    double[] lengths = new double[weights.length * weights.length * weights.length];
    for (int i = 0; i<lengths.length; i++) {
      lengths[i] = Double.POSITIVE_INFINITY;
    }
    int i = 0;
    int shortest_i = 0; // points to shortest path in paths
    int[] shortest_triangle = new int[3];
    
    for (int a=0; a<weights.length; a++) {
      for (int b=0; b<weights.length; b++) {
        if (b == a) continue;
        for (int c=0; c<weights.length; c++) {
          if (c == a || c == b) continue;
          
          paths[i] = originalSolve(new int[] {a, b, c});
          lengths[i] = pathLength(paths[i]);
          if (lengths[i] < lengths[shortest_i]) {
            shortest_i = i;
            shortest_triangle[0] = a;
            shortest_triangle[1] = b;
            shortest_triangle[2] = c;
          }
          if (PROGRESS) {
            System.err.print(CLEAR);
            System.err.printf("Shortest: %.2f\tProgress: %d/%d (%.3f%%)", lengths[shortest_i], i, paths.length, (double) (i*100) / (double)paths.length);
          }
          i++;
        }
      }
    }
    System.err.println();
    System.err.printf("Initial triangle coordinates: (%.2f, %.2f), (%.2f, %.2f), (%.2f, %.2f)\n",
      coordinates[shortest_triangle[0]][0], coordinates[shortest_triangle[0]][1],
      coordinates[shortest_triangle[1]][0], coordinates[shortest_triangle[1]][1],
      coordinates[shortest_triangle[2]][0], coordinates[shortest_triangle[2]][1]
      );
    
    display(paths[shortest_i], "Exhaustive triangle worst insert", (double[] nums, Graphics g) -> {
      int size = (int) nums[0];
      double width = nums[1];
      double height = nums[2];
      int offset = (int) nums[3];
      
      int oval_size = 5;
      g.setColor(Color.red);
      g.fillOval((int) ((coordinates[shortest_triangle[0]][0]/width)*size)+offset-oval_size,
                 (int) ((coordinates[shortest_triangle[0]][1]/width)*size)+offset-oval_size,
                 oval_size*2, oval_size*2
      );
      g.fillOval((int) ((coordinates[shortest_triangle[1]][0]/width)*size)+offset-oval_size,
                 (int) ((coordinates[shortest_triangle[1]][1]/width)*size)+offset-oval_size,
                 oval_size*2, oval_size*2
      );
      g.fillOval((int) ((coordinates[shortest_triangle[2]][0]/width)*size)+offset-oval_size,
                 (int) ((coordinates[shortest_triangle[2]][1]/width)*size)+offset-oval_size,
                 oval_size*2, oval_size*2
      );
      
    });
    
    return paths[shortest_i];
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
