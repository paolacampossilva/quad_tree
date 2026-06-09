import lpoo.geom.*;

/**
 *
 * @author Paulo Pagliosa
 */
public class Main
{
  public static void main(String[] args)
  {
    Particle2[] points = rand(500, -20, 30);
    Grid2<Particle2> grid = Grid2.New(points, 5);
    Bounds2 bounds = grid.bounds();
    Point2 p1 = bounds.p1();
    Point2 p2 = bounds.p2();

    bounds.print("**Grid bounds: ");
    System.out.println("**Controls:\n" +
      "+: zoom in\n" +
      "-: zoom out\n" +
      "p: pause (click left button to find neighbors)/resume animation");
    new Grid2Viewer<Particle2>(grid,
      points,
      (Particle2[] particles, float dt) ->
        {
          grid.clear();
          dt *= pts;
          for (Particle2 p : particles)
          {
            p.x += p.vx * dt;
            p.y += p.vy * dt;
            collide(p1, p2, p);
            grid.add(p);
          }
          return true;
        });
  }

  private static Particle2[] rand(int n, float min, float max)
  {
    Particle2[] points = new Particle2[n];
    float d = max - min;

    for (int i = 0; i < n; i++)
    {
      float x = min + (float)Math.random() * d;
      float y = min + (float)Math.random() * d;
      Particle2 p = new Particle2(x, y);

      p.vx = (float)Math.random();
      p.vy = (float)Math.random();
      points[i] = p;
    }
    return points;
  }

  private static void collide(Point2 p1, Point2 p2, Particle2 p)
  {
    if (p.x <= p1.x + eps)
    {
      p.x = p1.x + eps;
      p.vx *= -1;
    }
    else if (p.x >= p2.x - eps)
    {
      p.x = p2.x - eps;
      p.vx *= -1;
    }
    if (p.y <= p1.y + eps)
    {
      p.y = p1.y + eps;
      p.vy *= -1;
    }
    else if (p.y >= p2.y - eps)
    {
      p.y = p2.y - eps;
      p.vy *= -1;
    }
  }

  private static float eps = 1e-3f;
  private static float pts = 3; // time scale

} // Main
