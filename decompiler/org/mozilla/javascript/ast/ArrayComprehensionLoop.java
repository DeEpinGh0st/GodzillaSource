package org.mozilla.javascript.ast;















public class ArrayComprehensionLoop
  extends ForInLoop
{
  public ArrayComprehensionLoop() {}
  
  public ArrayComprehensionLoop(int pos) {
    super(pos);
  }
  
  public ArrayComprehensionLoop(int pos, int len) {
    super(pos, len);
  }





  
  public AstNode getBody() {
    return null;
  }






  
  public void setBody(AstNode body) {
    throw new UnsupportedOperationException("this node type has no body");
  }

  
  public String toSource(int depth) {
    return makeIndent(depth) + " for " + (isForEach() ? "each " : "") + "(" + this.iterator.toSource(0) + " in " + this.iteratedObject.toSource(0) + ")";
  }












  
  public void visit(NodeVisitor v) {
    if (v.visit(this)) {
      this.iterator.visit(v);
      this.iteratedObject.visit(v);
    } 
  }
}
