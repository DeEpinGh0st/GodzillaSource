package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;














@GwtCompatible(serializable = true)
final class CompoundOrdering<T>
  extends Ordering<T>
  implements Serializable
{
  final Comparator<? super T>[] comparators;
  private static final long serialVersionUID = 0L;
  
  CompoundOrdering(Comparator<? super T> primary, Comparator<? super T> secondary) {
    this.comparators = (Comparator<? super T>[])new Comparator[] { primary, secondary };
  }
  
  CompoundOrdering(Iterable<? extends Comparator<? super T>> comparators) {
    this.comparators = Iterables.<Comparator<? super T>>toArray(comparators, (Comparator<? super T>[])new Comparator[0]);
  }

  
  public int compare(T left, T right) {
    for (int i = 0; i < this.comparators.length; i++) {
      int result = this.comparators[i].compare(left, right);
      if (result != 0) {
        return result;
      }
    } 
    return 0;
  }

  
  public boolean equals(Object object) {
    if (object == this) {
      return true;
    }
    if (object instanceof CompoundOrdering) {
      CompoundOrdering<?> that = (CompoundOrdering)object;
      return Arrays.equals((Object[])this.comparators, (Object[])that.comparators);
    } 
    return false;
  }

  
  public int hashCode() {
    return Arrays.hashCode((Object[])this.comparators);
  }

  
  public String toString() {
    return "Ordering.compound(" + Arrays.toString((Object[])this.comparators) + ")";
  }
}
