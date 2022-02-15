package com.google.common.graph;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Set;

interface GraphConnections<N, V> {
  Set<N> adjacentNodes();
  
  Set<N> predecessors();
  
  Set<N> successors();
  
  V value(N paramN);
  
  void removePredecessor(N paramN);
  
  @CanIgnoreReturnValue
  V removeSuccessor(N paramN);
  
  void addPredecessor(N paramN, V paramV);
  
  @CanIgnoreReturnValue
  V addSuccessor(N paramN, V paramV);
}
