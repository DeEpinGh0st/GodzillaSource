package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Comparator;















































@Beta
@GwtCompatible
public final class UnsignedLongs
{
  public static final long MAX_VALUE = -1L;
  
  private static long flip(long a) {
    return a ^ Long.MIN_VALUE;
  }











  
  public static int compare(long a, long b) {
    return Longs.compare(flip(a), flip(b));
  }








  
  public static long min(long... array) {
    Preconditions.checkArgument((array.length > 0));
    long min = flip(array[0]);
    for (int i = 1; i < array.length; i++) {
      long next = flip(array[i]);
      if (next < min) {
        min = next;
      }
    } 
    return flip(min);
  }








  
  public static long max(long... array) {
    Preconditions.checkArgument((array.length > 0));
    long max = flip(array[0]);
    for (int i = 1; i < array.length; i++) {
      long next = flip(array[i]);
      if (next > max) {
        max = next;
      }
    } 
    return flip(max);
  }








  
  public static String join(String separator, long... array) {
    Preconditions.checkNotNull(separator);
    if (array.length == 0) {
      return "";
    }

    
    StringBuilder builder = new StringBuilder(array.length * 5);
    builder.append(toString(array[0]));
    for (int i = 1; i < array.length; i++) {
      builder.append(separator).append(toString(array[i]));
    }
    return builder.toString();
  }











  
  public static Comparator<long[]> lexicographicalComparator() {
    return LexicographicalComparator.INSTANCE;
  }
  
  enum LexicographicalComparator implements Comparator<long[]> {
    INSTANCE;

    
    public int compare(long[] left, long[] right) {
      int minLength = Math.min(left.length, right.length);
      for (int i = 0; i < minLength; i++) {
        if (left[i] != right[i]) {
          return UnsignedLongs.compare(left[i], right[i]);
        }
      } 
      return left.length - right.length;
    }

    
    public String toString() {
      return "UnsignedLongs.lexicographicalComparator()";
    }
  }





  
  public static void sort(long[] array) {
    Preconditions.checkNotNull(array);
    sort(array, 0, array.length);
  }






  
  public static void sort(long[] array, int fromIndex, int toIndex) {
    Preconditions.checkNotNull(array);
    Preconditions.checkPositionIndexes(fromIndex, toIndex, array.length); int i;
    for (i = fromIndex; i < toIndex; i++) {
      array[i] = flip(array[i]);
    }
    Arrays.sort(array, fromIndex, toIndex);
    for (i = fromIndex; i < toIndex; i++) {
      array[i] = flip(array[i]);
    }
  }






  
  public static void sortDescending(long[] array) {
    Preconditions.checkNotNull(array);
    sortDescending(array, 0, array.length);
  }






  
  public static void sortDescending(long[] array, int fromIndex, int toIndex) {
    Preconditions.checkNotNull(array);
    Preconditions.checkPositionIndexes(fromIndex, toIndex, array.length); int i;
    for (i = fromIndex; i < toIndex; i++) {
      array[i] = array[i] ^ Long.MAX_VALUE;
    }
    Arrays.sort(array, fromIndex, toIndex);
    for (i = fromIndex; i < toIndex; i++) {
      array[i] = array[i] ^ Long.MAX_VALUE;
    }
  }










  
  public static long divide(long dividend, long divisor) {
    if (divisor < 0L) {
      if (compare(dividend, divisor) < 0) {
        return 0L;
      }
      return 1L;
    } 


    
    if (dividend >= 0L) {
      return dividend / divisor;
    }






    
    long quotient = (dividend >>> 1L) / divisor << 1L;
    long rem = dividend - quotient * divisor;
    return quotient + ((compare(rem, divisor) >= 0) ? 1L : 0L);
  }











  
  public static long remainder(long dividend, long divisor) {
    if (divisor < 0L) {
      if (compare(dividend, divisor) < 0) {
        return dividend;
      }
      return dividend - divisor;
    } 


    
    if (dividend >= 0L) {
      return dividend % divisor;
    }






    
    long quotient = (dividend >>> 1L) / divisor << 1L;
    long rem = dividend - quotient * divisor;
    return rem - ((compare(rem, divisor) >= 0) ? divisor : 0L);
  }










  
  @CanIgnoreReturnValue
  public static long parseUnsignedLong(String string) {
    return parseUnsignedLong(string, 10);
  }













  
  @CanIgnoreReturnValue
  public static long parseUnsignedLong(String string, int radix) {
    Preconditions.checkNotNull(string);
    if (string.length() == 0) {
      throw new NumberFormatException("empty string");
    }
    if (radix < 2 || radix > 36) {
      throw new NumberFormatException("illegal radix: " + radix);
    }
    
    int maxSafePos = ParseOverflowDetection.maxSafeDigits[radix] - 1;
    long value = 0L;
    for (int pos = 0; pos < string.length(); pos++) {
      int digit = Character.digit(string.charAt(pos), radix);
      if (digit == -1) {
        throw new NumberFormatException(string);
      }
      if (pos > maxSafePos && ParseOverflowDetection.overflowInParse(value, digit, radix)) {
        throw new NumberFormatException("Too large for unsigned long: " + string);
      }
      value = value * radix + digit;
    } 
    
    return value;
  }
















  
  @CanIgnoreReturnValue
  public static long decode(String stringValue) {
    ParseRequest request = ParseRequest.fromString(stringValue);
    
    try {
      return parseUnsignedLong(request.rawValue, request.radix);
    } catch (NumberFormatException e) {
      NumberFormatException decodeException = new NumberFormatException("Error parsing value: " + stringValue);
      
      decodeException.initCause(e);
      throw decodeException;
    } 
  }






  
  private static final class ParseOverflowDetection
  {
    static final long[] maxValueDivs = new long[37];
    static final int[] maxValueMods = new int[37];
    static final int[] maxSafeDigits = new int[37];
    
