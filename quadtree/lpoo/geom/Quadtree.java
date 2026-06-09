package lpoo.geom;

import java.util.*;

/**
 *
 * @author Paulo Pagliosa
 */
public class Quadtree
  implements Iterable<Quadtree.NodeData>
{
  public static class NodeData
    implements Iterable<Point2>
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
    public final Iterator<Point2> iterator()
    {
      return points.iterator();
    }
  
    public final int pointCount()
    {
      return points.size();
    }
    
    public final int depth()
    {
      return depth;
    }
  
    protected final Bounds2 bounds;
    protected final LinkedList<Point2> points = new LinkedList<>();
    protected final int depth;
  
    protected NodeData(final Bounds2 bounds)
    {
      this.bounds = bounds;
      depth = 0;
    }
  
    protected NodeData(Point2 p, Point2 size, int depth)
    {
      bounds = new Bounds2(p, size);
      this.depth = depth;
    }
  
  } // NodeData
  
  public final int pointsPerNode;

  public Quadtree(final Point2[] points, int pointsPerNode)
  {
    this(bounds(points), pointsPerNode);
    for (Point2 p : points)
      root.add(p);
    split(root);
  }

  public final Bounds2 bounds()
  {
    return root.bounds();
  }

  public final int pointCount()
  {
    return pointCount(root);
  }

  public final int size()
  {
    return nodeCount;
  }

  public final int leafCount()
  {
    return leafCount;
  }

  @Override
  public Iterator<NodeData> iterator()
  {
    return new QuadtreeLeafIterator(root);
  }

  public static final float fatFactor = 1.01f;
  public static final int minPointsPerNode = 5;
  public static final int maxDepth = 8;

  static class Node extends NodeData
  {
    Node(final Bounds2 bounds)
    {
      super(bounds);
    }

    Node(Point2 p, Point2 size, int depth)
    {
      super(p, size, depth);
    }

    final boolean isLeaf()
    {
      return children == null;
    }

    final void clear()
    {
      points.clear();
    }

    final void add(Point2 p)
    {
      points.add(p);
    }

    Node[] children;

  } // Node

  private Node root;
  private int nodeCount;
  private int leafCount;

  private Quadtree(final Bounds2 bounds, int pointsPerNode)
  {
    this.pointsPerNode = Math.max(minPointsPerNode, pointsPerNode);
    root = new Node(new Bounds2(bounds).inflate(fatFactor));
    nodeCount = 1;
  }

  private void split(Node node)
  {
    if (node.pointCount() <= pointsPerNode || node.depth == maxDepth)
      return;
    {
      Point2 p = node.bounds.p1();
      Point2 s = node.bounds.size().mul(0.5f);
      int d = node.depth + 1;

      node.children = new Node[4];
      node.children[0] = new Node(p, s, d);
      node.children[1] = new Node(new Point2(p.x + s.x, p.y), s, d);
      node.children[2] = new Node(new Point2(p.x + s.x, p.y + s.y), s, d);
      node.children[3] = new Node(new Point2(p.x, p.y + s.y), s, d);
      leafCount += 3;
      nodeCount += 4;
    }
    for (Point2 p : node)
      for (int i = 0; i < 4; i++)
        if (node.children[i].contains(p))
        {
          node.children[i].add(p);
          break;
        }
    node.clear();
    for (int i = 0; i < 4; i++)
      split(node.children[i]);
  }

  private static Bounds2 bounds(final Point2[] points)
  {
    Bounds2 bounds = new Bounds2();

    for (Point2 p : points)
      bounds.inflate(p);
    return bounds;
  }

  private static int pointCount(Node node)
  {
    if (node.isLeaf())
      return node.pointCount();

    int count = 0;

    for (int i = 0; i < 4; i++)
      count += pointCount(node.children[i]);
    return count;
  }

} // Quadtree

final class QuadtreeLeafIterator
  implements Iterator<Quadtree.NodeData>
{
  @Override
  public boolean hasNext()
  {
    return !stack.empty();
  }

  @Override
  public Quadtree.NodeData next()
  {
    Quadtree.Node node = stack.pop();

    if (!node.isLeaf())
      for (int i = 4; i > 0;)
        stack.push(node.children[--i]);
    return node;
  }

  QuadtreeLeafIterator(Quadtree.Node root)
  {
    stack = new Stack<>();
    stack.push(root);
  }

  private Stack<Quadtree.Node> stack;

} // QuadtreeLeafIterator
