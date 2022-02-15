package org.mozilla.javascript.ast;


















public class EmptyExpression
  extends AstNode
{
  public EmptyExpression() {}
  
  public EmptyExpression(int pos) {
    super(pos);
  }
  
  public EmptyExpression(int pos, int len) {
    super(pos, len);
  }

  
  public String toSource(int depth) {
    return makeIndent(depth);
  }




  
  public void visit(NodeVisitor v) {
    v.visit(this);
  }
}
