package org.springframework.expression.spel.ast;

import org.springframework.asm.Label;
import org.springframework.asm.MethodVisitor;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.support.BooleanTypedValue;























public class OperatorNot
  extends SpelNodeImpl
{
  public OperatorNot(int startPos, int endPos, SpelNodeImpl operand) {
    super(startPos, endPos, new SpelNodeImpl[] { operand });
    this.exitTypeDescriptor = "Z";
  }


  
  public BooleanTypedValue getValueInternal(ExpressionState state) throws EvaluationException {
    try {
      Boolean value = this.children[0].<Boolean>getValue(state, Boolean.class);
      if (value == null) {
        throw new SpelEvaluationException(SpelMessage.TYPE_CONVERSION_ERROR, new Object[] { "null", "boolean" });
      }
      return BooleanTypedValue.forValue(!value.booleanValue());
    }
    catch (SpelEvaluationException ex) {
      ex.setPosition(getChild(0).getStartPosition());
      throw ex;
    } 
  }

  
  public String toStringAST() {
    return "!" + getChild(0).toStringAST();
  }

  
  public boolean isCompilable() {
    SpelNodeImpl child = this.children[0];
    return (child.isCompilable() && CodeFlow.isBooleanCompatible(child.exitTypeDescriptor));
  }

  
  public void generateCode(MethodVisitor mv, CodeFlow cf) {
    this.children[0].generateCode(mv, cf);
    cf.unboxBooleanIfNecessary(mv);
    Label elseTarget = new Label();
    Label endOfIf = new Label();
    mv.visitJumpInsn(154, elseTarget);
    mv.visitInsn(4);
    mv.visitJumpInsn(167, endOfIf);
    mv.visitLabel(elseTarget);
    mv.visitInsn(3);
    mv.visitLabel(endOfIf);
    cf.pushDescriptor(this.exitTypeDescriptor);
  }
}
