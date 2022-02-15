package com.google.common.graph;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Set;

interface NetworkConnections<N, E> {
  Set<N> adjacentNodes();
  
  Set<N> predecessors();
  
  Set<N> successors();
  
  Set<E> incidentEdges();
  
  Set<E> inEdges();
  
  Set<E> outEdges();
  
  Set<E> edgesConnecting(N paramN);
  
  N adjacentNode(E paramE);
  
  @CanIgnoreReturnValue
  N removeInEdge(E paramE, boolean paramBoolean);
  
  @CanIgnoreReturnValue
  N removeOutEdge(E paramE);
  
  void addInEdge(E paramE, N paramN, boolean paramBoolean);
  
  void addOutEdge(E paramE, N paramN);
}
