package org.mozilla.javascript.ast;

import org.mozilla.javascript.Node;




















public class Block
  extends AstNode
{
  public Block() {}
  
  public Block(int pos) {
    super(pos);
  }
  
  public Block(int pos, int len) {
    super(pos, len);
  }



  
  public void addStatement(AstNode statement) {
    addChild(statement);
  }

  
  public String toSource(int depth) {
    StringBuilder sb = new StringBuilder();
    sb.append(makeIndent(depth));
    sb.append("{\n");
    for (Node kid : this) {
      sb.append(((AstNode)kid).toSource(depth + 1));
    }
    sb.append(makeIndent(depth));
    sb.append("}\n");
    return sb.toString();
  }

  
  public void visit(NodeVisitor v) {
    if (v.visit(this))
      for (Node kid : this)
        ((AstNode)kid).visit(v);  
  }
}
