import static java.lang.System.out;
import java.util.stream.*;
import java.util.*;
import java.io.*;

/**
 * Evolutionary solution to travelling salesman
 * @author Jeffrey McAteer
 */
public class Evolutionary extends TSAlgo {
  public String getAlgoName() { return "Evolutionary"; }
  
  public static void main(String... args) throws Exception {
    test(new Evolutionary(), args);
  }
  
  public int[] solve() {
    int[] path = getDefaultPath();
    multiThreadedEvolution(path, 256, 10_000, 1_000);
    return path;
  }
  
  public static void multiThreadedEvolution(int[] original, int numPaths, int mutationCycles, int optimizationCycles) {
    int cores = Runtime.getRuntime().availableProcessors();
    int pathsPerCore = numPaths / cores;
    int[][] paths = new int[cores][original.length];
    Thread[] threads = new Thread[cores];
    for (int i=0; i<threads.length; i++) {
      final int j = i; // whatever
      threads[i] = new Thread() {
        private int index = j;
        public void run() {
          paths[index] = new int[original.length];
          moveFrom(original, paths[index]);
          evolvePath(paths[index], pathsPerCore, mutationCycles, optimizationCycles);
        }
      };
    }
    Stream.of(threads).forEach(t -> t.start());
    Stream.of(threads).forEach(t -> {
      try {
        t.join();
      } catch (InterruptedException e) {}
    });
    int[] bestPath = getBestPath(paths);
    moveFrom(bestPath, original);
  }
  
  public static void evolvePath(int[] original, int numPaths, int mutationCycles, int optimizationCycles) {
    int[][] paths = new int[numPaths][original.length];
    for (int i=0; i<paths.length; i++) {
      moveFrom(original, paths[i]);
    }
    for (int i=0; i<paths.length; i++) {
      mutatePath(paths[i], mutationCycles);
    }
    for (int i=0; i<paths.length; i++) {
      optimizePath(paths[i], optimizationCycles);
    }
    int[] bestPath = getBestPath(paths);
    moveFrom(bestPath, original);
  }
  
  public static void optimizePath(int[] path, int times) {
    int turnsSinceBetterPathFound = 0;
    //int bestPathLength = pathLength(path);
    double bestPathLength = fastPathLength(path);
    while (times --> 0 && turnsSinceBetterPathFound < times) { // extra breakpoint for performance
      int[] attempt = clone(path);
      mutatePath(attempt, 10);
      //int attemptLength = pathLength(attempt);
      double attemptLength = fastPathLength(attempt);
      if (attemptLength < bestPathLength) {
        turnsSinceBetterPathFound = 0;
        bestPathLength = attemptLength;
        moveFrom(attempt, path); // path now equals attempt
      } else {
        turnsSinceBetterPathFound++;
      }
    }
  }
  
  public static int[] getBestPath(int[][] paths) {
    double[] pathsLengths = new double[paths.length];
    for (int i=0; i<paths.length; i++) {
      pathsLengths[i] = fastPathLength(paths[i]);
    }
    int shortestIndex = 0;
    for (int i=0; i<pathsLengths.length; i++) {
      if (pathsLengths[i] < pathsLengths[shortestIndex]) {
        shortestIndex = i;
      }
    }
    return paths[shortestIndex];
  }
  
  public static int[] mutatePath(int[] path, int remainingMutations) {
    while (remainingMutations --> 0) {
      int indexToSwap = (int) (Math.random() * (path.length-1));
      int tmp = path[indexToSwap];
      path[indexToSwap] = path[indexToSwap+1];
      path[indexToSwap+1] = tmp;
    }
    return path;
  }
  
}
