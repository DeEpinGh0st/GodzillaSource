package com.kitfox.svg.animation.parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;






public class JJTAnimTimeParserState
  implements Serializable
{
  private List<Node> nodes = new ArrayList<Node>();
  private List<Integer> marks = new ArrayList<Integer>();
  private int sp = 0;
  private int mk = 0;

  
  private boolean node_created;

  
  public boolean nodeCreated() {
    return this.node_created;
  }


  
  public void reset() {
    this.nodes.clear();
    this.marks.clear();
    this.sp = 0;
    this.mk = 0;
  }


  
  public Node rootNode() {
    return this.nodes.get(0);
  }

  
  public void pushNode(Node n) {
    this.nodes.add(n);
    this.sp++;
  }


  
  public Node popNode() {
    this.sp--;
    if (this.sp < this.mk) {
      this.mk = ((Integer)this.marks.remove(this.marks.size() - 1)).intValue();
    }
    return this.nodes.remove(this.nodes.size() - 1);
  }

  
  public Node peekNode() {
    return this.nodes.get(this.nodes.size() - 1);
  }


  
  public int nodeArity() {
    return this.sp - this.mk;
  }

  
  public void clearNodeScope(Node n) {
    while (this.sp > this.mk) {
      popNode();
    }
    this.mk = ((Integer)this.marks.remove(this.marks.size() - 1)).intValue();
  }
  
  public void openNodeScope(Node n) {
    this.marks.add(Integer.valueOf(this.mk));
    this.mk = this.sp;
    n.jjtOpen();
  }




  
  public void closeNodeScope(Node n, int numIn) {
    this.mk = ((Integer)this.marks.remove(this.marks.size() - 1)).intValue();
    int num = numIn;
    while (num-- > 0) {
      Node c = popNode();
      c.jjtSetParent(n);
      n.jjtAddChild(c, num);
    } 
    n.jjtClose();
    pushNode(n);
    this.node_created = true;
  }






  
  public void closeNodeScope(Node n, boolean condition) {
    if (condition) {
      int a = nodeArity();
      this.mk = ((Integer)this.marks.remove(this.marks.size() - 1)).intValue();
      while (a-- > 0) {
        Node c = popNode();
        c.jjtSetParent(n);
        n.jjtAddChild(c, a);
      } 
      n.jjtClose();
      pushNode(n);
      this.node_created = true;
    } else {
      this.mk = ((Integer)this.marks.remove(this.marks.size() - 1)).intValue();
      this.node_created = false;
    } 
  }
}
