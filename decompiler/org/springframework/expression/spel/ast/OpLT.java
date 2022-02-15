package org.springframework.expression.spel.ast;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.springframework.asm.MethodVisitor;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.support.BooleanTypedValue;
import org.springframework.util.NumberUtils;
























public class OpLT
  extends Operator
{
  public OpLT(int startPos, int endPos, SpelNodeImpl... operands) {
    super("<", startPos, endPos, operands);
    this.exitTypeDescriptor = "Z";
  }


  
  public BooleanTypedValue getValueInternal(ExpressionState state) throws EvaluationException {
    Object left = getLeftOperand().getValueInternal(state).getValue();
    Object right = getRightOperand().getValueInternal(state).getValue();
    
    this.leftActualDescriptor = CodeFlow.toDescriptorFromObject(left);
    this.rightActualDescriptor = CodeFlow.toDescriptorFromObject(right);
    
    if (left instanceof Number && right instanceof Number) {
      Number leftNumber = (Number)left;
      Number rightNumber = (Number)right;
      
      if (leftNumber instanceof BigDecimal || rightNumber instanceof BigDecimal) {
        BigDecimal leftBigDecimal = (BigDecimal)NumberUtils.convertNumberToTargetClass(leftNumber, BigDecimal.class);
        BigDecimal rightBigDecimal = (BigDecimal)NumberUtils.convertNumberToTargetClass(rightNumber, BigDecimal.class);
        return BooleanTypedValue.forValue((leftBigDecimal.compareTo(rightBigDecimal) < 0));
      } 
      if (leftNumber instanceof Double || rightNumber instanceof Double) {
        return BooleanTypedValue.forValue((leftNumber.doubleValue() < rightNumber.doubleValue()));
      }
      if (leftNumber instanceof Float || rightNumber instanceof Float) {
        return BooleanTypedValue.forValue((leftNumber.floatValue() < rightNumber.floatValue()));
      }
      if (leftNumber instanceof BigInteger || rightNumber instanceof BigInteger) {
        BigInteger leftBigInteger = (BigInteger)NumberUtils.convertNumberToTargetClass(leftNumber, BigInteger.class);
        BigInteger rightBigInteger = (BigInteger)NumberUtils.convertNumberToTargetClass(rightNumber, BigInteger.class);
        return BooleanTypedValue.forValue((leftBigInteger.compareTo(rightBigInteger) < 0));
      } 
      if (leftNumber instanceof Long || rightNumber instanceof Long) {
        return BooleanTypedValue.forValue((leftNumber.longValue() < rightNumber.longValue()));
      }
      if (leftNumber instanceof Integer || rightNumber instanceof Integer) {
        return BooleanTypedValue.forValue((leftNumber.intValue() < rightNumber.intValue()));
      }
      if (leftNumber instanceof Short || rightNumber instanceof Short) {
        return BooleanTypedValue.forValue((leftNumber.shortValue() < rightNumber.shortValue()));
      }
      if (leftNumber instanceof Byte || rightNumber instanceof Byte) {
        return BooleanTypedValue.forValue((leftNumber.byteValue() < rightNumber.byteValue()));
      }

      
      return BooleanTypedValue.forValue((leftNumber.doubleValue() < rightNumber.doubleValue()));
    } 

    
    if (left instanceof CharSequence && right instanceof CharSequence) {
      left = left.toString();
      right = right.toString();
    } 
    
    return BooleanTypedValue.forValue((state.getTypeComparator().compare(left, right) < 0));
  }

  
  public boolean isCompilable() {
    return isCompilableOperatorUsingNumerics();
  }

  
  public void generateCode(MethodVisitor mv, CodeFlow cf) {
    generateComparisonCode(mv, cf, 156, 162);
  }
}
