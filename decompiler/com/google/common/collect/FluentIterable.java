package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.stream.Stream;






























































































@GwtCompatible(emulated = true)
public abstract class FluentIterable<E>
  implements Iterable<E>
{
  private final Optional<Iterable<E>> iterableDelegate;
  
  protected FluentIterable() {
    this.iterableDelegate = Optional.absent();
  }
  
  FluentIterable(Iterable<E> iterable) {
    Preconditions.checkNotNull(iterable);
    this.iterableDelegate = Optional.fromNullable((this != iterable) ? iterable : null);
  }
  
  private Iterable<E> getDelegate() {
    return (Iterable<E>)this.iterableDelegate.or(this);
  }







  
  public static <E> FluentIterable<E> from(final Iterable<E> iterable) {
    return (iterable instanceof FluentIterable) ? (FluentIterable<E>)iterable : new FluentIterable<E>(iterable)
      {
        
        public Iterator<E> iterator()
        {
          return iterable.iterator();
        }
      };
  }










  
  @Beta
  public static <E> FluentIterable<E> from(E[] elements) {
    return from(Arrays.asList(elements));
  }








  
  @Deprecated
  public static <E> FluentIterable<E> from(FluentIterable<E> iterable) {
    return (FluentIterable<E>)Preconditions.checkNotNull(iterable);
  }












  
  @Beta
  public static <T> FluentIterable<T> concat(Iterable<? extends T> a, Iterable<? extends T> b) {
    return concatNoDefensiveCopy((Iterable<? extends T>[])new Iterable[] { a, b });
  }














  
  @Beta
  public static <T> FluentIterable<T> concat(Iterable<? extends T> a, Iterable<? extends T> b, Iterable<? extends T> c) {
    return concatNoDefensiveCopy((Iterable<? extends T>[])new Iterable[] { a, b, c });
  }


















  
  @Beta
  public static <T> FluentIterable<T> concat(Iterable<? extends T> a, Iterable<? extends T> b, Iterable<? extends T> c, Iterable<? extends T> d) {
    return concatNoDefensiveCopy((Iterable<? extends T>[])new Iterable[] { a, b, c, d });
  }















  
  @Beta
  public static <T> FluentIterable<T> concat(Iterable<? extends T>... inputs) {
    return concatNoDefensiveCopy(Arrays.<Iterable<? extends T>>copyOf(inputs, inputs.length));
  }















  
  @Beta
  public static <T> FluentIterable<T> concat(final Iterable<? extends Iterable<? extends T>> inputs) {
    Preconditions.checkNotNull(inputs);
    return new FluentIterable<T>()
      {
        public Iterator<T> iterator() {
          return Iterators.concat(Iterators.transform(inputs.iterator(), (Function)Iterables.toIterator()));
        }
      };
  }


  
  private static <T> FluentIterable<T> concatNoDefensiveCopy(Iterable<? extends T>... inputs) {
    for (Iterable<? extends T> input : inputs) {
      Preconditions.checkNotNull(input);
    }
    return new FluentIterable<T>()
      {
        public Iterator<T> iterator() {
          return Iterators.concat(new AbstractIndexedListIterator(inputs.length)
              {
                
                public Iterator<? extends T> get(int i)
                {
                  return inputs[i].iterator();
                }
              });
        }
      };
  }







  
  @Beta
  public static <E> FluentIterable<E> of() {
    return from(ImmutableList.of());
  }








  
  @Beta
  public static <E> FluentIterable<E> of(E element, E... elements) {
    return from(Lists.asList(element, elements));
  }








  
  public String toString() {
    return Iterables.toString(getDelegate());
  }





  
  public final int size() {
    return Iterables.size(getDelegate());
  }






  
  public final boolean contains(Object target) {
    return Iterables.contains(getDelegate(), target);
  }

















  
  public final FluentIterable<E> cycle() {
    return from(Iterables.cycle(getDelegate()));
  }











  
  @Beta
  public final FluentIterable<E> append(Iterable<? extends E> other) {
    return concat(getDelegate(), other);
  }








  
  @Beta
  public final FluentIterable<E> append(E... elements) {
    return concat(getDelegate(), Arrays.asList(elements));
  }






  
  public final FluentIterable<E> filter(Predicate<? super E> predicate) {
    return from(Iterables.filter(getDelegate(), predicate));
  }













  
  @GwtIncompatible
  public final <T> FluentIterable<T> filter(Class<T> type) {
    return from(Iterables.filter(getDelegate(), type));
  }





  
  public final boolean anyMatch(Predicate<? super E> predicate) {
    return Iterables.any(getDelegate(), predicate);
  }






  
  public final boolean allMatch(Predicate<? super E> predicate) {
    return Iterables.all(getDelegate(), predicate);
  }









  
  public final Optional<E> firstMatch(Predicate<? super E> predicate) {
    return Iterables.tryFind(getDelegate(), predicate);
  }










  
  public final <T> FluentIterable<T> transform(Function<? super E, T> function) {
    return from(Iterables.transform(getDelegate(), function));
  }














  
  public <T> FluentIterable<T> transformAndConcat(Function<? super E, ? extends Iterable<? extends T>> function) {
    return concat(transform(function));
  }










  
  public final Optional<E> first() {
    Iterator<E> iterator = getDelegate().iterator();
    return iterator.hasNext() ? Optional.of(iterator.next()) : Optional.absent();
  }














  
  public final Optional<E> last() {
    Iterable<E> iterable = getDelegate();
    if (iterable instanceof List) {
      List<E> list = (List<E>)iterable;
      if (list.isEmpty()) {
        return Optional.absent();
      }
      return Optional.of(list.get(list.size() - 1));
    } 
    Iterator<E> iterator = iterable.iterator();
    if (!iterator.hasNext()) {
      return Optional.absent();
    }




    
    if (iterable instanceof SortedSet) {
      SortedSet<E> sortedSet = (SortedSet<E>)iterable;
      return Optional.of(sortedSet.last());
    } 
    
    while (true) {
      E current = iterator.next();
      if (!iterator.hasNext()) {
        return Optional.of(current);
      }
    } 
  }

















  
  public final FluentIterable<E> skip(int numberToSkip) {
    return from(Iterables.skip(getDelegate(), numberToSkip));
  }











  
  public final FluentIterable<E> limit(int maxSize) {
    return from(Iterables.limit(getDelegate(), maxSize));
  }





  
  public final boolean isEmpty() {
    return !getDelegate().iterator().hasNext();
  }










  
  public final ImmutableList<E> toList() {
    return ImmutableList.copyOf(getDelegate());
  }












  
  public final ImmutableList<E> toSortedList(Comparator<? super E> comparator) {
    return Ordering.<E>from(comparator).immutableSortedCopy(getDelegate());
  }










  
  public final ImmutableSet<E> toSet() {
    return ImmutableSet.copyOf(getDelegate());
  }













  
  public final ImmutableSortedSet<E> toSortedSet(Comparator<? super E> comparator) {
    return ImmutableSortedSet.copyOf(comparator, getDelegate());
  }









  
  public final ImmutableMultiset<E> toMultiset() {
    return ImmutableMultiset.copyOf(getDelegate());
  }
















  
  public final <V> ImmutableMap<E, V> toMap(Function<? super E, V> valueFunction) {
    return Maps.toMap(getDelegate(), valueFunction);
  }


















  
  public final <K> ImmutableListMultimap<K, E> index(Function<? super E, K> keyFunction) {
    return Multimaps.index(getDelegate(), keyFunction);
  }






























  
  public final <K> ImmutableMap<K, E> uniqueIndex(Function<? super E, K> keyFunction) {
    return Maps.uniqueIndex(getDelegate(), keyFunction);
  }












  
  @GwtIncompatible
  public final E[] toArray(Class<E> type) {
    return Iterables.toArray(getDelegate(), type);
  }











  
  @CanIgnoreReturnValue
  public final <C extends java.util.Collection<? super E>> C copyInto(C collection) {
    Preconditions.checkNotNull(collection);
    Iterable<E> iterable = getDelegate();
    if (iterable instanceof java.util.Collection) {
      collection.addAll(Collections2.cast(iterable));
    } else {
      for (E item : iterable) {
        collection.add(item);
      }
    } 
    return collection;
  }










  
  @Beta
  public final String join(Joiner joiner) {
    return joiner.join(this);
  }













  
  public final E get(int position) {
    return Iterables.get(getDelegate(), position);
  }










  
  public final Stream<E> stream() {
    return Streams.stream(getDelegate());
  }
  
  private static class FromIterableFunction<E>
    implements Function<Iterable<E>, FluentIterable<E>>
  {
    public FluentIterable<E> apply(Iterable<E> fromObject) {
      return FluentIterable.from(fromObject);
    }
  }
}
