package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.UnmodifiableIterator;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;









































































@Beta
public abstract class Traverser<N>
{
  public static <N> Traverser<N> forGraph(SuccessorsFunction<N> graph) {
    Preconditions.checkNotNull(graph);
    return new GraphTraverser<>(graph);
  }









































































  
  public static <N> Traverser<N> forTree(SuccessorsFunction<N> tree) {
    Preconditions.checkNotNull(tree);
    if (tree instanceof BaseGraph) {
      Preconditions.checkArgument(((BaseGraph)tree).isDirected(), "Undirected graphs can never be trees.");
    }
    if (tree instanceof Network) {
      Preconditions.checkArgument(((Network)tree).isDirected(), "Undirected networks can never be trees.");
    }
    return new TreeTraverser<>(tree);
  }














  
  private Traverser() {}














  
  public abstract Iterable<N> breadthFirst(N paramN);














  
  public abstract Iterable<N> breadthFirst(Iterable<? extends N> paramIterable);














  
  public abstract Iterable<N> depthFirstPreOrder(N paramN);














  
  public abstract Iterable<N> depthFirstPreOrder(Iterable<? extends N> paramIterable);













  
  public abstract Iterable<N> depthFirstPostOrder(N paramN);













  
  public abstract Iterable<N> depthFirstPostOrder(Iterable<? extends N> paramIterable);













  
  private static final class GraphTraverser<N>
    extends Traverser<N>
  {
    private final SuccessorsFunction<N> graph;













    
    GraphTraverser(SuccessorsFunction<N> graph) {
      this.graph = (SuccessorsFunction<N>)Preconditions.checkNotNull(graph);
    }

    
    public Iterable<N> breadthFirst(N startNode) {
      Preconditions.checkNotNull(startNode);
      return breadthFirst((Iterable<? extends N>)ImmutableSet.of(startNode));
    }

    
    public Iterable<N> breadthFirst(final Iterable<? extends N> startNodes) {
      Preconditions.checkNotNull(startNodes);
      if (Iterables.isEmpty(startNodes)) {
        return (Iterable<N>)ImmutableSet.of();
      }
      for (N startNode : startNodes) {
        checkThatNodeIsInGraph(startNode);
      }
      return new Iterable<N>()
        {
          public Iterator<N> iterator() {
            return (Iterator<N>)new Traverser.GraphTraverser.BreadthFirstIterator(startNodes);
          }
        };
    }

    
    public Iterable<N> depthFirstPreOrder(N startNode) {
      Preconditions.checkNotNull(startNode);
      return depthFirstPreOrder((Iterable<? extends N>)ImmutableSet.of(startNode));
    }

    
    public Iterable<N> depthFirstPreOrder(final Iterable<? extends N> startNodes) {
      Preconditions.checkNotNull(startNodes);
      if (Iterables.isEmpty(startNodes)) {
        return (Iterable<N>)ImmutableSet.of();
      }
      for (N startNode : startNodes) {
        checkThatNodeIsInGraph(startNode);
      }
      return new Iterable<N>()
        {
          public Iterator<N> iterator() {
            return (Iterator<N>)new Traverser.GraphTraverser.DepthFirstIterator(startNodes, Traverser.Order.PREORDER);
          }
        };
    }

    
    public Iterable<N> depthFirstPostOrder(N startNode) {
      Preconditions.checkNotNull(startNode);
      return depthFirstPostOrder((Iterable<? extends N>)ImmutableSet.of(startNode));
    }

    
    public Iterable<N> depthFirstPostOrder(final Iterable<? extends N> startNodes) {
      Preconditions.checkNotNull(startNodes);
      if (Iterables.isEmpty(startNodes)) {
        return (Iterable<N>)ImmutableSet.of();
      }
      for (N startNode : startNodes) {
        checkThatNodeIsInGraph(startNode);
      }
      return new Iterable<N>()
        {
          public Iterator<N> iterator() {
            return (Iterator<N>)new Traverser.GraphTraverser.DepthFirstIterator(startNodes, Traverser.Order.POSTORDER);
          }
        };
    }



    
    private void checkThatNodeIsInGraph(N startNode) {
      this.graph.successors(startNode);
    }
    
    private final class BreadthFirstIterator extends UnmodifiableIterator<N> {
      private final Queue<N> queue = new ArrayDeque<>();
      private final Set<N> visited = new HashSet<>();
      
      BreadthFirstIterator(Iterable<? extends N> roots) {
        for (N root : roots) {
          
          if (this.visited.add(root)) {
            this.queue.add(root);
          }
        } 
      }

      
      public boolean hasNext() {
        return !this.queue.isEmpty();
      }

      
      public N next() {
        N current = this.queue.remove();
        for (N neighbor : Traverser.GraphTraverser.this.graph.successors(current)) {
          if (this.visited.add(neighbor)) {
            this.queue.add(neighbor);
          }
        } 
        return current;
      }
    }
    
    private final class DepthFirstIterator extends AbstractIterator<N> {
      private final Deque<NodeAndSuccessors> stack = new ArrayDeque<>();
      private final Set<N> visited = new HashSet<>();
      private final Traverser.Order order;
      
      DepthFirstIterator(Iterable<? extends N> roots, Traverser.Order order) {
        this.stack.push(new NodeAndSuccessors(null, roots));
        this.order = order;
      }

      
      protected N computeNext() {
        while (true) {
          if (this.stack.isEmpty()) {
            return (N)endOfData();
          }
          NodeAndSuccessors nodeAndSuccessors = this.stack.getFirst();
          boolean firstVisit = this.visited.add(nodeAndSuccessors.node);
          boolean lastVisit = !nodeAndSuccessors.successorIterator.hasNext();
          boolean produceNode = ((firstVisit && this.order == Traverser.Order.PREORDER) || (lastVisit && this.order == Traverser.Order.POSTORDER));
          
          if (lastVisit) {
            this.stack.pop();
          } else {
            
            N successor = nodeAndSuccessors.successorIterator.next();
            if (!this.visited.contains(successor)) {
              this.stack.push(withSuccessors(successor));
            }
          } 
          if (produceNode && nodeAndSuccessors.node != null) {
            return nodeAndSuccessors.node;
          }
        } 
      }
      
      NodeAndSuccessors withSuccessors(N node) {
        return new NodeAndSuccessors(node, Traverser.GraphTraverser.this.graph.successors(node));
      }
      
      private final class NodeAndSuccessors
      {
        final N node;
        final Iterator<? extends N> successorIterator;
        
        NodeAndSuccessors(N node, Iterable<? extends N> successors) {
          this.node = node;
          this.successorIterator = successors.iterator();
        }
      }
    }
  }
  
