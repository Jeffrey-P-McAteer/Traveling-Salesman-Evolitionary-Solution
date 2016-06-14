import static java.lang.System.out;
import java.util.stream.*;
import java.util.*;
import java.io.*;

/**
 * Evolutionary solution to travelling salesman
 * @author Jeffrey McAteer
 */
public class RocketBoostedEvolution extends TSAlgo {
  public String getAlgoName() { return "RocketBoostedEvolution"; }
  
  public static void main(String... args) throws Exception {
    test(new RocketBoostedEvolution(), args);
  }
  
  public int[] solve() {
    // Update to use the best algorithm we have so far
    int[] path = new GrowExpensive().solve(); // literally the only difference between this and Evolutionary.java
    multiThreadedEvolution(path, 64, -1, 200, 120);
    //multiThreadedEvolution(path, 4096, -1, 1_000, 100);
    //multiThreadedEvolution(path, 128, -1, 1_000, 100); // on an i5 takes ~ 1 seconds
    //multiThreadedEvolution(path, 1024, -1, 1_000, 100); // on an i5 takes ~ 2 seconds
    //multiThreadedEvolution(path, 1024, -1, 10_000, 100); // on an i5 takes ~ 40 seconds
    //multiThreadedEvolution(path, 2048, -1, 100_000, 100); // on an i5 takes ~ 20 minutes
    return path;
  }
  
  public static void multiThreadedEvolution(int[] original, int numPaths, int mutationCycles, int optimizationCycles, int innerMutationCycles) {
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
          evolvePath(paths[index], pathsPerCore, mutationCycles, optimizationCycles, innerMutationCycles);
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
  
  public static void evolvePath(int[] original, int numPaths, int mutationCycles, int optimizationCycles, int innerMutationCycles) {
    int[][] paths = new int[numPaths][original.length];
    for (int i=0; i<paths.length; i++) {
      moveFrom(original, paths[i]);
    }
    for (int i=0; i<paths.length; i++) {
      mutatePath(paths[i], mutationCycles);
    }
    for (int i=0; i<paths.length; i++) {
      optimizePath(paths[i], optimizationCycles, innerMutationCycles);
    }
    int[] bestPath = getBestPath(paths);
    moveFrom(bestPath, original);
  }
  
  public static void optimizePath(int[] path, int times, int maxMutationCycles) {
    int turnsSinceBetterPathFound = 0;
    //int bestPathLength = pathLength(path);
    double bestPathLength = pathLength(path);
    while (times --> 0 && turnsSinceBetterPathFound < times) { // extra breakpoint for performance
      int[] attempt = clone(path);
      mutatePath(attempt, (int) (Math.random() * maxMutationCycles));
      //int attemptLength = pathLength(attempt);
      double attemptLength = pathLength(attempt);
      if (attemptLength < bestPathLength) {
        turnsSinceBetterPathFound = 0;
        bestPathLength = attemptLength;
        moveFrom(attempt, path); // path now equals attempt
      } else {
        turnsSinceBetterPathFound++;
      }
    }
  }
  
  // note for future optimization: swapping directly adjacent nodes is equally as effective as random nodes.
  public static int[] mutatePath(int[] path, int remainingMutations) {
    while (remainingMutations --> 0) {
      int indexToSwap = (int) (Math.random() * (path.length-1));
      int tmp = path[indexToSwap];
      path[indexToSwap] = path[indexToSwap+1];
      path[indexToSwap+1] = tmp;
    }
    return path;
  }
  
  // I do, however, leave the aforementioned method commented below if any ideas arise
  /*
  public static int[] mutatePath(int[] path, int remainingMutations) {
    while (remainingMutations --> 0) {
      int indexAToSwap = (int) (Math.random() * (path.length-1));
      int indexBToSwap = (int) (Math.random() * (path.length-2));
      if (indexAToSwap == indexBToSwap) indexBToSwap++;
      int tmp = path[indexAToSwap];
      path[indexAToSwap] = path[indexBToSwap];
      path[indexBToSwap] = tmp;
    }
    return path;
  }
  */
  
}
