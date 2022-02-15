package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;













































@GwtCompatible
public abstract class ForwardingMultiset<E>
  extends ForwardingCollection<E>
  implements Multiset<E>
{
  public int count(Object element) {
    return delegate().count(element);
  }

  
  @CanIgnoreReturnValue
  public int add(E element, int occurrences) {
    return delegate().add(element, occurrences);
  }

  
  @CanIgnoreReturnValue
  public int remove(Object element, int occurrences) {
    return delegate().remove(element, occurrences);
  }

  
  public Set<E> elementSet() {
    return delegate().elementSet();
  }

  
  public Set<Multiset.Entry<E>> entrySet() {
    return delegate().entrySet();
  }

  
  public boolean equals(Object object) {
    return (object == this || delegate().equals(object));
  }

  
  public int hashCode() {
    return delegate().hashCode();
  }

  
  @CanIgnoreReturnValue
  public int setCount(E element, int count) {
    return delegate().setCount(element, count);
  }

  
  @CanIgnoreReturnValue
  public boolean setCount(E element, int oldCount, int newCount) {
    return delegate().setCount(element, oldCount, newCount);
  }







  
  protected boolean standardContains(Object object) {
    return (count(object) > 0);
  }








  
  protected void standardClear() {
    Iterators.clear(entrySet().iterator());
  }







  
  @Beta
  protected int standardCount(Object object) {
    for (Multiset.Entry<?> entry : entrySet()) {
      if (Objects.equal(entry.getElement(), object)) {
        return entry.getCount();
      }
    } 
    return 0;
  }







  
  protected boolean standardAdd(E element) {
    add(element, 1);
    return true;
  }








  
  @Beta
  protected boolean standardAddAll(Collection<? extends E> elementsToAdd) {
    return Multisets.addAllImpl(this, elementsToAdd);
  }








  
  protected boolean standardRemove(Object element) {
    return (remove(element, 1) > 0);
  }








  
  protected boolean standardRemoveAll(Collection<?> elementsToRemove) {
    return Multisets.removeAllImpl(this, elementsToRemove);
  }








  
  protected boolean standardRetainAll(Collection<?> elementsToRetain) {
    return Multisets.retainAllImpl(this, elementsToRetain);
  }








  
  protected int standardSetCount(E element, int count) {
    return Multisets.setCountImpl(this, element, count);
  }







  
  protected boolean standardSetCount(E element, int oldCount, int newCount) {
    return Multisets.setCountImpl(this, element, oldCount, newCount);
  }













  
  @Beta
  protected class StandardElementSet
    extends Multisets.ElementSet<E>
  {
    Multiset<E> multiset() {
      return ForwardingMultiset.this;
    }

    
    public Iterator<E> iterator() {
      return Multisets.elementIterator(multiset().entrySet().iterator());
    }
  }







  
  protected Iterator<E> standardIterator() {
    return Multisets.iteratorImpl(this);
  }







  
  protected int standardSize() {
    return Multisets.linearTimeSizeImpl(this);
  }







  
  protected boolean standardEquals(Object object) {
    return Multisets.equalsImpl(this, object);
  }







  
  protected int standardHashCode() {
    return entrySet().hashCode();
  }








  
  protected String standardToString() {
    return entrySet().toString();
  }
  
  protected abstract Multiset<E> delegate();
}
