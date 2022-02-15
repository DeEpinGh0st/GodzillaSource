package org.springframework.expression.spel.ast;

import org.springframework.asm.MethodVisitor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
























public class FloatLiteral
  extends Literal
{
  private final TypedValue value;
  
  public FloatLiteral(String payload, int startPos, int endPos, float value) {
    super(payload, startPos, endPos);
    this.value = new TypedValue(Float.valueOf(value));
    this.exitTypeDescriptor = "F";
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
