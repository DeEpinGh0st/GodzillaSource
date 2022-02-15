package org.mozilla.javascript.ast;



















public class WhileLoop
  extends Loop
{
  private AstNode condition;
  
  public WhileLoop() {}
  
  public WhileLoop(int pos) {
    super(pos);
  }
  
  public WhileLoop(int pos, int len) {
    super(pos, len);
  }



  
  public AstNode getCondition() {
    return this.condition;
  }




  
  public void setCondition(AstNode condition) {
    assertNotNull(condition);
    this.condition = condition;
    condition.setParent(this);
  }

  
  public String toSource(int depth) {
    StringBuilder sb = new StringBuilder();
    sb.append(makeIndent(depth));
    sb.append("while (");
    sb.append(this.condition.toSource(0));
    sb.append(") ");
    if (this.body.getType() == 129) {
      sb.append(this.body.toSource(depth).trim());
      sb.append("\n");
    } else {
      sb.append("\n").append(this.body.toSource(depth + 1));
    } 
    return sb.toString();
  }




  
  public void visit(NodeVisitor v) {
    if (v.visit(this)) {
      this.condition.visit(v);
      this.body.visit(v);
    } 
  }
}
