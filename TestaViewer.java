
import lpoo.geom.*;
import java.util.Random;

public class TestaViewer {

    public static void main(String[] args) {
        System.out.println("=== Abrindo Teste Visual do Bónus: Clique com o Mouse ===");

        // 1. Gerar 100 pontos aleatórios para povoar a tela
        int totalPontos = 100;
        Point2[] pontos = new Point2[totalPontos];
        Random rand = new Random();

        // O visualizador original costuma usar coordenadas positivas visíveis na tela (ex: entre 10 e 400)
        for (int i = 0; i < totalPontos; i++) {
            float x = 50 + rand.nextFloat() * 400;
            float y = 50 + rand.nextFloat() * 300;
            pontos[i] = new Point2(x, y);
        }

        // 2. Criar a sua Quadtree genérica (limite de 5 pontos por nó)
        Quadtree<Point2> tree = new Quadtree<>(pontos, 5);

        // 3. Instanciar e abrir o visualizador do professor que você acabou de modificar!
        // Como ele agora é genérico, passamos <Point2> nele
        new QuadtreeViewer<Point2>(tree);

        System.out.println("Tela aberta! Clique em qualquer lugar do Canvas para testar a busca KNN interativa.");
    }
}