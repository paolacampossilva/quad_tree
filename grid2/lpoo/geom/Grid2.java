package lpoo.geom;

import lpoo.math.*;
import java.util.*;

/**
 *
 * @author Paulo Pagliosa
 */
public class Grid2<P extends Point2>
  extends Grid2Base<Grid2.Cell<P>>
{
  public static class Cell<P extends Point2>
    implements Iterable<P>
  {
    public final Bounds2 bounds()
    {
      return new Bounds2(bounds);
    }

    public final boolean contains(Point2 p)
    {
      return bounds.contains(p);
    }

    public final boolean isEmpty()
    {
      return points.isEmpty();
    }

    @Override
    public final Iterator<P> iterator()
    {
      return points.iterator();
    }

    Cell(Point2 p, Point2 size)
    {
      bounds = new Bounds2(p, size);
    }

    private final Bounds2 bounds;
    final LinkedList<P> points = new LinkedList<>();

  } // Cell

  public static <P extends Point2> Grid2<P> New(Bounds2 bounds, int nx, int ny)
  {
    return new Grid2<>(bounds.inflate(fatFactor), nx, ny);
  }

  public static <P extends Point2> Grid2<P> New(Bounds2 bounds, float h)
  {
    Point2 s = bounds.inflate(fatFactor).size();
    int nx = (int)Math.ceil(s.x / h);
    int ny = (int)Math.ceil(s.y / h);

    return new Grid2<>(bounds, nx, ny);
  }

  public static <P extends Point2> Grid2<P> New(final P[] points, float h)
  {
    Grid2<P> g = New(bounds(points), h);

    for (P p : points)
      g.cell(p).points.add(p);
    return g;
  }

  public final Bounds2 bounds()
  {
    return new Bounds2(bounds);
  }

  public final Point2 cellSize()
  {
    return new Point2(cellSize);
  }

  public final Int2 index(Point2 p)
  {
    Point2 p1 = bounds.p1();
    int x = (int)((p.x - p1.x) / cellSize.x);
    int y = (int)((p.y - p1.y) / cellSize.y);

    return new Int2(x, y);
  }

  public final Cell<P> cell(Point2 p)
  {
    return get(index(p));
  }

  public List<P> findNeighbors(Point2 point)
  {
    assert Real.isEqual(cellSize.x, cellSize.y) : "Non-square cells";

    LinkedList<P> neighbors = new LinkedList<>();
    float h = cellSize.x;
    Int2 cidx = index(point);

    for (int i = -1; i <= 1; i++)
    {
      int x = cidx.x + i;

      if (x < 0 || x >= nx)
        continue;
      for (int j = -1; j <= 1; j++)
      {
        int y = cidx.y + j;

        if (y < 0 || y >= ny)
          continue;
        for (P p : get(x, y))
          if (Point2.distance(point, p) <= h)
            neighbors.add(p);
      }
    }
    return neighbors;
  }

  public void add(P point)
  {
    try
    {
      cell(point).points.add(point);
    }
    finally
    {
      // do nothing
    }
  }

  public void add(P[] points)
  {
    for (P p: points)
      add(p);
  }

  public void clear()
  {
    for (Cell<P> c : this)
      c.points.clear();
  }

  private static float fatFactor = 1.01f;
  private final Bounds2 bounds;
  private final Point2 cellSize;

  private static <P extends Point2> Bounds2 bounds(final P[] points)
  {
    Bounds2 b = new Bounds2();

    for (P p : points)
      b.inflate(p);
    return b;
  }

  private Grid2(final Bounds2 bounds, int nx, int ny)
  {
    super(nx, ny);
    this.bounds = new Bounds2(bounds);
    cellSize = bounds.size();
    cellSize.x /= nx;
    cellSize.y /= ny;
    initCells();
  }

  private void initCells()
  {
    Point2 p = bounds.p1();
    float x1 = p.x;

    for (int i = 0, y = 0; y < ny; y++)
    {
      for (int x = 0; x < nx; x++, i++)
      {
        data.add(new Cell<>(p, cellSize));
        p.x += cellSize.x;
      }
      p.y += cellSize.y;
      p.x = x1;
    }
  }

} // Grid2
