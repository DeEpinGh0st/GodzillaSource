package org.springframework.expression.spel.ast;

import org.springframework.asm.MethodVisitor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;























public class RealLiteral
  extends Literal
{
  private final TypedValue value;
  
  public RealLiteral(String payload, int startPos, int endPos, double value) {
    super(payload, startPos, endPos);
    this.value = new TypedValue(Double.valueOf(value));
    this.exitTypeDescriptor = "D";
  }


  
  public TypedValue getLiteralValue() {
    return this.value;
  }

  
  public boolean isCompilable() {
    return true;
  }

  
  public void generateCode(MethodVisitor mv, CodeFlow cf) {
    mv.visitLdcInsn(this.value.getValue());
    cf.pushDescriptor(this.exitTypeDescriptor);
  }
}
