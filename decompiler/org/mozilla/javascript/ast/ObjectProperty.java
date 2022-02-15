package org.mozilla.javascript.ast;







































public class ObjectProperty
  extends InfixExpression
{
  public void setNodeType(int nodeType) {
    if (nodeType != 103 && nodeType != 151 && nodeType != 152)
    {
      
      throw new IllegalArgumentException("invalid node type: " + nodeType);
    }
    setType(nodeType);
  }

  
  public ObjectProperty() {}
  
  public ObjectProperty(int pos) {
    super(pos);
  }
  
  public ObjectProperty(int pos, int len) {
    super(pos, len);
  }



  
  public void setIsGetter() {
    this.type = 151;
  }



  
  public boolean isGetter() {
    return (this.type == 151);
  }



  
  public void setIsSetter() {
    this.type = 152;
  }



  
  public boolean isSetter() {
    return (this.type == 152);
  }

  
  public String toSource(int depth) {
    StringBuilder sb = new StringBuilder();
    sb.append("\n");
    sb.append(makeIndent(depth + 1));
    if (isGetter()) {
      sb.append("get ");
    } else if (isSetter()) {
      sb.append("set ");
    } 
    sb.append(this.left.toSource((getType() == 103) ? 0 : depth));
    if (this.type == 103) {
      sb.append(": ");
    }
    sb.append(this.right.toSource((getType() == 103) ? 0 : (depth + 1)));
    return sb.toString();
  }
}
