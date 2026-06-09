package lpoo.geom;

/**
 *
 * @author Paulo Pagliosa
 */
public class BadGridException
  extends RuntimeException
{
  public BadGridException(int nx, int ny)
  {
    super(String.format("Bad grid size: (%d,%d)", nx, ny));
  }

} // BadGridException
