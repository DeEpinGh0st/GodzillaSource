package org.mozilla.javascript.ast;














public class WithStatement
  extends AstNode
{
  private AstNode expression;
  private AstNode statement;
  private int lp = -1;
  private int rp = -1;



  
  public WithStatement() {}


  
  public WithStatement(int pos) {
    super(pos);
  }
  
  public WithStatement(int pos, int len) {
    super(pos, len);
  }



  
  public AstNode getExpression() {
    return this.expression;
  }




  
  public void setExpression(AstNode expression) {
    assertNotNull(expression);
    this.expression = expression;
    expression.setParent(this);
  }



  
  public AstNode getStatement() {
    return this.statement;
  }




  
  public void setStatement(AstNode statement) {
    assertNotNull(statement);
    this.statement = statement;
    statement.setParent(this);
  }



  
  public int getLp() {
    return this.lp;
  }



  
  public void setLp(int lp) {
    this.lp = lp;
  }



  
  public int getRp() {
    return this.rp;
  }



  
  public void setRp(int rp) {
    this.rp = rp;
  }



  
  public void setParens(int lp, int rp) {
    this.lp = lp;
    this.rp = rp;
  }

  
  public String toSource(int depth) {
    StringBuilder sb = new StringBuilder();
    sb.append(makeIndent(depth));
    sb.append("with (");
    sb.append(this.expression.toSource(0));
    sb.append(") ");
    if (this.statement.getType() == 129) {
      sb.append(this.statement.toSource(depth).trim());
      sb.append("\n");
    } else {
      sb.append("\n").append(this.statement.toSource(depth + 1));
    } 
    return sb.toString();
  }




  
  public void visit(NodeVisitor v) {
    if (v.visit(this)) {
      this.expression.visit(v);
      this.statement.visit(v);
    } 
  }
}
