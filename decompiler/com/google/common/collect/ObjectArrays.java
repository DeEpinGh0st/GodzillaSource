package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;


































@GwtCompatible(emulated = true)
public final class ObjectArrays
{
  @GwtIncompatible
  public static <T> T[] newArray(Class<T> type, int length) {
    return (T[])Array.newInstance(type, length);
  }






  
  public static <T> T[] newArray(T[] reference, int length) {
    return Platform.newArray(reference, length);
  }







  
  @GwtIncompatible
  public static <T> T[] concat(T[] first, T[] second, Class<T> type) {
    T[] result = newArray(type, first.length + second.length);
    System.arraycopy(first, 0, result, 0, first.length);
    System.arraycopy(second, 0, result, first.length, second.length);
    return result;
  }








  
  public static <T> T[] concat(T element, T[] array) {
    T[] result = newArray(array, array.length + 1);
    result[0] = element;
    System.arraycopy(array, 0, result, 1, array.length);
    return result;
  }








  
  public static <T> T[] concat(T[] array, T element) {
    T[] result = Arrays.copyOf(array, array.length + 1);
    result[array.length] = element;
    return result;
  }





















  
  static <T> T[] toArrayImpl(Collection<?> c, T[] array) {
    int size = c.size();
    if (array.length < size) {
      array = newArray(array, size);
    }
    fillArray(c, (Object[])array);
    if (array.length > size) {
      array[size] = null;
    }
    return array;
  }











  
  static <T> T[] toArrayImpl(Object[] src, int offset, int len, T[] dst) {
    Preconditions.checkPositionIndexes(offset, offset + len, src.length);
    if (dst.length < len) {
      dst = newArray(dst, len);
    } else if (dst.length > len) {
      dst[len] = null;
    } 
    System.arraycopy(src, offset, dst, 0, len);
    return dst;
  }












  
  static Object[] toArrayImpl(Collection<?> c) {
    return fillArray(c, new Object[c.size()]);
  }




  
  static Object[] copyAsObjectArray(Object[] elements, int offset, int length) {
    Preconditions.checkPositionIndexes(offset, offset + length, elements.length);
    if (length == 0) {
      return new Object[0];
    }
    Object[] result = new Object[length];
    System.arraycopy(elements, offset, result, 0, length);
    return result;
  }
  
  @CanIgnoreReturnValue
  private static Object[] fillArray(Iterable<?> elements, Object[] array) {
    int i = 0;
    for (Object element : elements) {
      array[i++] = element;
    }
    return array;
  }

  
  static void swap(Object[] array, int i, int j) {
    Object temp = array[i];
    array[i] = array[j];
    array[j] = temp;
  }
  
  @CanIgnoreReturnValue
  static Object[] checkElementsNotNull(Object... array) {
    return checkElementsNotNull(array, array.length);
  }
  
  @CanIgnoreReturnValue
  static Object[] checkElementsNotNull(Object[] array, int length) {
    for (int i = 0; i < length; i++) {
      checkElementNotNull(array[i], i);
    }
    return array;
  }


  
  @CanIgnoreReturnValue
  static Object checkElementNotNull(Object element, int index) {
    if (element == null) {
      throw new NullPointerException("at index " + index);
    }
    return element;
  }
}
