package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CompatibleWith;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;






























































































































































































































































































































@GwtCompatible
public interface Multiset<E>
  extends Collection<E>
{
  @Beta
  default void forEachEntry(ObjIntConsumer<? super E> action) {
    Preconditions.checkNotNull(action);
    entrySet().forEach(entry -> action.accept(entry.getElement(), entry.getCount()));
  }




















































































































  
  default void forEach(Consumer<? super E> action) {
    Preconditions.checkNotNull(action);
    entrySet()
      .forEach(entry -> {
          E elem = entry.getElement();
          int count = entry.getCount();
          for (int i = 0; i < count; i++) {
            action.accept(elem);
          }
        });
  }


  
  default Spliterator<E> spliterator() {
    return Multisets.spliteratorImpl(this);
  }
  
  int size();
  
  int count(@CompatibleWith("E") Object paramObject);
  
  @CanIgnoreReturnValue
  int add(E paramE, int paramInt);
  
  @CanIgnoreReturnValue
  boolean add(E paramE);
  
  @CanIgnoreReturnValue
  int remove(@CompatibleWith("E") Object paramObject, int paramInt);
  
  @CanIgnoreReturnValue
  boolean remove(Object paramObject);
  
  @CanIgnoreReturnValue
  int setCount(E paramE, int paramInt);
  
  @CanIgnoreReturnValue
  boolean setCount(E paramE, int paramInt1, int paramInt2);
  
  Set<E> elementSet();
  
  Set<Entry<E>> entrySet();
  
  boolean equals(Object paramObject);
  
  int hashCode();
  
  String toString();
  
  Iterator<E> iterator();
  
  boolean contains(Object paramObject);
  
  boolean containsAll(Collection<?> paramCollection);
  
  @CanIgnoreReturnValue
  boolean removeAll(Collection<?> paramCollection);
  
  @CanIgnoreReturnValue
  boolean retainAll(Collection<?> paramCollection);
  
  public static interface Entry<E> {
    E getElement();
    
    int getCount();
    
    boolean equals(Object param1Object);
    
    int hashCode();
    
    String toString();
  }
}
