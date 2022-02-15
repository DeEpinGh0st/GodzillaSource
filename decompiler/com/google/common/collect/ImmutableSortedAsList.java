package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.util.Comparator;
import java.util.Spliterator;





















@GwtCompatible(emulated = true)
final class ImmutableSortedAsList<E>
  extends RegularImmutableAsList<E>
  implements SortedIterable<E>
{
  ImmutableSortedAsList(ImmutableSortedSet<E> backingSet, ImmutableList<E> backingList) {
    super(backingSet, backingList);
  }

  
  ImmutableSortedSet<E> delegateCollection() {
    return (ImmutableSortedSet<E>)super.delegateCollection();
  }

  
  public Comparator<? super E> comparator() {
    return delegateCollection().comparator();
  }




  
  @GwtIncompatible
  public int indexOf(Object target) {
    int index = delegateCollection().indexOf(target);





    
    return (index >= 0 && get(index).equals(target)) ? index : -1;
  }

  
  @GwtIncompatible
  public int lastIndexOf(Object target) {
    return indexOf(target);
  }


  
  public boolean contains(Object target) {
    return (indexOf(target) >= 0);
  }






  
  @GwtIncompatible
  ImmutableList<E> subListUnchecked(int fromIndex, int toIndex) {
    ImmutableList<E> parentSubList = super.subListUnchecked(fromIndex, toIndex);
    return (new RegularImmutableSortedSet<>(parentSubList, comparator())).asList();
  }

  
  public Spliterator<E> spliterator() {
    return CollectSpliterators.indexed(
        size(), 1301, 
        
        delegateList()::get, 
        comparator());
  }
}
