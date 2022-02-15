package org.springframework.expression.spel.ast;

import org.springframework.asm.MethodVisitor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;























public class LongLiteral
  extends Literal
{
  private final TypedValue value;
  
  public LongLiteral(String payload, int startPos, int endPos, long value) {
    super(payload, startPos, endPos);
    this.value = new TypedValue(Long.valueOf(value));
    this.exitTypeDescriptor = "J";
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
