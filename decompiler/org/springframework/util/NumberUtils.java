package org.springframework.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.springframework.lang.Nullable;



























public abstract class NumberUtils
{
  private static final BigInteger LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);
  
  private static final BigInteger LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);


  
  public static final Set<Class<?>> STANDARD_NUMBER_TYPES;


  
  static {
    Set<Class<?>> numberTypes = new HashSet<>(8);
    numberTypes.add(Byte.class);
    numberTypes.add(Short.class);
    numberTypes.add(Integer.class);
    numberTypes.add(Long.class);
    numberTypes.add(BigInteger.class);
    numberTypes.add(Float.class);
    numberTypes.add(Double.class);
    numberTypes.add(BigDecimal.class);
    STANDARD_NUMBER_TYPES = Collections.unmodifiableSet(numberTypes);
  }




















  
  public static <T extends Number> T convertNumberToTargetClass(Number number, Class<T> targetClass) throws IllegalArgumentException {
    Assert.notNull(number, "Number must not be null");
    Assert.notNull(targetClass, "Target class must not be null");
    
    if (targetClass.isInstance(number)) {
      return (T)number;
    }
    if (Byte.class == targetClass) {
      long value = checkedLongValue(number, targetClass);
      if (value < -128L || value > 127L) {
        raiseOverflowException(number, targetClass);
      }
      return (T)Byte.valueOf(number.byteValue());
    } 
    if (Short.class == targetClass) {
      long value = checkedLongValue(number, targetClass);
      if (value < -32768L || value > 32767L) {
        raiseOverflowException(number, targetClass);
      }
      return (T)Short.valueOf(number.shortValue());
    } 
    if (Integer.class == targetClass) {
      long value = checkedLongValue(number, targetClass);
      if (value < -2147483648L || value > 2147483647L) {
        raiseOverflowException(number, targetClass);
      }
      return (T)Integer.valueOf(number.intValue());
    } 
    if (Long.class == targetClass) {
      long value = checkedLongValue(number, targetClass);
      return (T)Long.valueOf(value);
    } 
    if (BigInteger.class == targetClass) {
      if (number instanceof BigDecimal)
      {
        return (T)((BigDecimal)number).toBigInteger();
      }

      
      return (T)BigInteger.valueOf(number.longValue());
    } 
    
    if (Float.class == targetClass) {
      return (T)Float.valueOf(number.floatValue());
    }
    if (Double.class == targetClass) {
      return (T)Double.valueOf(number.doubleValue());
    }
    if (BigDecimal.class == targetClass)
    {
      
      return (T)new BigDecimal(number.toString());
    }
    
    throw new IllegalArgumentException("Could not convert number [" + number + "] of type [" + number
        .getClass().getName() + "] to unsupported target class [" + targetClass.getName() + "]");
  }










  
  private static long checkedLongValue(Number number, Class<? extends Number> targetClass) {
    BigInteger bigInt = null;
    if (number instanceof BigInteger) {
      bigInt = (BigInteger)number;
    }
    else if (number instanceof BigDecimal) {
      bigInt = ((BigDecimal)number).toBigInteger();
    } 
    
    if (bigInt != null && (bigInt.compareTo(LONG_MIN) < 0 || bigInt.compareTo(LONG_MAX) > 0)) {
      raiseOverflowException(number, targetClass);
    }
    return number.longValue();
  }






  
  private static void raiseOverflowException(Number number, Class<?> targetClass) {
    throw new IllegalArgumentException("Could not convert number [" + number + "] of type [" + number
        .getClass().getName() + "] to target class [" + targetClass.getName() + "]: overflow");
  }





















  
  public static <T extends Number> T parseNumber(String text, Class<T> targetClass) {
    Assert.notNull(text, "Text must not be null");
    Assert.notNull(targetClass, "Target class must not be null");
    String trimmed = StringUtils.trimAllWhitespace(text);
    
    if (Byte.class == targetClass) {
      return isHexNumber(trimmed) ? (T)Byte.decode(trimmed) : (T)Byte.valueOf(trimmed);
    }
    if (Short.class == targetClass) {
      return isHexNumber(trimmed) ? (T)Short.decode(trimmed) : (T)Short.valueOf(trimmed);
    }
    if (Integer.class == targetClass) {
      return isHexNumber(trimmed) ? (T)Integer.decode(trimmed) : (T)Integer.valueOf(trimmed);
    }
    if (Long.class == targetClass) {
      return isHexNumber(trimmed) ? (T)Long.decode(trimmed) : (T)Long.valueOf(trimmed);
    }
    if (BigInteger.class == targetClass) {
      return isHexNumber(trimmed) ? (T)decodeBigInteger(trimmed) : (T)new BigInteger(trimmed);
    }
    if (Float.class == targetClass) {
      return (T)Float.valueOf(trimmed);
    }
    if (Double.class == targetClass) {
      return (T)Double.valueOf(trimmed);
    }
    if (BigDecimal.class == targetClass || Number.class == targetClass) {
      return (T)new BigDecimal(trimmed);
    }
    
    throw new IllegalArgumentException("Cannot convert String [" + text + "] to target class [" + targetClass
        .getName() + "]");
  }


















  
  public static <T extends Number> T parseNumber(String text, Class<T> targetClass, @Nullable NumberFormat numberFormat) {
    if (numberFormat != null) {
      Assert.notNull(text, "Text must not be null");
      Assert.notNull(targetClass, "Target class must not be null");
      DecimalFormat decimalFormat = null;
      boolean resetBigDecimal = false;
      if (numberFormat instanceof DecimalFormat) {
        decimalFormat = (DecimalFormat)numberFormat;
        if (BigDecimal.class == targetClass && !decimalFormat.isParseBigDecimal()) {
          decimalFormat.setParseBigDecimal(true);
          resetBigDecimal = true;
        } 
      } 
      try {
        Number number = numberFormat.parse(StringUtils.trimAllWhitespace(text));
        return (T)convertNumberToTargetClass(number, (Class)targetClass);
      }
      catch (ParseException ex) {
        throw new IllegalArgumentException("Could not parse number: " + ex.getMessage());
      } finally {
        
        if (resetBigDecimal) {
          decimalFormat.setParseBigDecimal(false);
        }
      } 
    } 
    
    return parseNumber(text, targetClass);
  }






  
  private static boolean isHexNumber(String value) {
    int index = value.startsWith("-") ? 1 : 0;
    return (value.startsWith("0x", index) || value.startsWith("0X", index) || value.startsWith("#", index));
  }





  
  private static BigInteger decodeBigInteger(String value) {
    int radix = 10;
    int index = 0;
    boolean negative = false;

    
    if (value.startsWith("-")) {
      negative = true;
      index++;
    } 

    
    if (value.startsWith("0x", index) || value.startsWith("0X", index)) {
      index += 2;
      radix = 16;
    }
    else if (value.startsWith("#", index)) {
      index++;
      radix = 16;
    }
    else if (value.startsWith("0", index) && value.length() > 1 + index) {
      index++;
      radix = 8;
    } 
    
    BigInteger result = new BigInteger(value.substring(index), radix);
    return negative ? result.negate() : result;
  }
}
