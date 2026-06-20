import java.awt.geom.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import lpoo.geom.*;

/**
 *
 * @author Paulo Pagliosa
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
    getContentPane().add(new QuadtreeControl<P>(qt), BorderLayout.CENTER);
  }

  private final static class QuadtreeControl <P extends Point2> extends Canvas
  {
    private Quadtree<P> qt;
    private float scale;

    private Point2 queryPoint = null;
    private java.util.List<?> highlightedNeighbors = null;

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

      // bonus: captura o clique do mouse, executando a busca e atualizando a tela
      addMouseListener(new MouseAdapter()
      {
        @Override
        @SuppressWarnings("unchecked")
        public void mouseClicked(MouseEvent evt)
        {
          float mx = evt.getX() / scale;
          float my = evt.getY() / scale;
          queryPoint = new Point2(mx,my);
          
          // busca pelos 5 vizinhos mais próximos (k=5) usando nosso método
          KNN<P> knn = qt.findNeighbors((P) queryPoint, 5, null);
          if (knn != null)
            highlightedNeighbors = knn.toSortedList();
          
          repaint();
        }

      });
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
        {
          // bonus: verifica se o ponto atual faz parte dos vizinhos mais próximos
          boolean isNeighbor = false;
          if (highlightedNeighbors != null)
          {
            for (Object obj : highlightedNeighbors)
            {
              KNN.Entry<?> entry = (KNN.Entry<?>) obj;
              if (entry.point.equals(p))
              {
                isNeighbor = true;
                break;
              }

            }

          }

          if (isNeighbor)
            g2.setColor(Color.RED);
          else
            g2.setColor(Color.BLACK);

          drawPoint(g2, p);
        }
          
      }
    
    // bonus: desenha o ponto azul onde o usuário clicou para indicar o alvo da busca
    if (queryPoint != null){
      g2.setColor(Color.BLUE);
      g2.fill(shape(queryPoint.mul(scale)));
    } 

    }

    void drawPoint(Graphics2D g2, Point2 p)
    {
      g2.fill(shape(p.mul(scale)));
    }

  } // QuadtreeControl

} // QuadtreeViewer
