package com.google.common.graph;

import java.util.Optional;
import java.util.Set;



























abstract class ForwardingValueGraph<N, V>
  extends AbstractValueGraph<N, V>
{
  public Set<N> nodes() {
    return delegate().nodes();
  }





  
  protected long edgeCount() {
    return delegate().edges().size();
  }

  
  public boolean isDirected() {
    return delegate().isDirected();
  }

  
  public boolean allowsSelfLoops() {
    return delegate().allowsSelfLoops();
  }

  
  public ElementOrder<N> nodeOrder() {
    return delegate().nodeOrder();
  }

  
  public Set<N> adjacentNodes(N node) {
    return delegate().adjacentNodes(node);
  }

  
  public Set<N> predecessors(N node) {
    return delegate().predecessors(node);
  }

  
  public Set<N> successors(N node) {
    return delegate().successors(node);
  }

  
  public int degree(N node) {
    return delegate().degree(node);
  }

  
  public int inDegree(N node) {
    return delegate().inDegree(node);
  }

  
  public int outDegree(N node) {
    return delegate().outDegree(node);
  }

  
  public boolean hasEdgeConnecting(N nodeU, N nodeV) {
    return delegate().hasEdgeConnecting(nodeU, nodeV);
  }

  
  public boolean hasEdgeConnecting(EndpointPair<N> endpoints) {
    return delegate().hasEdgeConnecting(endpoints);
  }

  
  public Optional<V> edgeValue(N nodeU, N nodeV) {
    return delegate().edgeValue(nodeU, nodeV);
  }

  
  public Optional<V> edgeValue(EndpointPair<N> endpoints) {
    return delegate().edgeValue(endpoints);
  }

  
  public V edgeValueOrDefault(N nodeU, N nodeV, V defaultValue) {
    return delegate().edgeValueOrDefault(nodeU, nodeV, defaultValue);
  }

  
  public V edgeValueOrDefault(EndpointPair<N> endpoints, V defaultValue) {
    return delegate().edgeValueOrDefault(endpoints, defaultValue);
  }
  
  protected abstract ValueGraph<N, V> delegate();
}
