package com.google.common.graph;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.math.IntMath;
import com.google.common.primitives.Ints;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;


































abstract class AbstractBaseGraph<N>
  implements BaseGraph<N>
{
  protected long edgeCount() {
    long degreeSum = 0L;
    for (N node : nodes()) {
      degreeSum += degree(node);
    }
    
    Preconditions.checkState(((degreeSum & 0x1L) == 0L));
    return degreeSum >>> 1L;
  }





  
  public Set<EndpointPair<N>> edges() {
    return new AbstractSet<EndpointPair<N>>()
      {
        public UnmodifiableIterator<EndpointPair<N>> iterator() {
          return (UnmodifiableIterator)EndpointPairIterator.of(AbstractBaseGraph.this);
        }

        
        public int size() {
          return Ints.saturatedCast(AbstractBaseGraph.this.edgeCount());
        }

        
        public boolean remove(Object o) {
          throw new UnsupportedOperationException();
        }





        
        public boolean contains(Object obj) {
          if (!(obj instanceof EndpointPair)) {
            return false;
          }
          EndpointPair<?> endpointPair = (EndpointPair)obj;
          return (AbstractBaseGraph.this.isOrderingCompatible(endpointPair) && AbstractBaseGraph.this
            .nodes().contains(endpointPair.nodeU()) && AbstractBaseGraph.this
            .successors(endpointPair.nodeU()).contains(endpointPair.nodeV()));
        }
      };
  }

  
  public Set<EndpointPair<N>> incidentEdges(N node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(nodes().contains(node), "Node %s is not an element of this graph.", node);
    return IncidentEdgeSet.of(this, node);
  }

  
  public int degree(N node) {
    if (isDirected()) {
      return IntMath.saturatedAdd(predecessors(node).size(), successors(node).size());
    }
    Set<N> neighbors = adjacentNodes(node);
    int selfLoopCount = (allowsSelfLoops() && neighbors.contains(node)) ? 1 : 0;
    return IntMath.saturatedAdd(neighbors.size(), selfLoopCount);
  }


  
  public int inDegree(N node) {
    return isDirected() ? predecessors(node).size() : degree(node);
  }

  
  public int outDegree(N node) {
    return isDirected() ? successors(node).size() : degree(node);
  }

  
  public boolean hasEdgeConnecting(N nodeU, N nodeV) {
    Preconditions.checkNotNull(nodeU);
    Preconditions.checkNotNull(nodeV);
    return (nodes().contains(nodeU) && successors(nodeU).contains(nodeV));
  }

  
  public boolean hasEdgeConnecting(EndpointPair<N> endpoints) {
    Preconditions.checkNotNull(endpoints);
    if (!isOrderingCompatible(endpoints)) {
      return false;
    }
    N nodeU = endpoints.nodeU();
    N nodeV = endpoints.nodeV();
    return (nodes().contains(nodeU) && successors(nodeU).contains(nodeV));
  }




  
  protected final void validateEndpoints(EndpointPair<?> endpoints) {
    Preconditions.checkNotNull(endpoints);
    Preconditions.checkArgument(isOrderingCompatible(endpoints), "Mismatch: unordered endpoints cannot be used with directed graphs");
  }
  
  protected final boolean isOrderingCompatible(EndpointPair<?> endpoints) {
    return (endpoints.isOrdered() || !isDirected());
  }
  
  private static abstract class IncidentEdgeSet<N> extends AbstractSet<EndpointPair<N>> {
    protected final N node;
    protected final BaseGraph<N> graph;
    
    public static <N> IncidentEdgeSet<N> of(BaseGraph<N> graph, N node) {
      return graph.isDirected() ? new Directed<>(graph, node) : new Undirected<>(graph, node);
    }
    
    private IncidentEdgeSet(BaseGraph<N> graph, N node) {
      this.graph = graph;
      this.node = node;
    }

    
    public boolean remove(Object o) {
      throw new UnsupportedOperationException();
    }
    
    private static final class Directed<N>
      extends IncidentEdgeSet<N> {
      private Directed(BaseGraph<N> graph, N node) {
        super(graph, node);
      }

      
      public UnmodifiableIterator<EndpointPair<N>> iterator() {
        return Iterators.unmodifiableIterator(
            Iterators.concat(
              Iterators.transform(this.graph
                .predecessors(this.node).iterator(), new Function<N, EndpointPair<N>>()
                {
                  public EndpointPair<N> apply(N predecessor)
                  {
                    return EndpointPair.ordered(predecessor, AbstractBaseGraph.IncidentEdgeSet.Directed.this.node);
                  }
                }), Iterators.transform(
                
                (Iterator)Sets.difference(this.graph.successors(this.node), (Set)ImmutableSet.of(this.node)).iterator(), new Function<N, EndpointPair<N>>()
                {
                  public EndpointPair<N> apply(N successor)
                  {
                    return EndpointPair.ordered(AbstractBaseGraph.IncidentEdgeSet.Directed.this.node, successor);
                  }
                })));
      }

      
      public int size() {
        return this.graph.inDegree(this.node) + this.graph
          .outDegree(this.node) - (
          this.graph.successors(this.node).contains(this.node) ? 1 : 0);
      }

      
      public boolean contains(Object obj) {
        if (!(obj instanceof EndpointPair)) {
          return false;
        }
        
        EndpointPair<?> endpointPair = (EndpointPair)obj;
        if (!endpointPair.isOrdered()) {
          return false;
        }
        
        Object source = endpointPair.source();
        Object target = endpointPair.target();
        return ((this.node.equals(source) && this.graph.successors(this.node).contains(target)) || (this.node
          .equals(target) && this.graph.predecessors(this.node).contains(source)));
      }
    }
    
    private static final class Undirected<N> extends IncidentEdgeSet<N> {
      private Undirected(BaseGraph<N> graph, N node) {
        super(graph, node);
      }

      
      public UnmodifiableIterator<EndpointPair<N>> iterator() {
        return Iterators.unmodifiableIterator(
            Iterators.transform(this.graph
              .adjacentNodes(this.node).iterator(), new Function<N, EndpointPair<N>>()
              {
                public EndpointPair<N> apply(N adjacentNode)
                {
                  return EndpointPair.unordered(AbstractBaseGraph.IncidentEdgeSet.Undirected.this.node, adjacentNode);
                }
              }));
      }

      
      public int size() {
        return this.graph.adjacentNodes(this.node).size();
      }

      
      public boolean contains(Object obj) {
        if (!(obj instanceof EndpointPair)) {
          return false;
        }
        
        EndpointPair<?> endpointPair = (EndpointPair)obj;
        if (endpointPair.isOrdered()) {
          return false;
        }
        Set<N> adjacent = this.graph.adjacentNodes(this.node);
        Object nodeU = endpointPair.nodeU();
        Object nodeV = endpointPair.nodeV();
        
        return ((this.node.equals(nodeV) && adjacent.contains(nodeU)) || (this.node
          .equals(nodeU) && adjacent.contains(nodeV)));
      }
    }
  }
}
