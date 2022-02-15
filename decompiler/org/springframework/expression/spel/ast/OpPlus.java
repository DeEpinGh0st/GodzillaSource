package org.springframework.expression.spel.ast;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.springframework.asm.MethodVisitor;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Operation;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.NumberUtils;


































public class OpPlus
  extends Operator
{
  public OpPlus(int startPos, int endPos, SpelNodeImpl... operands) {
    super("+", startPos, endPos, operands);
    Assert.notEmpty((Object[])operands, "Operands must not be empty");
  }


  
  public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
    SpelNodeImpl leftOp = getLeftOperand();
    
    if (this.children.length < 2) {
      Object operandOne = leftOp.getValueInternal(state).getValue();
      if (operandOne instanceof Number) {
        if (operandOne instanceof Double) {
          this.exitTypeDescriptor = "D";
        }
        else if (operandOne instanceof Float) {
          this.exitTypeDescriptor = "F";
        }
        else if (operandOne instanceof Long) {
          this.exitTypeDescriptor = "J";
        }
        else if (operandOne instanceof Integer) {
          this.exitTypeDescriptor = "I";
        } 
        return new TypedValue(operandOne);
      } 
      return state.operate(Operation.ADD, operandOne, null);
    } 
    
    TypedValue operandOneValue = leftOp.getValueInternal(state);
    Object leftOperand = operandOneValue.getValue();
    TypedValue operandTwoValue = getRightOperand().getValueInternal(state);
    Object rightOperand = operandTwoValue.getValue();
    
    if (leftOperand instanceof Number && rightOperand instanceof Number) {
      Number leftNumber = (Number)leftOperand;
      Number rightNumber = (Number)rightOperand;
      
      if (leftNumber instanceof BigDecimal || rightNumber instanceof BigDecimal) {
        BigDecimal leftBigDecimal = (BigDecimal)NumberUtils.convertNumberToTargetClass(leftNumber, BigDecimal.class);
        BigDecimal rightBigDecimal = (BigDecimal)NumberUtils.convertNumberToTargetClass(rightNumber, BigDecimal.class);
        return new TypedValue(leftBigDecimal.add(rightBigDecimal));
      } 
      if (leftNumber instanceof Double || rightNumber instanceof Double) {
        this.exitTypeDescriptor = "D";
        return new TypedValue(Double.valueOf(leftNumber.doubleValue() + rightNumber.doubleValue()));
      } 
      if (leftNumber instanceof Float || rightNumber instanceof Float) {
        this.exitTypeDescriptor = "F";
        return new TypedValue(Float.valueOf(leftNumber.floatValue() + rightNumber.floatValue()));
      } 
      if (leftNumber instanceof BigInteger || rightNumber instanceof BigInteger) {
        BigInteger leftBigInteger = (BigInteger)NumberUtils.convertNumberToTargetClass(leftNumber, BigInteger.class);
        BigInteger rightBigInteger = (BigInteger)NumberUtils.convertNumberToTargetClass(rightNumber, BigInteger.class);
        return new TypedValue(leftBigInteger.add(rightBigInteger));
      } 
      if (leftNumber instanceof Long || rightNumber instanceof Long) {
        this.exitTypeDescriptor = "J";
        return new TypedValue(Long.valueOf(leftNumber.longValue() + rightNumber.longValue()));
      } 
      if (CodeFlow.isIntegerForNumericOp(leftNumber) || CodeFlow.isIntegerForNumericOp(rightNumber)) {
        this.exitTypeDescriptor = "I";
        return new TypedValue(Integer.valueOf(leftNumber.intValue() + rightNumber.intValue()));
      } 

      
      return new TypedValue(Double.valueOf(leftNumber.doubleValue() + rightNumber.doubleValue()));
    } 

    
    if (leftOperand instanceof String && rightOperand instanceof String) {
      this.exitTypeDescriptor = "Ljava/lang/String";
      return new TypedValue((String)leftOperand + rightOperand);
    } 
    
    if (leftOperand instanceof String) {
      return new TypedValue(leftOperand + ((rightOperand == null) ? "null" : 
          convertTypedValueToString(operandTwoValue, state)));
    }
    
    if (rightOperand instanceof String) {
      return new TypedValue(((leftOperand == null) ? "null" : 
          convertTypedValueToString(operandOneValue, state)) + rightOperand);
    }
    
    return state.operate(Operation.ADD, leftOperand, rightOperand);
  }

  
  public String toStringAST() {
    if (this.children.length < 2) {
      return "+" + getLeftOperand().toStringAST();
    }
    return super.toStringAST();
  }

  
  public SpelNodeImpl getRightOperand() {
    if (this.children.length < 2) {
      throw new IllegalStateException("No right operand");
    }
    return this.children[1];
  }







  
  private static String convertTypedValueToString(TypedValue value, ExpressionState state) {
    TypeConverter typeConverter = state.getEvaluationContext().getTypeConverter();
    TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(String.class);
    if (typeConverter.canConvert(value.getTypeDescriptor(), typeDescriptor)) {
      return String.valueOf(typeConverter.convertValue(value.getValue(), value
            .getTypeDescriptor(), typeDescriptor));
    }
    return String.valueOf(value.getValue());
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




  
  private void walk(MethodVisitor mv, CodeFlow cf, @Nullable SpelNodeImpl operand) {
    if (operand instanceof OpPlus) {
      OpPlus plus = (OpPlus)operand;
      walk(mv, cf, plus.getLeftOperand());
      walk(mv, cf, plus.getRightOperand());
    }
    else if (operand != null) {
      cf.enterCompilationScope();
      operand.generateCode(mv, cf);
      if (!"Ljava/lang/String".equals(cf.lastDescriptor())) {
        mv.visitTypeInsn(192, "java/lang/String");
      }
      cf.exitCompilationScope();
      mv.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
    } 
  }

  
  public void generateCode(MethodVisitor mv, CodeFlow cf) {
    if ("Ljava/lang/String".equals(this.exitTypeDescriptor)) {
      mv.visitTypeInsn(187, "java/lang/StringBuilder");
      mv.visitInsn(89);
      mv.visitMethodInsn(183, "java/lang/StringBuilder", "<init>", "()V", false);
      walk(mv, cf, getLeftOperand());
      walk(mv, cf, getRightOperand());
      mv.visitMethodInsn(182, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
    } else {
      
      this.children[0].generateCode(mv, cf);
      String leftDesc = (this.children[0]).exitTypeDescriptor;
      String exitDesc = this.exitTypeDescriptor;
      Assert.state((exitDesc != null), "No exit type descriptor");
      char targetDesc = exitDesc.charAt(0);
      CodeFlow.insertNumericUnboxOrPrimitiveTypeCoercion(mv, leftDesc, targetDesc);
      if (this.children.length > 1) {
        cf.enterCompilationScope();
        this.children[1].generateCode(mv, cf);
        String rightDesc = (this.children[1]).exitTypeDescriptor;
        cf.exitCompilationScope();
        CodeFlow.insertNumericUnboxOrPrimitiveTypeCoercion(mv, rightDesc, targetDesc);
        switch (targetDesc) {
          case 'I':
            mv.visitInsn(96);
            break;
          case 'J':
            mv.visitInsn(97);
            break;
          case 'F':
            mv.visitInsn(98);
            break;
          case 'D':
            mv.visitInsn(99);
            break;
          default:
            throw new IllegalStateException("Unrecognized exit type descriptor: '" + this.exitTypeDescriptor + "'");
        } 
      
      } 
    } 
    cf.pushDescriptor(this.exitTypeDescriptor);
  }
}
