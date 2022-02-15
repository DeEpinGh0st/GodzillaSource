package javassist.bytecode.analysis;

import java.util.ArrayList;
import java.util.List;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.stackmap.BasicBlock;








































public class ControlFlow
{
  private CtClass clazz;
  private MethodInfo methodInfo;
  private Block[] basicBlocks;
  private Frame[] frames;
  
  public ControlFlow(CtMethod method) throws BadBytecode {
    this(method.getDeclaringClass(), method.getMethodInfo2());
  }



  
  public ControlFlow(CtClass ctclazz, MethodInfo minfo) throws BadBytecode {
    this.clazz = ctclazz;
    this.methodInfo = minfo;
    this.frames = null;
    this







      
      .basicBlocks = (Block[])(new BasicBlock.Maker() { protected BasicBlock makeBlock(int pos) { return new ControlFlow.Block(pos, ControlFlow.this.methodInfo); } protected BasicBlock[] makeArray(int size) { return (BasicBlock[])new ControlFlow.Block[size]; } }).make(minfo);
    if (this.basicBlocks == null)
      this.basicBlocks = new Block[0]; 
    int size = this.basicBlocks.length;
    int[] counters = new int[size]; int i;
    for (i = 0; i < size; i++) {
      Block b = this.basicBlocks[i];
      b.index = i;
      b.entrances = new Block[b.incomings()];
      counters[i] = 0;
    } 
    
    for (i = 0; i < size; i++) {
      Block b = this.basicBlocks[i];
      for (int k = 0; k < b.exits(); k++) {
        Block e = b.exit(k);
        counters[e.index] = counters[e.index] + 1; e.entrances[counters[e.index]] = b;
      } 
      
      Catcher[] catchers = b.catchers();
      for (int j = 0; j < catchers.length; j++) {
        Block catchBlock = (catchers[j]).node;
        counters[catchBlock.index] = counters[catchBlock.index] + 1; catchBlock.entrances[counters[catchBlock.index]] = b;
      } 
    } 
  }






  
  public Block[] basicBlocks() {
    return this.basicBlocks;
  }








  
  public Frame frameAt(int pos) throws BadBytecode {
    if (this.frames == null) {
      this.frames = (new Analyzer()).analyze(this.clazz, this.methodInfo);
    }
    return this.frames[pos];
  }




















  
  public Node[] dominatorTree() {
    int size = this.basicBlocks.length;
    if (size == 0) {
      return null;
    }
    Node[] nodes = new Node[size];
    boolean[] visited = new boolean[size];
    int[] distance = new int[size];
    for (int i = 0; i < size; i++) {
      nodes[i] = new Node(this.basicBlocks[i]);
      visited[i] = false;
    } 
    
    Access access = new Access(nodes) {
        BasicBlock[] exits(ControlFlow.Node n) {
          return n.block.getExit();
        }
        BasicBlock[] entrances(ControlFlow.Node n) { return (BasicBlock[])n.block.entrances; }
      };
    nodes[0].makeDepth1stTree(null, visited, 0, distance, access);
    while (true) {
      for (int j = 0; j < size; j++)
        visited[j] = false; 
      if (!nodes[0].makeDominatorTree(visited, distance, access)) {
        Node.setChildren(nodes);
        return nodes;
      } 
    } 
  }

















  
  public Node[] postDominatorTree() {
    boolean changed;
    int size = this.basicBlocks.length;
    if (size == 0) {
      return null;
    }
    Node[] nodes = new Node[size];
    boolean[] visited = new boolean[size];
    int[] distance = new int[size];
    for (int i = 0; i < size; i++) {
      nodes[i] = new Node(this.basicBlocks[i]);
      visited[i] = false;
    } 
    
    Access access = new Access(nodes) {
        BasicBlock[] exits(ControlFlow.Node n) {
          return (BasicBlock[])n.block.entrances;
        } BasicBlock[] entrances(ControlFlow.Node n) {
          return n.block.getExit();
        }
      };
    int counter = 0;
    for (int j = 0; j < size; j++) {
      if ((nodes[j]).block.exits() == 0)
        counter = nodes[j].makeDepth1stTree(null, visited, counter, distance, access); 
    } 
    do {
      int k;
      for (k = 0; k < size; k++) {
        visited[k] = false;
      }
      changed = false;
      for (k = 0; k < size; k++)
      { if ((nodes[k]).block.exits() == 0 && 
          nodes[k].makeDominatorTree(visited, distance, access))
          changed = true;  } 
    } while (changed);
    
    Node.setChildren(nodes);
    return nodes;
  }











  
  public static class Block
    extends BasicBlock
  {
    public Object clientData = null;
    
    int index;
    MethodInfo method;
    Block[] entrances;
    
    Block(int pos, MethodInfo minfo) {
      super(pos);
      this.method = minfo;
    }

    
    protected void toString2(StringBuffer sbuf) {
      super.toString2(sbuf);
      sbuf.append(", incoming{");
      for (int i = 0; i < this.entrances.length; i++) {
        sbuf.append((this.entrances[i]).position).append(", ");
      }
      sbuf.append("}");
    }
    BasicBlock[] getExit() {
      return this.exit;
    }





    
    public int index() {
      return this.index;
    }


    
    public int position() {
      return this.position;
    }

    
    public int length() {
      return this.length;
    }

    
    public int incomings() {
      return this.incoming;
    }


    
    public Block incoming(int n) {
      return this.entrances[n];
    }



    
    public int exits() {
      return (this.exit == null) ? 0 : this.exit.length;
    }




    
    public Block exit(int n) {
      return (Block)this.exit[n];
    }



    
    public ControlFlow.Catcher[] catchers() {
      List<ControlFlow.Catcher> catchers = new ArrayList<>();
      BasicBlock.Catch c = this.toCatch;
      while (c != null) {
        catchers.add(new ControlFlow.Catcher(c));
        c = c.next;
      } 
      
      return catchers.<ControlFlow.Catcher>toArray(new ControlFlow.Catcher[catchers.size()]);
    }
  }
  
