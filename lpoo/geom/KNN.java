package lpoo.geom;

import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.ArrayList;

/**
 *
 * @author João Pedro Huppes Arenales
 */
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
  } // Entry
  
  public KNN(int k)
  {
    if (k < 1)
      k = 1;

    // k sempre é valido ou seja, k > 1. 
    this.k = k;

    // inicializa o max-heap, ordenando de forma decrescente (tendo em vista a distancia)
    this.maxheap = new PriorityQueue<>((a,b) -> Float.compare(b.distance, a.distance));
  }

  public int size()
  {
    return maxheap.size();
  }

  public boolean isFull()
  {
    return size() == k;
  }

  public void add(P point, float distance)
  {
    Entry<P> new_entry = new Entry<>(point, distance);

    if (!isFull())
      maxheap.add(new_entry);
    else {
      // caso o novo valor seja menor que a pior distância atual
      if (maxheap.peek().distance > new_entry.distance) {
        // remove o pior vizinho e insere o novo, que é melhor.
        maxheap.poll();
        maxheap.add(new_entry);
      }
    }
  }

  @Override
  public Iterator<Entry<P>> iterator() 
  {
    // PriorityQueue ja possui iterador interno
    return maxheap.iterator();
  }

  public List<Entry<P>> toSortedList() 
  {
    PriorityQueue<Entry<P>> tempHeap = new PriorityQueue<>(this.maxheap);
    ArrayList<Entry<P>> list = new ArrayList<>(this.size());

    // Adiciona normalmente no fim 
    // A lista vai ficar em ordem DECRESCENTE (Maior -> Menor)
    while(!tempHeap.isEmpty())
      list.add(tempHeap.poll());

    // Inverte a lista inteira de uma só vez para virar CRESCENTE (Menor -> Maior)
    java.util.Collections.reverse(list);

    return list;
  }

  public float worstDistance()
  {
    // vazio? entao o limite é infinto
    if(maxheap.isEmpty())
      return Float.MAX_VALUE;
    //caso contrario, limite esta no topo do heap         
    else
      return maxheap.peek().distance;
  }
}
