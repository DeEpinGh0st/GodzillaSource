package org.springframework.expression.spel.ast;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Operation;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.util.Assert;



























public class OpDec
  extends Operator
{
  private final boolean postfix;
  
  public OpDec(int startPos, int endPos, boolean postfix, SpelNodeImpl... operands) {
    super("--", startPos, endPos, operands);
    this.postfix = postfix;
    Assert.notEmpty((Object[])operands, "Operands must not be empty");
  }


  
  public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
    SpelNodeImpl operand = getLeftOperand();

    
    ValueRef lvalue = operand.getValueRef(state);
    
    TypedValue operandTypedValue = lvalue.getValue();
    Object operandValue = operandTypedValue.getValue();
    TypedValue returnValue = operandTypedValue;
    TypedValue newValue = null;
    
    if (operandValue instanceof Number) {
      Number op1 = (Number)operandValue;
      if (op1 instanceof BigDecimal) {
        newValue = new TypedValue(((BigDecimal)op1).subtract(BigDecimal.ONE), operandTypedValue.getTypeDescriptor());
      }
      else if (op1 instanceof Double) {
        newValue = new TypedValue(Double.valueOf(op1.doubleValue() - 1.0D), operandTypedValue.getTypeDescriptor());
      }
      else if (op1 instanceof Float) {
        newValue = new TypedValue(Float.valueOf(op1.floatValue() - 1.0F), operandTypedValue.getTypeDescriptor());
      }
      else if (op1 instanceof BigInteger) {
        newValue = new TypedValue(((BigInteger)op1).subtract(BigInteger.ONE), operandTypedValue.getTypeDescriptor());
      }
      else if (op1 instanceof Long) {
        newValue = new TypedValue(Long.valueOf(op1.longValue() - 1L), operandTypedValue.getTypeDescriptor());
      }
      else if (op1 instanceof Integer) {
        newValue = new TypedValue(Integer.valueOf(op1.intValue() - 1), operandTypedValue.getTypeDescriptor());
      }
      else if (op1 instanceof Short) {
        newValue = new TypedValue(Integer.valueOf(op1.shortValue() - 1), operandTypedValue.getTypeDescriptor());
      }
      else if (op1 instanceof Byte) {
        newValue = new TypedValue(Integer.valueOf(op1.byteValue() - 1), operandTypedValue.getTypeDescriptor());
      }
      else {
        
        newValue = new TypedValue(Double.valueOf(op1.doubleValue() - 1.0D), operandTypedValue.getTypeDescriptor());
      } 
    } 
    
    if (newValue == null) {
      try {
        newValue = state.operate(Operation.SUBTRACT, returnValue.getValue(), Integer.valueOf(1));
      }
      catch (SpelEvaluationException ex) {
        if (ex.getMessageCode() == SpelMessage.OPERATOR_NOT_SUPPORTED_BETWEEN_TYPES)
        {
          throw new SpelEvaluationException(operand.getStartPosition(), SpelMessage.OPERAND_NOT_DECREMENTABLE, new Object[] { operand
                .toStringAST() });
        }
        
        throw ex;
      } 
    }


    
    try {
      lvalue.setValue(newValue.getValue());
    }
    catch (SpelEvaluationException see) {
      
      if (see.getMessageCode() == SpelMessage.SETVALUE_NOT_SUPPORTED) {
        throw new SpelEvaluationException(operand.getStartPosition(), SpelMessage.OPERAND_NOT_DECREMENTABLE, new Object[0]);
      }

      
      throw see;
    } 

    
    if (!this.postfix)
    {
      returnValue = newValue;
    }
    
    return returnValue;
  }

  
  public String toStringAST() {
    return getLeftOperand().toStringAST() + "--";
  }

  
  public SpelNodeImpl getRightOperand() {
    throw new IllegalStateException("No right operand");
  }
}
