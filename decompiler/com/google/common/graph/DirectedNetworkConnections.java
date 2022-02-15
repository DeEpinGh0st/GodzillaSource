package com.google.common.graph;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import java.util.Collections;
import java.util.Map;
import java.util.Set;


























final class DirectedNetworkConnections<N, E>
  extends AbstractDirectedNetworkConnections<N, E>
{
  protected DirectedNetworkConnections(Map<E, N> inEdgeMap, Map<E, N> outEdgeMap, int selfLoopCount) {
    super(inEdgeMap, outEdgeMap, selfLoopCount);
  }
  
  static <N, E> DirectedNetworkConnections<N, E> of() {
    return new DirectedNetworkConnections<>(
        (Map<E, N>)HashBiMap.create(2), (Map<E, N>)HashBiMap.create(2), 0);
  }

  
  static <N, E> DirectedNetworkConnections<N, E> ofImmutable(Map<E, N> inEdges, Map<E, N> outEdges, int selfLoopCount) {
    return new DirectedNetworkConnections<>(
        (Map<E, N>)ImmutableBiMap.copyOf(inEdges), (Map<E, N>)ImmutableBiMap.copyOf(outEdges), selfLoopCount);
  }

  
  public Set<N> predecessors() {
    return Collections.unmodifiableSet(((BiMap)this.inEdgeMap).values());
  }

  
  public Set<N> successors() {
    return Collections.unmodifiableSet(((BiMap)this.outEdgeMap).values());
  }

  
  public Set<E> edgesConnecting(N node) {
    return new EdgesConnecting<>((Map<?, E>)((BiMap)this.outEdgeMap).inverse(), node);
  }
}
