package org.mozilla.javascript.ast;

















public class EmptyStatement
  extends AstNode
{
  public EmptyStatement() {}
  
  public EmptyStatement(int pos) {
    super(pos);
  }
  
  public EmptyStatement(int pos, int len) {
    super(pos, len);
  }

  
  public String toSource(int depth) {
    StringBuilder sb = new StringBuilder();
    sb.append(makeIndent(depth)).append(";\n");
    return sb.toString();
  }




  
  public void visit(NodeVisitor v) {
    v.visit(this);
  }
}
