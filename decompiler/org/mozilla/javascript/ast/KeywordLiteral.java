package org.mozilla.javascript.ast;

import org.mozilla.javascript.Node;

















public class KeywordLiteral
  extends AstNode
{
  public KeywordLiteral() {}
  
  public KeywordLiteral(int pos) {
    super(pos);
  }
  
  public KeywordLiteral(int pos, int len) {
    super(pos, len);
  }




  
  public KeywordLiteral(int pos, int len, int nodeType) {
    super(pos, len);
    setType(nodeType);
  }





  
  public KeywordLiteral setType(int nodeType) {
    if (nodeType != 43 && nodeType != 42 && nodeType != 45 && nodeType != 44 && nodeType != 160)
    {


      
      throw new IllegalArgumentException("Invalid node type: " + nodeType);
    }
    this.type = nodeType;
    return this;
  }




  
  public boolean isBooleanLiteral() {
    return (this.type == 45 || this.type == 44);
  }

  
  public String toSource(int depth) {
    StringBuilder sb = new StringBuilder();
    sb.append(makeIndent(depth));
    switch (getType()) {
      case 43:
        sb.append("this");
















        
        return sb.toString();case 42: sb.append("null"); return sb.toString();case 45: sb.append("true"); return sb.toString();case 44: sb.append("false"); return sb.toString();case 160: sb.append("debugger;\n"); return sb.toString();
    } 
    throw new IllegalStateException("Invalid keyword literal type: " + getType());
  }


  
  public void visit(NodeVisitor v) {
    v.visit(this);
  }
}
