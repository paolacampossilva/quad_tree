package lpoo.geom;

/**
 *
 * @author Paulo Pagliosa
 */
@FunctionalInterface
public interface PointUpdater<P extends Point2> 
{
  boolean update(P[] points, float dt);
} // PointUpdater
