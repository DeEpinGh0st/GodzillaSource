package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;















@GwtCompatible(serializable = true)
final class NullsLastOrdering<T>
  extends Ordering<T>
  implements Serializable
{
  final Ordering<? super T> ordering;
  private static final long serialVersionUID = 0L;
  
  NullsLastOrdering(Ordering<? super T> ordering) {
    this.ordering = ordering;
  }

  
  public int compare(T left, T right) {
    if (left == right) {
      return 0;
    }
    if (left == null) {
      return 1;
    }
    if (right == null) {
      return -1;
    }
    return this.ordering.compare(left, right);
  }


  
  public <S extends T> Ordering<S> reverse() {
    return this.ordering.<T>reverse().nullsFirst();
  }

  
  public <S extends T> Ordering<S> nullsFirst() {
    return this.ordering.nullsFirst();
  }


  
  public <S extends T> Ordering<S> nullsLast() {
    return this;
  }

  
  public boolean equals(Object object) {
    if (object == this) {
      return true;
    }
    if (object instanceof NullsLastOrdering) {
      NullsLastOrdering<?> that = (NullsLastOrdering)object;
      return this.ordering.equals(that.ordering);
    } 
    return false;
  }

  
  public int hashCode() {
    return this.ordering.hashCode() ^ 0xC9177248;
  }

  
  public String toString() {
    return this.ordering + ".nullsLast()";
  }
}
