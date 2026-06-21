import lpoo.geom.*;
import java.util.Random;

/**
 * @author João Pedro Huppes Arenales
 * @author Paola Campos da Silva
 */
public class TestViewer
{
  public static void main(String[] args)
  {
  System.out.println("=== Visual test for the bonus task: click with the mouse ===");

  int pointsTotal = 100;
  Point2[] points = new Point2[pointsTotal];
  Random rand = new Random();

  for (int i = 0; i < pointsTotal; i++)
  {
    float x = 50 + rand.nextFloat() * 400;
    float y = 50 + rand.nextFloat() * 300;
    points[i] = new Point2(x, y);
  }

  Quadtree<Point2> tree = new Quadtree<>(points, 5);

  new QuadtreeViewer<Point2>(tree);

  System.out.println("Screen was openned! Click anywhere on the canvas to highlight the KNN for the nearest point.");
  }
} // TestViewer
