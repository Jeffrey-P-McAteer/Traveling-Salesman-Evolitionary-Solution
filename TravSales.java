import java.util.stream.*;
import java.util.*;
import java.io.*;
/*
Example city data generated with
javarepl 'java.util.stream.IntStream.range(0,100).forEach(i -> System.out.println((int)(Math.random() * 100)+" "+(int)(Math.random() * 100)));'

*/

public class TravSales {
  public static int[][] locationCoords;
  
  public static void main(String... args) throws Exception {
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
    
    System.out.printf("Path length: %d\n", pathLength(path));
    System.out.println(Arrays.asList(path));
    
    evolvePath(path, 128);
    System.out.printf("Path length: %d\n", pathLength(path));
    System.out.println(Arrays.asList(path));
    
  }
  
  public static void evolvePath(Integer[] original, int numPaths) {
    Integer[][] paths = new Integer[numPaths][original.length];
    for (int i=0; i<paths.length; i++) {
      moveFrom(original, paths[i]);
    }
    for (int i=0; i<paths.length; i++) {
      mutatePath(paths[i], 100000);
    }
    for (int i=0; i<paths.length; i++) {
      optimizePath(paths[i], 100000);
    }
    int[] pathsLengths = new int[paths.length];
    for (int i=0; i<paths.length; i++) {
      pathsLengths[i] = pathLength(paths[i]);
    }
    int shortestIndex = 0;
    for (int i=0; i<pathsLengths.length; i++) {
      if (pathsLengths[i] < pathsLengths[shortestIndex]) {
        shortestIndex = i;
      }
    }
    moveFrom(paths[shortestIndex], original);
  }
  
  public static void optimizePath(Integer[] path, int times) {
    int bestPathLength = pathLength(path);
    while (times --> 0) {
      Integer[] attempt = clone(path);
      mutatePath(attempt, 10);
      int attemptLength = pathLength(attempt);
      if (attemptLength < bestPathLength) {
        bestPathLength = attemptLength;
        moveFrom(attempt, path); // path now equals attempt
      }
    }
  }
  
  public static int pathLength(Integer[] path) {
    int totalLen = 0;
    for (int i=0; i<path.length-1; i += 2) {
      int firstCity = path[i];
      int secondCity = path[i+1];
      totalLen += distance(locationCoords[firstCity], locationCoords[secondCity]);
    }
    int firstCity = path[0];
    int secondCity = path[path.length-1];
    totalLen += distance(locationCoords[firstCity], locationCoords[secondCity]);
    return totalLen;
  }
  
  public static int distance(int[] coord1, int[] coord2) {
    int[] delta = new int[] {
      coord1[0] - coord2[0],
      coord1[1] - coord2[1]
    };
    return (int) Math.sqrt(Math.pow(delta[0], 2) + Math.pow(delta[1], 2));
  }
  
  public static Integer[] mutatePath(Integer[] path, int remainingMutations) {
    while (remainingMutations --> 0) {
      int indexToSwap = (int) (Math.random() * (path.length-1));
      int tmp = path[indexToSwap];
      path[indexToSwap] = path[indexToSwap+1];
      path[indexToSwap+1] = tmp;
    }
    return path;
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
  
}
