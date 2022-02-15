package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

@Beta
public interface MutableNetwork<N, E> extends Network<N, E> {
  @CanIgnoreReturnValue
  boolean addNode(N paramN);
  
  @CanIgnoreReturnValue
  boolean addEdge(N paramN1, N paramN2, E paramE);
  
  @CanIgnoreReturnValue
  boolean addEdge(EndpointPair<N> paramEndpointPair, E paramE);
  
  @CanIgnoreReturnValue
  boolean removeNode(N paramN);
  
  @CanIgnoreReturnValue
  boolean removeEdge(E paramE);
}
