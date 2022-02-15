package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.errorprone.annotations.Immutable;
import java.util.Map;
import java.util.Set;

































@Immutable(containerOf = {"N", "E"})
@Beta
public final class ImmutableNetwork<N, E>
  extends ConfigurableNetwork<N, E>
{
  private ImmutableNetwork(Network<N, E> network) {
    super(
        NetworkBuilder.from(network), getNodeConnections(network), getEdgeToReferenceNode(network));
  }

  
  public static <N, E> ImmutableNetwork<N, E> copyOf(Network<N, E> network) {
    return (network instanceof ImmutableNetwork) ? (ImmutableNetwork<N, E>)network : new ImmutableNetwork<>(network);
  }







  
  @Deprecated
  public static <N, E> ImmutableNetwork<N, E> copyOf(ImmutableNetwork<N, E> network) {
    return (ImmutableNetwork<N, E>)Preconditions.checkNotNull(network);
  }

  
  public ImmutableGraph<N> asGraph() {
    return new ImmutableGraph<>(super.asGraph());
  }



  
  private static <N, E> Map<N, NetworkConnections<N, E>> getNodeConnections(Network<N, E> network) {
    ImmutableMap.Builder<N, NetworkConnections<N, E>> nodeConnections = ImmutableMap.builder();
    for (N node : network.nodes()) {
      nodeConnections.put(node, connectionsOf(network, node));
    }
    return (Map<N, NetworkConnections<N, E>>)nodeConnections.build();
  }



  
  private static <N, E> Map<E, N> getEdgeToReferenceNode(Network<N, E> network) {
    ImmutableMap.Builder<E, N> edgeToReferenceNode = ImmutableMap.builder();
    for (E edge : network.edges()) {
      edgeToReferenceNode.put(edge, network.incidentNodes(edge).nodeU());
    }
    return (Map<E, N>)edgeToReferenceNode.build();
  }
  
  private static <N, E> NetworkConnections<N, E> connectionsOf(Network<N, E> network, N node) {
    if (network.isDirected()) {
      Map<E, N> inEdgeMap = Maps.asMap(network.inEdges(node), sourceNodeFn(network));
      Map<E, N> outEdgeMap = Maps.asMap(network.outEdges(node), targetNodeFn(network));
      int selfLoopCount = network.edgesConnecting(node, node).size();
      return network.allowsParallelEdges() ? 
        DirectedMultiNetworkConnections.<N, E>ofImmutable(inEdgeMap, outEdgeMap, selfLoopCount) : 
        DirectedNetworkConnections.<N, E>ofImmutable(inEdgeMap, outEdgeMap, selfLoopCount);
    } 
    
    Map<E, N> incidentEdgeMap = Maps.asMap(network.incidentEdges(node), adjacentNodeFn(network, node));
    return network.allowsParallelEdges() ? 
      UndirectedMultiNetworkConnections.<N, E>ofImmutable(incidentEdgeMap) : 
      UndirectedNetworkConnections.<N, E>ofImmutable(incidentEdgeMap);
  }

  
  private static <N, E> Function<E, N> sourceNodeFn(final Network<N, E> network) {
    return new Function<E, N>()
      {
        public N apply(E edge) {
          return network.incidentNodes(edge).source();
        }
      };
  }
  
  private static <N, E> Function<E, N> targetNodeFn(final Network<N, E> network) {
    return new Function<E, N>()
      {
        public N apply(E edge) {
          return network.incidentNodes(edge).target();
        }
      };
  }
  
  private static <N, E> Function<E, N> adjacentNodeFn(final Network<N, E> network, final N node) {
    return new Function<E, N>()
      {
        public N apply(E edge) {
          return network.incidentNodes(edge).adjacentNode(node);
        }
      };
  }
}
