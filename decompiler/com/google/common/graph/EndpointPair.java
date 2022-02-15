package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import com.google.errorprone.annotations.Immutable;
import java.util.Iterator;




























@Immutable(containerOf = {"N"})
@Beta
public abstract class EndpointPair<N>
  implements Iterable<N>
{
  private final N nodeU;
  private final N nodeV;
  
  private EndpointPair(N nodeU, N nodeV) {
    this.nodeU = (N)Preconditions.checkNotNull(nodeU);
    this.nodeV = (N)Preconditions.checkNotNull(nodeV);
  }

  
  public static <N> EndpointPair<N> ordered(N source, N target) {
    return new Ordered<>(source, target);
  }


  
  public static <N> EndpointPair<N> unordered(N nodeU, N nodeV) {
    return new Unordered<>(nodeV, nodeU);
  }

  
  static <N> EndpointPair<N> of(Graph<?> graph, N nodeU, N nodeV) {
    return graph.isDirected() ? ordered(nodeU, nodeV) : unordered(nodeU, nodeV);
  }

  
  static <N> EndpointPair<N> of(Network<?, ?> network, N nodeU, N nodeV) {
    return network.isDirected() ? ordered(nodeU, nodeV) : unordered(nodeU, nodeV);
  }


















  
  public final N nodeU() {
    return this.nodeU;
  }




  
  public final N nodeV() {
    return this.nodeV;
  }





  
  public final N adjacentNode(Object node) {
    if (node.equals(this.nodeU))
      return this.nodeV; 
    if (node.equals(this.nodeV)) {
      return this.nodeU;
    }
    throw new IllegalArgumentException("EndpointPair " + this + " does not contain node " + node);
  }









  
  public final UnmodifiableIterator<N> iterator() {
    return Iterators.forArray(new Object[] { this.nodeU, this.nodeV });
  }

  
  public abstract N source();

  
  public abstract N target();

  
  public abstract boolean isOrdered();

  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  private static final class Ordered<N>
    extends EndpointPair<N>
  {
    private Ordered(N source, N target) {
      super(source, target);
    }

    
    public N source() {
      return nodeU();
    }

    
    public N target() {
      return nodeV();
    }

    
    public boolean isOrdered() {
      return true;
    }

    
    public boolean equals(Object obj) {
      if (obj == this) {
        return true;
      }
      if (!(obj instanceof EndpointPair)) {
        return false;
      }
      
      EndpointPair<?> other = (EndpointPair)obj;
      if (isOrdered() != other.isOrdered()) {
        return false;
      }
      
      return (source().equals(other.source()) && target().equals(other.target()));
    }

    
    public int hashCode() {
      return Objects.hashCode(new Object[] { source(), target() });
    }

    
    public String toString() {
      return "<" + source() + " -> " + target() + ">";
    }
  }
  
  private static final class Unordered<N> extends EndpointPair<N> {
    private Unordered(N nodeU, N nodeV) {
      super(nodeU, nodeV);
    }

    
    public N source() {
      throw new UnsupportedOperationException("Cannot call source()/target() on a EndpointPair from an undirected graph. Consider calling adjacentNode(node) if you already have a node, or nodeU()/nodeV() if you don't.");
    }

    
    public N target() {
      throw new UnsupportedOperationException("Cannot call source()/target() on a EndpointPair from an undirected graph. Consider calling adjacentNode(node) if you already have a node, or nodeU()/nodeV() if you don't.");
    }

    
    public boolean isOrdered() {
      return false;
    }

    
    public boolean equals(Object obj) {
      if (obj == this) {
        return true;
      }
      if (!(obj instanceof EndpointPair)) {
        return false;
      }
      
      EndpointPair<?> other = (EndpointPair)obj;
      if (isOrdered() != other.isOrdered()) {
        return false;
      }




      
      if (nodeU().equals(other.nodeU()))
      {



        
        return nodeV().equals(other.nodeV());
      }
      return (nodeU().equals(other.nodeV()) && nodeV().equals(other.nodeU()));
    }

    
    public int hashCode() {
      return nodeU().hashCode() + nodeV().hashCode();
    }

    
    public String toString() {
      return "[" + nodeU() + ", " + nodeV() + "]";
    }
  }
}
