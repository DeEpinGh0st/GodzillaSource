package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

@Beta
public interface MutableValueGraph<N, V> extends ValueGraph<N, V> {
  @CanIgnoreReturnValue
  boolean addNode(N paramN);
  
  @CanIgnoreReturnValue
  V putEdgeValue(N paramN1, N paramN2, V paramV);
  
  @CanIgnoreReturnValue
  V putEdgeValue(EndpointPair<N> paramEndpointPair, V paramV);
  
  @CanIgnoreReturnValue
  boolean removeNode(N paramN);
  
  @CanIgnoreReturnValue
  V removeEdge(N paramN1, N paramN2);
  
  @CanIgnoreReturnValue
  V removeEdge(EndpointPair<N> paramEndpointPair);
}
