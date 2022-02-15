package javassist.compiler.ast;

import javassist.compiler.CompileError;
import javassist.compiler.TokenId;


















public class NewExpr
  extends ASTList
  implements TokenId
{
  private static final long serialVersionUID = 1L;
  protected boolean newArray;
  protected int arrayType;
  
  public NewExpr(ASTList className, ASTList args) {
    super(className, new ASTList(args));
    this.newArray = false;
    this.arrayType = 307;
  }
  
  public NewExpr(int type, ASTList arraySize, ArrayInit init) {
    super(null, new ASTList(arraySize));
    this.newArray = true;
    this.arrayType = type;
    if (init != null) {
      append(this, init);
    }
  }
  
  public static NewExpr makeObjectArray(ASTList className, ASTList arraySize, ArrayInit init) {
    NewExpr e = new NewExpr(className, arraySize);
    e.newArray = true;
    if (init != null) {
      append(e, init);
    }
    return e;
  }
  public boolean isArray() {
    return this.newArray;
  }
  
  public int getArrayType() {
    return this.arrayType;
  } public ASTList getClassName() {
    return (ASTList)getLeft();
  } public ASTList getArguments() {
    return (ASTList)getRight().getLeft();
  } public ASTList getArraySize() {
    return getArguments();
  }
  public ArrayInit getInitializer() {
    ASTree t = getRight().getRight();
    if (t == null)
      return null; 
    return (ArrayInit)t.getLeft();
  }
  
  public void accept(Visitor v) throws CompileError {
    v.atNewExpr(this);
  }
  
  protected String getTag() {
    return this.newArray ? "new[]" : "new";
  }
}
