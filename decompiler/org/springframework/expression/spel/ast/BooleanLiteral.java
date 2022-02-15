package org.springframework.expression.spel.ast;

import org.springframework.asm.MethodVisitor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.support.BooleanTypedValue;






















public class BooleanLiteral
  extends Literal
{
  private final BooleanTypedValue value;
  
  public BooleanLiteral(String payload, int startPos, int endPos, boolean value) {
    super(payload, startPos, endPos);
    this.value = BooleanTypedValue.forValue(value);
    this.exitTypeDescriptor = "Z";
  }


  
  public BooleanTypedValue getLiteralValue() {
    return this.value;
  }

  
  public boolean isCompilable() {
    return true;
  }

  
  public void generateCode(MethodVisitor mv, CodeFlow cf) {
    if (this.value == BooleanTypedValue.TRUE) {
      mv.visitLdcInsn(Integer.valueOf(1));
    } else {
      
      mv.visitLdcInsn(Integer.valueOf(0));
    } 
    cf.pushDescriptor(this.exitTypeDescriptor);
  }
}
