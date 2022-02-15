package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.UnmodifiableIterator;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;




























abstract class MultiEdgesConnecting<E>
  extends AbstractSet<E>
{
  private final Map<E, ?> outEdgeToNode;
  private final Object targetNode;
  
  MultiEdgesConnecting(Map<E, ?> outEdgeToNode, Object targetNode) {
    this.outEdgeToNode = (Map<E, ?>)Preconditions.checkNotNull(outEdgeToNode);
    this.targetNode = Preconditions.checkNotNull(targetNode);
  }

  
  public UnmodifiableIterator<E> iterator() {
    final Iterator<? extends Map.Entry<E, ?>> entries = this.outEdgeToNode.entrySet().iterator();
    return (UnmodifiableIterator<E>)new AbstractIterator<E>()
      {
        protected E computeNext() {
          while (entries.hasNext()) {
            Map.Entry<E, ?> entry = entries.next();
            if (MultiEdgesConnecting.this.targetNode.equals(entry.getValue())) {
              return entry.getKey();
            }
          } 
          return (E)endOfData();
        }
      };
  }

  
  public boolean contains(Object edge) {
    return this.targetNode.equals(this.outEdgeToNode.get(edge));
  }
}