  static abstract class Access { ControlFlow.Node[] all;
    
    Access(ControlFlow.Node[] nodes) { this.all = nodes; } ControlFlow.Node node(BasicBlock b) {
      return this.all[((ControlFlow.Block)b).index];
    }
    
    abstract BasicBlock[] exits(ControlFlow.Node param1Node);
    
    abstract BasicBlock[] entrances(ControlFlow.Node param1Node); }

  
  public static class Node {
    private ControlFlow.Block block;
    private Node parent;
    private Node[] children;
    
    Node(ControlFlow.Block b) {
      this.block = b;
      this.parent = null;
    }




    
    public String toString() {
      StringBuffer sbuf = new StringBuffer();
      sbuf.append("Node[pos=").append(block().position());
      sbuf.append(", parent=");
      sbuf.append((this.parent == null) ? "*" : Integer.toString(this.parent.block().position()));
      sbuf.append(", children{");
      for (int i = 0; i < this.children.length; i++) {
        sbuf.append(this.children[i].block().position()).append(", ");
      }
      sbuf.append("}]");
      return sbuf.toString();
    }


    
    public ControlFlow.Block block() {
      return this.block;
    }

    
    public Node parent() {
      return this.parent;
    }

    
    public int children() {
      return this.children.length;
    }



    
    public Node child(int n) {
      return this.children[n];
    }




    
    int makeDepth1stTree(Node caller, boolean[] visited, int counter, int[] distance, ControlFlow.Access access) {
      int index = this.block.index;
      if (visited[index]) {
        return counter;
      }
      visited[index] = true;
      this.parent = caller;
      BasicBlock[] exits = access.exits(this);
      if (exits != null) {
        for (int i = 0; i < exits.length; i++) {
          Node n = access.node(exits[i]);
          counter = n.makeDepth1stTree(this, visited, counter, distance, access);
        } 
      }
      distance[index] = counter++;
      return counter;
    }
    
    boolean makeDominatorTree(boolean[] visited, int[] distance, ControlFlow.Access access) {
      int index = this.block.index;
      if (visited[index]) {
        return false;
      }
      visited[index] = true;
      boolean changed = false;
      BasicBlock[] exits = access.exits(this);
      if (exits != null)
        for (int i = 0; i < exits.length; i++) {
          Node n = access.node(exits[i]);
          if (n.makeDominatorTree(visited, distance, access)) {
            changed = true;
          }
        }  
      BasicBlock[] entrances = access.entrances(this);
      if (entrances != null) {
        for (int i = 0; i < entrances.length; i++) {
          if (this.parent != null) {
            Node n = getAncestor(this.parent, access.node(entrances[i]), distance);
            if (n != this.parent) {
              this.parent = n;
              changed = true;
            } 
          } 
        } 
      }
      return changed;
    }
    
    private static Node getAncestor(Node n1, Node n2, int[] distance) {
      while (n1 != n2) {
        if (distance[n1.block.index] < distance[n2.block.index]) {
          n1 = n1.parent;
        } else {
          n2 = n2.parent;
        } 
        if (n1 == null || n2 == null) {
          return null;
        }
      } 
      return n1;
    }
    
    private static void setChildren(Node[] all) {
      int size = all.length;
      int[] nchildren = new int[size]; int i;
      for (i = 0; i < size; i++) {
        nchildren[i] = 0;
      }
      for (i = 0; i < size; i++) {
        Node p = (all[i]).parent;
        if (p != null) {
          nchildren[p.block.index] = nchildren[p.block.index] + 1;
        }
      } 
      for (i = 0; i < size; i++) {
        (all[i]).children = new Node[nchildren[i]];
      }
      for (i = 0; i < size; i++) {
        nchildren[i] = 0;
      }
      for (i = 0; i < size; i++) {
        Node n = all[i];
        Node p = n.parent;
        if (p != null) {
          nchildren[p.block.index] = nchildren[p.block.index] + 1; p.children[nchildren[p.block.index]] = n;
        } 
      } 
    }
  }

  
  public static class Catcher
  {
    private ControlFlow.Block node;
    private int typeIndex;
    
    Catcher(BasicBlock.Catch c) {
      this.node = (ControlFlow.Block)c.body;
      this.typeIndex = c.typeIndex;
    }


    
    public ControlFlow.Block block() {
      return this.node;
    }



    
    public String type() {
      if (this.typeIndex == 0) {
        return "java.lang.Throwable";
      }
      return this.node.method.getConstPool().getClassInfo(this.typeIndex);
    }
  }
}
