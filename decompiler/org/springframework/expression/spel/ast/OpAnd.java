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
import org.springframework.lang.Nullable;
























public class OpAnd
  extends Operator
{
  public OpAnd(int startPos, int endPos, SpelNodeImpl... operands) {
    super("and", startPos, endPos, operands);
    this.exitTypeDescriptor = "Z";
  }


  
  public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
    if (!getBooleanValue(state, getLeftOperand()))
    {
      return (TypedValue)BooleanTypedValue.FALSE;
    }
    return (TypedValue)BooleanTypedValue.forValue(getBooleanValue(state, getRightOperand()));
  }
  
  private boolean getBooleanValue(ExpressionState state, SpelNodeImpl operand) {
    try {
      Boolean value = operand.<Boolean>getValue(state, Boolean.class);
      assertValueNotNull(value);
      return value.booleanValue();
    }
    catch (SpelEvaluationException ex) {
      ex.setPosition(operand.getStartPosition());
      throw ex;
    } 
  }
  
  private void assertValueNotNull(@Nullable Boolean value) {
    if (value == null) {
      throw new SpelEvaluationException(SpelMessage.TYPE_CONVERSION_ERROR, new Object[] { "null", "boolean" });
    }
  }

  
  public boolean isCompilable() {
    SpelNodeImpl left = getLeftOperand();
    SpelNodeImpl right = getRightOperand();
    return (left.isCompilable() && right.isCompilable() && 
      CodeFlow.isBooleanCompatible(left.exitTypeDescriptor) && 
      CodeFlow.isBooleanCompatible(right.exitTypeDescriptor));
  }


  
  public void generateCode(MethodVisitor mv, CodeFlow cf) {
    Label elseTarget = new Label();
    Label endOfIf = new Label();
    cf.enterCompilationScope();
    getLeftOperand().generateCode(mv, cf);
    cf.unboxBooleanIfNecessary(mv);
    cf.exitCompilationScope();
    mv.visitJumpInsn(154, elseTarget);
    mv.visitLdcInsn(Integer.valueOf(0));
    mv.visitJumpInsn(167, endOfIf);
    mv.visitLabel(elseTarget);
    cf.enterCompilationScope();
    getRightOperand().generateCode(mv, cf);
    cf.unboxBooleanIfNecessary(mv);
    cf.exitCompilationScope();
    mv.visitLabel(endOfIf);
    cf.pushDescriptor(this.exitTypeDescriptor);
  }
}
