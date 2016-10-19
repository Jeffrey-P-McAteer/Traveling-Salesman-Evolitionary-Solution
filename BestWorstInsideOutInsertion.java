import java.util.stream.IntStream;
import java.util.Arrays;

public class BestWorstInsideOutInsertion extends TSAlgo {
  public BestWorstInsideOutInsertion(String s) {
    super(s);
  }
  
  public int[] solve() {
    int[] path = new int[] {0, 1, 2};
    // We must choose the 3 points furthest away.
    // First, find the furthest 2 points in the matrix.
    for (int row=0; row<weights.length; row++) {
      for (int col=0; col<weights[0].length; col++) {
        if (row == col) continue;
        double best = weights[path[0]][path[1]];
        double current = weights[row][col];
        if (current > best) {
          path[0] = row;
          path[1] = col;
        }
      }
    }
    // path[0] and path[1] are the furthest points
    
    // ensure path[2] != path[0] or [1]
    while (path[2] == path[0] || path[2] == path[1]) {
      path[2] = (path[2]+1) % weights.length;
    }
    
    // pick the heaviest third point from the unselected points
    double best = weights[path[0]][path[2]] + weights[path[1]][path[2]];
    for (int row=0; row<weights.length; row++) {
      if (row == path[0] || row == path[1]) continue;
      double current = weights[path[0]][row] + weights[path[1]][row];
      if (current > best) {
        path[2] = row;
        best = current;
      }
    }
    // path[0], [1], and [2] now make the largest
    // triangle possible out of all points
    
    debugPath(path);
    
    // while we have not added every point
    while (path.length < weights.length) {
      if (PROGRESS) {
        System.err.print(CLEAR);
        System.err.printf("Solving %,d/%,d\r", path.length, weights.length);
      }
      
      int[] heaviestPointAndLocation = getBestLongestPoint(path);
      if (isInsidePath(path, heaviestPointAndLocation)) {
        int[] lightestPointAndLocation = getBestShortestPoint(path);
        path = insertPoint(path, lightestPointAndLocation);
      } else {
        path = insertPoint(path, heaviestPointAndLocation);
      }
      
      debugPath(path);
      //displayAsync(path, "step "+path.length);
    }
    if (PROGRESS) System.err.println();
    
    return path;
  }
  
  public boolean isInsidePath(int[] path, int[] pointAndLocation) {
    int point = pointAndLocation[0];
    int a_index_heaviest_weight = 0;
    int b_index_heaviest_weight = 1;
    for (int i : path) {
      for (int j : path) {
        if (i == j) continue;
        if (weights[i][j] > weights[a_index_heaviest_weight][b_index_heaviest_weight]) {
          a_index_heaviest_weight = i;
          b_index_heaviest_weight = j;
        }
      }
    }
    int point_b_index_heaviest = path[0] != point? path[0] : path[1];
    for (int i : path) {
      if (weights[i][point] > weights[point_b_index_heaviest][point]) {
        point_b_index_heaviest = i;
      }
    }
    // if longest path->point is heavier than longest path->path
    return weights[point_b_index_heaviest][point] < weights[a_index_heaviest_weight][b_index_heaviest_weight];
  }
  
  // syntactic sugar which takes the result of getBestLongestPoint() in one argument
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
  public int[] getBestShortestPoint(int[] path) {
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
    
    // now pick the best optimum_delta
    int best_best_delta = 0;
    for (int i=0; i<optimum_deltas.length; i++) {
      if (optimum_deltas[i] < optimum_deltas[best_best_delta]) {
        best_best_delta = i;
      }
    }
    
    return new int[] {
      pts_not_in_graph[best_best_delta], // the point to insert
      optimum_indexes[best_best_delta]   // where to insert it
    };
  }
  
  // return value [0] is the point to insert, [1] is the index at which to insert it
  public int[] getBestLongestPoint(int[] path) {
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
