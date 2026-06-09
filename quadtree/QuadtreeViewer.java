import java.awt.geom.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import lpoo.geom.*;

/**
 *
 * @author Paulo Pagliosa
 */
public class QuadtreeViewer extends JFrame
{
  public QuadtreeViewer(Quadtree qt)
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

  private void initViewer(Quadtree qt)
  {
    getContentPane().add(new QuadtreeControl(qt), BorderLayout.CENTER);
  }

  private final static class QuadtreeControl extends Canvas
  {
    private Quadtree qt;
    private float scale;

    // Constructor
    public QuadtreeControl(Quadtree qt)
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

      for (Quadtree.NodeData node : qt)
      {
        Shape r = shape(node.bounds());

        g2.setColor(node.isEmpty() ? Color.GREEN : Color.ORANGE);
        g2.fill(r);
        g2.setColor(Color.BLACK);
        g2.draw(r);
        for (Point2 p : node)
          drawPoint(g2, p);
      }
    }

    void drawPoint(Graphics2D g2, Point2 p)
    {
      g2.fill(shape(p.mul(scale)));
    }

  } // QuadtreeControl

} // QuadtreeViewer
