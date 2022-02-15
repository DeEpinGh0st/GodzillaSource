package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;






















@GwtIncompatible
abstract class AbstractRangeSet<C extends Comparable>
  implements RangeSet<C>
{
  public boolean contains(C value) {
    return (rangeContaining(value) != null);
  }

  
  public abstract Range<C> rangeContaining(C paramC);

  
  public boolean isEmpty() {
    return asRanges().isEmpty();
  }

  
  public void add(Range<C> range) {
    throw new UnsupportedOperationException();
  }

  
  public void remove(Range<C> range) {
    throw new UnsupportedOperationException();
  }

  
  public void clear() {
    remove((Range)Range.all());
  }

  
  public boolean enclosesAll(RangeSet<C> other) {
    return enclosesAll(other.asRanges());
  }

  
  public void addAll(RangeSet<C> other) {
    addAll(other.asRanges());
  }

  
  public void removeAll(RangeSet<C> other) {
    removeAll(other.asRanges());
  }

  
  public boolean intersects(Range<C> otherRange) {
    return !subRangeSet(otherRange).isEmpty();
  }

  
  public abstract boolean encloses(Range<C> paramRange);

  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (obj instanceof RangeSet) {
      RangeSet<?> other = (RangeSet)obj;
      return asRanges().equals(other.asRanges());
    } 
    return false;
  }

  
  public final int hashCode() {
    return asRanges().hashCode();
  }

  
  public final String toString() {
    return asRanges().toString();
  }
}
