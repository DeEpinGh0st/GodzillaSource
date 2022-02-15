package org.mozilla.javascript.ast;



















public class ReturnStatement
  extends AstNode
{
  private AstNode returnValue;
  
  public ReturnStatement() {}
  
  public ReturnStatement(int pos) {
    super(pos);
  }
  
  public ReturnStatement(int pos, int len) {
    super(pos, len);
  }
  
  public ReturnStatement(int pos, int len, AstNode returnValue) {
    super(pos, len);
    setReturnValue(returnValue);
  }



  
  public AstNode getReturnValue() {
    return this.returnValue;
  }




  
  public void setReturnValue(AstNode returnValue) {
    this.returnValue = returnValue;
    if (returnValue != null) {
      returnValue.setParent(this);
    }
  }
  
  public String toSource(int depth) {
    StringBuilder sb = new StringBuilder();
    sb.append(makeIndent(depth));
    sb.append("return");
    if (this.returnValue != null) {
      sb.append(" ");
      sb.append(this.returnValue.toSource(0));
    } 
    sb.append(";\n");
    return sb.toString();
  }




  
  public void visit(NodeVisitor v) {
    if (v.visit(this) && this.returnValue != null)
      this.returnValue.visit(v); 
  }
}
