package lpoo.geom;

import java.util.*;

/**
 *
 * @author Paulo Pagliosa
 */
public interface PointUpdater<P extends Point2> 
{
  boolean update(P[] points, float dt);
} // PointUpdater
