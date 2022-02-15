package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;




































































































































@GwtCompatible
public abstract class Ordering<T>
  implements Comparator<T>
{
  static final int LEFT_IS_GREATER = 1;
  static final int RIGHT_IS_GREATER = -1;
  
  @GwtCompatible(serializable = true)
  public static <C extends Comparable> Ordering<C> natural() {
    return NaturalOrdering.INSTANCE;
  }















  
  @GwtCompatible(serializable = true)
  public static <T> Ordering<T> from(Comparator<T> comparator) {
    return (comparator instanceof Ordering) ? (Ordering<T>)comparator : new ComparatorOrdering<>(comparator);
  }







  
  @Deprecated
  @GwtCompatible(serializable = true)
  public static <T> Ordering<T> from(Ordering<T> ordering) {
    return (Ordering<T>)Preconditions.checkNotNull(ordering);
  }




















  
  @GwtCompatible(serializable = true)
  public static <T> Ordering<T> explicit(List<T> valuesInOrder) {
    return new ExplicitOrdering<>(valuesInOrder);
  }





















  
  @GwtCompatible(serializable = true)
  public static <T> Ordering<T> explicit(T leastValue, T... remainingValuesInOrder) {
    return explicit(Lists.asList(leastValue, remainingValuesInOrder));
  }































  
  @GwtCompatible(serializable = true)
  public static Ordering<Object> allEqual() {
    return AllEqualOrdering.INSTANCE;
  }








  
  @GwtCompatible(serializable = true)
  public static Ordering<Object> usingToString() {
    return UsingToStringOrdering.INSTANCE;
  }
















  
  public static Ordering<Object> arbitrary() {
    return ArbitraryOrderingHolder.ARBITRARY_ORDERING;
  }
  
  private static class ArbitraryOrderingHolder {
    static final Ordering<Object> ARBITRARY_ORDERING = new Ordering.ArbitraryOrdering();
  }
  
  @VisibleForTesting
  static class ArbitraryOrdering
    extends Ordering<Object> {
    private final AtomicInteger counter = new AtomicInteger(0);
    
    private final ConcurrentMap<Object, Integer> uids = Platform.tryWeakKeys(new MapMaker()).makeMap();
    
    private Integer getUid(Object obj) {
      Integer uid = this.uids.get(obj);
      if (uid == null) {


        
        uid = Integer.valueOf(this.counter.getAndIncrement());
        Integer alreadySet = this.uids.putIfAbsent(obj, uid);
        if (alreadySet != null) {
          uid = alreadySet;
        }
      } 
      return uid;
    }

    
    public int compare(Object left, Object right) {
      if (left == right)
        return 0; 
      if (left == null)
        return -1; 
      if (right == null) {
        return 1;
      }
      int leftCode = identityHashCode(left);
      int rightCode = identityHashCode(right);
      if (leftCode != rightCode) {
        return (leftCode < rightCode) ? -1 : 1;
      }

      
      int result = getUid(left).compareTo(getUid(right));
      if (result == 0) {
        throw new AssertionError();
      }
      return result;
    }

    
    public String toString() {
      return "Ordering.arbitrary()";
    }








    
    int identityHashCode(Object object) {
      return System.identityHashCode(object);
    }
  }


















  
  @GwtCompatible(serializable = true)
  public <S extends T> Ordering<S> reverse() {
    return new ReverseOrdering<>(this);
  }








  
  @GwtCompatible(serializable = true)
  public <S extends T> Ordering<S> nullsFirst() {
    return new NullsFirstOrdering<>(this);
  }








  
  @GwtCompatible(serializable = true)
  public <S extends T> Ordering<S> nullsLast() {
    return new NullsLastOrdering<>(this);
  }













  
  @GwtCompatible(serializable = true)
  public <F> Ordering<F> onResultOf(Function<F, ? extends T> function) {
    return new ByFunctionOrdering<>(function, this);
  }
  
  <T2 extends T> Ordering<Map.Entry<T2, ?>> onKeys() {
    return onResultOf(Maps.keyFunction());
  }













  
  @GwtCompatible(serializable = true)
  public <U extends T> Ordering<U> compound(Comparator<? super U> secondaryComparator) {
    return new CompoundOrdering<>(this, (Comparator<? super U>)Preconditions.checkNotNull(secondaryComparator));
  }



















  
  @GwtCompatible(serializable = true)
  public static <T> Ordering<T> compound(Iterable<? extends Comparator<? super T>> comparators) {
    return new CompoundOrdering<>(comparators);
  }

























  
  @GwtCompatible(serializable = true)
  public <S extends T> Ordering<Iterable<S>> lexicographical() {
    return new LexicographicalOrdering<>(this);
  }










  
  @CanIgnoreReturnValue
  public abstract int compare(T paramT1, T paramT2);










  
  public <E extends T> E min(Iterator<E> iterator) {
    E minSoFar = iterator.next();
    
    while (iterator.hasNext()) {
      minSoFar = min(minSoFar, iterator.next());
    }
    
    return minSoFar;
  }















  
  public <E extends T> E min(Iterable<E> iterable) {
    return min(iterable.iterator());
  }















  
  public <E extends T> E min(E a, E b) {
    return (compare((T)a, (T)b) <= 0) ? a : b;
  }














  
  public <E extends T> E min(E a, E b, E c, E... rest) {
    E minSoFar = min(min(a, b), c);
    
    for (E r : rest) {
      minSoFar = min(minSoFar, r);
    }
    
    return minSoFar;
  }
















  
  public <E extends T> E max(Iterator<E> iterator) {
    E maxSoFar = iterator.next();
    
    while (iterator.hasNext()) {
      maxSoFar = max(maxSoFar, iterator.next());
    }
    
    return maxSoFar;
  }















  
  public <E extends T> E max(Iterable<E> iterable) {
    return max(iterable.iterator());
  }















  
  public <E extends T> E max(E a, E b) {
    return (compare((T)a, (T)b) >= 0) ? a : b;
  }














  
  public <E extends T> E max(E a, E b, E c, E... rest) {
    E maxSoFar = max(max(a, b), c);
    
    for (E r : rest) {
      maxSoFar = max(maxSoFar, r);
    }
    
    return maxSoFar;
  }
















  
  public <E extends T> List<E> leastOf(Iterable<E> iterable, int k) {
    if (iterable instanceof Collection) {
      Collection<E> collection = (Collection<E>)iterable;
      if (collection.size() <= 2L * k) {




        
        E[] array = (E[])collection.toArray();
        Arrays.sort(array, this);
        if (array.length > k) {
          array = Arrays.copyOf(array, k);
        }
        return Collections.unmodifiableList(Arrays.asList(array));
      } 
    } 
    return leastOf(iterable.iterator(), k);
  }
















  
  public <E extends T> List<E> leastOf(Iterator<E> iterator, int k) {
    Preconditions.checkNotNull(iterator);
    CollectPreconditions.checkNonnegative(k, "k");
    
    if (k == 0 || !iterator.hasNext())
      return Collections.emptyList(); 
    if (k >= 1073741823) {
      
      ArrayList<E> list = Lists.newArrayList(iterator);
      Collections.sort(list, this);
      if (list.size() > k) {
        list.subList(k, list.size()).clear();
      }
      list.trimToSize();
      return Collections.unmodifiableList(list);
    } 
    TopKSelector<E> selector = TopKSelector.least(k, this);
    selector.offerAll(iterator);
    return selector.topK();
  }



















  
  public <E extends T> List<E> greatestOf(Iterable<E> iterable, int k) {
    return reverse().leastOf(iterable, k);
  }
















  
  public <E extends T> List<E> greatestOf(Iterator<E> iterator, int k) {
    return reverse().leastOf(iterator, k);
  }

















  
  public <E extends T> List<E> sortedCopy(Iterable<E> elements) {
    E[] array = (E[])Iterables.toArray(elements);
    Arrays.sort(array, this);
    return Lists.newArrayList(Arrays.asList(array));
  }
















  
  public <E extends T> ImmutableList<E> immutableSortedCopy(Iterable<E> elements) {
    return ImmutableList.sortedCopyOf(this, elements);
  }









  
  public boolean isOrdered(Iterable<? extends T> iterable) {
    Iterator<? extends T> it = iterable.iterator();
    if (it.hasNext()) {
      T prev = it.next();
      while (it.hasNext()) {
        T next = it.next();
        if (compare(prev, next) > 0) {
          return false;
        }
        prev = next;
      } 
    } 
    return true;
  }









  
  public boolean isStrictlyOrdered(Iterable<? extends T> iterable) {
    Iterator<? extends T> it = iterable.iterator();
    if (it.hasNext()) {
      T prev = it.next();
      while (it.hasNext()) {
        T next = it.next();
        if (compare(prev, next) >= 0) {
          return false;
        }
        prev = next;
      } 
    } 
    return true;
  }








  
  @Deprecated
  public int binarySearch(List<? extends T> sortedList, T key) {
    return Collections.binarySearch(sortedList, key, this);
  }

  
  @VisibleForTesting
  static class IncomparableValueException
    extends ClassCastException
  {
    final Object value;
    
    private static final long serialVersionUID = 0L;
    
    IncomparableValueException(Object value) {
      super("Cannot compare value: " + value);
      this.value = value;
    }
  }
}
