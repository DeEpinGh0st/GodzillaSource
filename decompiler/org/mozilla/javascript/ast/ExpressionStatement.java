package org.mozilla.javascript.ast;





















public class ExpressionStatement
  extends AstNode
{
  private AstNode expr;
  
  public void setHasResult() {
    this.type = 134;
  }






  
  public ExpressionStatement() {}





  
  public ExpressionStatement(AstNode expr, boolean hasResult) {
    this(expr);
    if (hasResult) setHasResult();
  
  }







  
  public ExpressionStatement(AstNode expr) {
    this(expr.getPosition(), expr.getLength(), expr);
  }
  
  public ExpressionStatement(int pos, int len) {
    super(pos, len);
  }







  
  public ExpressionStatement(int pos, int len, AstNode expr) {
    super(pos, len);
    setExpression(expr);
  }



  
  public AstNode getExpression() {
    return this.expr;
  }




  
  public void setExpression(AstNode expression) {
    assertNotNull(expression);
    this.expr = expression;
    expression.setParent(this);
    setLineno(expression.getLineno());
  }






  
  public boolean hasSideEffects() {
    return (this.type == 134 || this.expr.hasSideEffects());
  }

  
  public String toSource(int depth) {
    StringBuilder sb = new StringBuilder();
    sb.append(this.expr.toSource(depth));
    sb.append(";\n");
    return sb.toString();
  }




  
  public void visit(NodeVisitor v) {
    if (v.visit(this))
      this.expr.visit(v); 
  }
}
