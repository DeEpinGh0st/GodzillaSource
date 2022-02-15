package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.Spliterators;




















@GwtCompatible(serializable = true, emulated = true)
class RegularImmutableList<E>
  extends ImmutableList<E>
{
  static final ImmutableList<Object> EMPTY = new RegularImmutableList(new Object[0]);
  @VisibleForTesting
  final transient Object[] array;
  
  RegularImmutableList(Object[] array) {
    this.array = array;
  }

  
  public int size() {
    return this.array.length;
  }

  
  boolean isPartialView() {
    return false;
  }

  
  Object[] internalArray() {
    return this.array;
  }

  
  int internalArrayStart() {
    return 0;
  }

  
  int internalArrayEnd() {
    return this.array.length;
  }

  
  int copyIntoArray(Object[] dst, int dstOff) {
    System.arraycopy(this.array, 0, dst, dstOff, this.array.length);
    return dstOff + this.array.length;
  }



  
  public E get(int index) {
    return (E)this.array[index];
  }




  
  public UnmodifiableListIterator<E> listIterator(int index) {
    return Iterators.forArray((E[])this.array, 0, this.array.length, index);
  }

  
  public Spliterator<E> spliterator() {
    return Spliterators.spliterator(this.array, 1296);
  }
}
