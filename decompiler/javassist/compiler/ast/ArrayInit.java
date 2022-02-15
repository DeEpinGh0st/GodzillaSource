package javassist.compiler.ast;

import javassist.compiler.CompileError;























public class ArrayInit
  extends ASTList
{
  private static final long serialVersionUID = 1L;
  
  public ArrayInit(ASTree firstElement) {
    super(firstElement);
  }





  
  public int size() {
    int s = length();
    if (s == 1 && head() == null) {
      return 0;
    }
    return s;
  }
  
  public void accept(Visitor v) throws CompileError {
    v.atArrayInit(this);
  }
  public String getTag() {
    return "array";
  }
}
