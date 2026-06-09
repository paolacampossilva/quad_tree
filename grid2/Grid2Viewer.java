import java.awt.geom.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import lpoo.geom.*;

/**
 *
 * @author Paulo Pagliosa
 */
public class Grid2Viewer<P extends Point2>
  extends JFrame
{
  public Grid2Viewer(Grid2<P> grid, P[] points, PointUpdater<P> updater)
  {
    super("Grid2 Viewer");

    Grid2Panel<P> panel = new Grid2Panel<>(grid, points);

    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(panel , BorderLayout.CENTER);
    setSize(640, 480);
    addWindowListener(new WindowAdapter()
    {
      @Override
      public void windowClosing(WindowEvent e)
      {
        System.exit(0);
      }
    });
    setVisible(true);
    toFront();
    if (updater != null)
    {
      Timer timer = new Timer(17, null);

      timer.addActionListener(new ActionListener()
      {
        @Override
        public void actionPerformed(ActionEvent e)
        {
          if (updater.update(points, timer.getDelay() / 1000f))
            panel.repaint();
        }
      });
      (panel.timer = timer).start();
    }
  }

  private final static class Grid2Panel<P extends Point2>
    extends Canvas
  {
    public Grid2Panel(Grid2<P> grid, P[] points)
    {
      this.grid = grid;
      this.points = points;

      Bounds2 b = grid.bounds();

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
              zoomFactor *= 1.2;
              repaint();
              break;
            case '-':
              zoomFactor /= 1.2;
              repaint();
              break;
            case 'p':
              if (timer != null)
                if (timer.isRunning())
                  timer.stop();
                else
                {
                  cidx.x = cidx.y = -1;
                  cp = null;
                  timer.start();
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
          if (timer != null && timer.isRunning())
            return;

          Point2 p = screenToWorld(e.getPoint());
          Int2 i = grid.index(p);

          if (grid.hasElement(i) && grid.get(i).contains(p))
          {
            cidx = i;
            neighbors = grid.findNeighbors(cp = p);
            repaint();
          }
        }
      });
    }

    @Override
    public void paint(Graphics g)
    {
      Rectangle r = getBounds();

      scale = (float)Math.min(r.getWidth() / size.x, r.getHeight() / size.y);
      scale *= zoomFactor;

      Point2 c = (new Point2(center)).mul(scale);

      offset.x = (float)r.getCenterX() - c.x;
      offset.y = (float)r.getCenterY() - c.y;

      Graphics2D g2 = (Graphics2D)g;
      Int2 dim = grid.dim();

      for (int y = 0; y < dim.y; y++)
        for (int x = 0; x < dim.x; x++)
        {
          Grid2.Cell<P> cell = grid.get(x, y);
          Shape s = shape(cell.bounds());

          if (cidx.x == x && cidx.y == y)
            g2.setColor(Color.LIGHT_GRAY);
          else
            g2.setColor(cell.isEmpty() ? Color.GREEN : Color.ORANGE);
          g2.fill(s);
          g2.setColor(Color.BLACK);
          g2.draw(s);
        }

      Shape s = shape(grid.bounds());

      g2.setColor(Color.RED);
      g2.setStroke(new BasicStroke(2));
      g2.draw(s);
      drawPoints(g2, Color.BLACK);
      if (cp != null)
      {
        drawNeighbors(g2);
        g2.setColor(Color.MAGENTA);
        drawPoint(g2, cp);
      }
    }

    Timer timer;
    private final Grid2<P> grid;
    private final Point2[] points;
    private java.util.List<P> neighbors;
    private final Point2 size, center, offset = new Point2();
    private float scale, zoomFactor = 1;
    private Int2 cidx = new Int2(-1, -1);
    private Point2 cp;

    private Point2 screenToWorld(Point p)
    {
      return (new Point2(p.x, p.y)).sub(offset).mul(1 / scale);
    }

    private Point2 transform(Point2 p)
    {
      return p.mul(scale).add(offset);
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

    private void drawPoints(Graphics2D g2, Color color)
    {
      g2.setColor(color);
      for (Point2 p : points)
        drawPoint(g2, p);
    }

    private void drawPoint(Graphics2D g2, Point2 p)
    {
      g2.fill(shape(p));
    }

    private Shape shape(Point2 center, Point2 size)
    {
      Point2 s = size.mul(scale);
      Point2 p = transform(new Point2(center));

      return new Ellipse2D.Float(p.x - s.x, p.y - s.y, s.x * 2, s.y * 2);
    }

    private void drawNeighbors(Graphics2D g2)
    {
      g2.setPaint(new Color(0, 0, 1, 0.25f));
      g2.fill(shape(cp, grid.cellSize()));
      g2.setColor(Color.WHITE);
      for (Point2 p : neighbors)
        drawPoint(g2, p);
    }

  } // Grid2Panel

} // Grid2Viewer
