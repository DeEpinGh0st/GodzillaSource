package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;

























@GwtCompatible(serializable = true, emulated = true)
final class RegularImmutableSortedSet<E>
  extends ImmutableSortedSet<E>
{
  static final RegularImmutableSortedSet<Comparable> NATURAL_EMPTY_SET = new RegularImmutableSortedSet(
      ImmutableList.of(), Ordering.natural());
  
  private final transient ImmutableList<E> elements;
  
  RegularImmutableSortedSet(ImmutableList<E> elements, Comparator<? super E> comparator) {
    super(comparator);
    this.elements = elements;
  }

  
  Object[] internalArray() {
    return this.elements.internalArray();
  }

  
  int internalArrayStart() {
    return this.elements.internalArrayStart();
  }

  
  int internalArrayEnd() {
    return this.elements.internalArrayEnd();
  }

  
  public UnmodifiableIterator<E> iterator() {
    return this.elements.iterator();
  }

  
  @GwtIncompatible
  public UnmodifiableIterator<E> descendingIterator() {
    return this.elements.reverse().iterator();
  }

  
  public Spliterator<E> spliterator() {
    return asList().spliterator();
  }

  
  public void forEach(Consumer<? super E> action) {
    this.elements.forEach(action);
  }

  
  public int size() {
    return this.elements.size();
  }

  
  public boolean contains(Object o) {
    try {
      return (o != null && unsafeBinarySearch(o) >= 0);
    } catch (ClassCastException e) {
      return false;
    } 
  }





  
  public boolean containsAll(Collection<?> targets) {
    if (targets instanceof Multiset) {
      targets = ((Multiset)targets).elementSet();
    }
    if (!SortedIterables.hasSameComparator(comparator(), targets) || targets.size() <= 1) {
      return super.containsAll(targets);
    }




    
    Iterator<E> thisIterator = iterator();
    
    Iterator<?> thatIterator = targets.iterator();

    
    if (!thisIterator.hasNext()) {
      return false;
    }
    
    Object target = thatIterator.next();
    E current = thisIterator.next();
    try {
      while (true) {
        int cmp = unsafeCompare(current, target);
        
        if (cmp < 0) {
          if (!thisIterator.hasNext()) {
            return false;
          }
          current = thisIterator.next(); continue;
        }  if (cmp == 0) {
          if (!thatIterator.hasNext()) {
            return true;
          }
          target = thatIterator.next(); continue;
        } 
        if (cmp > 0) {
          return false;
        }
      } 
    } catch (NullPointerException|ClassCastException e) {
      return false;
    } 
  }
  
  private int unsafeBinarySearch(Object key) throws ClassCastException {
    return Collections.binarySearch(this.elements, (E)key, (Comparator)unsafeComparator());
  }

  
  boolean isPartialView() {
    return this.elements.isPartialView();
  }

  
  int copyIntoArray(Object[] dst, int offset) {
    return this.elements.copyIntoArray(dst, offset);
  }

  
  public boolean equals(Object object) {
    if (object == this) {
      return true;
    }
    if (!(object instanceof Set)) {
      return false;
    }
    
    Set<?> that = (Set)object;
    if (size() != that.size())
      return false; 
    if (isEmpty()) {
      return true;
    }
    
    if (SortedIterables.hasSameComparator(this.comparator, that)) {
      Iterator<?> otherIterator = that.iterator();
      try {
        Iterator<E> iterator = iterator();
        while (iterator.hasNext()) {
          Object element = iterator.next();
          Object otherElement = otherIterator.next();
          if (otherElement == null || unsafeCompare(element, otherElement) != 0) {
            return false;
          }
        } 
        return true;
      } catch (ClassCastException e) {
        return false;
      } catch (NoSuchElementException e) {
        return false;
      } 
    } 
    return containsAll(that);
  }

  
  public E first() {
    if (isEmpty()) {
      throw new NoSuchElementException();
    }
    return this.elements.get(0);
  }

  
  public E last() {
    if (isEmpty()) {
      throw new NoSuchElementException();
    }
    return this.elements.get(size() - 1);
  }

  
  public E lower(E element) {
    int index = headIndex(element, false) - 1;
    return (index == -1) ? null : this.elements.get(index);
  }

  
  public E floor(E element) {
    int index = headIndex(element, true) - 1;
    return (index == -1) ? null : this.elements.get(index);
  }

  
  public E ceiling(E element) {
    int index = tailIndex(element, true);
    return (index == size()) ? null : this.elements.get(index);
  }

  
  public E higher(E element) {
    int index = tailIndex(element, false);
    return (index == size()) ? null : this.elements.get(index);
  }

  
  ImmutableSortedSet<E> headSetImpl(E toElement, boolean inclusive) {
    return getSubSet(0, headIndex(toElement, inclusive));
  }
  
  int headIndex(E toElement, boolean inclusive) {
    int index = Collections.binarySearch(this.elements, (E)Preconditions.checkNotNull(toElement), comparator());
    if (index >= 0) {
      return inclusive ? (index + 1) : index;
    }
    return index ^ 0xFFFFFFFF;
  }



  
  ImmutableSortedSet<E> subSetImpl(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
    return tailSetImpl(fromElement, fromInclusive).headSetImpl(toElement, toInclusive);
  }

  
  ImmutableSortedSet<E> tailSetImpl(E fromElement, boolean inclusive) {
    return getSubSet(tailIndex(fromElement, inclusive), size());
  }
  
  int tailIndex(E fromElement, boolean inclusive) {
    int index = Collections.binarySearch(this.elements, (E)Preconditions.checkNotNull(fromElement), comparator());
    if (index >= 0) {
      return inclusive ? index : (index + 1);
    }
    return index ^ 0xFFFFFFFF;
  }





  
  Comparator<Object> unsafeComparator() {
    return (Comparator)this.comparator;
  }
  
  RegularImmutableSortedSet<E> getSubSet(int newFromIndex, int newToIndex) {
    if (newFromIndex == 0 && newToIndex == size())
      return this; 
    if (newFromIndex < newToIndex) {
      return new RegularImmutableSortedSet(this.elements
          .subList(newFromIndex, newToIndex), this.comparator);
    }
    return emptySet(this.comparator);
  }

  
  int indexOf(Object target) {
    int position;
    if (target == null) {
      return -1;
    }
    
    try {
      position = Collections.binarySearch(this.elements, (E)target, (Comparator)unsafeComparator());
    } catch (ClassCastException e) {
      return -1;
    } 
    return (position >= 0) ? position : -1;
  }

  
  ImmutableList<E> createAsList() {
    return (size() <= 1) ? this.elements : new ImmutableSortedAsList<>(this, this.elements);
  }

  
  ImmutableSortedSet<E> createDescendingSet() {
    Comparator<? super E> reversedOrder = Collections.reverseOrder(this.comparator);
    return isEmpty() ? 
      emptySet(reversedOrder) : new RegularImmutableSortedSet(this.elements
        .reverse(), reversedOrder);
  }
}
