package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import java.util.Set;



















































































@Beta
@GwtIncompatible
public interface RangeSet<C extends Comparable>
{
  boolean contains(C paramC);
  
  Range<C> rangeContaining(C paramC);
  
  boolean intersects(Range<C> paramRange);
  
  boolean encloses(Range<C> paramRange);
  
  boolean enclosesAll(RangeSet<C> paramRangeSet);
  
  default boolean enclosesAll(Iterable<Range<C>> other) {
    for (Range<C> range : other) {
      if (!encloses(range)) {
        return false;
      }
    } 
    return true;
  }









  
  boolean isEmpty();








  
  Range<C> span();








  
  Set<Range<C>> asRanges();








  
  Set<Range<C>> asDescendingSetOfRanges();








  
  RangeSet<C> complement();








  
  RangeSet<C> subRangeSet(Range<C> paramRange);








  
  void add(Range<C> paramRange);








  
  void remove(Range<C> paramRange);








  
  void clear();








  
  void addAll(RangeSet<C> paramRangeSet);








  
  default void addAll(Iterable<Range<C>> ranges) {
    for (Range<C> range : ranges) {
      add(range);
    }
  }











  
  void removeAll(RangeSet<C> paramRangeSet);










  
  default void removeAll(Iterable<Range<C>> ranges) {
    for (Range<C> range : ranges)
      remove(range); 
  }
  
  boolean equals(Object paramObject);
  
  int hashCode();
  
  String toString();
}
