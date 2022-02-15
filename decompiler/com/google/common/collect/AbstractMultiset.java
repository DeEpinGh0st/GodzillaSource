package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
































@GwtCompatible
abstract class AbstractMultiset<E>
  extends AbstractCollection<E>
  implements Multiset<E>
{
  private transient Set<E> elementSet;
  private transient Set<Multiset.Entry<E>> entrySet;
  
  public boolean isEmpty() {
    return entrySet().isEmpty();
  }

  
  public boolean contains(Object element) {
    return (count(element) > 0);
  }


  
  @CanIgnoreReturnValue
  public final boolean add(E element) {
    add(element, 1);
    return true;
  }

  
  @CanIgnoreReturnValue
  public int add(E element, int occurrences) {
    throw new UnsupportedOperationException();
  }

  
  @CanIgnoreReturnValue
  public final boolean remove(Object element) {
    return (remove(element, 1) > 0);
  }

  
  @CanIgnoreReturnValue
  public int remove(Object element, int occurrences) {
    throw new UnsupportedOperationException();
  }

  
  @CanIgnoreReturnValue
  public int setCount(E element, int count) {
    return Multisets.setCountImpl(this, element, count);
  }

  
  @CanIgnoreReturnValue
  public boolean setCount(E element, int oldCount, int newCount) {
    return Multisets.setCountImpl(this, element, oldCount, newCount);
  }









  
  @CanIgnoreReturnValue
  public final boolean addAll(Collection<? extends E> elementsToAdd) {
    return Multisets.addAllImpl(this, elementsToAdd);
  }

  
  @CanIgnoreReturnValue
  public final boolean removeAll(Collection<?> elementsToRemove) {
    return Multisets.removeAllImpl(this, elementsToRemove);
  }

  
  @CanIgnoreReturnValue
  public final boolean retainAll(Collection<?> elementsToRetain) {
    return Multisets.retainAllImpl(this, elementsToRetain);
  }



  
  public abstract void clear();



  
  public Set<E> elementSet() {
    Set<E> result = this.elementSet;
    if (result == null) {
      this.elementSet = result = createElementSet();
    }
    return result;
  }




  
  Set<E> createElementSet() {
    return new ElementSet();
  }
  
  abstract Iterator<E> elementIterator();
  
  class ElementSet extends Multisets.ElementSet<E> {
    Multiset<E> multiset() {
      return AbstractMultiset.this;
    }

    
    public Iterator<E> iterator() {
      return AbstractMultiset.this.elementIterator();
    }
  }





  
  public Set<Multiset.Entry<E>> entrySet() {
    Set<Multiset.Entry<E>> result = this.entrySet;
    if (result == null) {
      this.entrySet = result = createEntrySet();
    }
    return result;
  }
  
  class EntrySet
    extends Multisets.EntrySet<E>
  {
    Multiset<E> multiset() {
      return AbstractMultiset.this;
    }

    
    public Iterator<Multiset.Entry<E>> iterator() {
      return AbstractMultiset.this.entryIterator();
    }

    
    public int size() {
      return AbstractMultiset.this.distinctElements();
    }
  }
  
  Set<Multiset.Entry<E>> createEntrySet() {
    return new EntrySet();
  }



  
  abstract Iterator<Multiset.Entry<E>> entryIterator();



  
  abstract int distinctElements();



  
  public final boolean equals(Object object) {
    return Multisets.equalsImpl(this, object);
  }






  
  public final int hashCode() {
    return entrySet().hashCode();
  }







  
  public final String toString() {
    return entrySet().toString();
  }
}
