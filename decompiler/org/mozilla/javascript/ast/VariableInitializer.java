package org.mozilla.javascript.ast;

























public class VariableInitializer
  extends AstNode
{
  private AstNode target;
  private AstNode initializer;
  
  public void setNodeType(int nodeType) {
    if (nodeType != 122 && nodeType != 154 && nodeType != 153)
    {
      
      throw new IllegalArgumentException("invalid node type"); } 
    setType(nodeType);
  }

  
  public VariableInitializer() {}
  
  public VariableInitializer(int pos) {
    super(pos);
  }
  
  public VariableInitializer(int pos, int len) {
    super(pos, len);
  }







  
  public boolean isDestructuring() {
    return !(this.target instanceof Name);
  }



  
  public AstNode getTarget() {
    return this.target;
  }







  
  public void setTarget(AstNode target) {
    if (target == null)
      throw new IllegalArgumentException("invalid target arg"); 
    this.target = target;
    target.setParent(this);
  }



  
  public AstNode getInitializer() {
    return this.initializer;
  }




  
  public void setInitializer(AstNode initializer) {
    this.initializer = initializer;
    if (initializer != null) {
      initializer.setParent(this);
    }
  }
  
  public String toSource(int depth) {
    StringBuilder sb = new StringBuilder();
    sb.append(makeIndent(depth));
    sb.append(this.target.toSource(0));
    if (this.initializer != null) {
      sb.append(" = ");
      sb.append(this.initializer.toSource(0));
    } 
    return sb.toString();
  }





  
  public void visit(NodeVisitor v) {
    if (v.visit(this)) {
      this.target.visit(v);
      if (this.initializer != null)
        this.initializer.visit(v); 
    } 
  }
}