  private static final class TreeTraverser<N> extends Traverser<N> {
    private final SuccessorsFunction<N> tree;
    
    TreeTraverser(SuccessorsFunction<N> tree) {
      this.tree = (SuccessorsFunction<N>)Preconditions.checkNotNull(tree);
    }

    
    public Iterable<N> breadthFirst(N startNode) {
      Preconditions.checkNotNull(startNode);
      return breadthFirst((Iterable<? extends N>)ImmutableSet.of(startNode));
    }

    
    public Iterable<N> breadthFirst(final Iterable<? extends N> startNodes) {
      Preconditions.checkNotNull(startNodes);
      if (Iterables.isEmpty(startNodes)) {
        return (Iterable<N>)ImmutableSet.of();
      }
      for (N startNode : startNodes) {
        checkThatNodeIsInTree(startNode);
      }
      return new Iterable<N>()
        {
          public Iterator<N> iterator() {
            return (Iterator<N>)new Traverser.TreeTraverser.BreadthFirstIterator(startNodes);
          }
        };
    }

    
    public Iterable<N> depthFirstPreOrder(N startNode) {
      Preconditions.checkNotNull(startNode);
      return depthFirstPreOrder((Iterable<? extends N>)ImmutableSet.of(startNode));
    }

    
    public Iterable<N> depthFirstPreOrder(final Iterable<? extends N> startNodes) {
      Preconditions.checkNotNull(startNodes);
      if (Iterables.isEmpty(startNodes)) {
        return (Iterable<N>)ImmutableSet.of();
      }
      for (N node : startNodes) {
        checkThatNodeIsInTree(node);
      }
      return new Iterable<N>()
        {
          public Iterator<N> iterator() {
            return (Iterator<N>)new Traverser.TreeTraverser.DepthFirstPreOrderIterator(startNodes);
          }
        };
    }

    
    public Iterable<N> depthFirstPostOrder(N startNode) {
      Preconditions.checkNotNull(startNode);
      return depthFirstPostOrder((Iterable<? extends N>)ImmutableSet.of(startNode));
    }

    
    public Iterable<N> depthFirstPostOrder(final Iterable<? extends N> startNodes) {
      Preconditions.checkNotNull(startNodes);
      if (Iterables.isEmpty(startNodes)) {
        return (Iterable<N>)ImmutableSet.of();
      }
      for (N startNode : startNodes) {
        checkThatNodeIsInTree(startNode);
      }
      return new Iterable<N>()
        {
          public Iterator<N> iterator() {
            return (Iterator<N>)new Traverser.TreeTraverser.DepthFirstPostOrderIterator(startNodes);
          }
        };
    }



    
    private void checkThatNodeIsInTree(N startNode) {
      this.tree.successors(startNode);
    }
    
