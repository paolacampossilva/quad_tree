package lpoo.geom;

/**
 * 
 * @author Valentina Campos Soares
 */
public class ColorParticle2 extends Particle2
{
  public int r;
  public int g;
  public int b;

  public ColorParticle2(float x, float y, int r, int g, int b)
  {
    super(x, y);
    this.r = r;
    this.g = g;
    this.b = b;
  }

  @Override
  public String toString()
  {
    return super.toString() + String.format(" RGB(%d,%d,%d)", r, g, b);
  }
} // ColorParticle2
