package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Iterator;
import java.util.ListIterator;





































@GwtCompatible
public abstract class ForwardingListIterator<E>
  extends ForwardingIterator<E>
  implements ListIterator<E>
{
  public void add(E element) {
    delegate().add(element);
  }

  
  public boolean hasPrevious() {
    return delegate().hasPrevious();
  }

  
  public int nextIndex() {
    return delegate().nextIndex();
  }

  
  @CanIgnoreReturnValue
  public E previous() {
    return delegate().previous();
  }

  
  public int previousIndex() {
    return delegate().previousIndex();
  }

  
  public void set(E element) {
    delegate().set(element);
  }
  
  protected abstract ListIterator<E> delegate();
}
