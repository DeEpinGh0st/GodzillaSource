package org.mozilla.javascript.ast;









public abstract class Loop
  extends Scope
{
  protected AstNode body;
  protected int lp = -1;
  protected int rp = -1;

  
  public Loop() {}
  
  public Loop(int pos) {
    super(pos);
  }
  
  public Loop(int pos, int len) {
    super(pos, len);
  }



  
  public AstNode getBody() {
    return this.body;
  }





  
  public void setBody(AstNode body) {
    this.body = body;
    int end = body.getPosition() + body.getLength();
    setLength(end - getPosition());
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
}
