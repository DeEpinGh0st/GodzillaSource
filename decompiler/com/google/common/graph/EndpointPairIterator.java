package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Set;






















abstract class EndpointPairIterator<N>
  extends AbstractIterator<EndpointPair<N>>
{
  private final BaseGraph<N> graph;
  private final Iterator<N> nodeIterator;
  protected N node = null;
  protected Iterator<N> successorIterator = (Iterator<N>)ImmutableSet.of().iterator();
  
  static <N> EndpointPairIterator<N> of(BaseGraph<N> graph) {
    return graph.isDirected() ? new Directed<>(graph) : new Undirected<>(graph);
  }
  
  private EndpointPairIterator(BaseGraph<N> graph) {
    this.graph = graph;
    this.nodeIterator = graph.nodes().iterator();
  }




  
  protected final boolean advance() {
    Preconditions.checkState(!this.successorIterator.hasNext());
    if (!this.nodeIterator.hasNext()) {
      return false;
    }
    this.node = this.nodeIterator.next();
    this.successorIterator = this.graph.successors(this.node).iterator();
    return true;
  }


  
  private static final class Directed<N>
    extends EndpointPairIterator<N>
  {
    private Directed(BaseGraph<N> graph) {
      super(graph);
    }

    
    protected EndpointPair<N> computeNext() {
      while (true) {
        if (this.successorIterator.hasNext()) {
          return EndpointPair.ordered(this.node, this.successorIterator.next());
        }
        if (!advance()) {
          return (EndpointPair<N>)endOfData();
        }
      } 
    }
  }












  
  private static final class Undirected<N>
    extends EndpointPairIterator<N>
  {
    private Set<N> visitedNodes;












    
    private Undirected(BaseGraph<N> graph) {
      super(graph);
      this.visitedNodes = Sets.newHashSetWithExpectedSize(graph.nodes().size());
    }

    
    protected EndpointPair<N> computeNext() {
      while (true) {
        while (this.successorIterator.hasNext()) {
          N otherNode = this.successorIterator.next();
          if (!this.visitedNodes.contains(otherNode)) {
            return EndpointPair.unordered(this.node, otherNode);
          }
        } 
        
        this.visitedNodes.add(this.node);
        if (!advance()) {
          this.visitedNodes = null;
          return (EndpointPair<N>)endOfData();
        } 
      } 
    }
  }
}
