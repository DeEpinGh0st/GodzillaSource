package com.google.common.graph;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

























abstract class AbstractUndirectedNetworkConnections<N, E>
  implements NetworkConnections<N, E>
{
  protected final Map<E, N> incidentEdgeMap;
  
  protected AbstractUndirectedNetworkConnections(Map<E, N> incidentEdgeMap) {
    this.incidentEdgeMap = (Map<E, N>)Preconditions.checkNotNull(incidentEdgeMap);
  }

  
  public Set<N> predecessors() {
    return adjacentNodes();
  }

  
  public Set<N> successors() {
    return adjacentNodes();
  }

  
  public Set<E> incidentEdges() {
    return Collections.unmodifiableSet(this.incidentEdgeMap.keySet());
  }

  
  public Set<E> inEdges() {
    return incidentEdges();
  }

  
  public Set<E> outEdges() {
    return incidentEdges();
  }

  
  public N adjacentNode(E edge) {
    return (N)Preconditions.checkNotNull(this.incidentEdgeMap.get(edge));
  }

  
  public N removeInEdge(E edge, boolean isSelfLoop) {
    if (!isSelfLoop) {
      return removeOutEdge(edge);
    }
    return null;
  }

  
  public N removeOutEdge(E edge) {
    N previousNode = this.incidentEdgeMap.remove(edge);
    return (N)Preconditions.checkNotNull(previousNode);
  }

  
  public void addInEdge(E edge, N node, boolean isSelfLoop) {
    if (!isSelfLoop) {
      addOutEdge(edge, node);
    }
  }

  
  public void addOutEdge(E edge, N node) {
    N previousNode = this.incidentEdgeMap.put(edge, node);
    Preconditions.checkState((previousNode == null));
  }
}
