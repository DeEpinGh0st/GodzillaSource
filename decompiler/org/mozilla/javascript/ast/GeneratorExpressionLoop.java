package org.mozilla.javascript.ast;









public class GeneratorExpressionLoop
  extends ForInLoop
{
  public GeneratorExpressionLoop() {}
  
  public GeneratorExpressionLoop(int pos) {
    super(pos);
  }
  
  public GeneratorExpressionLoop(int pos, int len) {
    super(pos, len);
  }




  
  public boolean isForEach() {
    return false;
  }




  
  public void setIsForEach(boolean isForEach) {
    throw new UnsupportedOperationException("this node type does not support for each");
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
