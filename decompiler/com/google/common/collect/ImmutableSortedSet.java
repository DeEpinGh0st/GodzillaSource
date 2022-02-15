package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Collector;













































@GwtCompatible(serializable = true, emulated = true)
public abstract class ImmutableSortedSet<E>
  extends ImmutableSortedSetFauxverideShim<E>
  implements NavigableSet<E>, SortedIterable<E>
{
  static final int SPLITERATOR_CHARACTERISTICS = 1301;
  final transient Comparator<? super E> comparator;
  @LazyInit
  @GwtIncompatible
  transient ImmutableSortedSet<E> descendingSet;
  
  public static <E> Collector<E, ?, ImmutableSortedSet<E>> toImmutableSortedSet(Comparator<? super E> comparator) {
    return CollectCollectors.toImmutableSortedSet(comparator);
  }
  
  static <E> RegularImmutableSortedSet<E> emptySet(Comparator<? super E> comparator) {
    if (Ordering.<Comparable>natural().equals(comparator)) {
      return (RegularImmutableSortedSet)RegularImmutableSortedSet.NATURAL_EMPTY_SET;
    }
    return new RegularImmutableSortedSet<>(ImmutableList.of(), comparator);
  }


  
  public static <E> ImmutableSortedSet<E> of() {
    return (ImmutableSortedSet)RegularImmutableSortedSet.NATURAL_EMPTY_SET;
  }

  
  public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E element) {
    return new RegularImmutableSortedSet<>(ImmutableList.of(element), Ordering.natural());
  }








  
  public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E e1, E e2) {
    return construct(Ordering.natural(), 2, (E[])new Comparable[] { (Comparable)e1, (Comparable)e2 });
  }








  
  public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E e1, E e2, E e3) {
    return construct(Ordering.natural(), 3, (E[])new Comparable[] { (Comparable)e1, (Comparable)e2, (Comparable)e3 });
  }








  
  public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E e1, E e2, E e3, E e4) {
    return construct(Ordering.natural(), 4, (E[])new Comparable[] { (Comparable)e1, (Comparable)e2, (Comparable)e3, (Comparable)e4 });
  }









  
  public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E e1, E e2, E e3, E e4, E e5) {
    return construct(Ordering.natural(), 5, (E[])new Comparable[] { (Comparable)e1, (Comparable)e2, (Comparable)e3, (Comparable)e4, (Comparable)e5 });
  }










  
  public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E... remaining) {
    Comparable[] contents = new Comparable[6 + remaining.length];
    contents[0] = (Comparable)e1;
    contents[1] = (Comparable)e2;
    contents[2] = (Comparable)e3;
    contents[3] = (Comparable)e4;
    contents[4] = (Comparable)e5;
    contents[5] = (Comparable)e6;
    System.arraycopy(remaining, 0, contents, 6, remaining.length);
    return construct(Ordering.natural(), contents.length, (E[])contents);
  }










  
  public static <E extends Comparable<? super E>> ImmutableSortedSet<E> copyOf(E[] elements) {
    return construct(Ordering.natural(), elements.length, (E[])elements.clone());
  }
























  
  public static <E> ImmutableSortedSet<E> copyOf(Iterable<? extends E> elements) {
    Ordering<E> naturalOrder = Ordering.natural();
    return copyOf(naturalOrder, elements);
  }



























  
  public static <E> ImmutableSortedSet<E> copyOf(Collection<? extends E> elements) {
    Ordering<E> naturalOrder = Ordering.natural();
    return copyOf(naturalOrder, elements);
  }














  
  public static <E> ImmutableSortedSet<E> copyOf(Iterator<? extends E> elements) {
    Ordering<E> naturalOrder = Ordering.natural();
    return copyOf(naturalOrder, elements);
  }








  
  public static <E> ImmutableSortedSet<E> copyOf(Comparator<? super E> comparator, Iterator<? extends E> elements) {
    return (new Builder<>(comparator)).addAll(elements).build();
  }












  
  public static <E> ImmutableSortedSet<E> copyOf(Comparator<? super E> comparator, Iterable<? extends E> elements) {
    Preconditions.checkNotNull(comparator);
    boolean hasSameComparator = SortedIterables.hasSameComparator(comparator, elements);
    
    if (hasSameComparator && elements instanceof ImmutableSortedSet) {
      
      ImmutableSortedSet<E> original = (ImmutableSortedSet)elements;
      if (!original.isPartialView()) {
        return original;
      }
    } 
    
    E[] array = (E[])Iterables.toArray(elements);
    return construct(comparator, array.length, array);
  }
















  
  public static <E> ImmutableSortedSet<E> copyOf(Comparator<? super E> comparator, Collection<? extends E> elements) {
    return copyOf(comparator, elements);
  }














  
  public static <E> ImmutableSortedSet<E> copyOfSorted(SortedSet<E> sortedSet) {
    Comparator<? super E> comparator = SortedIterables.comparator(sortedSet);
    ImmutableList<E> list = ImmutableList.copyOf(sortedSet);
    if (list.isEmpty()) {
      return emptySet(comparator);
    }
    return new RegularImmutableSortedSet<>(list, comparator);
  }













  
  static <E> ImmutableSortedSet<E> construct(Comparator<? super E> comparator, int n, E... contents) {
    if (n == 0) {
      return emptySet(comparator);
    }
    ObjectArrays.checkElementsNotNull((Object[])contents, n);
    Arrays.sort(contents, 0, n, comparator);
    int uniques = 1;
    for (int i = 1; i < n; i++) {
      E cur = contents[i];
      E prev = contents[uniques - 1];
      if (comparator.compare(cur, prev) != 0) {
        contents[uniques++] = cur;
      }
    } 
    Arrays.fill((Object[])contents, uniques, n, (Object)null);
    return new RegularImmutableSortedSet<>(
        ImmutableList.asImmutableList((Object[])contents, uniques), comparator);
  }








  
  public static <E> Builder<E> orderedBy(Comparator<E> comparator) {
    return new Builder<>(comparator);
  }




  
  public static <E extends Comparable<?>> Builder<E> reverseOrder() {
    return new Builder<>(Collections.reverseOrder());
  }






  
  public static <E extends Comparable<?>> Builder<E> naturalOrder() {
    return new Builder<>(Ordering.natural());
  }





  
  public static final class Builder<E>
    extends ImmutableSet.Builder<E>
  {
    private final Comparator<? super E> comparator;




    
    private E[] elements;




    
    private int n;




    
    public Builder(Comparator<? super E> comparator) {
      super(true);
      this.comparator = (Comparator<? super E>)Preconditions.checkNotNull(comparator);
      this.elements = (E[])new Object[4];
      this.n = 0;
    }

    
    void copy() {
      this.elements = Arrays.copyOf(this.elements, this.elements.length);
    }
    
    private void sortAndDedup() {
      if (this.n == 0) {
        return;
      }
      Arrays.sort(this.elements, 0, this.n, this.comparator);
      int unique = 1;
      for (int i = 1; i < this.n; i++) {
        int cmp = this.comparator.compare(this.elements[unique - 1], this.elements[i]);
        if (cmp < 0) {
          this.elements[unique++] = this.elements[i];
        } else if (cmp > 0) {
          throw new AssertionError("Comparator " + this.comparator + " compare method violates its contract");
        } 
      } 
      
      Arrays.fill((Object[])this.elements, unique, this.n, (Object)null);
      this.n = unique;
    }










    
    @CanIgnoreReturnValue
    public Builder<E> add(E element) {
      Preconditions.checkNotNull(element);
      copyIfNecessary();
      if (this.n == this.elements.length) {
        sortAndDedup();




        
        int newLength = ImmutableCollection.Builder.expandedCapacity(this.n, this.n + 1);
        if (newLength > this.elements.length) {
          this.elements = Arrays.copyOf(this.elements, newLength);
        }
      } 
      this.elements[this.n++] = element;
      return this;
    }









    
    @CanIgnoreReturnValue
    public Builder<E> add(E... elements) {
      ObjectArrays.checkElementsNotNull((Object[])elements);
      for (E e : elements) {
        add(e);
      }
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

    
    @CanIgnoreReturnValue
    Builder<E> combine(ImmutableSet.Builder<E> builder) {
      copyIfNecessary();
      Builder<E> other = (Builder<E>)builder;
      for (int i = 0; i < other.n; i++) {
        add(other.elements[i]);
      }
      return this;
    }





    
    public ImmutableSortedSet<E> build() {
      sortAndDedup();
      if (this.n == 0) {
        return ImmutableSortedSet.emptySet(this.comparator);
      }
      this.forceCopy = true;
      return new RegularImmutableSortedSet<>(
          ImmutableList.asImmutableList((Object[])this.elements, this.n), this.comparator);
    }
  }

  
  int unsafeCompare(Object a, Object b) {
    return unsafeCompare(this.comparator, a, b);
  }




  
  static int unsafeCompare(Comparator<?> comparator, Object a, Object b) {
    Comparator<Object> unsafeComparator = (Comparator)comparator;
    return unsafeComparator.compare(a, b);
  }


  
  ImmutableSortedSet(Comparator<? super E> comparator) {
    this.comparator = comparator;
  }






  
  public Comparator<? super E> comparator() {
    return this.comparator;
  }














  
  public ImmutableSortedSet<E> headSet(E toElement) {
    return headSet(toElement, false);
  }


  
  @GwtIncompatible
  public ImmutableSortedSet<E> headSet(E toElement, boolean inclusive) {
    return headSetImpl((E)Preconditions.checkNotNull(toElement), inclusive);
  }













  
  public ImmutableSortedSet<E> subSet(E fromElement, E toElement) {
    return subSet(fromElement, true, toElement, false);
  }



  
  @GwtIncompatible
  public ImmutableSortedSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
    Preconditions.checkNotNull(fromElement);
    Preconditions.checkNotNull(toElement);
    Preconditions.checkArgument((this.comparator.compare(fromElement, toElement) <= 0));
    return subSetImpl(fromElement, fromInclusive, toElement, toInclusive);
  }











  
  public ImmutableSortedSet<E> tailSet(E fromElement) {
    return tailSet(fromElement, true);
  }


  
  @GwtIncompatible
  public ImmutableSortedSet<E> tailSet(E fromElement, boolean inclusive) {
    return tailSetImpl((E)Preconditions.checkNotNull(fromElement), inclusive);
  }













  
  @GwtIncompatible
  public E lower(E e) {
    return Iterators.getNext(headSet(e, false).descendingIterator(), null);
  }


  
  @GwtIncompatible
  public E floor(E e) {
    return Iterators.getNext(headSet(e, true).descendingIterator(), null);
  }


  
  @GwtIncompatible
  public E ceiling(E e) {
    return Iterables.getFirst(tailSet(e, true), null);
  }


  
  @GwtIncompatible
  public E higher(E e) {
    return Iterables.getFirst(tailSet(e, false), null);
  }

  
  public E first() {
    return iterator().next();
  }

  
  public E last() {
    return descendingIterator().next();
  }








  
  @Deprecated
  @CanIgnoreReturnValue
  @GwtIncompatible
  public final E pollFirst() {
    throw new UnsupportedOperationException();
  }








  
  @Deprecated
  @CanIgnoreReturnValue
  @GwtIncompatible
  public final E pollLast() {
    throw new UnsupportedOperationException();
  }







  
  @GwtIncompatible
  public ImmutableSortedSet<E> descendingSet() {
    ImmutableSortedSet<E> result = this.descendingSet;
    if (result == null) {
      result = this.descendingSet = createDescendingSet();
      result.descendingSet = this;
    } 
    return result;
  }







  
  public Spliterator<E> spliterator() {
    return new Spliterators.AbstractSpliterator<E>(
        size(), 1365) {
        final UnmodifiableIterator<E> iterator = ImmutableSortedSet.this.iterator();

        
        public boolean tryAdvance(Consumer<? super E> action) {
          if (this.iterator.hasNext()) {
            action.accept(this.iterator.next());
            return true;
          } 
          return false;
        }


        
        public Comparator<? super E> getComparator() {
          return ImmutableSortedSet.this.comparator;
        }
      };
  }



  
  private static class SerializedForm<E>
    implements Serializable
  {
    final Comparator<? super E> comparator;


    
    final Object[] elements;


    
    private static final long serialVersionUID = 0L;


    
    public SerializedForm(Comparator<? super E> comparator, Object[] elements) {
      this.comparator = comparator;
      this.elements = elements;
    }

    
    Object readResolve() {
      return (new ImmutableSortedSet.Builder((Comparator)this.comparator)).add(this.elements).build();
    }
  }


  
  private void readObject(ObjectInputStream unused) throws InvalidObjectException {
    throw new InvalidObjectException("Use SerializedForm");
  }

  
  Object writeReplace() {
    return new SerializedForm<>(this.comparator, toArray());
  }
  
  public abstract UnmodifiableIterator<E> iterator();
  
  abstract ImmutableSortedSet<E> headSetImpl(E paramE, boolean paramBoolean);
  
  abstract ImmutableSortedSet<E> subSetImpl(E paramE1, boolean paramBoolean1, E paramE2, boolean paramBoolean2);
  
  abstract ImmutableSortedSet<E> tailSetImpl(E paramE, boolean paramBoolean);
  
  @GwtIncompatible
  abstract ImmutableSortedSet<E> createDescendingSet();
  
  @GwtIncompatible
  public abstract UnmodifiableIterator<E> descendingIterator();
  
  abstract int indexOf(Object paramObject);
}
