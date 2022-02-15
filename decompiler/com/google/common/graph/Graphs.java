package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;






































@Beta
public final class Graphs
{
  public static <N> boolean hasCycle(Graph<N> graph) {
    int numEdges = graph.edges().size();
    if (numEdges == 0) {
      return false;
    }
    if (!graph.isDirected() && numEdges >= graph.nodes().size()) {
      return true;
    }

    
    Map<Object, NodeVisitState> visitedNodes = Maps.newHashMapWithExpectedSize(graph.nodes().size());
    for (N node : graph.nodes()) {
      if (subgraphHasCycle(graph, visitedNodes, node, null)) {
        return true;
      }
    } 
    return false;
  }









  
  public static boolean hasCycle(Network<?, ?> network) {
    if (!network.isDirected() && network
      .allowsParallelEdges() && network
      .edges().size() > network.asGraph().edges().size()) {
      return true;
    }
    return hasCycle(network.asGraph());
  }






  
  private static <N> boolean subgraphHasCycle(Graph<N> graph, Map<Object, NodeVisitState> visitedNodes, N node, N previousNode) {
    NodeVisitState state = visitedNodes.get(node);
    if (state == NodeVisitState.COMPLETE) {
      return false;
    }
    if (state == NodeVisitState.PENDING) {
      return true;
    }
    
    visitedNodes.put(node, NodeVisitState.PENDING);
    for (N nextNode : graph.successors(node)) {
      if (canTraverseWithoutReusingEdge(graph, nextNode, previousNode) && 
        subgraphHasCycle(graph, visitedNodes, nextNode, node)) {
        return true;
      }
    } 
    visitedNodes.put(node, NodeVisitState.COMPLETE);
    return false;
  }







  
  private static boolean canTraverseWithoutReusingEdge(Graph<?> graph, Object nextNode, Object previousNode) {
    if (graph.isDirected() || !Objects.equal(previousNode, nextNode)) {
      return true;
    }

    
    return false;
  }










  
  public static <N> Graph<N> transitiveClosure(Graph<N> graph) {
    MutableGraph<N> transitiveClosure = GraphBuilder.<N>from(graph).allowsSelfLoops(true).build();


    
    if (graph.isDirected()) {
      
      for (N node : graph.nodes()) {
        for (N reachableNode : reachableNodes(graph, node)) {
          transitiveClosure.putEdge(node, reachableNode);
        }
      }
    
    } else {
      
      Set<N> visitedNodes = new HashSet<>();
      for (N node : graph.nodes()) {
        if (!visitedNodes.contains(node)) {
          Set<N> reachableNodes = reachableNodes(graph, node);
          visitedNodes.addAll(reachableNodes);
          int pairwiseMatch = 1;
          for (N nodeU : reachableNodes) {
            for (N nodeV : Iterables.limit(reachableNodes, pairwiseMatch++)) {
              transitiveClosure.putEdge(nodeU, nodeV);
            }
          } 
        } 
      } 
    } 
    
    return transitiveClosure;
  }











  
  public static <N> Set<N> reachableNodes(Graph<N> graph, N node) {
    Preconditions.checkArgument(graph.nodes().contains(node), "Node %s is not an element of this graph.", node);
    Set<N> visitedNodes = new LinkedHashSet<>();
    Queue<N> queuedNodes = new ArrayDeque<>();
    visitedNodes.add(node);
    queuedNodes.add(node);
    
    while (!queuedNodes.isEmpty()) {
      N currentNode = queuedNodes.remove();
      for (N successor : graph.successors(currentNode)) {
        if (visitedNodes.add(successor)) {
          queuedNodes.add(successor);
        }
      } 
    } 
    return Collections.unmodifiableSet(visitedNodes);
  }








  
  public static <N> Graph<N> transpose(Graph<N> graph) {
    if (!graph.isDirected()) {
      return graph;
    }
    
    if (graph instanceof TransposedGraph) {
      return ((TransposedGraph)graph).graph;
    }
    
    return new TransposedGraph<>(graph);
  }




  
  public static <N, V> ValueGraph<N, V> transpose(ValueGraph<N, V> graph) {
    if (!graph.isDirected()) {
      return graph;
    }
    
    if (graph instanceof TransposedValueGraph) {
      return ((TransposedValueGraph)graph).graph;
    }
    
    return new TransposedValueGraph<>(graph);
  }




  
  public static <N, E> Network<N, E> transpose(Network<N, E> network) {
    if (!network.isDirected()) {
      return network;
    }
    
    if (network instanceof TransposedNetwork) {
      return ((TransposedNetwork)network).network;
    }
    
    return new TransposedNetwork<>(network);
  }
  
  static <N> EndpointPair<N> transpose(EndpointPair<N> endpoints) {
    if (endpoints.isOrdered()) {
      return EndpointPair.ordered(endpoints.target(), endpoints.source());
    }
    return endpoints;
  }
  
