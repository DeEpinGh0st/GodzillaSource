package org.mozilla.javascript.ast;




















public class Yield
  extends AstNode
{
  private AstNode value;
  
  public Yield() {}
  
  public Yield(int pos) {
    super(pos);
  }
  
  public Yield(int pos, int len) {
    super(pos, len);
  }
  
  public Yield(int pos, int len, AstNode value) {
    super(pos, len);
    setValue(value);
  }



  
  public AstNode getValue() {
    return this.value;
  }




  
  public void setValue(AstNode expr) {
    this.value = expr;
    if (expr != null) {
      expr.setParent(this);
    }
  }
  
  public String toSource(int depth) {
    return (this.value == null) ? "yield" : ("yield " + this.value.toSource(0));
  }






  
  public void visit(NodeVisitor v) {
    if (v.visit(this) && this.value != null)
      this.value.visit(v); 
  }
}
