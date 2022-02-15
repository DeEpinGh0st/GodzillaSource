package org.springframework.expression.spel.ast;

import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Type;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.support.BooleanTypedValue;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
























public class OperatorInstanceof
  extends Operator
{
  @Nullable
  private Class<?> type;
  
  public OperatorInstanceof(int startPos, int endPos, SpelNodeImpl... operands) {
    super("instanceof", startPos, endPos, operands);
  }









  
  public BooleanTypedValue getValueInternal(ExpressionState state) throws EvaluationException {
    BooleanTypedValue result;
    SpelNodeImpl rightOperand = getRightOperand();
    TypedValue left = getLeftOperand().getValueInternal(state);
    TypedValue right = rightOperand.getValueInternal(state);
    Object leftValue = left.getValue();
    Object rightValue = right.getValue();
    
    if (!(rightValue instanceof Class)) {
      throw new SpelEvaluationException(getRightOperand().getStartPosition(), SpelMessage.INSTANCEOF_OPERATOR_NEEDS_CLASS_OPERAND, new Object[] { (rightValue == null) ? "null" : rightValue
            
            .getClass().getName() });
    }
    Class<?> rightClass = (Class)rightValue;
    if (leftValue == null) {
      result = BooleanTypedValue.FALSE;
    } else {
      
      result = BooleanTypedValue.forValue(rightClass.isAssignableFrom(leftValue.getClass()));
    } 
    this.type = rightClass;
    if (rightOperand instanceof TypeReference)
    {
      
      this.exitTypeDescriptor = "Z";
    }
    return result;
  }

  
  public boolean isCompilable() {
    return (this.exitTypeDescriptor != null && getLeftOperand().isCompilable());
  }

  
  public void generateCode(MethodVisitor mv, CodeFlow cf) {
    getLeftOperand().generateCode(mv, cf);
    CodeFlow.insertBoxIfNecessary(mv, cf.lastDescriptor());
    Assert.state((this.type != null), "No type available");
    if (this.type.isPrimitive()) {

      
      mv.visitInsn(87);
      mv.visitInsn(3);
    } else {
      
      mv.visitTypeInsn(193, Type.getInternalName(this.type));
    } 
    cf.pushDescriptor(this.exitTypeDescriptor);
  }
}
