import java.io.*;
import java.util.*;
import lpoo.geom.*;

/**
 *
 * @author Paola Campos da Silva
 * 
 * @author Paulo Pagliosa (base)
 */
public class Main
{
  // ---------------------------------------------------------------
  // Leitura / geracao de pontos
  // ---------------------------------------------------------------

  static int toInteger(String s)
  {
    return Integer.parseInt(s);
  }

  static float toFloat(String s)
  {
    return Float.parseFloat(s);
  }

  /**
   * Le pontos de um arquivo texto no formato usado por points.txt:
   *   linha 1: largura altura (tamanho do dominio)
   *   linha 2: n (quantidade de pontos)
   *   linhas seguintes: x y (um ponto por linha)
   */
  static Point2[] readPointsFromFile(String filename)
    throws IOException
  {
    try (BufferedReader is = new BufferedReader(new FileReader(filename)))
    {
      StringTokenizer t = new StringTokenizer(is.readLine());
      Point2 domain = new Point2(toFloat(t.nextToken()), toFloat(t.nextToken()));

      domain.print("Domain size: ");

      int n = toInteger(is.readLine());
      Point2[] points = new Point2[n];

      for (int i = 0; i < n; i++)
      {
        t = new StringTokenizer(is.readLine());
        points[i] = new Point2(toFloat(t.nextToken()), toFloat(t.nextToken()));
      }
      System.out.printf("%d points read%n", n);
      return points;
    }
  }

  /**
   * Gera n pontos aleatorios dentro de [0, domain.x] x [0, domain.y].
   */
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

  // Paleta fixa usada para colorir particulas geradas aleatoriamente.
  // (255,0,0) vermelho, (0,255,0) verde, (0,0,255) azul, (255,255,0) amarelo
  static final int[][] PALETTE =
  {
    { 255, 0, 0 },
    { 0, 255, 0 },
    { 0, 0, 255 },
    { 255, 255, 0 },
  };

  static String colorName(int[] rgb)
  {
    if (rgb[0] == 255 && rgb[1] == 0 && rgb[2] == 0) return "red";
    if (rgb[0] == 0 && rgb[1] == 255 && rgb[2] == 0) return "green";
    if (rgb[0] == 0 && rgb[1] == 0 && rgb[2] == 255) return "blue";
    if (rgb[0] == 255 && rgb[1] == 255 && rgb[2] == 0) return "yellow";
    return String.format("rgb(%d,%d,%d)", rgb[0], rgb[1], rgb[2]);
  }

  /**
   * Gera n particulas aleatorias dentro de [0, domain.x] x [0, domain.y],
   * cada uma com uma cor sorteada da paleta fixa.
   */
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

  // ---------------------------------------------------------------
  // Filtro de cor (lambda) - pedido explicitamente em A4
  // ---------------------------------------------------------------

  /**
   * Devolve um PointFunc que aceita somente particulas com a cor dada,
   * descartando as demais do KNN/busca por raio. ColorParticle2 expoe
   * os campos r, g, b diretamente (sem metodo isColor), entao a
   * comparacao e feita aqui mesmo, dentro do lambda.
   */
  static PointFunc<ColorParticle2> sameColorFilter(int r, int g, int b)
  {
    return p -> p.r == r && p.g == g && p.b == b;
  }

  // ---------------------------------------------------------------
  // Metodos de teste (parte central do A4)
  // ---------------------------------------------------------------

  /**
   * Imprime o ponto de referencia (indicado por index) e os k vizinhos
   * mais proximos encontrados via findNeighbors, com suas distancias.
   * KNN.toSortedList() devolve os pares (Entry) ja ordenados da menor
   * para a maior distancia.
   */
  static <P extends Point2> void testKNN(
    P[] points, Quadtree<P> qt, int index, int k, PointFunc<P> filter)
  {
    P reference = points[index];

    System.out.println("---- KNN test ----");
    System.out.println("Reference point [" + index + "]: " + reference);
    System.out.println("k = " + k);

    KNN<P> result = qt.findNeighbors(reference, k, filter);
    List<KNN.Entry<P>> sorted = result.toSortedList();

    System.out.println("Found: " + sorted.size());
    for (KNN.Entry<P> entry : sorted)
      System.out.printf("  %s  distance = %.4f%n", entry.point, entry.distance);
  }

  /**
   * Imprime o ponto de referencia e todos os pontos encontrados dentro
   * de radius, usando forEachNeighbor.
   */
  static <P extends Point2> void testRadiusSearch(
    P[] points, Quadtree<P> qt, int index, float radius, PointFunc<P> filter)
  {
    P reference = points[index];

    System.out.println("---- Radius test ----");
    System.out.println("Reference point [" + index + "]: " + reference);
    System.out.println("radius = " + radius);

    long count = qt.forEachNeighbor(reference, radius, p ->
    {
      float d = Point2.distance(reference, p);
      System.out.printf("  %s  distance = %.4f%n", p, d);
      return true; // continua ate esgotar os pontos dentro do raio
    }, filter);

    System.out.println("Total processed: " + count);
  }

