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



























public class OpInc
  extends Operator
{
  private final boolean postfix;
  
  public OpInc(int startPos, int endPos, boolean postfix, SpelNodeImpl... operands) {
    super("++", startPos, endPos, operands);
    this.postfix = postfix;
    Assert.notEmpty((Object[])operands, "Operands must not be empty");
  }


  
  public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
    SpelNodeImpl operand = getLeftOperand();
    ValueRef valueRef = operand.getValueRef(state);
    
    TypedValue typedValue = valueRef.getValue();
    Object value = typedValue.getValue();
    TypedValue returnValue = typedValue;
    TypedValue newValue = null;
    
    if (value instanceof Number) {
      Number op1 = (Number)value;
      if (op1 instanceof BigDecimal) {
        newValue = new TypedValue(((BigDecimal)op1).add(BigDecimal.ONE), typedValue.getTypeDescriptor());
      }
      else if (op1 instanceof Double) {
        newValue = new TypedValue(Double.valueOf(op1.doubleValue() + 1.0D), typedValue.getTypeDescriptor());
      }
      else if (op1 instanceof Float) {
        newValue = new TypedValue(Float.valueOf(op1.floatValue() + 1.0F), typedValue.getTypeDescriptor());
      }
      else if (op1 instanceof BigInteger) {
        newValue = new TypedValue(((BigInteger)op1).add(BigInteger.ONE), typedValue.getTypeDescriptor());
      }
      else if (op1 instanceof Long) {
        newValue = new TypedValue(Long.valueOf(op1.longValue() + 1L), typedValue.getTypeDescriptor());
      }
      else if (op1 instanceof Integer) {
        newValue = new TypedValue(Integer.valueOf(op1.intValue() + 1), typedValue.getTypeDescriptor());
      }
      else if (op1 instanceof Short) {
        newValue = new TypedValue(Integer.valueOf(op1.shortValue() + 1), typedValue.getTypeDescriptor());
      }
      else if (op1 instanceof Byte) {
        newValue = new TypedValue(Integer.valueOf(op1.byteValue() + 1), typedValue.getTypeDescriptor());
      }
      else {
        
        newValue = new TypedValue(Double.valueOf(op1.doubleValue() + 1.0D), typedValue.getTypeDescriptor());
      } 
    } 
    
    if (newValue == null) {
      try {
        newValue = state.operate(Operation.ADD, returnValue.getValue(), Integer.valueOf(1));
      }
      catch (SpelEvaluationException ex) {
        if (ex.getMessageCode() == SpelMessage.OPERATOR_NOT_SUPPORTED_BETWEEN_TYPES)
        {
          throw new SpelEvaluationException(operand.getStartPosition(), SpelMessage.OPERAND_NOT_INCREMENTABLE, new Object[] { operand
                .toStringAST() });
        }
        throw ex;
      } 
    }

    
    try {
      valueRef.setValue(newValue.getValue());
    }
    catch (SpelEvaluationException see) {
      
      if (see.getMessageCode() == SpelMessage.SETVALUE_NOT_SUPPORTED) {
        throw new SpelEvaluationException(operand.getStartPosition(), SpelMessage.OPERAND_NOT_INCREMENTABLE, new Object[0]);
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
    return getLeftOperand().toStringAST() + "++";
  }

  
  public SpelNodeImpl getRightOperand() {
    throw new IllegalStateException("No right operand");
  }
}
