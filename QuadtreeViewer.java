import java.awt.geom.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import lpoo.geom.*;

/**
 *
 * @author Paola Campos da Silva 
 * @author Paulo Pagliosa (base/matemática de visualização)
 */
public class QuadtreeViewer<P extends Point2> extends JFrame
{
  // Construtor para árvores estáticas (usado no Main normal)
  public QuadtreeViewer(Quadtree<P> qt)
  {
    this(qt, null, null, 0, 0);
  }

  // Construtor para árvores animadas (usado no teste visual avançado)
  public QuadtreeViewer(Quadtree<P> qt, P[] points, PointUpdater<P> updater, int nmax, int lmax)
  {
    super("Quadtree Viewer Pro");
    QuadtreeControl<P> panel = new QuadtreeControl<>(qt, points);
    
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(panel, BorderLayout.CENTER);
    setSize(800, 600);
    
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

    // Configuração do Timer para Animação a ~60 FPS
    if (updater != null && points != null)
    {
      Timer timer = new Timer(17, null);
      timer.addActionListener(new ActionListener()
      {
        @Override
        public void actionPerformed(ActionEvent e)
        {
          // Atualiza as posições das partículas
          if (updater.update(points, timer.getDelay() / 1000f)) {
            // Reconstroi a Quadtree com as novas posições e manda desenhar
            panel.qt = new Quadtree<>(points, nmax, lmax);
            panel.repaint();
          }
        }
      });
      (panel.timer = timer).start();
    }
  }

  private final static class QuadtreeControl<P extends Point2> extends Canvas
  {
    Quadtree<P> qt;
    Timer timer;
    private final P[] points;
    
    // Controlo de Câmara (Pan e Zoom)
    private final Point2 size, center, offset = new Point2();
    private float scale, zoomFactor = 1;

    // Estado da busca interativa 
    private static final int SEARCH_K = 8;
    private P searchReference;
    private java.util.List<KNN.Entry<P>> searchResult;

    public QuadtreeControl(Quadtree<P> qt, P[] points)
    {
      this.qt = qt;
      this.points = points;
      
      Bounds2 b = qt.bounds();
      size = b.size();
      center = b.center();
      
      setBackground(Color.WHITE);
      
      addKeyListener(new KeyAdapter()
      {
        @Override
        public void keyTyped(KeyEvent e)
        {
          switch (e.getKeyChar())
          {
            case '+':
              zoomFactor *= 1.2f;
              repaint();
              break;
            case '-':
              zoomFactor /= 1.2f;
              repaint();
              break;
            case 'p':
            case 'P': // Tecla P para Pausar/Retomar
              if (timer != null) {
                if (timer.isRunning()) {
                  timer.stop();
                } else {
                  searchReference = null;
                  searchResult = null;
                  timer.start();
                }
                repaint();
              }
              break;
          }
        }
      });
      
      addMouseListener(new MouseAdapter()
      {
        @Override
        public void mouseClicked(MouseEvent e)
        {
          // Impede o clique enquanto está a animar (requer pausa)
          if (timer != null && timer.isRunning()) return;
          
          runSearchAt(e.getPoint());
        }
      });
      setFocusable(true);
    }

    private Point2 screenToWorld(Point p)
    {
      return (new Point2(p.x, p.y)).sub(offset).mul(1 / scale);
    }

    private Point2 transform(Point2 p)
    {
      return p.mul(scale).add(offset);
    }

    private void runSearchAt(Point screenPoint)
    {
      Point2 worldClick = screenToWorld(screenPoint);
      P nearest = null;
      float bestDist = Float.MAX_VALUE;

      // Procura o ponto mais perto do clique do mouse
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
        
      if (nearest == null) return;

      searchReference = nearest;
      searchResult = qt.findNeighbors(nearest, SEARCH_K, null).toSortedList();
      repaint();
    }

    private Shape shape(Bounds2 b)
    {
      Point2 p1 = transform(b.p1());
      Point2 s = b.size().mul(scale);
      return new Rectangle2D.Float(p1.x, p1.y, s.x, s.y);
    }

    private Shape shape(Point2 p)
    {
      p = transform(new Point2(p));
      return new Ellipse2D.Float(p.x - 3, p.y - 3, 6, 6);
    }

    private Shape shape(Point2 center, float worldRadius)
    {
      float s = worldRadius * scale;
      Point2 p = transform(new Point2(center));
      return new Ellipse2D.Float(p.x - s, p.y - s, s * 2, s * 2);
    }

    @Override
    public void paint(Graphics g)
    {
      Rectangle r = getBounds();
      // Cálculo elegante do professor para manter a câmara centrada
      scale = (float)Math.min(r.getWidth() / size.x, r.getHeight() / size.y);
      scale *= zoomFactor;
      Point2 c = (new Point2(center)).mul(scale);
      offset.x = (float)r.getCenterX() - c.x;
      offset.y = (float)r.getCenterY() - c.y;

      Graphics2D g2 = (Graphics2D) g;

      // 1. Desenha as caixas da Quadtree
      for (Quadtree.NodeData<P> node : qt)
      {
        Shape s = shape(node.bounds());
        g2.setColor(node.isEmpty() ? Color.GREEN : Color.ORANGE);
        g2.fill(s);
        g2.setColor(Color.BLACK);
        g2.draw(s);
      }

      // 2. Desenha os Pontos/Partículas (Se não for interativo, usa preto)
      if (points != null) {
        for (P p : points) drawPoint(g2, p, Color.BLACK);
      } else {
        for (Quadtree.NodeData<P> node : qt)
          for (P p : node) drawPoint(g2, p, Color.BLACK);
      }

      // 3. Destaca o resultado da busca interativa 
      if (searchResult != null && searchReference != null)
      {
        for (KNN.Entry<P> entry : searchResult)
          drawPoint(g2, entry.point, Color.RED);
          
        drawPoint(g2, searchReference, Color.BLUE);

        if (!searchResult.isEmpty())
        {
          float worstDistance = searchResult.get(searchResult.size() - 1).distance;
          Shape circle = shape(searchReference, worstDistance);
          Stroke oldStroke = g2.getStroke();

          g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{ 5f }, 0f));
          g2.setColor(Color.BLUE);
          g2.draw(circle);
          g2.setStroke(oldStroke);
        }
      }
    }

    private void drawPoint(Graphics2D g2, P p, Color defaultColor)
    {
      // Se for ColorParticle2, tenta usar a cor real da partícula 
      if (p instanceof ColorParticle2) {
        ColorParticle2 cp = (ColorParticle2) p;
        g2.setColor(new Color(cp.r, cp.g, cp.b));
      } else {
        g2.setColor(defaultColor);
      }
      g2.fill(shape(p));
    }

  } // QuadtreeControl

} // QuadtreeViewer
