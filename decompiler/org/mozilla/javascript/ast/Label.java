package org.mozilla.javascript.ast;


















public class Label
  extends Jump
{
  private String name;
  
  public Label() {}
  
  public Label(int pos) {
    this(pos, -1);
  }

  
  public Label(int pos, int len) {
    this.position = pos;
    this.length = len;
  }
  
  public Label(int pos, int len, String name) {
    this(pos, len);
    setName(name);
  }



  
  public String getName() {
    return this.name;
  }





  
  public void setName(String name) {
    name = (name == null) ? null : name.trim();
    if (name == null || "".equals(name))
      throw new IllegalArgumentException("invalid label name"); 
    this.name = name;
  }

  
  public String toSource(int depth) {
    StringBuilder sb = new StringBuilder();
    sb.append(makeIndent(depth));
    sb.append(this.name);
    sb.append(":\n");
    return sb.toString();
  }




  
  public void visit(NodeVisitor v) {
    v.visit(this);
  }
}
