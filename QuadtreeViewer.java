import java.awt.geom.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import lpoo.geom.*;

/**
 *
 * @author Paola Campos da Silva 
 * 
 * @author Paulo Pagliosa (base)
 */
public class QuadtreeViewer<P extends Point2> extends JFrame
{
  public QuadtreeViewer(Quadtree<P> qt)
  {
    super("Quadtree Viewer");
    getContentPane().setLayout(new BorderLayout());
    initViewer(qt);
    setSize(640, 480);
    addWindowListener(new WindowAdapter()
    {
      @Override
      public void windowClosing(WindowEvent evt)
      {
        System.exit(0);
      }
    });
    setVisible(true);
    toFront();
  }

  private void initViewer(Quadtree<P> qt)
  {
    getContentPane().add(new QuadtreeControl<>(qt), BorderLayout.CENTER);
  }

  private final static class QuadtreeControl<P extends Point2> extends Canvas
  {
    private Quadtree<P> qt;
    private float scale;

    // -------- estado da busca interativa (bonus A5) --------
    private static final int SEARCH_K = 8;
    private P searchReference;
    private java.util.List<KNN.Entry<P>> searchResult;

    // Constructor
    public QuadtreeControl(Quadtree<P> qt)
    {
      this.qt = qt;
      this.scale = 1f;
      setBackground(Color.WHITE);
      addKeyListener(new KeyAdapter()
      {
        @Override
        public void keyTyped(KeyEvent evt)
        {
          switch (evt.getKeyChar())
          {
            case '+':
              scale *= 1.1;
              repaint();
              break;

            case '-':
              scale /= 1.1;
              repaint();
              break;
          }
        }
      });
      addMouseListener(new MouseAdapter()
      {
        @Override
        public void mouseClicked(MouseEvent evt)
        {
          runSearchAt(evt.getX(), evt.getY());
        }
      });
      setFocusable(true);
    }

    /**
     * Converte o clique (em pixels de tela) para coordenadas do mundo,
     * encontra o ponto da arvore mais proximo do clique e usa esse
     * ponto como referencia para uma busca KNN, guardando o resultado
     * para ser destacado em paint().
     */
    private void runSearchAt(int screenX, int screenY)
    {
      Point2 worldClick = new Point2(screenX / scale, screenY / scale);
      P nearest = null;
      float bestDist = Float.MAX_VALUE;

      for (Quadtree.NodeData<P> node : qt)
        for (P p : node)
        {
          float d = Point2.distance(worldClick, p);

          if (d < bestDist)
          {
            bestDist = d;
            nearest = p;
          }
        }
      if (nearest == null)
        return;

      searchReference = nearest;
      searchResult = qt.findNeighbors(nearest, SEARCH_K, null).toSortedList();
      repaint();
    }

    private Shape shape(Bounds2 bounds)
    {
      Point2 p = bounds.p1().mul(scale);
      Point2 s = bounds.size().mul(scale);

      return new Rectangle2D.Float(p.x, p.y, s.x, s.y);
    }

    private Shape shape(Point2 p)
    {
      return new Ellipse2D.Float(p.x - 2, p.y - 2, 4, 4);
    }

    private Shape shape(Point2 p, float radius)
    {
      return new Ellipse2D.Float(p.x - radius, p.y - radius, radius * 2, radius * 2);
    }

    @Override
    public void paint(Graphics g)
    {
      Graphics2D g2 = (Graphics2D) g;

      for (Quadtree.NodeData<P> node : qt)
      {
        Shape r = shape(node.bounds());

        g2.setColor(node.isEmpty() ? Color.GREEN : Color.ORANGE);
        g2.fill(r);
        g2.setColor(Color.BLACK);
        g2.draw(r);
        for (P p : node)
          drawPoint(g2, p, Color.BLACK);
      }

      // bonus A5: destaca o resultado da ultima busca KNN feita por clique
      if (searchResult != null)
      {
        for (KNN.Entry<P> entry : searchResult)
          drawPoint(g2, entry.point, Color.RED);
        if (searchReference != null)
        {
          drawPoint(g2, searchReference, Color.BLUE);

          // circulo tracejado mostrando o alcance ate o vizinho mais distante do KNN
          if (!searchResult.isEmpty())
          {
            float worstDistance = searchResult.get(searchResult.size() - 1).distance;
            Point2 center = searchReference.mul(scale);
            Shape circle = shape(center, worstDistance * scale);
            Stroke oldStroke = g2.getStroke();

            g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT,
              BasicStroke.JOIN_MITER, 10f, new float[]{ 4f }, 0f));
            g2.setColor(Color.BLUE);
            g2.draw(circle);
            g2.setStroke(oldStroke);
          }
        }
      }
    }

    void drawPoint(Graphics2D g2, Point2 p, Color color)
    {
      g2.setColor(color);
      g2.fill(shape(p.mul(scale)));
    }

  } // QuadtreeControl

} // QuadtreeViewer