  private static class TransposedGraph<N>
    extends ForwardingGraph<N>
  {
    private final Graph<N> graph;
    
    TransposedGraph(Graph<N> graph) {
      this.graph = graph;
    }

    
    protected Graph<N> delegate() {
      return this.graph;
    }

    
    public Set<N> predecessors(N node) {
      return delegate().successors(node);
    }

    
    public Set<N> successors(N node) {
      return delegate().predecessors(node);
    }

    
    public int inDegree(N node) {
      return delegate().outDegree(node);
    }

    
    public int outDegree(N node) {
      return delegate().inDegree(node);
    }

    
    public boolean hasEdgeConnecting(N nodeU, N nodeV) {
      return delegate().hasEdgeConnecting(nodeV, nodeU);
    }

    
    public boolean hasEdgeConnecting(EndpointPair<N> endpoints) {
      return delegate().hasEdgeConnecting(Graphs.transpose(endpoints));
    }
  }
  
  private static class TransposedValueGraph<N, V>
    extends ForwardingValueGraph<N, V>
  {
    private final ValueGraph<N, V> graph;
    
    TransposedValueGraph(ValueGraph<N, V> graph) {
      this.graph = graph;
    }

    
    protected ValueGraph<N, V> delegate() {
      return this.graph;
    }

    
    public Set<N> predecessors(N node) {
      return delegate().successors(node);
    }

    
    public Set<N> successors(N node) {
      return delegate().predecessors(node);
    }

    
    public int inDegree(N node) {
      return delegate().outDegree(node);
    }

    
    public int outDegree(N node) {
      return delegate().inDegree(node);
    }

    
    public boolean hasEdgeConnecting(N nodeU, N nodeV) {
      return delegate().hasEdgeConnecting(nodeV, nodeU);
    }

    
    public boolean hasEdgeConnecting(EndpointPair<N> endpoints) {
      return delegate().hasEdgeConnecting(Graphs.transpose(endpoints));
    }

    
    public Optional<V> edgeValue(N nodeU, N nodeV) {
      return delegate().edgeValue(nodeV, nodeU);
    }

    
    public Optional<V> edgeValue(EndpointPair<N> endpoints) {
      return delegate().edgeValue(Graphs.transpose(endpoints));
    }

    
    public V edgeValueOrDefault(N nodeU, N nodeV, V defaultValue) {
      return delegate().edgeValueOrDefault(nodeV, nodeU, defaultValue);
    }

    
    public V edgeValueOrDefault(EndpointPair<N> endpoints, V defaultValue) {
      return delegate().edgeValueOrDefault(Graphs.transpose(endpoints), defaultValue);
    }
  }
  
  private static class TransposedNetwork<N, E> extends ForwardingNetwork<N, E> {
    private final Network<N, E> network;
    
