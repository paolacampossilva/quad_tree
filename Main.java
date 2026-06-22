import java.io.*;
import java.util.*;
import lpoo.geom.*;

/**
 *
 * @author Paola Campos da Silva
 * @author Paulo Pagliosa (base)
 */
public class Main
{
  // Leitura / geracao de pontos

  static int toInteger(String s)
  {
    return Integer.parseInt(s);
  }

  static float toFloat(String s)
  {
    return Float.parseFloat(s);
  }

  static Point2[] readPointsFromFile(String filename)
    throws IOException
  {
    try (BufferedReader is = new BufferedReader(new FileReader(filename)))
    {
      StringTokenizer t = new StringTokenizer(is.readLine());
      Point2 domain = new Point2(toFloat(t.nextToken()), toFloat(t.nextToken()));

      System.out.println();
      Console.info("Domain size read from file:");
      domain.print("      -> ");

      int n = toInteger(is.readLine());
      Point2[] points = new Point2[n];

      for (int i = 0; i < n; i++)
      {
        t = new StringTokenizer(is.readLine());
        points[i] = new Point2(toFloat(t.nextToken()), toFloat(t.nextToken()));
      }
      Console.info(String.format("%d points successfully read.", n));
      return points;
    }
  }

  static Point2[] randomPoints(Point2 domain, int n)
  {
    Random rng = new Random();
    Point2[] points = new Point2[n];

    for (int i = 0; i < n; i++)
      points[i] = new Point2(
        rng.nextFloat() * domain.x,
        rng.nextFloat() * domain.y);
    return points;
  }

  // Paleta fixa usada para colorir particulas
  static final int[][] PALETTE =
  {
    { 255, 0, 0 },   // Red
    { 0, 255, 0 },   // Green
    { 0, 0, 255 },   // Blue
    { 255, 255, 0 }, // Yellow
  };

  static String colorName(int[] rgb)
  {
    if (rgb[0] == 255 && rgb[1] == 0 && rgb[2] == 0) return "RED";
    if (rgb[0] == 0 && rgb[1] == 255 && rgb[2] == 0) return "GREEN";
    if (rgb[0] == 0 && rgb[1] == 0 && rgb[2] == 255) return "BLUE";
    if (rgb[0] == 255 && rgb[1] == 255 && rgb[2] == 0) return "YELLOW";
    return String.format("RGB(%d,%d,%d)", rgb[0], rgb[1], rgb[2]);
  }

  static ColorParticle2[] randomParticles(Point2 domain, int n)
  {
    Random rng = new Random();
    ColorParticle2[] particles = new ColorParticle2[n];

    for (int i = 0; i < n; i++)
    {
      int[] c = PALETTE[rng.nextInt(PALETTE.length)];
      particles[i] = new ColorParticle2(
        rng.nextFloat() * domain.x,
        rng.nextFloat() * domain.y,
        c[0], c[1], c[2]);
    }
    return particles;
  }

  // Filtro de cor (lambda)

  static PointFunc<ColorParticle2> sameColorFilter(int r, int g, int b)
  {
    return p -> p.r == r && p.g == g && p.b == b;
  }

  // Métodos de teste (A4)

  static <P extends Point2> void testKNNSearch(
    P[] points, Quadtree<P> qt, int index, int k, PointFunc<P> filter)
  {
    P reference = points[index];

    Console.printSubHeader("KNN SEARCH RESULT");
    System.out.println("   Reference point (Index " + index + "): " + reference);
    System.out.println("   K: " + k + "\n");

    KNN<P> knn = qt.findNeighbors(reference, k, filter);

    List<KNN.Entry<P>> sortedResults = knn.toSortedList();

    for (KNN.Entry<P> entry : sortedResults) 
    {
      System.out.printf("      >> %s | Distance: %.4f%n", 
        entry.point.toString(), entry.distance);
    }

    System.out.println("\n   >> Total neighbors found: " + knn.size());
  }

  static <P extends Point2> void testRadiusSearch(
    P[] points, Quadtree<P> qt, int index, float radius, PointFunc<P> filter)
  {
    P reference = points[index];

    Console.printSubHeader("RADIUS SEARCH RESULT");
    System.out.println("   Reference point (Index " + index + "): " + reference);
    System.out.println("   Search radius: " + radius + "\n");

    // Classe auxiliar local para guardar o ponto e a sua distância
    class NeighborResult {
      P point;
      float distance;
      NeighborResult(P point, float distance) {
        this.point = point;
        this.distance = distance;
      }
    } // NeighborResult
    
    // Lista para coletar todos os pontos encontrados no raio
    List<NeighborResult> foundNeighbors = new ArrayList<>();

    long count = qt.forEachNeighbor(reference, radius, p ->
    {
      float d = Point2.distance(reference, p);
      foundNeighbors.add(new NeighborResult(p, d));
      return true; // Continua a busca
    }, filter);

    // Ordena a lista da menor para a maior distância
    foundNeighbors.sort((a, b) -> Float.compare(a.distance, b.distance));

    for (NeighborResult res : foundNeighbors) {
      System.out.printf("      >> %s | Distance: %.4f%n", res.point.toString(), res.distance);
    }

    System.out.println("\n   >> Total points found within radius: " + count);
  }

  // Menu principal

