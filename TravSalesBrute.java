import java.util.stream.*;
import java.util.*;
import java.io.*;

/**
 * 
 * @author Jeffrey McAteer
 */
public class TravSalesBrute {
  public static int[][] locationCoords;
  
  public static void main(String... args) throws Exception {
    // Note that MS systems appear to break this method of reading a file, it may be easier
    // to hard-code the city data in these cases.
    String citiesData = new Scanner(new File("cities.txt")).useDelimiter("\\Z").next();
    String[] cities = citiesData.split("\n");
    locationCoords = new int[cities.length][2];
    IntStream.range(0, cities.length)
      .forEach(i -> {
        String[] xAndY = cities[i].split(" ");
        IntStream.range(0, xAndY.length)
          .forEach(j -> locationCoords[i][j] = Integer.parseInt(xAndY[j]));
      });
    // locationCoords is now populated with each city's x and y coord
    Integer[] path = Stream.iterate(0, n -> n + 1).limit(locationCoords.length).toArray(i -> new Integer[i]);
    
    System.out.printf("Original Path length: %d\n", pathLength(path));
    System.out.println(Arrays.asList(path));
    
    brute(path);
    
    System.out.printf("Brute length: %d\n", pathLength(path));
    System.out.printf("Took %d steps\n", steps);
    System.out.println(Arrays.asList(path));
    
  }
  
  public static int steps = 0;
  
  public static void brute(Integer[] path) {
    Integer[][] allPaths = new Integer[fact(path.length)][path.length];
    // set first path to 1,2,3,4 (etc)
    for (int i=0; i<allPaths[0].length; i++) {
      allPaths[0][i] = i;
    }
    steps++; // for the first one
    int swapIndex = 0;
    int swapDelta = 1;
    for (int i=1; i<allPaths.length; i++) { steps++;
      // copy previous data into next set
      moveFrom(allPaths[i-1], allPaths[i]);
      
      // swap the elements
      Integer temp = allPaths[i][swapIndex];
      allPaths[i][swapIndex] = allPaths[i][swapIndex+1];
      allPaths[i][swapIndex+1] = temp;
      //System.out.printf("Swapped index %d and %d\n", swapIndex, swapIndex+1);
      
      // check our swap bounds
      if (swapIndex <= 0) {
        swapDelta = 1;
      }
      else if (swapIndex >= allPaths[i].length-2) {
        swapDelta = -1;
      }
      swapIndex += swapDelta;
    }
    // now we have all the possible permientations
    int bestPathIndex = 0;
    for (int i=0; i<allPaths.length; i++) {
      if (fastPathLength(allPaths[i]) < fastPathLength(allPaths[bestPathIndex])) {
        bestPathIndex = i;
      }
    }
    // finally copy data to given register
    moveFrom(allPaths[bestPathIndex], path);
  }
  
  // not real length, is inaccurate but comparable
  public static int fastPathLength(Integer[] path) {
    double totalLen = 0;
    for (int i=0; i<path.length-1; i += 2) {
      int firstCity = path[i];
      int secondCity = path[i+1];
      totalLen += fastDistance(locationCoords[firstCity], locationCoords[secondCity]);
    }
    int firstCity = path[0];
    int secondCity = path[path.length-1];
    totalLen += fastDistance(locationCoords[firstCity], locationCoords[secondCity]);
    return (int) totalLen;
  }
  
  public static int pathLength(Integer[] path) {
    double totalLen = 0;
    for (int i=0; i<path.length-1; i += 2) {
      int firstCity = path[i];
      int secondCity = path[i+1];
      totalLen += distance(locationCoords[firstCity], locationCoords[secondCity]);
    }
    int firstCity = path[0];
    int secondCity = path[path.length-1];
    totalLen += distance(locationCoords[firstCity], locationCoords[secondCity]);
    return (int) totalLen;
  }
  
  // not real distance, is inaccurate but comparable
  public static double fastDistance(int[] coord1, int[] coord2) {
    int[] delta = new int[] {
      coord1[0] - coord2[0],
      coord1[1] - coord2[1]
    };
    return (delta[0]*delta[0]) + (delta[1]*delta[1]);
  }
  
  public static double distance(int[] coord1, int[] coord2) {
    int[] delta = new int[] {
      coord1[0] - coord2[0],
      coord1[1] - coord2[1]
    };
    return Math.sqrt(Math.pow(delta[0], 2) + Math.pow(delta[1], 2));
  }
  
  public static Integer[] clone(Integer[] nums) {
    Integer[] newNums = new Integer[nums.length];
    moveFrom(nums, newNums);
    return newNums;
  }
  
  public static void moveFrom(Integer[] a, Integer[] b) {
    for (int i=0; i<a.length; i++) {
      b[i] = a[i];
    }
  }
  
  public static Integer[] removeNulls(Integer[] i) {
    int firstNullIndex = 0;
    while (firstNullIndex < i.length && i[firstNullIndex] != null) firstNullIndex++;
    return Arrays.copyOfRange(i, 0, firstNullIndex);
  }
  
  public static int fact(int n) {
    if (n < 2) return 1;
    return fact(n-1) * n;
  }
  
}
