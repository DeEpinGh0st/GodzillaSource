package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import java.util.Collection;
import java.util.Map;
import java.util.Set;




















@GwtCompatible
final class JdkBackedImmutableMultiset<E>
  extends ImmutableMultiset<E>
{
  private final Map<E, Integer> delegateMap;
  private final ImmutableList<Multiset.Entry<E>> entries;
  private final long size;
  private transient ImmutableSet<E> elementSet;
  
  static <E> ImmutableMultiset<E> create(Collection<? extends Multiset.Entry<? extends E>> entries) {
    Multiset.Entry[] arrayOfEntry = entries.<Multiset.Entry>toArray(new Multiset.Entry[0]);
    Map<E, Integer> delegateMap = Maps.newHashMapWithExpectedSize(arrayOfEntry.length);
    long size = 0L;
    for (int i = 0; i < arrayOfEntry.length; i++) {
      Multiset.Entry<E> entry = arrayOfEntry[i];
      int count = entry.getCount();
      size += count;
      E element = (E)Preconditions.checkNotNull(entry.getElement());
      delegateMap.put(element, Integer.valueOf(count));
      if (!(entry instanceof Multisets.ImmutableEntry)) {
        arrayOfEntry[i] = Multisets.immutableEntry(element, count);
      }
    } 
    return new JdkBackedImmutableMultiset<>(delegateMap, 
        ImmutableList.asImmutableList((Object[])arrayOfEntry), size);
  }

  
  private JdkBackedImmutableMultiset(Map<E, Integer> delegateMap, ImmutableList<Multiset.Entry<E>> entries, long size) {
    this.delegateMap = delegateMap;
    this.entries = entries;
    this.size = size;
  }

  
  public int count(Object element) {
    return ((Integer)this.delegateMap.getOrDefault(element, Integer.valueOf(0))).intValue();
  }



  
  public ImmutableSet<E> elementSet() {
    ImmutableSet<E> result = this.elementSet;
    return (result == null) ? (this.elementSet = new ImmutableMultiset.ElementSet<>(this.entries, this)) : result;
  }

  
  Multiset.Entry<E> getEntry(int index) {
    return this.entries.get(index);
  }

  
  boolean isPartialView() {
    return false;
  }

  
  public int size() {
    return Ints.saturatedCast(this.size);
  }
}
