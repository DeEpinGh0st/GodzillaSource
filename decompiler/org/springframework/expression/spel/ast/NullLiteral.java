package org.springframework.expression.spel.ast;

import org.springframework.asm.MethodVisitor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;






















public class NullLiteral
  extends Literal
{
  public NullLiteral(int startPos, int endPos) {
    super((String)null, startPos, endPos);
    this.exitTypeDescriptor = "Ljava/lang/Object";
  }


  
  public TypedValue getLiteralValue() {
    return TypedValue.NULL;
  }

  
  public String toString() {
    return "null";
  }

  
  public boolean isCompilable() {
    return true;
  }

  
  public void generateCode(MethodVisitor mv, CodeFlow cf) {
    mv.visitInsn(1);
    cf.pushDescriptor(this.exitTypeDescriptor);
  }
}