  public static void main(String[] args)
  {
    Console.printHeader("QUADTREE TEST SUITE");
    
    try
    {
      System.out.println("   Select the data type to test:");
      System.out.println("    [p] Points (Basic 2D Coordinates)");
      System.out.println("    [c] Colored Particles (Points + RGB Data)");
      System.out.println();
      
      char kind = Console.readOption("Your choice", "pc");

      if (kind == 'p')
        runForPoints();
      else
        runForParticles();
    }
    catch (Exception e)
    {
      Console.error(e.getMessage());
    }
  }

  // fluxo para Point2

  static void runForPoints() throws IOException
  {
    Console.printSubHeader("POINT CONFIGURATION");
    Point2[] points = loadPoints();
    
    System.out.println();
    int nmax = Console.readInt("Max points per node before subdividing (nmax)");
    int lmax = Console.readInt("Max tree depth (lmax)");
    
    System.out.println();
    Console.info("Building Quadtree...");
    Quadtree<Point2> qtree = new Quadtree<>(points, nmax, lmax);

    Console.info("Quadtree built successfully!");
    qtree.bounds().print("      -> Bounds: ");
    System.out.printf("      -> Total Nodes: %d%n      -> Leaf Nodes: %d%n", qtree.size(), qtree.leafCount());

    searchMenu(points, qtree, null);

    System.out.println();
    if (Console.readOption("Open Quadtree viewer window? (y/n)", "yn") == 'y')
      new QuadtreeViewer<>(qtree);
      
    Console.printHeader("END OF EXECUTION");
  }

  static Point2[] loadPoints() throws IOException
  {
    System.out.println("\n   Select data source:");
    System.out.println("    [f] Read from file");
    System.out.println("    [r] Generate random points\n");
    char src = Console.readOption("Source", "fr");

    if (src == 'f')
    {
      System.out.println();
      String fn = Console.readString("File name");
      return readPointsFromFile(fn);
    }
    else
    {
      System.out.println();
      float w = Console.readFloat("Domain width");
      float h = Console.readFloat("Domain height");
      int n = Console.readInt("Number of points to generate");
      return randomPoints(new Point2(w, h), n);
    }
  }

  // fluxo para ColorParticle2

  static void runForParticles()
  {
    Console.printSubHeader("COLORED PARTICLES CONFIGURATION");
    System.out.println();
    
    float w = Console.readFloat("Domain width");
    float h = Console.readFloat("Domain height");
    int n = Console.readInt("Number of particles to generate");

    System.out.println();
    Console.info("Generating random particles...");
    ColorParticle2[] particles = randomParticles(new Point2(w, h), n);
    
    System.out.println();
    int nmax = Console.readInt("Max points per node before subdividing (nmax)");
    int lmax = Console.readInt("Max tree depth (lmax)");
    
    System.out.println();
    Console.info("Building Quadtree...");
    Quadtree<ColorParticle2> qtree = new Quadtree<>(particles, nmax, lmax);

    Console.info("Quadtree built successfully!");
    qtree.bounds().print("      -> Bounds: ");
    System.out.printf("      -> Total Nodes: %d%n      -> Leaf Nodes: %d%n", qtree.size(), qtree.leafCount());

    PointFunc<ColorParticle2> filter = chooseColorFilter();

    searchMenu(particles, qtree, filter);

    System.out.println();
    if (Console.readOption("Open Quadtree viewer window? (y/n)", "yn") == 'y')
      new QuadtreeViewer<>(qtree);
      
    Console.printHeader("END OF EXECUTION");
  }

  static PointFunc<ColorParticle2> chooseColorFilter()
  {
    Console.printSubHeader("FILTER SETTINGS");
    System.out.println();
    
    if (Console.readOption("Enable color filter? (y/n)", "yn") != 'y') {
      System.out.println("\n   -> [Filter Disabled] Searching all particles.");
      return null;
    }

    System.out.println("\n   Available colors:");
    System.out.println("    [0] Red\n    [1] Green\n    [2] Blue\n    [3] Yellow\n");
    int choice = Console.readInt("Choose color index (0-3)");

    if (choice < 0 || choice >= PALETTE.length)
    {
      Console.error("Invalid color index. Filter disabled");
      return null;
    }

    int[] c = PALETTE[choice];
    System.out.println("\n   -> [Filter Enabled] Only searching for " + colorName(c) + " particles.");
    return sameColorFilter(c[0], c[1], c[2]);
  }

  // menu de buscas (KNN / raio)

  static <P extends Point2> void searchMenu(P[] points, Quadtree<P> qtree, PointFunc<P> filter)
  {
    for (;;)
    {
      Console.printHeader("INTERACTIVE SEARCH MENU");
      System.out.println("   [k] KNN Search (K-Nearest Neighbors)");
      System.out.println("   [r] Radius Search");
      System.out.println("   [q] Quit\n");
      
      char op = Console.readOption("Choose search operation", "krq");

      if (op == 'q') break;

      System.out.println();
      int index = readValidIndex(points.length);
      if (index < 0) continue; 

      if (op == 'k')
      {
        int k = Console.readInt("Enter 'k' (number of neighbors)");
        testKNNSearch(points, qtree, index, k, filter);
      }
      else
      {
        float radius = Console.readFloat("Enter search radius");
        testRadiusSearch(points, qtree, index, radius, filter);
      }
      
      System.out.println();
      Console.readString("Press ENTER to continue");
    }
  }

  static int readValidIndex(int length)
  {
    int index = Console.readInt("Enter reference point index (0 to " + (length - 1) + ")");

    if (index < 0 || index >= length)
    {
      Console.error("Index out of range");
      return -1;
    }
    return index;
  }

} // Main
