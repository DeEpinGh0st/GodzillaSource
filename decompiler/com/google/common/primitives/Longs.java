package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Converter;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.Spliterators;























































@GwtCompatible
public final class Longs
{
  public static final int BYTES = 8;
  public static final long MAX_POWER_OF_TWO = 4611686018427387904L;
  
  public static int hashCode(long value) {
    return (int)(value ^ value >>> 32L);
  }












  
  public static int compare(long a, long b) {
    return (a < b) ? -1 : ((a > b) ? 1 : 0);
  }







  
  public static boolean contains(long[] array, long target) {
    for (long value : array) {
      if (value == target) {
        return true;
      }
    } 
    return false;
  }








  
  public static int indexOf(long[] array, long target) {
    return indexOf(array, target, 0, array.length);
  }

  
  private static int indexOf(long[] array, long target, int start, int end) {
    for (int i = start; i < end; i++) {
      if (array[i] == target) {
        return i;
      }
    } 
    return -1;
  }










  
  public static int indexOf(long[] array, long[] target) {
    Preconditions.checkNotNull(array, "array");
    Preconditions.checkNotNull(target, "target");
    if (target.length == 0) {
      return 0;
    }

    
    for (int i = 0; i < array.length - target.length + 1; i++) {
      int j = 0; while (true) { if (j < target.length) {
          if (array[i + j] != target[j])
            break;  j++;
          continue;
        } 
        return i; }
    
    }  return -1;
  }








  
  public static int lastIndexOf(long[] array, long target) {
    return lastIndexOf(array, target, 0, array.length);
  }

  
  private static int lastIndexOf(long[] array, long target, int start, int end) {
    for (int i = end - 1; i >= start; i--) {
      if (array[i] == target) {
        return i;
      }
    } 
    return -1;
  }








  
  public static long min(long... array) {
    Preconditions.checkArgument((array.length > 0));
    long min = array[0];
    for (int i = 1; i < array.length; i++) {
      if (array[i] < min) {
        min = array[i];
      }
    } 
    return min;
  }








  
  public static long max(long... array) {
    Preconditions.checkArgument((array.length > 0));
    long max = array[0];
    for (int i = 1; i < array.length; i++) {
      if (array[i] > max) {
        max = array[i];
      }
    } 
    return max;
  }













  
  @Beta
  public static long constrainToRange(long value, long min, long max) {
    Preconditions.checkArgument((min <= max), "min (%s) must be less than or equal to max (%s)", min, max);
    return Math.min(Math.max(value, min), max);
  }







  
  public static long[] concat(long[]... arrays) {
    int length = 0;
    for (long[] array : arrays) {
      length += array.length;
    }
    long[] result = new long[length];
    int pos = 0;
    for (long[] array : arrays) {
      System.arraycopy(array, 0, result, pos, array.length);
      pos += array.length;
    } 
    return result;
  }












  
  public static byte[] toByteArray(long value) {
    byte[] result = new byte[8];
    for (int i = 7; i >= 0; i--) {
      result[i] = (byte)(int)(value & 0xFFL);
      value >>= 8L;
    } 
    return result;
  }











  
  public static long fromByteArray(byte[] bytes) {
    Preconditions.checkArgument((bytes.length >= 8), "array too small: %s < %s", bytes.length, 8);
    return fromBytes(bytes[0], bytes[1], bytes[2], bytes[3], bytes[4], bytes[5], bytes[6], bytes[7]);
  }








  
  public static long fromBytes(byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7, byte b8) {
    return (b1 & 0xFFL) << 56L | (b2 & 0xFFL) << 48L | (b3 & 0xFFL) << 40L | (b4 & 0xFFL) << 32L | (b5 & 0xFFL) << 24L | (b6 & 0xFFL) << 16L | (b7 & 0xFFL) << 8L | b8 & 0xFFL;
  }






  
  static final class AsciiDigits
  {
    private static final byte[] asciiDigits;






    
    static {
      byte[] result = new byte[128];
      Arrays.fill(result, (byte)-1); int i;
      for (i = 0; i <= 9; i++) {
        result[48 + i] = (byte)i;
      }
      for (i = 0; i <= 26; i++) {
        result[65 + i] = (byte)(10 + i);
        result[97 + i] = (byte)(10 + i);
      } 
      asciiDigits = result;
    }
    
