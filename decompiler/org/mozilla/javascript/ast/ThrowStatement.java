package org.mozilla.javascript.ast;



















public class ThrowStatement
  extends AstNode
{
  private AstNode expression;
  
  public ThrowStatement() {}
  
  public ThrowStatement(int pos) {
    super(pos);
  }
  
  public ThrowStatement(int pos, int len) {
    super(pos, len);
  }
  
  public ThrowStatement(AstNode expr) {
    setExpression(expr);
  }
  
  public ThrowStatement(int pos, AstNode expr) {
    super(pos, expr.getLength());
    setExpression(expr);
  }
  
  public ThrowStatement(int pos, int len, AstNode expr) {
    super(pos, len);
    setExpression(expr);
  }



  
  public AstNode getExpression() {
    return this.expression;
  }





  
  public void setExpression(AstNode expression) {
    assertNotNull(expression);
    this.expression = expression;
    expression.setParent(this);
  }

  
  public String toSource(int depth) {
    StringBuilder sb = new StringBuilder();
    sb.append(makeIndent(depth));
    sb.append("throw");
    sb.append(" ");
    sb.append(this.expression.toSource(0));
    sb.append(";\n");
    return sb.toString();
  }




  
  public void visit(NodeVisitor v) {
    if (v.visit(this))
      this.expression.visit(v); 
  }
}
