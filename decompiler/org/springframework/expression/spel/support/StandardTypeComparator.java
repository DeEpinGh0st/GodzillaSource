package org.springframework.expression.spel.support;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.springframework.expression.TypeComparator;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.lang.Nullable;
import org.springframework.util.NumberUtils;



























public class StandardTypeComparator
  implements TypeComparator
{
  public boolean canCompare(@Nullable Object left, @Nullable Object right) {
    if (left == null || right == null) {
      return true;
    }
    if (left instanceof Number && right instanceof Number) {
      return true;
    }
    if (left instanceof Comparable) {
      return true;
    }
    return false;
  }



  
  public int compare(@Nullable Object left, @Nullable Object right) throws SpelEvaluationException {
    if (left == null) {
      return (right == null) ? 0 : -1;
    }
    if (right == null) {
      return 1;
    }

    
    if (left instanceof Number && right instanceof Number) {
      Number leftNumber = (Number)left;
      Number rightNumber = (Number)right;
      
      if (leftNumber instanceof BigDecimal || rightNumber instanceof BigDecimal) {
        BigDecimal leftBigDecimal = (BigDecimal)NumberUtils.convertNumberToTargetClass(leftNumber, BigDecimal.class);
        BigDecimal rightBigDecimal = (BigDecimal)NumberUtils.convertNumberToTargetClass(rightNumber, BigDecimal.class);
        return leftBigDecimal.compareTo(rightBigDecimal);
      } 
      if (leftNumber instanceof Double || rightNumber instanceof Double) {
        return Double.compare(leftNumber.doubleValue(), rightNumber.doubleValue());
      }
      if (leftNumber instanceof Float || rightNumber instanceof Float) {
        return Float.compare(leftNumber.floatValue(), rightNumber.floatValue());
      }
      if (leftNumber instanceof BigInteger || rightNumber instanceof BigInteger) {
        BigInteger leftBigInteger = (BigInteger)NumberUtils.convertNumberToTargetClass(leftNumber, BigInteger.class);
        BigInteger rightBigInteger = (BigInteger)NumberUtils.convertNumberToTargetClass(rightNumber, BigInteger.class);
        return leftBigInteger.compareTo(rightBigInteger);
      } 
      if (leftNumber instanceof Long || rightNumber instanceof Long) {
        return Long.compare(leftNumber.longValue(), rightNumber.longValue());
      }
      if (leftNumber instanceof Integer || rightNumber instanceof Integer) {
        return Integer.compare(leftNumber.intValue(), rightNumber.intValue());
      }
      if (leftNumber instanceof Short || rightNumber instanceof Short) {
        return Short.compare(leftNumber.shortValue(), rightNumber.shortValue());
      }
      if (leftNumber instanceof Byte || rightNumber instanceof Byte) {
        return Byte.compare(leftNumber.byteValue(), rightNumber.byteValue());
      }

      
      return Double.compare(leftNumber.doubleValue(), rightNumber.doubleValue());
    } 

    
    try {
      if (left instanceof Comparable) {
        return ((Comparable<Object>)left).compareTo(right);
      }
    }
    catch (ClassCastException ex) {
      throw new SpelEvaluationException(ex, SpelMessage.NOT_COMPARABLE, new Object[] { left.getClass(), right.getClass() });
    } 
    
    throw new SpelEvaluationException(SpelMessage.NOT_COMPARABLE, new Object[] { left.getClass(), right.getClass() });
  }
}
