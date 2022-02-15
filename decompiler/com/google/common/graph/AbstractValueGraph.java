package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import java.util.Set;






























@Beta
public abstract class AbstractValueGraph<N, V>
  extends AbstractBaseGraph<N>
  implements ValueGraph<N, V>
{
  public Graph<N> asGraph() {
    return new AbstractGraph<N>()
      {
        public Set<N> nodes() {
          return AbstractValueGraph.this.nodes();
        }

        
        public Set<EndpointPair<N>> edges() {
          return AbstractValueGraph.this.edges();
        }

        
        public boolean isDirected() {
          return AbstractValueGraph.this.isDirected();
        }

        
        public boolean allowsSelfLoops() {
          return AbstractValueGraph.this.allowsSelfLoops();
        }

        
        public ElementOrder<N> nodeOrder() {
          return AbstractValueGraph.this.nodeOrder();
        }

        
        public Set<N> adjacentNodes(N node) {
          return AbstractValueGraph.this.adjacentNodes(node);
        }

        
        public Set<N> predecessors(N node) {
          return AbstractValueGraph.this.predecessors(node);
        }

        
        public Set<N> successors(N node) {
          return AbstractValueGraph.this.successors(node);
        }

        
        public int degree(N node) {
          return AbstractValueGraph.this.degree(node);
        }

        
        public int inDegree(N node) {
          return AbstractValueGraph.this.inDegree(node);
        }

        
        public int outDegree(N node) {
          return AbstractValueGraph.this.outDegree(node);
        }
      };
  }

  
  public Optional<V> edgeValue(N nodeU, N nodeV) {
    return Optional.ofNullable(edgeValueOrDefault(nodeU, nodeV, null));
  }

  
  public Optional<V> edgeValue(EndpointPair<N> endpoints) {
    return Optional.ofNullable(edgeValueOrDefault(endpoints, null));
  }

  
  public final boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof ValueGraph)) {
      return false;
    }
    ValueGraph<?, ?> other = (ValueGraph<?, ?>)obj;
    
    return (isDirected() == other.isDirected() && 
      nodes().equals(other.nodes()) && 
      edgeValueMap(this).equals(edgeValueMap(other)));
  }

  
  public final int hashCode() {
    return edgeValueMap(this).hashCode();
  }


  
  public String toString() {
    return "isDirected: " + 
      isDirected() + ", allowsSelfLoops: " + 
      
      allowsSelfLoops() + ", nodes: " + 
      
      nodes() + ", edges: " + 
      
      edgeValueMap(this);
  }
  
  private static <N, V> Map<EndpointPair<N>, V> edgeValueMap(final ValueGraph<N, V> graph) {
    Function<EndpointPair<N>, V> edgeToValueFn = new Function<EndpointPair<N>, V>()
      {
        public V apply(EndpointPair<N> edge)
        {
          return (V)graph.edgeValueOrDefault(edge.nodeU(), edge.nodeV(), null);
        }
      };
    return Maps.asMap(graph.edges(), edgeToValueFn);
  }
}
