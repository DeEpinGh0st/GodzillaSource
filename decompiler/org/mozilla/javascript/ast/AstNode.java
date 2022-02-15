package org.mozilla.javascript.ast;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.Token;



















































public abstract class AstNode
  extends Node
  implements Comparable<AstNode>
{
  protected int position = -1;
  protected int length = 1;
  
  protected AstNode parent;
  private static Map<Integer, String> operatorNames = new HashMap<Integer, String>();

  
  static {
    operatorNames.put(Integer.valueOf(52), "in");
    operatorNames.put(Integer.valueOf(32), "typeof");
    operatorNames.put(Integer.valueOf(53), "instanceof");
    operatorNames.put(Integer.valueOf(31), "delete");
    operatorNames.put(Integer.valueOf(89), ",");
    operatorNames.put(Integer.valueOf(103), ":");
    operatorNames.put(Integer.valueOf(104), "||");
    operatorNames.put(Integer.valueOf(105), "&&");
    operatorNames.put(Integer.valueOf(106), "++");
    operatorNames.put(Integer.valueOf(107), "--");
    operatorNames.put(Integer.valueOf(9), "|");
    operatorNames.put(Integer.valueOf(10), "^");
    operatorNames.put(Integer.valueOf(11), "&");
    operatorNames.put(Integer.valueOf(12), "==");
    operatorNames.put(Integer.valueOf(13), "!=");
    operatorNames.put(Integer.valueOf(14), "<");
    operatorNames.put(Integer.valueOf(16), ">");
    operatorNames.put(Integer.valueOf(15), "<=");
    operatorNames.put(Integer.valueOf(17), ">=");
    operatorNames.put(Integer.valueOf(18), "<<");
    operatorNames.put(Integer.valueOf(19), ">>");
    operatorNames.put(Integer.valueOf(20), ">>>");
    operatorNames.put(Integer.valueOf(21), "+");
    operatorNames.put(Integer.valueOf(22), "-");
    operatorNames.put(Integer.valueOf(23), "*");
    operatorNames.put(Integer.valueOf(24), "/");
    operatorNames.put(Integer.valueOf(25), "%");
    operatorNames.put(Integer.valueOf(26), "!");
    operatorNames.put(Integer.valueOf(27), "~");
    operatorNames.put(Integer.valueOf(28), "+");
    operatorNames.put(Integer.valueOf(29), "-");
    operatorNames.put(Integer.valueOf(46), "===");
    operatorNames.put(Integer.valueOf(47), "!==");
    operatorNames.put(Integer.valueOf(90), "=");
    operatorNames.put(Integer.valueOf(91), "|=");
    operatorNames.put(Integer.valueOf(93), "&=");
    operatorNames.put(Integer.valueOf(94), "<<=");
    operatorNames.put(Integer.valueOf(95), ">>=");
    operatorNames.put(Integer.valueOf(96), ">>>=");
    operatorNames.put(Integer.valueOf(97), "+=");
    operatorNames.put(Integer.valueOf(98), "-=");
    operatorNames.put(Integer.valueOf(99), "*=");
    operatorNames.put(Integer.valueOf(100), "/=");
    operatorNames.put(Integer.valueOf(101), "%=");
    operatorNames.put(Integer.valueOf(92), "^=");
    operatorNames.put(Integer.valueOf(126), "void");
  }


  
  public static class PositionComparator
    implements Comparator<AstNode>, Serializable
  {
    private static final long serialVersionUID = 1L;

    
    public int compare(AstNode n1, AstNode n2) {
      return n1.position - n2.position;
    }
  }
  
  public AstNode() {
    super(-1);
  }




  
  public AstNode(int pos) {
    this();
    this.position = pos;
  }






  
  public AstNode(int pos, int len) {
    this();
    this.position = pos;
    this.length = len;
  }



  
  public int getPosition() {
    return this.position;
  }



  
  public void setPosition(int position) {
    this.position = position;
  }





  
  public int getAbsolutePosition() {
    int pos = this.position;
    AstNode parent = this.parent;
    while (parent != null) {
      pos += parent.getPosition();
      parent = parent.getParent();
    } 
    return pos;
  }



  
  public int getLength() {
    return this.length;
  }



  
  public void setLength(int length) {
    this.length = length;
  }




  
  public void setBounds(int position, int end) {
    setPosition(position);
    setLength(end - position);
  }







  
  public void setRelative(int parentPosition) {
    this.position -= parentPosition;
  }



  
  public AstNode getParent() {
    return this.parent;
  }





  
  public void setParent(AstNode parent) {
    if (parent == this.parent) {
      return;
    }

    
    if (this.parent != null) {
      setRelative(-this.parent.getPosition());
    }
    
    this.parent = parent;
    if (parent != null) {
      setRelative(parent.getPosition());
    }
  }








  
  public void addChild(AstNode kid) {
    assertNotNull(kid);
    int end = kid.getPosition() + kid.getLength();
    setLength(end - getPosition());
    addChildToBack(kid);
    kid.setParent(this);
  }





  
  public AstRoot getAstRoot() {
    AstNode parent = this;
    while (parent != null && !(parent instanceof AstRoot)) {
      parent = parent.getParent();
    }
    return (AstRoot)parent;
  }



















  
  public String toSource() {
    return toSource(0);
  }




  
  public String makeIndent(int indent) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < indent; i++) {
      sb.append("  ");
    }
    return sb.toString();
  }




  
  public String shortName() {
    String classname = getClass().getName();
    int last = classname.lastIndexOf(".");
    return classname.substring(last + 1);
  }





  
  public static String operatorToString(int op) {
    String result = operatorNames.get(Integer.valueOf(op));
    if (result == null)
      throw new IllegalArgumentException("Invalid operator: " + op); 
    return result;
  }






















  
  public boolean hasSideEffects() {
    switch (getType()) {
      case -1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 30:
      case 31:
      case 35:
      case 37:
      case 38:
      case 50:
      case 51:
      case 56:
      case 57:
      case 64:
      case 68:
      case 69:
      case 70:
      case 72:
      case 81:
      case 82:
      case 90:
      case 91:
      case 92:
      case 93:
      case 94:
      case 95:
      case 96:
      case 97:
      case 98:
      case 99:
      case 100:
      case 101:
      case 106:
      case 107:
      case 109:
      case 110:
      case 111:
      case 112:
      case 113:
      case 114:
      case 117:
      case 118:
      case 119:
      case 120:
      case 121:
      case 122:
      case 123:
      case 124:
      case 125:
      case 129:
      case 130:
      case 131:
      case 132:
      case 134:
      case 135:
      case 139:
      case 140:
      case 141:
      case 142:
      case 153:
      case 154:
      case 158:
      case 159:
        return true;
    } 
    
    return false;
  }






  
  protected void assertNotNull(Object arg) {
    if (arg == null) {
      throw new IllegalArgumentException("arg cannot be null");
    }
  }





  
  protected <T extends AstNode> void printList(List<T> items, StringBuilder sb) {
    int max = items.size();
    int count = 0;
    for (AstNode item : items) {
      sb.append(item.toSource(0));
      if (count++ < max - 1) {
        sb.append(", "); continue;
      }  if (item instanceof EmptyExpression) {
        sb.append(",");
      }
    } 
  }





  
  public static RuntimeException codeBug() throws RuntimeException {
    throw Kit.codeBug();
  }

















  
  public FunctionNode getEnclosingFunction() {
    AstNode parent = getParent();
    while (parent != null && !(parent instanceof FunctionNode)) {
      parent = parent.getParent();
    }
    return (FunctionNode)parent;
  }







  
  public Scope getEnclosingScope() {
    AstNode parent = getParent();
    while (parent != null && !(parent instanceof Scope)) {
      parent = parent.getParent();
    }
    return (Scope)parent;
  }











  
  public int compareTo(AstNode other) {
    if (equals(other)) return 0; 
    int abs1 = getAbsolutePosition();
    int abs2 = other.getAbsolutePosition();
    if (abs1 < abs2) return -1; 
    if (abs2 < abs1) return 1; 
    int len1 = getLength();
    int len2 = other.getLength();
    if (len1 < len2) return -1; 
    if (len2 < len1) return 1; 
    return hashCode() - other.hashCode();
  }





  
  public int depth() {
    return (this.parent == null) ? 0 : (1 + this.parent.depth());
  }
  
  protected static class DebugPrintVisitor implements NodeVisitor { private StringBuilder buffer;
    private static final int DEBUG_INDENT = 2;
    
    public DebugPrintVisitor(StringBuilder buf) {
      this.buffer = buf;
    }
    
    public String toString() {
      return this.buffer.toString();
    }
    private String makeIndent(int depth) {
      StringBuilder sb = new StringBuilder(2 * depth);
      for (int i = 0; i < 2 * depth; i++) {
        sb.append(" ");
      }
      return sb.toString();
    }
    public boolean visit(AstNode node) {
      int tt = node.getType();
      String name = Token.typeToName(tt);
      this.buffer.append(node.getAbsolutePosition()).append("\t");
      this.buffer.append(makeIndent(node.depth()));
      this.buffer.append(name).append(" ");
      this.buffer.append(node.getPosition()).append(" ");
      this.buffer.append(node.getLength());
      if (tt == 39) {
        this.buffer.append(" ").append(((Name)node).getIdentifier());
      }
      this.buffer.append("\n");
      return true;
    } }







  
  public int getLineno() {
    if (this.lineno != -1)
      return this.lineno; 
    if (this.parent != null)
      return this.parent.getLineno(); 
    return -1;
  }






  
  public String debugPrint() {
    DebugPrintVisitor dpv = new DebugPrintVisitor(new StringBuilder(1000));
    visit(dpv);
    return dpv.toString();
  }
  
  public abstract String toSource(int paramInt);
  
  public abstract void visit(NodeVisitor paramNodeVisitor);
}
