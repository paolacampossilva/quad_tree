import java.io.*;
import java.util.*;
import lpoo.geom.*;

/**
 *
 * @author Paulo Pagliosa
 */
public class Main
{
  static int toInteger(String s)
  {
    return Integer.parseInt(s);
  }

  static float toFloat(String s)
  {
    return Float.parseFloat(s);
  }

  static Point2[] readPointsFromFile(String filename)
    throws IOException
  {
    try (BufferedReader is = new BufferedReader(new FileReader(filename)))
    {
      StringTokenizer t = new StringTokenizer(is.readLine());
      Point2 s = new Point2(toFloat(t.nextToken()), toFloat(t.nextToken()));

      s.print("Domain size: ");

      int n = toInteger(is.readLine());
      Point2[] points = new Point2[n];

      for (int i = 0; i < n; i++)
      {
        t = new StringTokenizer(is.readLine());
        points[i] = new Point2(toFloat(t.nextToken()), toFloat(t.nextToken()));
      }
      System.out.printf("%d points read\n", n);
      return points;
    }
  }

  public static void main(String[] args)
  {
    try
    {
      String fn = args.length == 0 ? Console.readString("File") : args[0];
      Point2[] points = readPointsFromFile(fn);
      Quadtree qtree = new Quadtree(points, 5);

      qtree.bounds().print("Quadtree bounds: ");
      System.out.printf("Nodes: %d\nLeaf nodes: %d\n",
        qtree.size(),
        qtree.leafCount());
      new QuadtreeViewer(qtree);
    }
    catch (Exception e)
    {
      System.out.println(e.getMessage());
    }
  }

} // Main
