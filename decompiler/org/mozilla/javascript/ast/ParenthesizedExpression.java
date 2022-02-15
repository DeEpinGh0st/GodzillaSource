package org.mozilla.javascript.ast;

















public class ParenthesizedExpression
  extends AstNode
{
  private AstNode expression;
  
  public ParenthesizedExpression() {}
  
  public ParenthesizedExpression(int pos) {
    super(pos);
  }
  
  public ParenthesizedExpression(int pos, int len) {
    super(pos, len);
  }
  
  public ParenthesizedExpression(AstNode expr) {
    this((expr != null) ? expr.getPosition() : 0, (expr != null) ? expr.getLength() : 1, expr);
  }


  
  public ParenthesizedExpression(int pos, int len, AstNode expr) {
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
    return makeIndent(depth) + "(" + this.expression.toSource(0) + ")";
  }




  
  public void visit(NodeVisitor v) {
    if (v.visit(this))
      this.expression.visit(v); 
  }
}
