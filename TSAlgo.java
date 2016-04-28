import static java.lang.System.out;
import java.util.stream.*;
import java.util.*;
import java.io.*;


/**
 * Superclas holding useful TS Stuff that ought to be the same in all algorithms
 * main acts as a tester
 * @author Jeffrey McAteer
 */
public class TSAlgo {
  // Override and change to appropriate algo name
  public String getAlgoName() { return "None"; }
  
  public static int[][] locationCoords;
  
  // Must be overidden
  // Should return the best possible path for the cities in locationCoords
  public int[] solve() { return null; }
  
  public static void main(String... args) throws Exception {
    test(new TSAlgo(), args);
  }
  
  public static void test(TSAlgo algo, String... args) throws Exception {
    if (args.length < 1) {
      out.printf("Usage: java %s citydata.txt [false]\n", algo.getAlgoName());
      out.printf("Where citydata.txt holds one space-deliminated coordinate pair per line\n");
      out.printf("and the last argument is to control if the found path is printed\n");
      out.printf("(defaults to true, any value at all causes falsey behaviour)");
    }
    populateLocationCoords(args[0]);
    
    long begin = System.nanoTime();
    int[] path = algo.solve();
    long delta = System.nanoTime() - begin;
    if (path == null) {
      System.out.printf("%s does not implement travelling salesman.\n", algo.getAlgoName());
      return;
    }
    System.out.printf("%s took %,.2fms to find a path of length %,.2f\n", algo.getAlgoName(), delta/1_000_000.0, pathLength(path));
    
    if (args.length < 2) {
      /*
      System.out.printf("Path indexes: ");
      for (int p : path) {
        System.out.printf("%d, ", p);
      }
      System.out.println();
      /**/
      //*
      System.out.printf("Path coordinates: ");
      for (int p : path) {
        System.out.printf("(%d, %d) ", locationCoords[p][0], locationCoords[p][1]);
      }
      System.out.println();
      /**/
    }
  }
  
  /**
   * Reads a test file with city coords on each line, such as:
   * 10 20
   * 40 60
   * 5 6
   * which represents 3 cities at (10,20), (40,60), and (5,6)
   */
  private static void populateLocationCoords(String filename) throws Exception {
    String citiesData = new Scanner(new File(filename)).useDelimiter("\\Z").next();
    String[] cities = citiesData.split("\n");
    locationCoords = new int[cities.length][2];
    IntStream.range(0, cities.length)
      .forEach(i -> {
        String[] xAndY = cities[i].split(" ");
        IntStream.range(0, xAndY.length)
          .forEach(j -> locationCoords[i][j] = Integer.parseInt(xAndY[j]));
      });
  }
  
  /* development functions */
  
  public static void print(int[][] ints) {
    for (int[] i : ints) {
      out.print("[ ");
      print(i);
      out.print(" ],");
    }
    out.println();
  }
  
  public static void print(int[] ints) {
    out.print("{");
    for (int i : ints) out.print(i+", ");
    out.print("}");
    out.println();
  }
  
  /* useful functions which ought to be standard throughout every algorithm for comparison purposes */
  
  // create original path which stupidly goes from city 1 to city 2 to city 3....
  public static int[] getDefaultPath() {
    int[] path = new int[locationCoords.length];
    for (int i=0; i<path.length; i++) path[i] = i;
    return path;
  }
  
  // returns the best path from a list of possible paths
  public static int[] getBestPath(int[]... paths) {
    double[] pathsLengths = new double[paths.length];
    for (int i=0; i<paths.length; i++) {
      pathsLengths[i] = pathLength(paths[i]);
    }
    int shortestIndex = 0;
    for (int i=0; i<pathsLengths.length; i++) {
      if (pathsLengths[i] < pathsLengths[shortestIndex]) {
        shortestIndex = i;
      }
    }
    return paths[shortestIndex];
  }
  
  // returns the shortest UNCLOSED path from the given paths
  // (does not calculate final edge's length)
  public static int[] getBestUnclosedPath(int[]... paths) {
    double[] pathsLengths = new double[paths.length];
    for (int i=0; i<paths.length; i++) {
      if (paths[i].length < 1) {
        pathsLengths[i] = Double.MAX_VALUE;
      } else {
        pathsLengths[i] = unclosedPathLength(paths[i]);
      }
    }
    int shortestIndex = 0;
    for (int i=0; i<pathsLengths.length; i++) {
      if (pathsLengths[i] < pathsLengths[shortestIndex]) {
        shortestIndex = i;
      }
    }
    return paths[shortestIndex];
  }
  
  public static double pathLength(int... path) {
    double totalLen = 0;
    int firstCity = path[0];
    int secondCity = path[path.length-1];
    totalLen += distance(locationCoords[firstCity], locationCoords[secondCity]);
    return unclosedPathLength(path) + totalLen;
  }
  
  // calculates everything but last edge
  public static double unclosedPathLength(int... path) {
    double totalLen = 0;
    for (int i=0; i<path.length-1; i++) {
      int firstCity = path[i];
      int secondCity = path[i+1];
      totalLen += distance(locationCoords[firstCity], locationCoords[secondCity]);
    }
    return totalLen;
  }
  
  public static double distance(int[] coord1, int[] coord2) {
    int[] delta = new int[] {
      coord1[0] - coord2[0],
      coord1[1] - coord2[1]
    };
    return Math.sqrt(Math.pow(delta[0], 2) + Math.pow(delta[1], 2));
  }
  
  /* Array manipulation methods */
  
  public static int[] clone(int[] arr) {
    int[] newArr = new int[arr.length];
    moveFrom(arr, newArr);
    return newArr;
  }
  
  public static void moveFrom(int[] a, int[] b) {
    assert a.length == b.length;
    for (int i=0; i<a.length; i++) {
      b[i] = a[i];
    }
  }
  
  // sets all values in 2d array to -1
  public static void setIntsToNullish(int[][] arr) {
    for (int i=0; i<arr.length; i++) {
      for (int j=0; j<arr[i].length; j++) {
        arr[i][j] = -1;
      }
    }
  }
  
  // in the case of int datatypes we assume negatives are 'null' values
  // removes every element after the first null (eg {1,2,3,null,4,5} -> {1,2,3})
  // should only be used when it can be garanteed that the array ends in nulls only
  public static int[] removeNulls(int[] arr) {
    int firstNullIndex = 0;
    while (firstNullIndex < arr.length && arr[firstNullIndex] >= 0) firstNullIndex++;
    return Arrays.copyOfRange(arr, 0, firstNullIndex);
  }
  
  public static int fact(int n) {
    if (n < 2) return 1;
    return fact(n-1) * n;
  }
  
  public static int[] reverse(int[] b) {
    int[] a = clone(b);
    // stole & modified from stackoverflow.com/a/2137791
    for(int i = 0; i < a.length / 2; i++) {
      int temp = a[i];
      a[i] = a[a.length - i - 1];
      a[a.length - i - 1] = temp;
    }
    return a;
  }
  
  // stolen at stackoverflow.com/a/80503, courtesy of jeannicolas
  public static int[] concatenate (int[] a, int[] b) {
    int aLen = a.length;
    int bLen = b.length;
    
    @SuppressWarnings("unchecked")
    int[] c = (int[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), aLen+bLen);
    System.arraycopy(a, 0, c, 0, aLen);
    System.arraycopy(b, 0, c, aLen, bLen);
    
    return c;
  }
  
}
