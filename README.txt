TRABALHO 2 - LPOO 2026
Quadtree / KNN / Busca por raio

Autores:
- João Pedro Huppes Arenales
- Paola Campos da Silva
- Valentina Campos Soares

Vídeo: [LINK DO VÍDEO]
Repositório no GitHub: [LINK DO GITHUB]

-----------------------------------------------------------
Status das atividades
-----------------------------------------------------------

A1 - Tornar Quadtree generica (Quadtree<P extends Point2>)
    Concluída. Quadtree.java aceita qualquer P que estenda Point2,
    com construtores públicos que recebem nmax (pointsPerNode) e,
    opcionalmente, lmax (profundidade máxima da arvore):
        Quadtree(P[] points, int pointsPerNode)
        Quadtree(P[] points, int pointsPerNode, int maxDepth)

A2 - Classe KNN e métodos findNeighbors / forEachNeighbor
    Concluída. KNN<P> implementada com max-heap (PriorityQueue),
    armazenando os k pares mais proximos e expondo o resultado
    ordenado via toSortedList(). findNeighbors() usa poda por
    distância mínima a AABB (branch and bound), visitando primeiro
    os quadrantes filhos mais proximos do ponto de busca.
    forEachNeighbor() usa poda por interseção da AABB com o circulo
    de busca (radius) e interrompe a execução se a função f devolver
    false.

A3 - ColorParticle2 (cor RGB)
    Concluída. ColorParticle2 estende Particle2 (que estende Point2)
    e adiciona os campos públicos r, g, b.

A4 - Main.java (aplicação de teste)
    Concluída. O programa funciona tanto para Point2 quanto para
    ColorParticle2:
    - leitura de pontos de arquivo texto OU geração aleatória
    - geração aleatória de particulas com cor sorteada de uma paleta
      fixa (vermelho, verde, azul, amarelo)
    - filtro de cor implementado como expressão lambda, usado tanto
      no KNN quanto na busca por raio
    - pergunta nmax e lmax ao usuário antes de construir a árvore
    - método de teste que recebe o índice de um ponto/particula e
      imprime seus dados, a distância e os dados de todos os
      vizinhos encontrados (KNN e busca por raio)
    - testado com valores distintos de k e radius, e com conjuntos
      de tamanhos variados (pequenos e grandes), com e sem filtro de
      cor

A5 - Bônus: extensão do QuadtreeViewer
    Implementada: clique do mouse no canvas converte a posição do
    clique para coordenadas do mundo, encontra o ponto da árvore
    mais próximo do clique e dispara uma busca KNN (k=8) a partir
    dele. O resultado é destacado visualmente: ponto de referência
    em azul, vizinhos encontrados em vermelho, e um círculo
    tracejado mostrando o alcance até o vizinho mais distante.

A6 - Vídeo
    Concluído. Vídeo gravado com os autores explicando as definições
    efetuadas no código-fonte (Quadtree genérica, KNN, busca por raio,
    ColorParticle2 e a aplicação de teste) e demonstrando a compilação
    e execução do programa, incluindo os testes de KNN e busca por
    raio para pontos e particulas, com e sem filtro de cor.

-----------------------------------------------------------
Observações gerais
-----------------------------------------------------------

- O algoritmo de poda de findNeighbors visita os quadrantes filhos
  em ordem de proximidade (menor distância mínima a AABB primeiro)
  e interrompe a descida em um ramo quando o KNN já está cheio e
  nenhum ponto daquele ramo pode melhorar o resultado.
- forEachNeighbor ignora o próprio ponto de referência (comparação
  por identidade) ao contar e processar vizinhos.
- pointsPerNode (nmax) tem um mínimo de 5 (minPointsPerNode), mesmo
  que o usuário informe um valor menor.
