package org.mozilla.javascript.ast;

















public class ErrorNode
  extends AstNode
{
  private String message;
  
  public ErrorNode() {}
  
  public ErrorNode(int pos) {
    super(pos);
  }
  
  public ErrorNode(int pos, int len) {
    super(pos, len);
  }



  
  public String getMessage() {
    return this.message;
  }



  
  public void setMessage(String message) {
    this.message = message;
  }

  
  public String toSource(int depth) {
    return "";
  }





  
  public void visit(NodeVisitor v) {
    v.visit(this);
  }
}