    static int digit(char c) {
      return (c < '') ? asciiDigits[c] : -1;
    }
  }
















  
  @Beta
  public static Long tryParse(String string) {
    return tryParse(string, 10);
  }



















  
  @Beta
  public static Long tryParse(String string, int radix) {
    if (((String)Preconditions.checkNotNull(string)).isEmpty()) {
      return null;
    }
    if (radix < 2 || radix > 36) {
      throw new IllegalArgumentException("radix must be between MIN_RADIX and MAX_RADIX but was " + radix);
    }
    
    boolean negative = (string.charAt(0) == '-');
    int index = negative ? 1 : 0;
    if (index == string.length()) {
      return null;
    }
    int digit = AsciiDigits.digit(string.charAt(index++));
    if (digit < 0 || digit >= radix) {
      return null;
    }
    long accum = -digit;
    
    long cap = Long.MIN_VALUE / radix;
    
    while (index < string.length()) {
      digit = AsciiDigits.digit(string.charAt(index++));
      if (digit < 0 || digit >= radix || accum < cap) {
        return null;
      }
      accum *= radix;
      if (accum < Long.MIN_VALUE + digit) {
        return null;
      }
      accum -= digit;
    } 
    
    if (negative)
      return Long.valueOf(accum); 
    if (accum == Long.MIN_VALUE) {
      return null;
    }
    return Long.valueOf(-accum);
  }
  
  private static final class LongConverter
    extends Converter<String, Long> implements Serializable {
    static final LongConverter INSTANCE = new LongConverter();
    private static final long serialVersionUID = 1L;
    
    protected Long doForward(String value) {
      return Long.decode(value);
    }

    
    protected String doBackward(Long value) {
      return value.toString();
    }

    
    public String toString() {
      return "Longs.stringConverter()";
    }
    
    private Object readResolve() {
      return INSTANCE;
    }
  }













  
  @Beta
  public static Converter<String, Long> stringConverter() {
    return LongConverter.INSTANCE;
  }













  
  public static long[] ensureCapacity(long[] array, int minLength, int padding) {
    Preconditions.checkArgument((minLength >= 0), "Invalid minLength: %s", minLength);
    Preconditions.checkArgument((padding >= 0), "Invalid padding: %s", padding);
    return (array.length < minLength) ? Arrays.copyOf(array, minLength + padding) : array;
  }








  
  public static String join(String separator, long... array) {
    Preconditions.checkNotNull(separator);
    if (array.length == 0) {
      return "";
    }

    
    StringBuilder builder = new StringBuilder(array.length * 10);
    builder.append(array[0]);
    for (int i = 1; i < array.length; i++) {
      builder.append(separator).append(array[i]);
    }
    return builder.toString();
  }













  
  public static Comparator<long[]> lexicographicalComparator() {
    return LexicographicalComparator.INSTANCE;
  }
  
  private enum LexicographicalComparator implements Comparator<long[]> {
    INSTANCE;

    
    public int compare(long[] left, long[] right) {
      int minLength = Math.min(left.length, right.length);
      for (int i = 0; i < minLength; i++) {
        int result = Longs.compare(left[i], right[i]);
        if (result != 0) {
          return result;
        }
      } 
      return left.length - right.length;
    }

    
    public String toString() {
      return "Longs.lexicographicalComparator()";
    }
  }





  
  public static void sortDescending(long[] array) {
    Preconditions.checkNotNull(array);
    sortDescending(array, 0, array.length);
  }






  
  public static void sortDescending(long[] array, int fromIndex, int toIndex) {
    Preconditions.checkNotNull(array);
    Preconditions.checkPositionIndexes(fromIndex, toIndex, array.length);
    Arrays.sort(array, fromIndex, toIndex);
    reverse(array, fromIndex, toIndex);
  }






  
  public static void reverse(long[] array) {
    Preconditions.checkNotNull(array);
    reverse(array, 0, array.length);
  }










  
  public static void reverse(long[] array, int fromIndex, int toIndex) {
    Preconditions.checkNotNull(array);
    Preconditions.checkPositionIndexes(fromIndex, toIndex, array.length);
    for (int i = fromIndex, j = toIndex - 1; i < j; i++, j--) {
      long tmp = array[i];
      array[i] = array[j];
      array[j] = tmp;
    } 
  }













  
  public static long[] toArray(Collection<? extends Number> collection) {
    if (collection instanceof LongArrayAsList) {
      return ((LongArrayAsList)collection).toLongArray();
    }
    
    Object[] boxedArray = collection.toArray();
    int len = boxedArray.length;
    long[] array = new long[len];
    for (int i = 0; i < len; i++)
    {
      array[i] = ((Number)Preconditions.checkNotNull(boxedArray[i])).longValue();
    }
    return array;
  }















  
  public static List<Long> asList(long... backingArray) {
    if (backingArray.length == 0) {
      return Collections.emptyList();
    }
    return new LongArrayAsList(backingArray);
  }
  
