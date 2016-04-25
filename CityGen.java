/**
 * 
 * @author Jeffrey McAteer
 */
public class CityGen {
  public static void main(String... args) throws Exception {
    java.util.stream.IntStream.range(0,Integer.parseInt(args[0])).forEach(i -> System.out.println((int)(Math.random() * 100)+" "+(int)(Math.random() * 100)));
  }
}
