package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;





















@GwtCompatible(serializable = true, emulated = true)
final class RegularImmutableSet<E>
  extends ImmutableSet<E>
{
  static final RegularImmutableSet<Object> EMPTY = new RegularImmutableSet(new Object[0], 0, null, 0);
  
  private final transient Object[] elements;
  
  @VisibleForTesting
  final transient Object[] table;
  
  private final transient int mask;
  private final transient int hashCode;
  
  RegularImmutableSet(Object[] elements, int hashCode, Object[] table, int mask) {
    this.elements = elements;
    this.table = table;
    this.mask = mask;
    this.hashCode = hashCode;
  }

  
  public boolean contains(Object target) {
    Object[] table = this.table;
    if (target == null || table == null) {
      return false;
    }
    for (int i = Hashing.smearedHash(target);; i++) {
      i &= this.mask;
      Object candidate = table[i];
      if (candidate == null)
        return false; 
      if (candidate.equals(target)) {
        return true;
      }
    } 
  }

  
  public int size() {
    return this.elements.length;
  }

  
  public UnmodifiableIterator<E> iterator() {
    return Iterators.forArray((E[])this.elements);
  }

  
  public Spliterator<E> spliterator() {
    return Spliterators.spliterator(this.elements, 1297);
  }

  
  Object[] internalArray() {
    return this.elements;
  }

  
  int internalArrayStart() {
    return 0;
  }

  
  int internalArrayEnd() {
    return this.elements.length;
  }

  
  int copyIntoArray(Object[] dst, int offset) {
    System.arraycopy(this.elements, 0, dst, offset, this.elements.length);
    return offset + this.elements.length;
  }

  
  ImmutableList<E> createAsList() {
    return (this.table == null) ? ImmutableList.<E>of() : new RegularImmutableAsList<>(this, this.elements);
  }

  
  boolean isPartialView() {
    return false;
  }

  
  public int hashCode() {
    return this.hashCode;
  }

  
  boolean isHashCodeFast() {
    return true;
  }
}
