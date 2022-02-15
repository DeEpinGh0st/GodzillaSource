package com.google.common.graph;

import com.google.common.annotations.Beta;
import java.util.Optional;
import java.util.Set;

@Beta
public interface ValueGraph<N, V> extends BaseGraph<N> {
  Set<N> nodes();
  
  Set<EndpointPair<N>> edges();
  
  Graph<N> asGraph();
  
  boolean isDirected();
  
  boolean allowsSelfLoops();
  
  ElementOrder<N> nodeOrder();
  
  Set<N> adjacentNodes(N paramN);
  
  Set<N> predecessors(N paramN);
  
  Set<N> successors(N paramN);
  
  Set<EndpointPair<N>> incidentEdges(N paramN);
  
  int degree(N paramN);
  
  int inDegree(N paramN);
  
  int outDegree(N paramN);
  
  boolean hasEdgeConnecting(N paramN1, N paramN2);
  
  boolean hasEdgeConnecting(EndpointPair<N> paramEndpointPair);
  
  Optional<V> edgeValue(N paramN1, N paramN2);
  
  Optional<V> edgeValue(EndpointPair<N> paramEndpointPair);
  
  V edgeValueOrDefault(N paramN1, N paramN2, V paramV);
  
  V edgeValueOrDefault(EndpointPair<N> paramEndpointPair, V paramV);
  
  boolean equals(Object paramObject);
  
  int hashCode();
}
