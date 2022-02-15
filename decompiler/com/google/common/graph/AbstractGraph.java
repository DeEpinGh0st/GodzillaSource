package com.google.common.graph;

import com.google.common.annotations.Beta;
import java.util.Set;
























@Beta
public abstract class AbstractGraph<N>
  extends AbstractBaseGraph<N>
  implements Graph<N>
{
  public final boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof Graph)) {
      return false;
    }
    Graph<?> other = (Graph)obj;
    
    return (isDirected() == other.isDirected() && 
      nodes().equals(other.nodes()) && 
      edges().equals(other.edges()));
  }

  
  public final int hashCode() {
    return edges().hashCode();
  }


  
  public String toString() {
    return "isDirected: " + 
      isDirected() + ", allowsSelfLoops: " + 
      
      allowsSelfLoops() + ", nodes: " + 
      
      nodes() + ", edges: " + 
      
      edges();
  }
}