    private final class BreadthFirstIterator extends UnmodifiableIterator<N> {
      private final Queue<N> queue = new ArrayDeque<>();
      
      BreadthFirstIterator(Iterable<? extends N> roots) {
        for (N root : roots) {
          this.queue.add(root);
        }
      }

      
      public boolean hasNext() {
        return !this.queue.isEmpty();
      }

      
      public N next() {
        N current = this.queue.remove();
        Iterables.addAll(this.queue, Traverser.TreeTraverser.this.tree.successors(current));
        return current;
      }
    }
    
    private final class DepthFirstPreOrderIterator extends UnmodifiableIterator<N> {
      private final Deque<Iterator<? extends N>> stack = new ArrayDeque<>();
      
      DepthFirstPreOrderIterator(Iterable<? extends N> roots) {
        this.stack.addLast(roots.iterator());
      }

      
      public boolean hasNext() {
        return !this.stack.isEmpty();
      }

      
      public N next() {
        Iterator<? extends N> iterator = this.stack.getLast();
        N result = (N)Preconditions.checkNotNull(iterator.next());
        if (!iterator.hasNext()) {
          this.stack.removeLast();
        }
        Iterator<? extends N> childIterator = Traverser.TreeTraverser.this.tree.successors(result).iterator();
        if (childIterator.hasNext()) {
          this.stack.addLast(childIterator);
        }
        return result;
      }
    }
    
    private final class DepthFirstPostOrderIterator extends AbstractIterator<N> {
      private final ArrayDeque<NodeAndChildren> stack = new ArrayDeque<>();
      
      DepthFirstPostOrderIterator(Iterable<? extends N> roots) {
        this.stack.addLast(new NodeAndChildren(null, roots));
      }

      
      protected N computeNext() {
        while (!this.stack.isEmpty()) {
          NodeAndChildren top = this.stack.getLast();
          if (top.childIterator.hasNext()) {
            N child = top.childIterator.next();
            this.stack.addLast(withChildren(child)); continue;
          } 
          this.stack.removeLast();
          if (top.node != null) {
            return top.node;
          }
        } 
        
        return (N)endOfData();
      }
      
      NodeAndChildren withChildren(N node) {
        return new NodeAndChildren(node, Traverser.TreeTraverser.this.tree.successors(node));
      }
      
      private final class NodeAndChildren
      {
        final N node;
        final Iterator<? extends N> childIterator;
        
        NodeAndChildren(N node, Iterable<? extends N> children) {
          this.node = node;
          this.childIterator = children.iterator();
        }
      }
    }
  }
  
  private enum Order {
    PREORDER,
    POSTORDER;
  }
}
