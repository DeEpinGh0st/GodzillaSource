package org.mozilla.javascript.ast;






















public class Name
  extends AstNode
{
  private String identifier;
  private Scope scope;
  
  public Name() {}
  
  public Name(int pos) {
    super(pos);
  }
  
  public Name(int pos, int len) {
    super(pos, len);
  }






  
  public Name(int pos, int len, String name) {
    super(pos, len);
    setIdentifier(name);
  }
  
  public Name(int pos, String name) {
    super(pos);
    setIdentifier(name);
    setLength(name.length());
  }



  
  public String getIdentifier() {
    return this.identifier;
  }




  
  public void setIdentifier(String identifier) {
    assertNotNull(identifier);
    this.identifier = identifier;
    setLength(identifier.length());
  }











  
  public void setScope(Scope s) {
    this.scope = s;
  }








  
  public Scope getScope() {
    return this.scope;
  }





  
  public Scope getDefiningScope() {
    Scope enclosing = getEnclosingScope();
    String name = getIdentifier();
    return (enclosing == null) ? null : enclosing.getDefiningScope(name);
  }













  
  public boolean isLocalName() {
    Scope scope = getDefiningScope();
    return (scope != null && scope.getParentScope() != null);
  }






  
  public int length() {
    return (this.identifier == null) ? 0 : this.identifier.length();
  }

  
  public String toSource(int depth) {
    return makeIndent(depth) + ((this.identifier == null) ? "<null>" : this.identifier);
  }




  
  public void visit(NodeVisitor v) {
    v.visit(this);
  }
}
