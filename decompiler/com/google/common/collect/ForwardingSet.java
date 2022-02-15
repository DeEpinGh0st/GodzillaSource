package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Set;















































@GwtCompatible
public abstract class ForwardingSet<E>
  extends ForwardingCollection<E>
  implements Set<E>
{
  public boolean equals(Object object) {
    return (object == this || delegate().equals(object));
  }

  
  public int hashCode() {
    return delegate().hashCode();
  }








  
  protected boolean standardRemoveAll(Collection<?> collection) {
    return Sets.removeAllImpl(this, (Collection)Preconditions.checkNotNull(collection));
  }







  
  protected boolean standardEquals(Object object) {
    return Sets.equalsImpl(this, object);
  }






  
  protected int standardHashCode() {
    return Sets.hashCodeImpl(this);
  }
  
  protected abstract Set<E> delegate();
}
