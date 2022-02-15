package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

@Beta
public interface MutableGraph<N> extends Graph<N> {
  @CanIgnoreReturnValue
  boolean addNode(N paramN);
  
  @CanIgnoreReturnValue
  boolean putEdge(N paramN1, N paramN2);
  
  @CanIgnoreReturnValue
  boolean putEdge(EndpointPair<N> paramEndpointPair);
  
  @CanIgnoreReturnValue
  boolean removeNode(N paramN);
  
  @CanIgnoreReturnValue
  boolean removeEdge(N paramN1, N paramN2);
  
  @CanIgnoreReturnValue
  boolean removeEdge(EndpointPair<N> paramEndpointPair);
}
