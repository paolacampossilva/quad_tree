package lpoo.geom;

@FunctionalInterface 
public interface PointFunc <P extends Point2> {
    boolean run(P point);
} 
