package org.mozilla.javascript.ast;














public class DoLoop
  extends Loop
{
  private AstNode condition;
  private int whilePosition = -1;



  
  public DoLoop() {}


  
  public DoLoop(int pos) {
    super(pos);
  }
  
  public DoLoop(int pos, int len) {
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



  
  public int getWhilePosition() {
    return this.whilePosition;
  }



  
  public void setWhilePosition(int whilePosition) {
    this.whilePosition = whilePosition;
  }

  
  public String toSource(int depth) {
    StringBuilder sb = new StringBuilder();
    sb.append(makeIndent(depth));
    sb.append("do ");
    sb.append(this.body.toSource(depth).trim());
    sb.append(" while (");
    sb.append(this.condition.toSource(0));
    sb.append(");\n");
    return sb.toString();
  }




  
  public void visit(NodeVisitor v) {
    if (v.visit(this)) {
      this.body.visit(v);
      this.condition.visit(v);
    } 
  }
}
