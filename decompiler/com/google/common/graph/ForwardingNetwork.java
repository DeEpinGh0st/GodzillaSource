package com.google.common.graph;

import java.util.Optional;
import java.util.Set;


























abstract class ForwardingNetwork<N, E>
  extends AbstractNetwork<N, E>
{
  public Set<N> nodes() {
    return delegate().nodes();
  }

  
  public Set<E> edges() {
    return delegate().edges();
  }

  
  public boolean isDirected() {
    return delegate().isDirected();
  }

  
  public boolean allowsParallelEdges() {
    return delegate().allowsParallelEdges();
  }

  
  public boolean allowsSelfLoops() {
    return delegate().allowsSelfLoops();
  }

  
  public ElementOrder<N> nodeOrder() {
    return delegate().nodeOrder();
  }

  
  public ElementOrder<E> edgeOrder() {
    return delegate().edgeOrder();
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

  
  public Set<E> incidentEdges(N node) {
    return delegate().incidentEdges(node);
  }

  
  public Set<E> inEdges(N node) {
    return delegate().inEdges(node);
  }

  
  public Set<E> outEdges(N node) {
    return delegate().outEdges(node);
  }

  
  public EndpointPair<N> incidentNodes(E edge) {
    return delegate().incidentNodes(edge);
  }

  
  public Set<E> adjacentEdges(E edge) {
    return delegate().adjacentEdges(edge);
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

  
  public Set<E> edgesConnecting(N nodeU, N nodeV) {
    return delegate().edgesConnecting(nodeU, nodeV);
  }

  
  public Set<E> edgesConnecting(EndpointPair<N> endpoints) {
    return delegate().edgesConnecting(endpoints);
  }

  
  public Optional<E> edgeConnecting(N nodeU, N nodeV) {
    return delegate().edgeConnecting(nodeU, nodeV);
  }

  
  public Optional<E> edgeConnecting(EndpointPair<N> endpoints) {
    return delegate().edgeConnecting(endpoints);
  }

  
  public E edgeConnectingOrNull(N nodeU, N nodeV) {
    return delegate().edgeConnectingOrNull(nodeU, nodeV);
  }

  
  public E edgeConnectingOrNull(EndpointPair<N> endpoints) {
    return delegate().edgeConnectingOrNull(endpoints);
  }

  
  public boolean hasEdgeConnecting(N nodeU, N nodeV) {
    return delegate().hasEdgeConnecting(nodeU, nodeV);
  }

  
  public boolean hasEdgeConnecting(EndpointPair<N> endpoints) {
    return delegate().hasEdgeConnecting(endpoints);
  }
  
  protected abstract Network<N, E> delegate();
}
