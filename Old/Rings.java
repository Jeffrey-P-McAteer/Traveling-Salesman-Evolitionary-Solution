import static java.lang.System.out;
import java.util.stream.*;
import java.util.*;
import java.io.*;

/**
 * A new algorithm based upon an observation made at McDonalds
 * 
 * @author Jeffrey McAteer
 */
public class Rings extends TSAlgo {
  public String getAlgoName() { return "Rings"; }
  
  public static void main(String... args) throws Exception {
    test(new Rings(), args);
  }
  
  public int[] solve() {
    int[] path = getDefaultPath();
    int[][] allRings = new int[4][];
    int[][] ringBounds = ringBounds(allRings.length, path);
    
    out.println("ringBounds:");
    for (int[] ring : ringBounds) {
      for (int i : ring) out.print(i+", ");
      out.println();
    }
    out.println();
    
    allRings[0] = new int[] {closest(center(path), path)}; // first ring is the centermost point
    for (int i=1; i<allRings.length-1; i++) {
      allRings[i] = setIntsToNullish(new int[path.length /* - already processed items */]);
      int all_r_i = 0;
      for (int j=0; j<path.length; j++) {
        if (isOutside(ringBounds[i-1], path[j]) && isInside(ringBounds[i], path[j])) {
          allRings[i][all_r_i] = path[j];
          all_r_i++;
        }
      }
      allRings[i] = removeNulls(allRings[i]);
    }
    /*for (int j=0; j<path.length; j++) {
      if (isOutside(ringBounds[i-1], path[j]) && isInside(ringBounds[i+1], path[j])) {
        allRings[allRings.length-1]; = path[j];
      }
    }*/
    
    out.println("allRings:");
    for (int[] ring : allRings) {
      for (int i : ring) out.print(i+", ");
      out.println();
    }
    out.println();
    
    boolean broken = false;
    for (int i=0; i<allRings.length-1; i++) {
      if (allRings[i] == null || allRings[i].length < 1) {
        assert allRings[i-1].length == path.length;
        path = allRings[i-1];
        broken = true;
        break;
      }
      allRings[i+1] = mergeRings(allRings[i], allRings[i+1]);
    }
    if (!broken) {
      assert allRings[allRings.length-1].length == path.length;
      path = allRings[allRings.length-1];
    }
    
    return path;
  }
  
  public int[] mergeRings(int[] innerRing, int[] outerRing) {
    return null;
  }
  
  // candy
  public boolean isOutside(int[] box, int coord_index_from_path) {
    return isOutside(box, locationCoords[coord_index_from_path]);
  }
  
  public boolean isOutside(int[] box, int[] coordinate) {
    return (coordinate[0] < box[LEFT] || coordinate[0] > box[RIGHT]) &&
           (coordinate[1] < box[BOT] || coordinate[1] > box[TOP]);
  }
  
  public boolean isInside(int[] box, int coord_index_from_path) {
    return isInside(box, locationCoords[coord_index_from_path]);
  }
  
  public boolean isInside(int[] box, int[] coordinate) {
    return (coordinate[0] >= box[LEFT] && coordinate[0] <= box[RIGHT]) &&
           (coordinate[1] >= box[BOT] && coordinate[1] <= box[TOP]);
  }
  
  public double[][] ringBounds(int number, int... points) {
    int[] outermostBox = boundingBox(points);
    int[][] bounds = new int[number][4];
    double ring_x_width = (outermostBox[RIGHT] - outermostBox[LEFT]) / (double) number;
    double ring_y_width = (outermostBox[TOP] - outermostBox[BOT]) / (double) number;
    bounds[0] = outermostBox;
    for (int i=1; i<bounds.length; i++) {
      bounds[i][TOP] = outermostBox[TOP] - (i*ring_y_width);
      bounds[i][BOT] = outermostBox[BOT] + (i*ring_y_width);
      bounds[i][LEFT] = outermostBox[LEFT] + (i*ring_x_width);
      bounds[i][RIGHT] = outermostBox[RIGHT] - (i*ring_x_width);
    }
    return bounds;
  }
  
  static final int TOP = 0;
  static final int LEFT = 1;
  static final int BOT = 2;
  static final int RIGHT = 3;
  // return {top, left, bot, right}; where top and bot are y coordinates, left and right are x coordinates
  public int[] boundingBox(int... points) {
    int left_most = points[0];
      for (int i : points) {
        if (locationCoords[i][0] < locationCoords[left_most][0]) {
          left_most = i;
        }
      }
      
      int right_most = points[0];
      for (int i : points) {
        if (locationCoords[i][0] > locationCoords[right_most][0]) {
          right_most = i;
        }
      }
      
      int top_most = points[0];
      for (int i : points) {
        if (locationCoords[i][1] > locationCoords[top_most][1]) {
          top_most = i;
        }
      }
      
      int bot_most = points[0];
      for (int i : points) {
        if (locationCoords[i][1] < locationCoords[bot_most][1]) {
          bot_most = i;
        }
      }
      
      int top   = locationCoords[top_most][1];
      int left  = locationCoords[left_most][0];
      int bot   = locationCoords[bot_most][1];
      int right = locationCoords[right_most][0];
      
      return new int[] {top, left, bot, right};
  }
  
  public static int closest(double[] location, int[] points) {
    double[] distances = new double[points.length];
    for (int i=0; i<distances.length; i++) {
      distances[i] = distance(location, locationCoords[points[i]]);
    }
    int shortest = 0;
    for (int i=0; i<distances.length; i++) {
      if (distances[i] < distances[shortest]) {
        shortest = i;
      }
    }
    return shortest; // index of points
  }
  
  public static double distance(double[] coord1, int[] coord2) {
    double[] delta = new double[] {
      coord1[0] - (double) coord2[0],
      coord1[1] - (double) coord2[1]
    };
    return Math.sqrt(Math.pow(delta[0], 2) + Math.pow(delta[1], 2));
  }
  
  public static double distance(int[] coord1, int[] coord2) {
    int[] delta = new int[] {
      coord1[0] - coord2[0],
      coord1[1] - coord2[1]
    };
    return Math.sqrt(Math.pow(delta[0], 2) + Math.pow(delta[1], 2));
  }
  
  public static double[] center(int... points) {
    double[] center = new double[] {0.0, 0.0};
    for (int i : points) {
      center[0] += locationCoords[i][0];
      center[1] += locationCoords[i][1];
    }
    if (points.length > 1) {
      center[0] /= points.length;
      center[1] /= points.length;
    }
    return center;
  }
  
}
