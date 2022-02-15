package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;
















































@GwtCompatible(emulated = true)
public final class Chars
{
  public static final int BYTES = 2;
  
  public static int hashCode(char value) {
    return value;
  }








  
  public static char checkedCast(long value) {
    char result = (char)(int)value;
    Preconditions.checkArgument((result == value), "Out of range: %s", value);
    return result;
  }








  
  public static char saturatedCast(long value) {
    if (value > 65535L) {
      return Character.MAX_VALUE;
    }
    if (value < 0L) {
      return Character.MIN_VALUE;
    }
    return (char)(int)value;
  }












  
  public static int compare(char a, char b) {
    return a - b;
  }







  
  public static boolean contains(char[] array, char target) {
    for (char value : array) {
      if (value == target) {
        return true;
      }
    } 
    return false;
  }








  
  public static int indexOf(char[] array, char target) {
    return indexOf(array, target, 0, array.length);
  }

  
  private static int indexOf(char[] array, char target, int start, int end) {
    for (int i = start; i < end; i++) {
      if (array[i] == target) {
        return i;
      }
    } 
    return -1;
  }










  
  public static int indexOf(char[] array, char[] target) {
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








  
  public static int lastIndexOf(char[] array, char target) {
    return lastIndexOf(array, target, 0, array.length);
  }

  
  private static int lastIndexOf(char[] array, char target, int start, int end) {
    for (int i = end - 1; i >= start; i--) {
      if (array[i] == target) {
        return i;
      }
    } 
    return -1;
  }








  
  public static char min(char... array) {
    Preconditions.checkArgument((array.length > 0));
    char min = array[0];
    for (int i = 1; i < array.length; i++) {
      if (array[i] < min) {
        min = array[i];
      }
    } 
    return min;
  }








  
  public static char max(char... array) {
    Preconditions.checkArgument((array.length > 0));
    char max = array[0];
    for (int i = 1; i < array.length; i++) {
      if (array[i] > max) {
        max = array[i];
      }
    } 
    return max;
  }













  
  @Beta
  public static char constrainToRange(char value, char min, char max) {
    Preconditions.checkArgument((min <= max), "min (%s) must be less than or equal to max (%s)", min, max);
    return (value < min) ? min : ((value < max) ? value : max);
  }







  
  public static char[] concat(char[]... arrays) {
    int length = 0;
    for (char[] array : arrays) {
      length += array.length;
    }
    char[] result = new char[length];
    int pos = 0;
    for (char[] array : arrays) {
      System.arraycopy(array, 0, result, pos, array.length);
      pos += array.length;
    } 
    return result;
  }









  
  @GwtIncompatible
  public static byte[] toByteArray(char value) {
    return new byte[] { (byte)(value >> 8), (byte)value };
  }










  
  @GwtIncompatible
  public static char fromByteArray(byte[] bytes) {
    Preconditions.checkArgument((bytes.length >= 2), "array too small: %s < %s", bytes.length, 2);
    return fromBytes(bytes[0], bytes[1]);
  }






  
  @GwtIncompatible
  public static char fromBytes(byte b1, byte b2) {
    return (char)(b1 << 8 | b2 & 0xFF);
  }













  
  public static char[] ensureCapacity(char[] array, int minLength, int padding) {
    Preconditions.checkArgument((minLength >= 0), "Invalid minLength: %s", minLength);
    Preconditions.checkArgument((padding >= 0), "Invalid padding: %s", padding);
    return (array.length < minLength) ? Arrays.copyOf(array, minLength + padding) : array;
  }








  
  public static String join(String separator, char... array) {
    Preconditions.checkNotNull(separator);
    int len = array.length;
    if (len == 0) {
      return "";
    }
    
    StringBuilder builder = new StringBuilder(len + separator.length() * (len - 1));
    builder.append(array[0]);
    for (int i = 1; i < len; i++) {
      builder.append(separator).append(array[i]);
    }
    return builder.toString();
  }














  
  public static Comparator<char[]> lexicographicalComparator() {
    return LexicographicalComparator.INSTANCE;
  }
  
  private enum LexicographicalComparator implements Comparator<char[]> {
    INSTANCE;

    
    public int compare(char[] left, char[] right) {
      int minLength = Math.min(left.length, right.length);
      for (int i = 0; i < minLength; i++) {
        int result = Chars.compare(left[i], right[i]);
        if (result != 0) {
          return result;
        }
      } 
      return left.length - right.length;
    }

    
    public String toString() {
      return "Chars.lexicographicalComparator()";
    }
  }












  
  public static char[] toArray(Collection<Character> collection) {
    if (collection instanceof CharArrayAsList) {
      return ((CharArrayAsList)collection).toCharArray();
    }
    
    Object[] boxedArray = collection.toArray();
    int len = boxedArray.length;
    char[] array = new char[len];
    for (int i = 0; i < len; i++)
    {
      array[i] = ((Character)Preconditions.checkNotNull(boxedArray[i])).charValue();
    }
    return array;
  }





  
  public static void sortDescending(char[] array) {
    Preconditions.checkNotNull(array);
    sortDescending(array, 0, array.length);
  }






  
  public static void sortDescending(char[] array, int fromIndex, int toIndex) {
    Preconditions.checkNotNull(array);
    Preconditions.checkPositionIndexes(fromIndex, toIndex, array.length);
    Arrays.sort(array, fromIndex, toIndex);
    reverse(array, fromIndex, toIndex);
  }






  
  public static void reverse(char[] array) {
    Preconditions.checkNotNull(array);
    reverse(array, 0, array.length);
  }










  
  public static void reverse(char[] array, int fromIndex, int toIndex) {
    Preconditions.checkNotNull(array);
    Preconditions.checkPositionIndexes(fromIndex, toIndex, array.length);
    for (int i = fromIndex, j = toIndex - 1; i < j; i++, j--) {
      char tmp = array[i];
      array[i] = array[j];
      array[j] = tmp;
    } 
  }












  
  public static List<Character> asList(char... backingArray) {
    if (backingArray.length == 0) {
      return Collections.emptyList();
    }
    return new CharArrayAsList(backingArray);
  }
  
  @GwtCompatible
  private static class CharArrayAsList extends AbstractList<Character> implements RandomAccess, Serializable {
    final char[] array;
    final int start;
    final int end;
    private static final long serialVersionUID = 0L;
    
    CharArrayAsList(char[] array) {
      this(array, 0, array.length);
    }
    
    CharArrayAsList(char[] array, int start, int end) {
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

    
    public Character get(int index) {
      Preconditions.checkElementIndex(index, size());
      return Character.valueOf(this.array[this.start + index]);
    }


    
    public boolean contains(Object target) {
      return (target instanceof Character && Chars
        .indexOf(this.array, ((Character)target).charValue(), this.start, this.end) != -1);
    }


    
    public int indexOf(Object target) {
      if (target instanceof Character) {
        int i = Chars.indexOf(this.array, ((Character)target).charValue(), this.start, this.end);
        if (i >= 0) {
          return i - this.start;
        }
      } 
      return -1;
    }


    
    public int lastIndexOf(Object target) {
      if (target instanceof Character) {
        int i = Chars.lastIndexOf(this.array, ((Character)target).charValue(), this.start, this.end);
        if (i >= 0) {
          return i - this.start;
        }
      } 
      return -1;
    }

    
    public Character set(int index, Character element) {
      Preconditions.checkElementIndex(index, size());
      char oldValue = this.array[this.start + index];
      
      this.array[this.start + index] = ((Character)Preconditions.checkNotNull(element)).charValue();
      return Character.valueOf(oldValue);
    }

    
    public List<Character> subList(int fromIndex, int toIndex) {
      int size = size();
      Preconditions.checkPositionIndexes(fromIndex, toIndex, size);
      if (fromIndex == toIndex) {
        return Collections.emptyList();
      }
      return new CharArrayAsList(this.array, this.start + fromIndex, this.start + toIndex);
    }

    
    public boolean equals(Object object) {
      if (object == this) {
        return true;
      }
      if (object instanceof CharArrayAsList) {
        CharArrayAsList that = (CharArrayAsList)object;
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
        result = 31 * result + Chars.hashCode(this.array[i]);
      }
      return result;
    }

    
    public String toString() {
      StringBuilder builder = new StringBuilder(size() * 3);
      builder.append('[').append(this.array[this.start]);
      for (int i = this.start + 1; i < this.end; i++) {
        builder.append(", ").append(this.array[i]);
      }
      return builder.append(']').toString();
    }
    
    char[] toCharArray() {
      return Arrays.copyOfRange(this.array, this.start, this.end);
    }
  }
}