    static {
      BigInteger overflow = new BigInteger("10000000000000000", 16);
      for (int i = 2; i <= 36; i++) {
        maxValueDivs[i] = UnsignedLongs.divide(-1L, i);
        maxValueMods[i] = (int)UnsignedLongs.remainder(-1L, i);
        maxSafeDigits[i] = overflow.toString(i).length() - 1;
      } 
    }






    
    static boolean overflowInParse(long current, int digit, int radix) {
      if (current >= 0L) {
        if (current < maxValueDivs[radix]) {
          return false;
        }
        if (current > maxValueDivs[radix]) {
          return true;
        }
        
        return (digit > maxValueMods[radix]);
      } 

      
      return true;
    }
  }





  
  public static String toString(long x) {
    return toString(x, 10);
  }











  
  public static String toString(long x, int radix) {
    Preconditions.checkArgument((radix >= 2 && radix <= 36), "radix (%s) must be between Character.MIN_RADIX and Character.MAX_RADIX", radix);


    
    if (x == 0L)
    {
      return "0"; } 
    if (x > 0L) {
      return Long.toString(x, radix);
    }
    char[] buf = new char[64];
    int i = buf.length;
    if ((radix & radix - 1) == 0) {
      
      int shift = Integer.numberOfTrailingZeros(radix);
      int mask = radix - 1;
      do {
        buf[--i] = Character.forDigit((int)x & mask, radix);
        x >>>= shift;
      } while (x != 0L);
    } else {
      long quotient;

      
      if ((radix & 0x1) == 0) {
        
        quotient = (x >>> 1L) / (radix >>> 1);
      } else {
        quotient = divide(x, radix);
      } 
      long rem = x - quotient * radix;
      buf[--i] = Character.forDigit((int)rem, radix);
      x = quotient;
      
      while (x > 0L) {
        buf[--i] = Character.forDigit((int)(x % radix), radix);
        x /= radix;
      } 
    } 
    
    return new String(buf, i, buf.length - i);
  }
}
