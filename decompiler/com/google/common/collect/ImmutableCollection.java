package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Predicate;































































































































































@GwtCompatible(emulated = true)
public abstract class ImmutableCollection<E>
  extends AbstractCollection<E>
  implements Serializable
{
  static final int SPLITERATOR_CHARACTERISTICS = 1296;
  
  public Spliterator<E> spliterator() {
    return Spliterators.spliterator(this, 1296);
  }
  
  private static final Object[] EMPTY_ARRAY = new Object[0];

  
  public final Object[] toArray() {
    return toArray(EMPTY_ARRAY);
  }

  
  @CanIgnoreReturnValue
  public final <T> T[] toArray(T[] other) {
    Preconditions.checkNotNull(other);
    int size = size();
    
    if (other.length < size) {
      Object[] internal = internalArray();
      if (internal != null) {
        return Platform.copy(internal, internalArrayStart(), internalArrayEnd(), other);
      }
      other = ObjectArrays.newArray(other, size);
    } else if (other.length > size) {
      other[size] = null;
    } 
    copyIntoArray((Object[])other, 0);
    return other;
  }


  
  Object[] internalArray() {
    return null;
  }




  
  int internalArrayStart() {
    throw new UnsupportedOperationException();
  }




  
  int internalArrayEnd() {
    throw new UnsupportedOperationException();
  }










  
  @Deprecated
  @CanIgnoreReturnValue
  public final boolean add(E e) {
    throw new UnsupportedOperationException();
  }







  
  @Deprecated
  @CanIgnoreReturnValue
  public final boolean remove(Object object) {
    throw new UnsupportedOperationException();
  }







  
  @Deprecated
  @CanIgnoreReturnValue
  public final boolean addAll(Collection<? extends E> newElements) {
    throw new UnsupportedOperationException();
  }







  
  @Deprecated
  @CanIgnoreReturnValue
  public final boolean removeAll(Collection<?> oldElements) {
    throw new UnsupportedOperationException();
  }







  
  @Deprecated
  @CanIgnoreReturnValue
  public final boolean removeIf(Predicate<? super E> filter) {
    throw new UnsupportedOperationException();
  }







  
  @Deprecated
  public final boolean retainAll(Collection<?> elementsToKeep) {
    throw new UnsupportedOperationException();
  }







  
  @Deprecated
  public final void clear() {
    throw new UnsupportedOperationException();
  }










  
  public ImmutableList<E> asList() {
    switch (size()) {
      case 0:
        return ImmutableList.of();
      case 1:
        return ImmutableList.of(iterator().next());
    } 
    return new RegularImmutableAsList<>(this, toArray());
  }













  
  @CanIgnoreReturnValue
  int copyIntoArray(Object[] dst, int offset) {
    for (UnmodifiableIterator<E> unmodifiableIterator = iterator(); unmodifiableIterator.hasNext(); ) { E e = unmodifiableIterator.next();
      dst[offset++] = e; }
    
    return offset;
  }

  
  Object writeReplace() {
    return new ImmutableList.SerializedForm(toArray());
  }
  
  public abstract UnmodifiableIterator<E> iterator();
  
  public abstract boolean contains(Object paramObject);
  
  abstract boolean isPartialView();
  
  public static abstract class Builder<E> { static final int DEFAULT_INITIAL_CAPACITY = 4;
    
    static int expandedCapacity(int oldCapacity, int minCapacity) {
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











    
    @CanIgnoreReturnValue
    public abstract Builder<E> add(E param1E);










    
    @CanIgnoreReturnValue
    public Builder<E> add(E... elements) {
      for (E element : elements) {
        add(element);
      }
      return this;
    }










    
    @CanIgnoreReturnValue
    public Builder<E> addAll(Iterable<? extends E> elements) {
      for (E element : elements) {
        add(element);
      }
      return this;
    }










    
    @CanIgnoreReturnValue
    public Builder<E> addAll(Iterator<? extends E> elements) {
      while (elements.hasNext()) {
        add(elements.next());
      }
      return this;
    }
    
    public abstract ImmutableCollection<E> build(); }

}
