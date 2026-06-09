package lpoo.geom;

import java.util.*;

/**
 *
 * @author Paulo Pagliosa
 */
public abstract class Grid2Base<T>
  implements Iterable<T>
{
  public final Int2 dim()
  {
    return new Int2(nx, ny);
  }

  public final boolean hasElement(int x, int y)
  {
    return x >= 0 && x < nx && y >= 0 && y < ny;
  }

  public final boolean hasElement(Int2 index)
  {
    return hasElement(index.x, index.y);
  }

  public T get(int x, int y)
  {
    if (!hasElement(x, y))
      throw new IndexOutOfBoundsException();
    return data.get(x + y * nx);
  }

  public final T get(Int2 index)
  {
    return get(index.x, index.y);
  }

  public void set(int x, int y, T element)
  {
    if (!hasElement(x, y))
      throw new IndexOutOfBoundsException();
    data.set(x + y * nx, element);
  }

  public final void set(Int2 index, T element)
  {
    set(index.x, index.y, element);
  }

  @Override
  public final Iterator<T> iterator()
  {
    return data.iterator();
  }

  protected final int nx;
  protected final int ny;
  protected ArrayList<T> data;

  protected Grid2Base(int nx, int ny)
  {
    if (nx <= 0 || ny <= 0)
      throw new BadGridException(nx, ny);
    this.nx = nx;
    this.ny = ny;
    this.data = new ArrayList<>(nx * ny);
  }

  protected Grid2Base(int n)
  {
    this(n, n);
  }

} // Grid2Base
