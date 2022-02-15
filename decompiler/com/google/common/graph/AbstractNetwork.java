package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.math.IntMath;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


































@Beta
public abstract class AbstractNetwork<N, E>
  implements Network<N, E>
{
  public Graph<N> asGraph() {
    return new AbstractGraph<N>()
      {
        public Set<N> nodes() {
          return AbstractNetwork.this.nodes();
        }

        
        public Set<EndpointPair<N>> edges() {
          if (AbstractNetwork.this.allowsParallelEdges()) {
            return super.edges();
          }

          
          return new AbstractSet<EndpointPair<N>>()
            {
              public Iterator<EndpointPair<N>> iterator() {
                return Iterators.transform(AbstractNetwork.this
                    .edges().iterator(), new Function<E, EndpointPair<N>>()
                    {
                      public EndpointPair<N> apply(E edge)
                      {
                        return AbstractNetwork.this.incidentNodes(edge);
                      }
                    });
              }

              
              public int size() {
                return AbstractNetwork.this.edges().size();
              }





              
              public boolean contains(Object obj) {
                if (!(obj instanceof EndpointPair)) {
                  return false;
                }
                EndpointPair<?> endpointPair = (EndpointPair)obj;
                return (AbstractNetwork.null.this.isOrderingCompatible(endpointPair) && AbstractNetwork.null.this
                  .nodes().contains(endpointPair.nodeU()) && AbstractNetwork.null.this
                  .successors((N)endpointPair.nodeU()).contains(endpointPair.nodeV()));
              }
            };
        }

        
        public ElementOrder<N> nodeOrder() {
          return AbstractNetwork.this.nodeOrder();
        }

        
        public boolean isDirected() {
          return AbstractNetwork.this.isDirected();
        }

        
        public boolean allowsSelfLoops() {
          return AbstractNetwork.this.allowsSelfLoops();
        }

        
        public Set<N> adjacentNodes(N node) {
          return AbstractNetwork.this.adjacentNodes(node);
        }

        
        public Set<N> predecessors(N node) {
          return AbstractNetwork.this.predecessors(node);
        }

        
        public Set<N> successors(N node) {
          return AbstractNetwork.this.successors(node);
        }
      };
  }



  
  public int degree(N node) {
    if (isDirected()) {
      return IntMath.saturatedAdd(inEdges(node).size(), outEdges(node).size());
    }
    return IntMath.saturatedAdd(incidentEdges(node).size(), edgesConnecting(node, node).size());
  }


  
  public int inDegree(N node) {
    return isDirected() ? inEdges(node).size() : degree(node);
  }

  
  public int outDegree(N node) {
    return isDirected() ? outEdges(node).size() : degree(node);
  }

  
  public Set<E> adjacentEdges(E edge) {
    EndpointPair<N> endpointPair = incidentNodes(edge);
    
    Sets.SetView setView = Sets.union(incidentEdges(endpointPair.nodeU()), incidentEdges(endpointPair.nodeV()));
    return (Set<E>)Sets.difference((Set)setView, (Set)ImmutableSet.of(edge));
  }

  
  public Set<E> edgesConnecting(N nodeU, N nodeV) {
    Set<E> outEdgesU = outEdges(nodeU);
    Set<E> inEdgesV = inEdges(nodeV);
    return (outEdgesU.size() <= inEdgesV.size()) ? 
      Collections.<E>unmodifiableSet(Sets.filter(outEdgesU, connectedPredicate(nodeU, nodeV))) : 
      Collections.<E>unmodifiableSet(Sets.filter(inEdgesV, connectedPredicate(nodeV, nodeU)));
  }

  
  public Set<E> edgesConnecting(EndpointPair<N> endpoints) {
    validateEndpoints(endpoints);
    return edgesConnecting(endpoints.nodeU(), endpoints.nodeV());
  }
  
  private Predicate<E> connectedPredicate(final N nodePresent, final N nodeToCheck) {
    return new Predicate<E>()
      {
        public boolean apply(E edge) {
          return AbstractNetwork.this.incidentNodes(edge).adjacentNode(nodePresent).equals(nodeToCheck);
        }
      };
  }

  
  public Optional<E> edgeConnecting(N nodeU, N nodeV) {
    return Optional.ofNullable(edgeConnectingOrNull(nodeU, nodeV));
  }

  
  public Optional<E> edgeConnecting(EndpointPair<N> endpoints) {
    validateEndpoints(endpoints);
    return edgeConnecting(endpoints.nodeU(), endpoints.nodeV());
  }

  
  public E edgeConnectingOrNull(N nodeU, N nodeV) {
    Set<E> edgesConnecting = edgesConnecting(nodeU, nodeV);
    switch (edgesConnecting.size()) {
      case 0:
        return null;
      case 1:
        return edgesConnecting.iterator().next();
    } 
    throw new IllegalArgumentException(String.format("Cannot call edgeConnecting() when parallel edges exist between %s and %s. Consider calling edgesConnecting() instead.", new Object[] { nodeU, nodeV }));
  }


  
  public E edgeConnectingOrNull(EndpointPair<N> endpoints) {
    validateEndpoints(endpoints);
    return edgeConnectingOrNull(endpoints.nodeU(), endpoints.nodeV());
  }

  
  public boolean hasEdgeConnecting(N nodeU, N nodeV) {
    return !edgesConnecting(nodeU, nodeV).isEmpty();
  }

  
  public boolean hasEdgeConnecting(EndpointPair<N> endpoints) {
    Preconditions.checkNotNull(endpoints);
    if (!isOrderingCompatible(endpoints)) {
      return false;
    }
    return !edgesConnecting(endpoints.nodeU(), endpoints.nodeV()).isEmpty();
  }




  
  protected final void validateEndpoints(EndpointPair<?> endpoints) {
    Preconditions.checkNotNull(endpoints);
    Preconditions.checkArgument(isOrderingCompatible(endpoints), "Mismatch: unordered endpoints cannot be used with directed graphs");
  }
  
  protected final boolean isOrderingCompatible(EndpointPair<?> endpoints) {
    return (endpoints.isOrdered() || !isDirected());
  }

  
  public final boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof Network)) {
      return false;
    }
    Network<?, ?> other = (Network<?, ?>)obj;
    
    return (isDirected() == other.isDirected() && 
      nodes().equals(other.nodes()) && 
      edgeIncidentNodesMap(this).equals(edgeIncidentNodesMap(other)));
  }

  
  public final int hashCode() {
    return edgeIncidentNodesMap(this).hashCode();
  }


  
  public String toString() {
    return "isDirected: " + 
      isDirected() + ", allowsParallelEdges: " + 
      
      allowsParallelEdges() + ", allowsSelfLoops: " + 
      
      allowsSelfLoops() + ", nodes: " + 
      
      nodes() + ", edges: " + 
      
      edgeIncidentNodesMap(this);
  }
  
  private static <N, E> Map<E, EndpointPair<N>> edgeIncidentNodesMap(final Network<N, E> network) {
    Function<E, EndpointPair<N>> edgeToIncidentNodesFn = new Function<E, EndpointPair<N>>()
      {
        public EndpointPair<N> apply(E edge)
        {
          return network.incidentNodes(edge);
        }
      };
    return Maps.asMap(network.edges(), edgeToIncidentNodesFn);
  }
}
