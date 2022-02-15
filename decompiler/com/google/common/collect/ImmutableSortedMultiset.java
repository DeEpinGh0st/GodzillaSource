package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;





































@GwtIncompatible
public abstract class ImmutableSortedMultiset<E>
  extends ImmutableSortedMultisetFauxverideShim<E>
  implements SortedMultiset<E>
{
  @LazyInit
  transient ImmutableSortedMultiset<E> descendingMultiset;
  
  public static <E> Collector<E, ?, ImmutableSortedMultiset<E>> toImmutableSortedMultiset(Comparator<? super E> comparator) {
    return toImmutableSortedMultiset(comparator, (Function)Function.identity(), e -> 1);
  }














  
  public static <T, E> Collector<T, ?, ImmutableSortedMultiset<E>> toImmutableSortedMultiset(Comparator<? super E> comparator, Function<? super T, ? extends E> elementFunction, ToIntFunction<? super T> countFunction) {
    Preconditions.checkNotNull(comparator);
    Preconditions.checkNotNull(elementFunction);
    Preconditions.checkNotNull(countFunction);
    return Collector.of(() -> TreeMultiset.create(comparator), (multiset, t) -> multiset.add(Preconditions.checkNotNull(elementFunction.apply(t)), countFunction.applyAsInt(t)), (multiset1, multiset2) -> { multiset1.addAll(multiset2); return multiset1; }multiset -> copyOfSortedEntries(comparator, multiset.entrySet()), new Collector.Characteristics[0]);
  }










  
  public static <E> ImmutableSortedMultiset<E> of() {
    return (ImmutableSortedMultiset)RegularImmutableSortedMultiset.NATURAL_EMPTY_MULTISET;
  }


  
  public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E element) {
    RegularImmutableSortedSet<E> elementSet = (RegularImmutableSortedSet<E>)ImmutableSortedSet.<E>of(element);
    long[] cumulativeCounts = { 0L, 1L };
    return new RegularImmutableSortedMultiset<>(elementSet, cumulativeCounts, 0, 1);
  }







  
  public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E e1, E e2) {
    return copyOf(Ordering.natural(), Arrays.asList((E[])new Comparable[] { (Comparable)e1, (Comparable)e2 }));
  }







  
  public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E e1, E e2, E e3) {
    return copyOf(Ordering.natural(), Arrays.asList((E[])new Comparable[] { (Comparable)e1, (Comparable)e2, (Comparable)e3 }));
  }








  
  public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E e1, E e2, E e3, E e4) {
    return copyOf(Ordering.natural(), Arrays.asList((E[])new Comparable[] { (Comparable)e1, (Comparable)e2, (Comparable)e3, (Comparable)e4 }));
  }








  
  public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E e1, E e2, E e3, E e4, E e5) {
    return copyOf(Ordering.natural(), Arrays.asList((E[])new Comparable[] { (Comparable)e1, (Comparable)e2, (Comparable)e3, (Comparable)e4, (Comparable)e5 }));
  }








  
  public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E... remaining) {
    int size = remaining.length + 6;
    List<E> all = Lists.newArrayListWithCapacity(size);
    Collections.addAll(all, (E[])new Comparable[] { (Comparable)e1, (Comparable)e2, (Comparable)e3, (Comparable)e4, (Comparable)e5, (Comparable)e6 });
    Collections.addAll(all, remaining);
    return copyOf(Ordering.natural(), all);
  }






  
  public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> copyOf(E[] elements) {
    return copyOf(Ordering.natural(), Arrays.asList(elements));
  }
























  
  public static <E> ImmutableSortedMultiset<E> copyOf(Iterable<? extends E> elements) {
    Ordering<E> naturalOrder = Ordering.natural();
    return copyOf(naturalOrder, elements);
  }













  
  public static <E> ImmutableSortedMultiset<E> copyOf(Iterator<? extends E> elements) {
    Ordering<E> naturalOrder = Ordering.natural();
    return copyOf(naturalOrder, elements);
  }







  
  public static <E> ImmutableSortedMultiset<E> copyOf(Comparator<? super E> comparator, Iterator<? extends E> elements) {
    Preconditions.checkNotNull(comparator);
    return (new Builder<>(comparator)).addAll(elements).build();
  }











  
  public static <E> ImmutableSortedMultiset<E> copyOf(Comparator<? super E> comparator, Iterable<? extends E> elements) {
    if (elements instanceof ImmutableSortedMultiset) {
      
      ImmutableSortedMultiset<E> multiset = (ImmutableSortedMultiset)elements;
      if (comparator.equals(multiset.comparator())) {
        if (multiset.isPartialView()) {
          return copyOfSortedEntries(comparator, multiset.entrySet().asList());
        }
        return multiset;
      } 
    } 
    
    elements = Lists.newArrayList(elements);
    TreeMultiset<E> sortedCopy = TreeMultiset.create((Comparator<? super E>)Preconditions.checkNotNull(comparator));
    Iterables.addAll(sortedCopy, elements);
    return copyOfSortedEntries(comparator, sortedCopy.entrySet());
  }














  
  public static <E> ImmutableSortedMultiset<E> copyOfSorted(SortedMultiset<E> sortedMultiset) {
    return copyOfSortedEntries(sortedMultiset
        .comparator(), Lists.newArrayList(sortedMultiset.entrySet()));
  }

  
  private static <E> ImmutableSortedMultiset<E> copyOfSortedEntries(Comparator<? super E> comparator, Collection<Multiset.Entry<E>> entries) {
    if (entries.isEmpty()) {
      return emptyMultiset(comparator);
    }
    ImmutableList.Builder<E> elementsBuilder = new ImmutableList.Builder<>(entries.size());
    long[] cumulativeCounts = new long[entries.size() + 1];
    int i = 0;
    for (Multiset.Entry<E> entry : entries) {
      elementsBuilder.add(entry.getElement());
      cumulativeCounts[i + 1] = cumulativeCounts[i] + entry.getCount();
      i++;
    } 
    return new RegularImmutableSortedMultiset<>(new RegularImmutableSortedSet<>(elementsBuilder
          .build(), comparator), cumulativeCounts, 0, entries

        
        .size());
  }

  
  static <E> ImmutableSortedMultiset<E> emptyMultiset(Comparator<? super E> comparator) {
    if (Ordering.<Comparable>natural().equals(comparator)) {
      return (ImmutableSortedMultiset)RegularImmutableSortedMultiset.NATURAL_EMPTY_MULTISET;
    }
    return new RegularImmutableSortedMultiset<>(comparator);
  }




  
  public final Comparator<? super E> comparator() {
    return elementSet().comparator();
  }






  
  public ImmutableSortedMultiset<E> descendingMultiset() {
    ImmutableSortedMultiset<E> result = this.descendingMultiset;
    if (result == null) {
      return this
        
        .descendingMultiset = isEmpty() ? emptyMultiset(Ordering.from(comparator()).reverse()) : new DescendingImmutableSortedMultiset<>(this);
    }
    
    return result;
  }









  
  @Deprecated
  @CanIgnoreReturnValue
  public final Multiset.Entry<E> pollFirstEntry() {
    throw new UnsupportedOperationException();
  }









  
  @Deprecated
  @CanIgnoreReturnValue
  public final Multiset.Entry<E> pollLastEntry() {
    throw new UnsupportedOperationException();
  }





  
  public ImmutableSortedMultiset<E> subMultiset(E lowerBound, BoundType lowerBoundType, E upperBound, BoundType upperBoundType) {
    Preconditions.checkArgument(
        (comparator().compare(lowerBound, upperBound) <= 0), "Expected lowerBound <= upperBound but %s > %s", lowerBound, upperBound);


    
    return tailMultiset(lowerBound, lowerBoundType).headMultiset(upperBound, upperBoundType);
  }











  
  public static <E> Builder<E> orderedBy(Comparator<E> comparator) {
    return new Builder<>(comparator);
  }








  
  public static <E extends Comparable<?>> Builder<E> reverseOrder() {
    return new Builder<>(Ordering.<Comparable>natural().reverse());
  }










  
  public static <E extends Comparable<?>> Builder<E> naturalOrder() {
    return new Builder<>(Ordering.natural());
  }





















  
  public static class Builder<E>
    extends ImmutableMultiset.Builder<E>
  {
    public Builder(Comparator<? super E> comparator) {
      super(TreeMultiset.create((Comparator<? super E>)Preconditions.checkNotNull(comparator)));
    }








    
    @CanIgnoreReturnValue
    public Builder<E> add(E element) {
      super.add(element);
      return this;
    }








    
    @CanIgnoreReturnValue
    public Builder<E> add(E... elements) {
      super.add(elements);
      return this;
    }












    
    @CanIgnoreReturnValue
    public Builder<E> addCopies(E element, int occurrences) {
      super.addCopies(element, occurrences);
      return this;
    }











    
    @CanIgnoreReturnValue
    public Builder<E> setCount(E element, int count) {
      super.setCount(element, count);
      return this;
    }








    
    @CanIgnoreReturnValue
    public Builder<E> addAll(Iterable<? extends E> elements) {
      super.addAll(elements);
      return this;
    }








    
    @CanIgnoreReturnValue
    public Builder<E> addAll(Iterator<? extends E> elements) {
      super.addAll(elements);
      return this;
    }





    
    public ImmutableSortedMultiset<E> build() {
      return ImmutableSortedMultiset.copyOfSorted((SortedMultiset<E>)this.contents);
    }
  }
  
  private static final class SerializedForm<E>
    implements Serializable {
    final Comparator<? super E> comparator;
    final E[] elements;
    final int[] counts;
    
    SerializedForm(SortedMultiset<E> multiset) {
      this.comparator = multiset.comparator();
      int n = multiset.entrySet().size();
      this.elements = (E[])new Object[n];
      this.counts = new int[n];
      int i = 0;
      for (Multiset.Entry<E> entry : multiset.entrySet()) {
        this.elements[i] = entry.getElement();
        this.counts[i] = entry.getCount();
        i++;
      } 
    }
    
    Object readResolve() {
      int n = this.elements.length;
      ImmutableSortedMultiset.Builder<E> builder = new ImmutableSortedMultiset.Builder<>(this.comparator);
      for (int i = 0; i < n; i++) {
        builder.addCopies(this.elements[i], this.counts[i]);
      }
      return builder.build();
    }
  }

  
  Object writeReplace() {
    return new SerializedForm<>(this);
  }
  
  public abstract ImmutableSortedSet<E> elementSet();
  
  public abstract ImmutableSortedMultiset<E> headMultiset(E paramE, BoundType paramBoundType);
  
  public abstract ImmutableSortedMultiset<E> tailMultiset(E paramE, BoundType paramBoundType);
}
