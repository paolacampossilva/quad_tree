import lpoo.geom.*;
import java.util.Random;

/**
 * @author João Pedro Huppes Arenales
 * @author Paola Campos da Silva
 */
public class TestViewer
{
  public static void main(String[] args)
  {
    Console.printHeader("VISUAL VIEWER (BONUS A5)");
    
    System.out.println("   Starting interactive visual test...\n");

    int pointsTotal = 100;
    Point2[] points = new Point2[pointsTotal];
    Random rand = new Random();

    Console.info("Generating " + pointsTotal + " random points...");
    
    // Gera os pontos espalhados num domínio de 400x300
    for (int i = 0; i < pointsTotal; i++)
    {
      float x = 50 + rand.nextFloat() * 400;
      float y = 50 + rand.nextFloat() * 300;
      points[i] = new Point2(x, y);
    }

    Console.info("Building Quadtree...");
    
    // nmax = 5 e lmax = 6 (Adicionei o lmax para ficar compativel com a sua Main)
    Quadtree<Point2> tree = new Quadtree<>(points, 5, 6); 

    Console.info("Quadtree built successfully!\n");
    
    System.out.println("   [INSTRUCTIONS]");
    System.out.println("    -> Click anywhere on the canvas.");
    System.out.println("    -> The nearest point to your click will turn BLUE.");
    System.out.println("    -> Its 8 nearest neighbors will turn RED.");
    System.out.println("    -> A dashed circle will show the search radius.");
    System.out.println("    -> Use the '+' and '-' keys to zoom in and out.");
    
    Console.printSubHeader("Opening Window...");

    // Abre a interface gráfica
    new QuadtreeViewer<>(tree);

    System.out.println("   >> Screen was opened successfully! Interact with the window.\n");
  }
} // TestViewer
