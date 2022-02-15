package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.errorprone.annotations.Immutable;
import java.util.Map;
import java.util.Set;































@Immutable(containerOf = {"N"})
@Beta
public class ImmutableGraph<N>
  extends ForwardingGraph<N>
{
  private final BaseGraph<N> backingGraph;
  
  ImmutableGraph(BaseGraph<N> backingGraph) {
    this.backingGraph = backingGraph;
  }

  
  public static <N> ImmutableGraph<N> copyOf(Graph<N> graph) {
    return (graph instanceof ImmutableGraph) ? (ImmutableGraph<N>)graph : new ImmutableGraph<>(new ConfigurableValueGraph<>(


          
          GraphBuilder.from(graph), (Map<N, GraphConnections<N, ?>>)getNodeConnections(graph), graph.edges().size()));
  }





  
  @Deprecated
  public static <N> ImmutableGraph<N> copyOf(ImmutableGraph<N> graph) {
    return (ImmutableGraph<N>)Preconditions.checkNotNull(graph);
  }




  
  private static <N> ImmutableMap<N, GraphConnections<N, GraphConstants.Presence>> getNodeConnections(Graph<N> graph) {
    ImmutableMap.Builder<N, GraphConnections<N, GraphConstants.Presence>> nodeConnections = ImmutableMap.builder();
    for (N node : graph.nodes()) {
      nodeConnections.put(node, connectionsOf(graph, node));
    }
    return nodeConnections.build();
  }
  
  private static <N> GraphConnections<N, GraphConstants.Presence> connectionsOf(Graph<N> graph, N node) {
    Function<Object, GraphConstants.Presence> edgeValueFn = Functions.constant(GraphConstants.Presence.EDGE_EXISTS);
    return graph.isDirected() ? 
      DirectedGraphConnections.<N, GraphConstants.Presence>ofImmutable(graph
        .predecessors(node), Maps.asMap(graph.successors(node), edgeValueFn)) : 
      UndirectedGraphConnections.<N, GraphConstants.Presence>ofImmutable(
        Maps.asMap(graph.adjacentNodes(node), edgeValueFn));
  }

  
  protected BaseGraph<N> delegate() {
    return this.backingGraph;
  }
}