  // ---------------------------------------------------------------
  // Menu principal
  // ---------------------------------------------------------------

  public static void main(String[] args)
  {
    try
    {
      char kind = Console.readOption("Use (p)oints or (c)olored particles", "pc");

      if (kind == 'p')
        runForPoints();
      else
        runForParticles();
    }
    catch (Exception e)
    {
      System.out.println(e.getMessage());
    }
  }

  // -------- fluxo para Point2 --------

  static void runForPoints() throws IOException
  {
    Point2[] points = loadPoints();
    int nmax = Console.readInt("nmax (max points per node before subdividing)");
    int lmax = Console.readInt("lmax (max tree depth)");
    Quadtree<Point2> qtree = new Quadtree<>(points, nmax, lmax);

    qtree.bounds().print("Quadtree bounds: ");
    System.out.printf("Nodes: %d%nLeaf nodes: %d%n", qtree.size(), qtree.leafCount());

    searchMenu(points, qtree, null); // sem filtro de cor para Point2

    if (Console.readOption("Open viewer? (y/n)", "yn") == 'y')
      new QuadtreeViewer<>(qtree);
  }

  static Point2[] loadPoints() throws IOException
  {
    char src = Console.readOption("Read from (f)ile or (r)andom generation", "fr");

    if (src == 'f')
    {
      String fn = Console.readString("File name");
      return readPointsFromFile(fn);
    }
    else
    {
      float w = Console.readFloat("Domain width");
      float h = Console.readFloat("Domain height");
      int n = Console.readInt("Number of points");
      return randomPoints(new Point2(w, h), n);
    }
  }

  // -------- fluxo para ColorParticle2 --------

  static void runForParticles()
  {
    float w = Console.readFloat("Domain width");
    float h = Console.readFloat("Domain height");
    int n = Console.readInt("Number of particles");

    ColorParticle2[] particles = randomParticles(new Point2(w, h), n);
    int nmax = Console.readInt("nmax (max points per node before subdividing)");
    int lmax = Console.readInt("lmax (max tree depth)");
    Quadtree<ColorParticle2> qtree = new Quadtree<>(particles, nmax, lmax);

    qtree.bounds().print("Quadtree bounds: ");
    System.out.printf("Nodes: %d%nLeaf nodes: %d%n", qtree.size(), qtree.leafCount());

    PointFunc<ColorParticle2> filter = chooseColorFilter();

    searchMenu(particles, qtree, filter);

    if (Console.readOption("Open viewer? (y/n)", "yn") == 'y')
      new QuadtreeViewer<>(qtree);
  }

  /**
   * Pergunta ao usuario se deseja filtrar por cor e, em caso afirmativo,
   * qual cor da paleta usar. Devolve null se nenhum filtro deve ser
   * aplicado (todas as particulas sao testadas).
   */
  static PointFunc<ColorParticle2> chooseColorFilter()
  {
    if (Console.readOption("Filter by color? (y/n)", "yn") != 'y')
      return null;

    System.out.println("Available colors: 0=red 1=green 2=blue 3=yellow");
    int choice = Console.readInt("Color index (0-3)");

    if (choice < 0 || choice >= PALETTE.length)
    {
      Console.error("invalid color index, no filter applied");
      return null;
    }

    int[] c = PALETTE[choice];
    System.out.println("Filtering by color: " + colorName(c));
    return sameColorFilter(c[0], c[1], c[2]);
  }

  // -------- menu de buscas (KNN / raio), comum a pontos e particulas --------

  static <P extends Point2> void searchMenu(P[] points, Quadtree<P> qtree, PointFunc<P> filter)
  {
    for (;;)
    {
      char op = Console.readOption(
        "(k)NN search, (r)adius search or (q)uit", "krq");

      if (op == 'q')
        break;

      int index = readValidIndex(points.length);
      if (index < 0)
        continue; // indice invalido, tenta de novo

      if (op == 'k')
      {
        int k = Console.readInt("k");
        testKNN(points, qtree, index, k, filter);
      }
      else
      {
        float radius = Console.readFloat("radius");
        testRadiusSearch(points, qtree, index, radius, filter);
      }
    }
  }

  /**
   * Le um indice do usuario e valida contra o tamanho do array, evitando
   * ArrayIndexOutOfBoundsException. Devolve -1 se o indice for invalido.
   */
  static int readValidIndex(int length)
  {
    int index = Console.readInt("Reference point index (0.." + (length - 1) + ")");

    if (index < 0 || index >= length)
    {
      Console.error("index out of range");
      return -1;
    }
    return index;
  }

} // Main
