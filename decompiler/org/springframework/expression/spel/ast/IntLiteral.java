package org.springframework.expression.spel.ast;

import org.springframework.asm.MethodVisitor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.util.Assert;























public class IntLiteral
  extends Literal
{
  private final TypedValue value;
  
  public IntLiteral(String payload, int startPos, int endPos, int value) {
    super(payload, startPos, endPos);
    this.value = new TypedValue(Integer.valueOf(value));
    this.exitTypeDescriptor = "I";
  }


  
  public TypedValue getLiteralValue() {
    return this.value;
  }

  
  public boolean isCompilable() {
    return true;
  }

  
  public void generateCode(MethodVisitor mv, CodeFlow cf) {
    Integer intValue = (Integer)this.value.getValue();
    Assert.state((intValue != null), "No int value");
    if (intValue.intValue() == -1) {
      
      mv.visitInsn(2);
    }
    else if (intValue.intValue() >= 0 && intValue.intValue() < 6) {
      mv.visitInsn(3 + intValue.intValue());
    } else {
      
      mv.visitLdcInsn(intValue);
    } 
    cf.pushDescriptor(this.exitTypeDescriptor);
  }
}
