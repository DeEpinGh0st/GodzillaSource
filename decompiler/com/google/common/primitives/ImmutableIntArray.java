package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;



































































@Immutable
@Beta
@GwtCompatible
public final class ImmutableIntArray
  implements Serializable
{
  private static final ImmutableIntArray EMPTY = new ImmutableIntArray(new int[0]);
  private final int[] array;
  
  public static ImmutableIntArray of() {
    return EMPTY;
  }
  private final transient int start; private final int end;
  
  public static ImmutableIntArray of(int e0) {
    return new ImmutableIntArray(new int[] { e0 });
  }

  
  public static ImmutableIntArray of(int e0, int e1) {
    return new ImmutableIntArray(new int[] { e0, e1 });
  }

  
  public static ImmutableIntArray of(int e0, int e1, int e2) {
    return new ImmutableIntArray(new int[] { e0, e1, e2 });
  }

  
  public static ImmutableIntArray of(int e0, int e1, int e2, int e3) {
    return new ImmutableIntArray(new int[] { e0, e1, e2, e3 });
  }

  
  public static ImmutableIntArray of(int e0, int e1, int e2, int e3, int e4) {
    return new ImmutableIntArray(new int[] { e0, e1, e2, e3, e4 });
  }

  
  public static ImmutableIntArray of(int e0, int e1, int e2, int e3, int e4, int e5) {
    return new ImmutableIntArray(new int[] { e0, e1, e2, e3, e4, e5 });
  }









  
  public static ImmutableIntArray of(int first, int... rest) {
    Preconditions.checkArgument((rest.length <= 2147483646), "the total number of elements must fit in an int");
    
    int[] array = new int[rest.length + 1];
    array[0] = first;
    System.arraycopy(rest, 0, array, 1, rest.length);
    return new ImmutableIntArray(array);
  }

  
  public static ImmutableIntArray copyOf(int[] values) {
    return (values.length == 0) ? EMPTY : new ImmutableIntArray(Arrays.copyOf(values, values.length));
  }

  
  public static ImmutableIntArray copyOf(Collection<Integer> values) {
    return values.isEmpty() ? EMPTY : new ImmutableIntArray(Ints.toArray((Collection)values));
  }







  
  public static ImmutableIntArray copyOf(Iterable<Integer> values) {
    if (values instanceof Collection) {
      return copyOf((Collection<Integer>)values);
    }
    return builder().addAll(values).build();
  }


  
  public static ImmutableIntArray copyOf(IntStream stream) {
    int[] array = stream.toArray();
    return (array.length == 0) ? EMPTY : new ImmutableIntArray(array);
  }










  
  public static Builder builder(int initialCapacity) {
    Preconditions.checkArgument((initialCapacity >= 0), "Invalid initialCapacity: %s", initialCapacity);
    return new Builder(initialCapacity);
  }








  
  public static Builder builder() {
    return new Builder(10);
  }


  
  @CanIgnoreReturnValue
  public static final class Builder
  {
    private int[] array;
    
    private int count = 0;
    
    Builder(int initialCapacity) {
      this.array = new int[initialCapacity];
    }




    
    public Builder add(int value) {
      ensureRoomFor(1);
      this.array[this.count] = value;
      this.count++;
      return this;
    }




    
    public Builder addAll(int[] values) {
      ensureRoomFor(values.length);
      System.arraycopy(values, 0, this.array, this.count, values.length);
      this.count += values.length;
      return this;
    }




    
    public Builder addAll(Iterable<Integer> values) {
      if (values instanceof Collection) {
        return addAll((Collection<Integer>)values);
      }
      for (Integer value : values) {
        add(value.intValue());
      }
      return this;
    }




    
    public Builder addAll(Collection<Integer> values) {
      ensureRoomFor(values.size());
      for (Integer value : values) {
        this.array[this.count++] = value.intValue();
      }
      return this;
    }




    
    public Builder addAll(IntStream stream) {
      Spliterator.OfInt spliterator = stream.spliterator();
      long size = spliterator.getExactSizeIfKnown();
      if (size > 0L) {
        ensureRoomFor(Ints.saturatedCast(size));
      }
      spliterator.forEachRemaining(this::add);
      return this;
    }




    
    public Builder addAll(ImmutableIntArray values) {
      ensureRoomFor(values.length());
      System.arraycopy(values.array, values.start, this.array, this.count, values.length());
      this.count += values.length();
      return this;
    }
    
    private void ensureRoomFor(int numberToAdd) {
      int newCount = this.count + numberToAdd;
      if (newCount > this.array.length) {
        int[] newArray = new int[expandedCapacity(this.array.length, newCount)];
        System.arraycopy(this.array, 0, newArray, 0, this.count);
        this.array = newArray;
      } 
    }

    
    private static int expandedCapacity(int oldCapacity, int minCapacity) {
      if (minCapacity < 0) {
        throw new AssertionError("cannot store more than MAX_VALUE elements");
      }
      
      int newCapacity = oldCapacity + (oldCapacity >> 1) + 1;
      if (newCapacity < minCapacity) {
        newCapacity = Integer.highestOneBit(minCapacity - 1) << 1;
      }
      if (newCapacity < 0) {
        newCapacity = Integer.MAX_VALUE;
      }
      return newCapacity;
    }








    
    @CheckReturnValue
    public ImmutableIntArray build() {
      return (this.count == 0) ? ImmutableIntArray.EMPTY : new ImmutableIntArray(this.array, 0, this.count);
    }
  }
















  
  private ImmutableIntArray(int[] array) {
    this(array, 0, array.length);
  }
  
  private ImmutableIntArray(int[] array, int start, int end) {
    this.array = array;
    this.start = start;
    this.end = end;
  }

  
  public int length() {
    return this.end - this.start;
  }

  
  public boolean isEmpty() {
    return (this.end == this.start);
  }






  
  public int get(int index) {
    Preconditions.checkElementIndex(index, length());
    return this.array[this.start + index];
  }




  
  public int indexOf(int target) {
    for (int i = this.start; i < this.end; i++) {
      if (this.array[i] == target) {
        return i - this.start;
      }
    } 
    return -1;
  }




  
  public int lastIndexOf(int target) {
    for (int i = this.end - 1; i >= this.start; i--) {
      if (this.array[i] == target) {
        return i - this.start;
      }
    } 
    return -1;
  }




  
  public boolean contains(int target) {
    return (indexOf(target) >= 0);
  }

  
  public void forEach(IntConsumer consumer) {
    Preconditions.checkNotNull(consumer);
    for (int i = this.start; i < this.end; i++) {
      consumer.accept(this.array[i]);
    }
  }

  
  public IntStream stream() {
    return Arrays.stream(this.array, this.start, this.end);
  }

  
  public int[] toArray() {
    return Arrays.copyOfRange(this.array, this.start, this.end);
  }







  
  public ImmutableIntArray subArray(int startIndex, int endIndex) {
    Preconditions.checkPositionIndexes(startIndex, endIndex, length());
    return (startIndex == endIndex) ? EMPTY : new ImmutableIntArray(this.array, this.start + startIndex, this.start + endIndex);
  }


  
  private Spliterator.OfInt spliterator() {
    return Spliterators.spliterator(this.array, this.start, this.end, 1040);
  }












  
  public List<Integer> asList() {
    return new AsList(this);
  }
  
  static class AsList extends AbstractList<Integer> implements RandomAccess, Serializable {
    private final ImmutableIntArray parent;
    
    private AsList(ImmutableIntArray parent) {
      this.parent = parent;
    }



    
    public int size() {
      return this.parent.length();
    }

    
    public Integer get(int index) {
      return Integer.valueOf(this.parent.get(index));
    }

    
    public boolean contains(Object target) {
      return (indexOf(target) >= 0);
    }

    
    public int indexOf(Object target) {
      return (target instanceof Integer) ? this.parent.indexOf(((Integer)target).intValue()) : -1;
    }

    
    public int lastIndexOf(Object target) {
      return (target instanceof Integer) ? this.parent.lastIndexOf(((Integer)target).intValue()) : -1;
    }

    
    public List<Integer> subList(int fromIndex, int toIndex) {
      return this.parent.subArray(fromIndex, toIndex).asList();
    }


    
    public Spliterator<Integer> spliterator() {
      return this.parent.spliterator();
    }

    
    public boolean equals(Object object) {
      if (object instanceof AsList) {
        AsList asList = (AsList)object;
        return this.parent.equals(asList.parent);
      } 
      
      if (!(object instanceof List)) {
        return false;
      }
      List<?> that = (List)object;
      if (size() != that.size()) {
        return false;
      }
      int i = this.parent.start;
      
      for (Object element : that) {
        if (!(element instanceof Integer) || this.parent.array[i++] != ((Integer)element).intValue()) {
          return false;
        }
      } 
      return true;
    }


    
    public int hashCode() {
      return this.parent.hashCode();
    }

    
    public String toString() {
      return this.parent.toString();
    }
  }





  
  public boolean equals(Object object) {
    if (object == this) {
      return true;
    }
    if (!(object instanceof ImmutableIntArray)) {
      return false;
    }
    ImmutableIntArray that = (ImmutableIntArray)object;
    if (length() != that.length()) {
      return false;
    }
    for (int i = 0; i < length(); i++) {
      if (get(i) != that.get(i)) {
        return false;
      }
    } 
    return true;
  }


  
  public int hashCode() {
    int hash = 1;
    for (int i = this.start; i < this.end; i++) {
      hash *= 31;
      hash += Ints.hashCode(this.array[i]);
    } 
    return hash;
  }





  
  public String toString() {
    if (isEmpty()) {
      return "[]";
    }
    StringBuilder builder = new StringBuilder(length() * 5);
    builder.append('[').append(this.array[this.start]);
    
    for (int i = this.start + 1; i < this.end; i++) {
      builder.append(", ").append(this.array[i]);
    }
    builder.append(']');
    return builder.toString();
  }






  
  public ImmutableIntArray trimmed() {
    return isPartialView() ? new ImmutableIntArray(toArray()) : this;
  }
  
  private boolean isPartialView() {
    return (this.start > 0 || this.end < this.array.length);
  }
  
  Object writeReplace() {
    return trimmed();
  }
  
  Object readResolve() {
    return isEmpty() ? EMPTY : this;
  }
}
