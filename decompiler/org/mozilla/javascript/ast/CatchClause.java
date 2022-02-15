package org.mozilla.javascript.ast;















public class CatchClause
  extends AstNode
{
  private Name varName;
  private AstNode catchCondition;
  private Block body;
  private int ifPosition = -1;
  private int lp = -1;
  private int rp = -1;



  
  public CatchClause() {}


  
  public CatchClause(int pos) {
    super(pos);
  }
  
  public CatchClause(int pos, int len) {
    super(pos, len);
  }




  
  public Name getVarName() {
    return this.varName;
  }





  
  public void setVarName(Name varName) {
    assertNotNull(varName);
    this.varName = varName;
    varName.setParent(this);
  }




  
  public AstNode getCatchCondition() {
    return this.catchCondition;
  }




  
  public void setCatchCondition(AstNode catchCondition) {
    this.catchCondition = catchCondition;
    if (catchCondition != null) {
      catchCondition.setParent(this);
    }
  }


  
  public Block getBody() {
    return this.body;
  }




  
  public void setBody(Block body) {
    assertNotNull(body);
    this.body = body;
    body.setParent(this);
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




  
  public int getIfPosition() {
    return this.ifPosition;
  }




  
  public void setIfPosition(int ifPosition) {
    this.ifPosition = ifPosition;
  }

  
  public String toSource(int depth) {
    StringBuilder sb = new StringBuilder();
    sb.append(makeIndent(depth));
    sb.append("catch (");
    sb.append(this.varName.toSource(0));
    if (this.catchCondition != null) {
      sb.append(" if ");
      sb.append(this.catchCondition.toSource(0));
    } 
    sb.append(") ");
    sb.append(this.body.toSource(0));
    return sb.toString();
  }





  
  public void visit(NodeVisitor v) {
    if (v.visit(this)) {
      this.varName.visit(v);
      if (this.catchCondition != null) {
        this.catchCondition.visit(v);
      }
      this.body.visit(v);
    } 
  }
}
