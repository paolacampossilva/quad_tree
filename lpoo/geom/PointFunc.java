package lpoo.geom;

/**
 *
 * @author João Pedro Huppes Arenales
 */
@FunctionalInterface 
public interface PointFunc <P extends Point2> {
  boolean run(P point);
} 
