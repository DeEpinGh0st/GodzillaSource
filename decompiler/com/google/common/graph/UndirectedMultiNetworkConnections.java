package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multiset;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

























final class UndirectedMultiNetworkConnections<N, E>
  extends AbstractUndirectedNetworkConnections<N, E>
{
  @LazyInit
  private transient Reference<Multiset<N>> adjacentNodesReference;
  
  private UndirectedMultiNetworkConnections(Map<E, N> incidentEdges) {
    super(incidentEdges);
  }
  
  static <N, E> UndirectedMultiNetworkConnections<N, E> of() {
    return new UndirectedMultiNetworkConnections<>(new HashMap<>(2, 1.0F));
  }

  
  static <N, E> UndirectedMultiNetworkConnections<N, E> ofImmutable(Map<E, N> incidentEdges) {
    return new UndirectedMultiNetworkConnections<>((Map<E, N>)ImmutableMap.copyOf(incidentEdges));
  }



  
  public Set<N> adjacentNodes() {
    return Collections.unmodifiableSet(adjacentNodesMultiset().elementSet());
  }
  private Multiset<N> adjacentNodesMultiset() {
    HashMultiset hashMultiset;
    Multiset<N> adjacentNodes = getReference(this.adjacentNodesReference);
    if (adjacentNodes == null) {
      hashMultiset = HashMultiset.create(this.incidentEdgeMap.values());
      this.adjacentNodesReference = new SoftReference(hashMultiset);
    } 
    return (Multiset<N>)hashMultiset;
  }

  
  public Set<E> edgesConnecting(final N node) {
    return new MultiEdgesConnecting<E>(this.incidentEdgeMap, node)
      {
        public int size() {
          return UndirectedMultiNetworkConnections.this.adjacentNodesMultiset().count(node);
        }
      };
  }

  
  public N removeInEdge(E edge, boolean isSelfLoop) {
    if (!isSelfLoop) {
      return removeOutEdge(edge);
    }
    return null;
  }

  
  public N removeOutEdge(E edge) {
    N node = super.removeOutEdge(edge);
    Multiset<N> adjacentNodes = getReference(this.adjacentNodesReference);
    if (adjacentNodes != null) {
      Preconditions.checkState(adjacentNodes.remove(node));
    }
    return node;
  }

  
  public void addInEdge(E edge, N node, boolean isSelfLoop) {
    if (!isSelfLoop) {
      addOutEdge(edge, node);
    }
  }

  
  public void addOutEdge(E edge, N node) {
    super.addOutEdge(edge, node);
    Multiset<N> adjacentNodes = getReference(this.adjacentNodesReference);
    if (adjacentNodes != null) {
      Preconditions.checkState(adjacentNodes.add(node));
    }
  }
  
  private static <T> T getReference(Reference<T> reference) {
    return (reference == null) ? null : reference.get();
  }
}
