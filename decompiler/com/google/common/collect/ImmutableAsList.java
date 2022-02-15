package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;


























@GwtCompatible(serializable = true, emulated = true)
abstract class ImmutableAsList<E>
  extends ImmutableList<E>
{
  abstract ImmutableCollection<E> delegateCollection();
  
  public boolean contains(Object target) {
    return delegateCollection().contains(target);
  }

  
  public int size() {
    return delegateCollection().size();
  }

  
  public boolean isEmpty() {
    return delegateCollection().isEmpty();
  }

  
  boolean isPartialView() {
    return delegateCollection().isPartialView();
  }
  
  @GwtIncompatible
  static class SerializedForm implements Serializable {
    final ImmutableCollection<?> collection;
    private static final long serialVersionUID = 0L;
    
    SerializedForm(ImmutableCollection<?> collection) {
      this.collection = collection;
    }
    
    Object readResolve() {
      return this.collection.asList();
    }
  }


  
  @GwtIncompatible
  private void readObject(ObjectInputStream stream) throws InvalidObjectException {
    throw new InvalidObjectException("Use SerializedForm");
  }

  
  @GwtIncompatible
  Object writeReplace() {
    return new SerializedForm(delegateCollection());
  }
}
