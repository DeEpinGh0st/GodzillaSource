package org.mozilla.javascript.ast;

















public class RegExpLiteral
  extends AstNode
{
  private String value;
  private String flags;
  
  public RegExpLiteral() {}
  
  public RegExpLiteral(int pos) {
    super(pos);
  }
  
  public RegExpLiteral(int pos, int len) {
    super(pos, len);
  }



  
  public String getValue() {
    return this.value;
  }




  
  public void setValue(String value) {
    assertNotNull(value);
    this.value = value;
  }



  
  public String getFlags() {
    return this.flags;
  }



  
  public void setFlags(String flags) {
    this.flags = flags;
  }

  
  public String toSource(int depth) {
    return makeIndent(depth) + "/" + this.value + "/" + ((this.flags == null) ? "" : this.flags);
  }





  
  public void visit(NodeVisitor v) {
    v.visit(this);
  }
}
