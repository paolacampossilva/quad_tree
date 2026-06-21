package lpoo.geom;

/**
 * 
 * @author Valentina Campos Soares
 * 
 * @author Paulo Pagliosa (base)
 */
public class Particle2
  extends Point2
{
  public float vx;
  public float vy;

  public Particle2()
  {
    // do nothing
  }

  public Particle2(float x, float y)
  {
    super(x, y);
  }

  public Particle2(Point2 p)
  {
    super(p);
  }

  public Particle2(Particle2 p)
  {
    super(p);
    vx = p.vx;
    vy = p.vy;
  }

  @Override
  public String toString()
  {
    return super.toString() + String.format( "<%.2f,%.2f>", vx, vy);
  }

} // Particle2
