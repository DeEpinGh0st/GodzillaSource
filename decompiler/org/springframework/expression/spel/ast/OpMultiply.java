package org.springframework.expression.spel.ast;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.springframework.asm.MethodVisitor;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Operation;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.util.Assert;
import org.springframework.util.NumberUtils;







































public class OpMultiply
  extends Operator
{
  public OpMultiply(int startPos, int endPos, SpelNodeImpl... operands) {
    super("*", startPos, endPos, operands);
  }












  
  public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
    Object leftOperand = getLeftOperand().getValueInternal(state).getValue();
    Object rightOperand = getRightOperand().getValueInternal(state).getValue();
    
    if (leftOperand instanceof Number && rightOperand instanceof Number) {
      Number leftNumber = (Number)leftOperand;
      Number rightNumber = (Number)rightOperand;
      
      if (leftNumber instanceof BigDecimal || rightNumber instanceof BigDecimal) {
        BigDecimal leftBigDecimal = (BigDecimal)NumberUtils.convertNumberToTargetClass(leftNumber, BigDecimal.class);
        BigDecimal rightBigDecimal = (BigDecimal)NumberUtils.convertNumberToTargetClass(rightNumber, BigDecimal.class);
        return new TypedValue(leftBigDecimal.multiply(rightBigDecimal));
      } 
      if (leftNumber instanceof Double || rightNumber instanceof Double) {
        this.exitTypeDescriptor = "D";
        return new TypedValue(Double.valueOf(leftNumber.doubleValue() * rightNumber.doubleValue()));
      } 
      if (leftNumber instanceof Float || rightNumber instanceof Float) {
        this.exitTypeDescriptor = "F";
        return new TypedValue(Float.valueOf(leftNumber.floatValue() * rightNumber.floatValue()));
      } 
      if (leftNumber instanceof BigInteger || rightNumber instanceof BigInteger) {
        BigInteger leftBigInteger = (BigInteger)NumberUtils.convertNumberToTargetClass(leftNumber, BigInteger.class);
        BigInteger rightBigInteger = (BigInteger)NumberUtils.convertNumberToTargetClass(rightNumber, BigInteger.class);
        return new TypedValue(leftBigInteger.multiply(rightBigInteger));
      } 
      if (leftNumber instanceof Long || rightNumber instanceof Long) {
        this.exitTypeDescriptor = "J";
        return new TypedValue(Long.valueOf(leftNumber.longValue() * rightNumber.longValue()));
      } 
      if (CodeFlow.isIntegerForNumericOp(leftNumber) || CodeFlow.isIntegerForNumericOp(rightNumber)) {
        this.exitTypeDescriptor = "I";
        return new TypedValue(Integer.valueOf(leftNumber.intValue() * rightNumber.intValue()));
      } 

      
      return new TypedValue(Double.valueOf(leftNumber.doubleValue() * rightNumber.doubleValue()));
    } 

    
    if (leftOperand instanceof String && rightOperand instanceof Integer) {
      int repeats = ((Integer)rightOperand).intValue();
      StringBuilder result = new StringBuilder();
      for (int i = 0; i < repeats; i++) {
        result.append(leftOperand);
      }
      return new TypedValue(result.toString());
    } 
    
    return state.operate(Operation.MULTIPLY, leftOperand, rightOperand);
  }

  
  public boolean isCompilable() {
    if (!getLeftOperand().isCompilable()) {
      return false;
    }
    if (this.children.length > 1 && 
      !getRightOperand().isCompilable()) {
      return false;
    }
    
    return (this.exitTypeDescriptor != null);
  }

  
  public void generateCode(MethodVisitor mv, CodeFlow cf) {
    getLeftOperand().generateCode(mv, cf);
    String leftDesc = (getLeftOperand()).exitTypeDescriptor;
    String exitDesc = this.exitTypeDescriptor;
    Assert.state((exitDesc != null), "No exit type descriptor");
    char targetDesc = exitDesc.charAt(0);
    CodeFlow.insertNumericUnboxOrPrimitiveTypeCoercion(mv, leftDesc, targetDesc);
    if (this.children.length > 1) {
      cf.enterCompilationScope();
      getRightOperand().generateCode(mv, cf);
      String rightDesc = (getRightOperand()).exitTypeDescriptor;
      cf.exitCompilationScope();
      CodeFlow.insertNumericUnboxOrPrimitiveTypeCoercion(mv, rightDesc, targetDesc);
      switch (targetDesc) {
        case 'I':
          mv.visitInsn(104);
          break;
        case 'J':
          mv.visitInsn(105);
          break;
        case 'F':
          mv.visitInsn(106);
          break;
        case 'D':
          mv.visitInsn(107);
          break;
        default:
          throw new IllegalStateException("Unrecognized exit type descriptor: '" + this.exitTypeDescriptor + "'");
      } 
    
    } 
    cf.pushDescriptor(this.exitTypeDescriptor);
  }
}