  @GwtCompatible
  private static class LongArrayAsList extends AbstractList<Long> implements RandomAccess, Serializable {
    final long[] array;
    final int start;
    final int end;
    private static final long serialVersionUID = 0L;
    
    LongArrayAsList(long[] array) {
      this(array, 0, array.length);
    }
    
    LongArrayAsList(long[] array, int start, int end) {
      this.array = array;
      this.start = start;
      this.end = end;
    }

    
    public int size() {
      return this.end - this.start;
    }

    
    public boolean isEmpty() {
      return false;
    }

    
    public Long get(int index) {
      Preconditions.checkElementIndex(index, size());
      return Long.valueOf(this.array[this.start + index]);
    }

    
    public Spliterator.OfLong spliterator() {
      return Spliterators.spliterator(this.array, this.start, this.end, 0);
    }


    
    public boolean contains(Object target) {
      return (target instanceof Long && Longs.indexOf(this.array, ((Long)target).longValue(), this.start, this.end) != -1);
    }


    
    public int indexOf(Object target) {
      if (target instanceof Long) {
        int i = Longs.indexOf(this.array, ((Long)target).longValue(), this.start, this.end);
        if (i >= 0) {
          return i - this.start;
        }
      } 
      return -1;
    }


    
    public int lastIndexOf(Object target) {
      if (target instanceof Long) {
        int i = Longs.lastIndexOf(this.array, ((Long)target).longValue(), this.start, this.end);
        if (i >= 0) {
          return i - this.start;
        }
      } 
      return -1;
    }

    
    public Long set(int index, Long element) {
      Preconditions.checkElementIndex(index, size());
      long oldValue = this.array[this.start + index];
      
      this.array[this.start + index] = ((Long)Preconditions.checkNotNull(element)).longValue();
      return Long.valueOf(oldValue);
    }

    
    public List<Long> subList(int fromIndex, int toIndex) {
      int size = size();
      Preconditions.checkPositionIndexes(fromIndex, toIndex, size);
      if (fromIndex == toIndex) {
        return Collections.emptyList();
      }
      return new LongArrayAsList(this.array, this.start + fromIndex, this.start + toIndex);
    }

    
    public boolean equals(Object object) {
      if (object == this) {
        return true;
      }
      if (object instanceof LongArrayAsList) {
        LongArrayAsList that = (LongArrayAsList)object;
        int size = size();
        if (that.size() != size) {
          return false;
        }
        for (int i = 0; i < size; i++) {
          if (this.array[this.start + i] != that.array[that.start + i]) {
            return false;
          }
        } 
        return true;
      } 
      return super.equals(object);
    }

    
    public int hashCode() {
      int result = 1;
      for (int i = this.start; i < this.end; i++) {
        result = 31 * result + Longs.hashCode(this.array[i]);
      }
      return result;
    }

    
    public String toString() {
      StringBuilder builder = new StringBuilder(size() * 10);
      builder.append('[').append(this.array[this.start]);
      for (int i = this.start + 1; i < this.end; i++) {
        builder.append(", ").append(this.array[i]);
      }
      return builder.append(']').toString();
    }
    
    long[] toLongArray() {
      return Arrays.copyOfRange(this.array, this.start, this.end);
    }
  }
}
