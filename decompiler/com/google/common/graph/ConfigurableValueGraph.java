package com.google.common.graph;

import com.google.common.base.Preconditions;
import java.util.Map;
import java.util.Set;








































class ConfigurableValueGraph<N, V>
  extends AbstractValueGraph<N, V>
{
  private final boolean isDirected;
  private final boolean allowsSelfLoops;
  private final ElementOrder<N> nodeOrder;
  protected final MapIteratorCache<N, GraphConnections<N, V>> nodeConnections;
  protected long edgeCount;
  
  ConfigurableValueGraph(AbstractGraphBuilder<? super N> builder) {
    this(builder, builder.nodeOrder
        
        .createMap(((Integer)builder.expectedNodeCount
          .or(Integer.valueOf(10))).intValue()), 0L);
  }








  
  ConfigurableValueGraph(AbstractGraphBuilder<? super N> builder, Map<N, GraphConnections<N, V>> nodeConnections, long edgeCount) {
    this.isDirected = builder.directed;
    this.allowsSelfLoops = builder.allowsSelfLoops;
    this.nodeOrder = builder.nodeOrder.cast();
    
    this.nodeConnections = (nodeConnections instanceof java.util.TreeMap) ? new MapRetrievalCache<>(nodeConnections) : new MapIteratorCache<>(nodeConnections);


    
    this.edgeCount = Graphs.checkNonNegative(edgeCount);
  }

  
  public Set<N> nodes() {
    return this.nodeConnections.unmodifiableKeySet();
  }

  
  public boolean isDirected() {
    return this.isDirected;
  }

  
  public boolean allowsSelfLoops() {
    return this.allowsSelfLoops;
  }

  
  public ElementOrder<N> nodeOrder() {
    return this.nodeOrder;
  }

  
  public Set<N> adjacentNodes(N node) {
    return checkedConnections(node).adjacentNodes();
  }

  
  public Set<N> predecessors(N node) {
    return checkedConnections(node).predecessors();
  }

  
  public Set<N> successors(N node) {
    return checkedConnections(node).successors();
  }

  
  public boolean hasEdgeConnecting(N nodeU, N nodeV) {
    return hasEdgeConnecting_internal((N)Preconditions.checkNotNull(nodeU), (N)Preconditions.checkNotNull(nodeV));
  }

  
  public boolean hasEdgeConnecting(EndpointPair<N> endpoints) {
    Preconditions.checkNotNull(endpoints);
    return (isOrderingCompatible(endpoints) && 
      hasEdgeConnecting_internal(endpoints.nodeU(), endpoints.nodeV()));
  }

  
  public V edgeValueOrDefault(N nodeU, N nodeV, V defaultValue) {
    return edgeValueOrDefault_internal((N)Preconditions.checkNotNull(nodeU), (N)Preconditions.checkNotNull(nodeV), defaultValue);
  }

  
  public V edgeValueOrDefault(EndpointPair<N> endpoints, V defaultValue) {
    validateEndpoints(endpoints);
    return edgeValueOrDefault_internal(endpoints.nodeU(), endpoints.nodeV(), defaultValue);
  }

  
  protected long edgeCount() {
    return this.edgeCount;
  }
  
  protected final GraphConnections<N, V> checkedConnections(N node) {
    GraphConnections<N, V> connections = this.nodeConnections.get(node);
    if (connections == null) {
      Preconditions.checkNotNull(node);
      throw new IllegalArgumentException("Node " + node + " is not an element of this graph.");
    } 
    return connections;
  }
  
  protected final boolean containsNode(N node) {
    return this.nodeConnections.containsKey(node);
  }
  
  protected final boolean hasEdgeConnecting_internal(N nodeU, N nodeV) {
    GraphConnections<N, V> connectionsU = this.nodeConnections.get(nodeU);
    return (connectionsU != null && connectionsU.successors().contains(nodeV));
  }
  
  protected final V edgeValueOrDefault_internal(N nodeU, N nodeV, V defaultValue) {
    GraphConnections<N, V> connectionsU = this.nodeConnections.get(nodeU);
    V value = (connectionsU == null) ? null : connectionsU.value(nodeV);
    return (value == null) ? defaultValue : value;
  }
}