    TransposedNetwork(Network<N, E> network) {
      this.network = network;
    }

    
    protected Network<N, E> delegate() {
      return this.network;
    }

    
    public Set<N> predecessors(N node) {
      return delegate().successors(node);
    }

    
    public Set<N> successors(N node) {
      return delegate().predecessors(node);
    }

    
    public int inDegree(N node) {
      return delegate().outDegree(node);
    }

    
    public int outDegree(N node) {
      return delegate().inDegree(node);
    }

    
    public Set<E> inEdges(N node) {
      return delegate().outEdges(node);
    }

    
    public Set<E> outEdges(N node) {
      return delegate().inEdges(node);
    }

    
    public EndpointPair<N> incidentNodes(E edge) {
      EndpointPair<N> endpointPair = delegate().incidentNodes(edge);
      return EndpointPair.of(this.network, endpointPair.nodeV(), endpointPair.nodeU());
    }

    
    public Set<E> edgesConnecting(N nodeU, N nodeV) {
      return delegate().edgesConnecting(nodeV, nodeU);
    }

    
    public Set<E> edgesConnecting(EndpointPair<N> endpoints) {
      return delegate().edgesConnecting(Graphs.transpose(endpoints));
    }

    
    public Optional<E> edgeConnecting(N nodeU, N nodeV) {
      return delegate().edgeConnecting(nodeV, nodeU);
    }

    
    public Optional<E> edgeConnecting(EndpointPair<N> endpoints) {
      return delegate().edgeConnecting(Graphs.transpose(endpoints));
    }

    
    public E edgeConnectingOrNull(N nodeU, N nodeV) {
      return delegate().edgeConnectingOrNull(nodeV, nodeU);
    }

    
    public E edgeConnectingOrNull(EndpointPair<N> endpoints) {
      return delegate().edgeConnectingOrNull(Graphs.transpose(endpoints));
    }

    
    public boolean hasEdgeConnecting(N nodeU, N nodeV) {
      return delegate().hasEdgeConnecting(nodeV, nodeU);
    }

    
    public boolean hasEdgeConnecting(EndpointPair<N> endpoints) {
      return delegate().hasEdgeConnecting(Graphs.transpose(endpoints));
    }
  }












  
  public static <N> MutableGraph<N> inducedSubgraph(Graph<N> graph, Iterable<? extends N> nodes) {
    MutableGraph<N> subgraph = (nodes instanceof Collection) ? GraphBuilder.<N>from(graph).expectedNodeCount(((Collection)nodes).size()).build() : GraphBuilder.<N>from(graph).build();
    for (N node : nodes) {
      subgraph.addNode(node);
    }
    for (N node : subgraph.nodes()) {
      for (N successorNode : graph.successors(node)) {
        if (subgraph.nodes().contains(successorNode)) {
          subgraph.putEdge(node, successorNode);
        }
      } 
    } 
    return subgraph;
  }












  
  public static <N, V> MutableValueGraph<N, V> inducedSubgraph(ValueGraph<N, V> graph, Iterable<? extends N> nodes) {
    MutableValueGraph<N, V> subgraph = (nodes instanceof Collection) ? ValueGraphBuilder.<N, V>from(graph).expectedNodeCount(((Collection)nodes).size()).build() : ValueGraphBuilder.<N, V>from(graph).build();
    for (N node : nodes) {
      subgraph.addNode(node);
    }
    for (N node : subgraph.nodes()) {
      for (N successorNode : graph.successors(node)) {
        if (subgraph.nodes().contains(successorNode)) {
          subgraph.putEdgeValue(node, successorNode, graph
              .edgeValueOrDefault(node, successorNode, null));
        }
      } 
    } 
    return subgraph;
  }












  
  public static <N, E> MutableNetwork<N, E> inducedSubgraph(Network<N, E> network, Iterable<? extends N> nodes) {
    MutableNetwork<N, E> subgraph = (nodes instanceof Collection) ? NetworkBuilder.<N, E>from(network).expectedNodeCount(((Collection)nodes).size()).build() : NetworkBuilder.<N, E>from(network).build();
    for (N node : nodes) {
      subgraph.addNode(node);
    }
    for (N node : subgraph.nodes()) {
      for (E edge : network.outEdges(node)) {
        N successorNode = network.incidentNodes(edge).adjacentNode(node);
        if (subgraph.nodes().contains(successorNode)) {
          subgraph.addEdge(node, successorNode, edge);
        }
      } 
    } 
    return subgraph;
  }

  
  public static <N> MutableGraph<N> copyOf(Graph<N> graph) {
    MutableGraph<N> copy = GraphBuilder.<N>from(graph).expectedNodeCount(graph.nodes().size()).build();
    for (N node : graph.nodes()) {
      copy.addNode(node);
    }
    for (EndpointPair<N> edge : graph.edges()) {
      copy.putEdge(edge.nodeU(), edge.nodeV());
    }
    return copy;
  }


  
  public static <N, V> MutableValueGraph<N, V> copyOf(ValueGraph<N, V> graph) {
    MutableValueGraph<N, V> copy = ValueGraphBuilder.<N, V>from(graph).expectedNodeCount(graph.nodes().size()).build();
    for (N node : graph.nodes()) {
      copy.addNode(node);
    }
    for (EndpointPair<N> edge : graph.edges()) {
      copy.putEdgeValue(edge
          .nodeU(), edge.nodeV(), graph.edgeValueOrDefault(edge.nodeU(), edge.nodeV(), null));
    }
    return copy;
  }





  
  public static <N, E> MutableNetwork<N, E> copyOf(Network<N, E> network) {
    MutableNetwork<N, E> copy = NetworkBuilder.<N, E>from(network).expectedNodeCount(network.nodes().size()).expectedEdgeCount(network.edges().size()).build();
    for (N node : network.nodes()) {
      copy.addNode(node);
    }
    for (E edge : network.edges()) {
      EndpointPair<N> endpointPair = network.incidentNodes(edge);
      copy.addEdge(endpointPair.nodeU(), endpointPair.nodeV(), edge);
    } 
    return copy;
  }
  
  @CanIgnoreReturnValue
  static int checkNonNegative(int value) {
    Preconditions.checkArgument((value >= 0), "Not true that %s is non-negative.", value);
    return value;
  }
  
  @CanIgnoreReturnValue
  static long checkNonNegative(long value) {
    Preconditions.checkArgument((value >= 0L), "Not true that %s is non-negative.", value);
    return value;
  }
  
  @CanIgnoreReturnValue
  static int checkPositive(int value) {
    Preconditions.checkArgument((value > 0), "Not true that %s is positive.", value);
    return value;
  }
  
  @CanIgnoreReturnValue
  static long checkPositive(long value) {
    Preconditions.checkArgument((value > 0L), "Not true that %s is positive.", value);
    return value;
  }




  
  private enum NodeVisitState
  {
    PENDING,
    COMPLETE;
  }
}
