package lpoo.geom;

import java.util.Iterator;
import java.util.PriorityQueue;

public class KNN<P extends Point2> 
    implements Iterable<KNN.Entry<P>>
{

    private final int k;
    private final PriorityQueue<Entry<P>> maxheap; 

    public static class Entry <P extends Point2>
    {
        public final P point;
        public final float distance;
        
        public Entry(P point, float distance){
            this.point = point;
            this.distance = distance;
        }
    }//Entry
    
    public KNN(int k){
        if (k < 1)
            k = 1;

        // k sempre é valido ou seja, k > 1. 
        this.k = k;

        // inicializa o max-heap, ordenando de forma decrescente (tendo em vista a distancia)
        this.maxheap = new PriorityQueue<>((a,b) -> Float.compare(b.distance, a.distance));
    }

    public int size(){
        return maxheap.size();
    }

    public boolean isFull(){
        return size() == k;
    }

    public void add(P point, float distance){
        Entry<P> new_entry = new Entry<>(point, distance);

        if (!isFull())
            maxheap.add(new_entry);
        else{
            // caso o novo valor seja menor que a pior distância atual
            if (maxheap.peek().distance > new_entry.distance){
                // remove o pior vizinho e insere o novo, que é melhor.
                maxheap.poll();
                maxheap.add(new_entry);
            }
        }
    }

    @Override
    public Iterator<Entry<P>> iterator() {
        // PriorityQueue ja possui iterador interno
        return maxheap.iterator();
    }

    public float worstDistance(){
        // vazio? entao o limite é infinto
        if(maxheap.isEmpty())
            return Float.MAX_VALUE;
        //caso contrario, limite esta no topo do heap         
        else
            return maxheap.peek().distance;
    }
}

/*
NOTA TEMPORARIA:
    O interator da PriorityQueue (o que estamos usando no KNN) não percorre em ordem perfeitamente crescente ou descrecente. Os elementos vão sair na ordem da árvore binária do Heap!
    Logo, se precisar que sai de forma ordenada ou jogue o elemento em uma lista e ordene por "distance" usando um Sort, ou vá dando .poll() em uma *cópia* do heap.
*/