import static java.lang.System.out;
import java.util.stream.*;
import java.util.*;
import java.io.*;

/**
 * The original Split-And-Merge algorithm I wrote.
 * Runs into stack overflow errors with inputs larger than ~250 cities
 * @author Jeffrey McAteer
 */
public class OriginalPath extends Merge {
  public String getAlgoName() { return "Original Path"; }
  
  public static void main(String... args) throws Exception {
    test(new OriginalPath(), args);
  }
  
  public int[] solve() {
    return getDefaultPath();
  }
  
}
