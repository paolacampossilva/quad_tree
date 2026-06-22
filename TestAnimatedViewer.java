import lpoo.geom.*;
import java.util.Random;

/**
 * @author João Pedro Huppes Arenales
 * @author Paola Campos da Silva
 */
public class TestAnimatedViewer
{
  private static final float EPSILON = 1e-3f;
  private static final float TIME_SCALE = 3f;

  public static void main(String[] args)
  {
    Console.printHeader("ANIMATED VIEWER (BONUS A5)");
    
    System.out.println("   Starting interactive animated test...\n");

    int totalParticles = 400; // 400 partículas a moverem-se
    float boundsSize = 100f;
    ColorParticle2[] particles = new ColorParticle2[totalParticles];
    Random rand = new Random();

    // Paleta de cores para o visual ficar bonito no ecrã
    int[][] palette = { {255,0,0}, {0,200,0}, {0,0,255}, {255,165,0}, {128,0,128} };

    Console.info("Generating " + totalParticles + " random particles...");

    for (int i = 0; i < totalParticles; i++)
    {
      float x = rand.nextFloat() * boundsSize;
      float y = rand.nextFloat() * boundsSize;
      int[] c = palette[rand.nextInt(palette.length)];
      
      ColorParticle2 p = new ColorParticle2(x, y, c[0], c[1], c[2]);
      
      // Dá velocidade aleatória a cada partícula
      p.vx = (rand.nextFloat() * 2f - 1f) * 2f; 
      p.vy = (rand.nextFloat() * 2f - 1f) * 2f;
      particles[i] = p;
    }

    int nmax = 4;
    int lmax = 6;
    
    Console.info("Building initial Quadtree...");

    // Cria a Quadtree inicial
    Quadtree<ColorParticle2> tree = new Quadtree<>(particles, nmax, lmax);
    Point2 p1 = tree.bounds().p1();
    Point2 p2 = tree.bounds().p2();

    Console.info("Quadtree built successfully!\n");
    
    System.out.println("   [INSTRUCTIONS]");
    System.out.println("    -> The particles will move and the Quadtree will rebuild dynamically.");
    System.out.println("    -> Press 'P' to PAUSE or RESUME the animation.");
    System.out.println("    -> When paused, click anywhere to run the KNN search (k=8).");
    System.out.println("    -> Use the '+' and '-' keys to zoom in and out.");
    
    Console.printSubHeader("Opening Window...");

    // Lança a janela animada passando a Expressão Lambda do Updater
    new QuadtreeViewer<>(tree, particles, (ColorParticle2[] pts, float dt) -> 
    {
      dt *= TIME_SCALE;
      for (ColorParticle2 p : pts)
      {
        // Atualiza a posição baseada na velocidade (Cinemática simples)
        p.x += p.vx * dt;
        p.y += p.vy * dt;
        
        // Verifica colisões com os limites do mundo (ressalto elástico)
        collide(p1, p2, p);
      }
      return true; // Retorna true para informar que o ecrã deve ser redesenhado
    }, nmax, lmax);
    
    System.out.println("   >> Screen was opened successfully! Interact with the window.\n");
  }

  /**
   * Faz a partícula inverter a velocidade se bater nas paredes
   */
  private static void collide(Point2 p1, Point2 p2, Particle2 p)
  {
    if (p.x <= p1.x + EPSILON) {
      p.x = p1.x + EPSILON;
      p.vx *= -1;
    } else if (p.x >= p2.x - EPSILON) {
      p.x = p2.x - EPSILON;
      p.vx *= -1;
    }

    if (p.y <= p1.y + EPSILON) {
      p.y = p1.y + EPSILON;
      p.vy *= -1;
    } else if (p.y >= p2.y - EPSILON) {
      p.y = p2.y - EPSILON;
      p.vy *= -1;
    }
  }
} // TestAnimatedViewer