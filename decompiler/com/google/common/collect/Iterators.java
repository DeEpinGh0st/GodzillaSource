package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.primitives.Ints;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;














































@GwtCompatible(emulated = true)
public final class Iterators
{
  static <T> UnmodifiableIterator<T> emptyIterator() {
    return emptyListIterator();
  }







  
  static <T> UnmodifiableListIterator<T> emptyListIterator() {
    return (UnmodifiableListIterator)ArrayItr.EMPTY;
  }


  
  private enum EmptyModifiableIterator
    implements Iterator<Object>
  {
    INSTANCE;

    
    public boolean hasNext() {
      return false;
    }

    
    public Object next() {
      throw new NoSuchElementException();
    }

    
    public void remove() {
      CollectPreconditions.checkRemove(false);
    }
  }






  
  static <T> Iterator<T> emptyModifiableIterator() {
    return EmptyModifiableIterator.INSTANCE;
  }


  
  public static <T> UnmodifiableIterator<T> unmodifiableIterator(final Iterator<? extends T> iterator) {
    Preconditions.checkNotNull(iterator);
    if (iterator instanceof UnmodifiableIterator) {
      
      UnmodifiableIterator<T> result = (UnmodifiableIterator)iterator;
      return result;
    } 
    return new UnmodifiableIterator<T>()
      {
        public boolean hasNext() {
          return iterator.hasNext();
        }

        
        public T next() {
          return iterator.next();
        }
      };
  }






  
  @Deprecated
  public static <T> UnmodifiableIterator<T> unmodifiableIterator(UnmodifiableIterator<T> iterator) {
    return (UnmodifiableIterator<T>)Preconditions.checkNotNull(iterator);
  }




  
  public static int size(Iterator<?> iterator) {
    long count = 0L;
    while (iterator.hasNext()) {
      iterator.next();
      count++;
    } 
    return Ints.saturatedCast(count);
  }

  
  public static boolean contains(Iterator<?> iterator, Object element) {
    if (element == null) {
      while (iterator.hasNext()) {
        if (iterator.next() == null) {
          return true;
        }
      } 
    } else {
      while (iterator.hasNext()) {
        if (element.equals(iterator.next())) {
          return true;
        }
      } 
    } 
    return false;
  }








  
  @CanIgnoreReturnValue
  public static boolean removeAll(Iterator<?> removeFrom, Collection<?> elementsToRemove) {
    Preconditions.checkNotNull(elementsToRemove);
    boolean result = false;
    while (removeFrom.hasNext()) {
      if (elementsToRemove.contains(removeFrom.next())) {
        removeFrom.remove();
        result = true;
      } 
    } 
    return result;
  }









  
  @CanIgnoreReturnValue
  public static <T> boolean removeIf(Iterator<T> removeFrom, Predicate<? super T> predicate) {
    Preconditions.checkNotNull(predicate);
    boolean modified = false;
    while (removeFrom.hasNext()) {
      if (predicate.apply(removeFrom.next())) {
        removeFrom.remove();
        modified = true;
      } 
    } 
    return modified;
  }









  
  @CanIgnoreReturnValue
  public static boolean retainAll(Iterator<?> removeFrom, Collection<?> elementsToRetain) {
    Preconditions.checkNotNull(elementsToRetain);
    boolean result = false;
    while (removeFrom.hasNext()) {
      if (!elementsToRetain.contains(removeFrom.next())) {
        removeFrom.remove();
        result = true;
      } 
    } 
    return result;
  }









  
  public static boolean elementsEqual(Iterator<?> iterator1, Iterator<?> iterator2) {
    while (iterator1.hasNext()) {
      if (!iterator2.hasNext()) {
        return false;
      }
      Object o1 = iterator1.next();
      Object o2 = iterator2.next();
      if (!Objects.equal(o1, o2)) {
        return false;
      }
    } 
    return !iterator2.hasNext();
  }




  
  public static String toString(Iterator<?> iterator) {
    StringBuilder sb = (new StringBuilder()).append('[');
    boolean first = true;
    while (iterator.hasNext()) {
      if (!first) {
        sb.append(", ");
      }
      first = false;
      sb.append(iterator.next());
    } 
    return sb.append(']').toString();
  }







  
  public static <T> T getOnlyElement(Iterator<T> iterator) {
    T first = iterator.next();
    if (!iterator.hasNext()) {
      return first;
    }
    
    StringBuilder sb = (new StringBuilder()).append("expected one element but was: <").append(first);
    for (int i = 0; i < 4 && iterator.hasNext(); i++) {
      sb.append(", ").append(iterator.next());
    }
    if (iterator.hasNext()) {
      sb.append(", ...");
    }
    sb.append('>');
    
    throw new IllegalArgumentException(sb.toString());
  }








  
  public static <T> T getOnlyElement(Iterator<? extends T> iterator, T defaultValue) {
    return iterator.hasNext() ? getOnlyElement((Iterator)iterator) : defaultValue;
  }








  
  @GwtIncompatible
  public static <T> T[] toArray(Iterator<? extends T> iterator, Class<T> type) {
    List<T> list = Lists.newArrayList(iterator);
    return Iterables.toArray(list, type);
  }






  
  @CanIgnoreReturnValue
  public static <T> boolean addAll(Collection<T> addTo, Iterator<? extends T> iterator) {
    Preconditions.checkNotNull(addTo);
    Preconditions.checkNotNull(iterator);
    boolean wasModified = false;
    while (iterator.hasNext()) {
      wasModified |= addTo.add(iterator.next());
    }
    return wasModified;
  }






  
  public static int frequency(Iterator<?> iterator, Object element) {
    int count = 0;
    while (contains(iterator, element))
    {
      
      count++;
    }
    return count;
  }












  
  public static <T> Iterator<T> cycle(final Iterable<T> iterable) {
    Preconditions.checkNotNull(iterable);
    return new Iterator<T>() {
        Iterator<T> iterator = Iterators.emptyModifiableIterator();










        
        public boolean hasNext() {
          return (this.iterator.hasNext() || iterable.iterator().hasNext());
        }

        
        public T next() {
          if (!this.iterator.hasNext()) {
            this.iterator = iterable.iterator();
            if (!this.iterator.hasNext()) {
              throw new NoSuchElementException();
            }
          } 
          return this.iterator.next();
        }

        
        public void remove() {
          this.iterator.remove();
        }
      };
  }












  
  @SafeVarargs
  public static <T> Iterator<T> cycle(T... elements) {
    return cycle(Lists.newArrayList(elements));
  }






  
  private static <T> Iterator<T> consumingForArray(T... elements) {
    return new UnmodifiableIterator<T>() {
        int index = 0;

        
        public boolean hasNext() {
          return (this.index < elements.length);
        }

        
        public T next() {
          if (!hasNext()) {
            throw new NoSuchElementException();
          }
          T result = (T)elements[this.index];
          elements[this.index] = null;
          this.index++;
          return result;
        }
      };
  }








  
  public static <T> Iterator<T> concat(Iterator<? extends T> a, Iterator<? extends T> b) {
    Preconditions.checkNotNull(a);
    Preconditions.checkNotNull(b);
    return concat(consumingForArray((Iterator<? extends T>[])new Iterator[] { a, b }));
  }









  
  public static <T> Iterator<T> concat(Iterator<? extends T> a, Iterator<? extends T> b, Iterator<? extends T> c) {
    Preconditions.checkNotNull(a);
    Preconditions.checkNotNull(b);
    Preconditions.checkNotNull(c);
    return concat(consumingForArray((Iterator<? extends T>[])new Iterator[] { a, b, c }));
  }













  
  public static <T> Iterator<T> concat(Iterator<? extends T> a, Iterator<? extends T> b, Iterator<? extends T> c, Iterator<? extends T> d) {
    Preconditions.checkNotNull(a);
    Preconditions.checkNotNull(b);
    Preconditions.checkNotNull(c);
    Preconditions.checkNotNull(d);
    return concat(consumingForArray((Iterator<? extends T>[])new Iterator[] { a, b, c, d }));
  }










  
  public static <T> Iterator<T> concat(Iterator<? extends T>... inputs) {
    return concatNoDefensiveCopy(Arrays.<Iterator<? extends T>>copyOf(inputs, inputs.length));
  }









  
  public static <T> Iterator<T> concat(Iterator<? extends Iterator<? extends T>> inputs) {
    return new ConcatenatedIterator<>(inputs);
  }

  
  static <T> Iterator<T> concatNoDefensiveCopy(Iterator<? extends T>... inputs) {
    for (Iterator<? extends T> input : (Iterator[])Preconditions.checkNotNull(inputs)) {
      Preconditions.checkNotNull(input);
    }
    return concat(consumingForArray(inputs));
  }














  
  public static <T> UnmodifiableIterator<List<T>> partition(Iterator<T> iterator, int size) {
    return partitionImpl(iterator, size, false);
  }














  
  public static <T> UnmodifiableIterator<List<T>> paddedPartition(Iterator<T> iterator, int size) {
    return partitionImpl(iterator, size, true);
  }

  
  private static <T> UnmodifiableIterator<List<T>> partitionImpl(final Iterator<T> iterator, final int size, final boolean pad) {
    Preconditions.checkNotNull(iterator);
    Preconditions.checkArgument((size > 0));
    return new UnmodifiableIterator<List<T>>()
      {
        public boolean hasNext() {
          return iterator.hasNext();
        }

        
        public List<T> next() {
          if (!hasNext()) {
            throw new NoSuchElementException();
          }
          Object[] array = new Object[size];
          int count = 0;
          for (; count < size && iterator.hasNext(); count++) {
            array[count] = iterator.next();
          }
          for (int i = count; i < size; i++) {
            array[i] = null;
          }

          
          List<T> list = Collections.unmodifiableList(Arrays.asList((T[])array));
          return (pad || count == size) ? list : list.subList(0, count);
        }
      };
  }





  
  public static <T> UnmodifiableIterator<T> filter(final Iterator<T> unfiltered, final Predicate<? super T> retainIfTrue) {
    Preconditions.checkNotNull(unfiltered);
    Preconditions.checkNotNull(retainIfTrue);
    return new AbstractIterator<T>()
      {
        protected T computeNext() {
          while (unfiltered.hasNext()) {
            T element = unfiltered.next();
            if (retainIfTrue.apply(element)) {
              return element;
            }
          } 
          return endOfData();
        }
      };
  }





  
  @GwtIncompatible
  public static <T> UnmodifiableIterator<T> filter(Iterator<?> unfiltered, Class<T> desiredType) {
    return filter((Iterator)unfiltered, Predicates.instanceOf(desiredType));
  }




  
  public static <T> boolean any(Iterator<T> iterator, Predicate<? super T> predicate) {
    return (indexOf(iterator, predicate) != -1);
  }




  
  public static <T> boolean all(Iterator<T> iterator, Predicate<? super T> predicate) {
    Preconditions.checkNotNull(predicate);
    while (iterator.hasNext()) {
      T element = iterator.next();
      if (!predicate.apply(element)) {
        return false;
      }
    } 
    return true;
  }









  
  public static <T> T find(Iterator<T> iterator, Predicate<? super T> predicate) {
    Preconditions.checkNotNull(iterator);
    Preconditions.checkNotNull(predicate);
    while (iterator.hasNext()) {
      T t = iterator.next();
      if (predicate.apply(t)) {
        return t;
      }
    } 
    throw new NoSuchElementException();
  }









  
  public static <T> T find(Iterator<? extends T> iterator, Predicate<? super T> predicate, T defaultValue) {
    Preconditions.checkNotNull(iterator);
    Preconditions.checkNotNull(predicate);
    while (iterator.hasNext()) {
      T t = iterator.next();
      if (predicate.apply(t)) {
        return t;
      }
    } 
    return defaultValue;
  }











  
  public static <T> Optional<T> tryFind(Iterator<T> iterator, Predicate<? super T> predicate) {
    Preconditions.checkNotNull(iterator);
    Preconditions.checkNotNull(predicate);
    while (iterator.hasNext()) {
      T t = iterator.next();
      if (predicate.apply(t)) {
        return Optional.of(t);
      }
    } 
    return Optional.absent();
  }














  
  public static <T> int indexOf(Iterator<T> iterator, Predicate<? super T> predicate) {
    Preconditions.checkNotNull(predicate, "predicate");
    for (int i = 0; iterator.hasNext(); i++) {
      T current = iterator.next();
      if (predicate.apply(current)) {
        return i;
      }
    } 
    return -1;
  }









  
  public static <F, T> Iterator<T> transform(Iterator<F> fromIterator, final Function<? super F, ? extends T> function) {
    Preconditions.checkNotNull(function);
    return new TransformedIterator<F, T>(fromIterator)
      {
        T transform(F from) {
          return (T)function.apply(from);
        }
      };
  }









  
  public static <T> T get(Iterator<T> iterator, int position) {
    checkNonnegative(position);
    int skipped = advance(iterator, position);
    if (!iterator.hasNext()) {
      throw new IndexOutOfBoundsException("position (" + position + ") must be less than the number of elements that remained (" + skipped + ")");
    }




    
    return iterator.next();
  }













  
  public static <T> T get(Iterator<? extends T> iterator, int position, T defaultValue) {
    checkNonnegative(position);
    advance(iterator, position);
    return getNext(iterator, defaultValue);
  }
  
  static void checkNonnegative(int position) {
    if (position < 0) {
      throw new IndexOutOfBoundsException("position (" + position + ") must not be negative");
    }
  }








  
  public static <T> T getNext(Iterator<? extends T> iterator, T defaultValue) {
    return iterator.hasNext() ? iterator.next() : defaultValue;
  }






  
  public static <T> T getLast(Iterator<T> iterator) {
    while (true) {
      T current = iterator.next();
      if (!iterator.hasNext()) {
        return current;
      }
    } 
  }








  
  public static <T> T getLast(Iterator<? extends T> iterator, T defaultValue) {
    return iterator.hasNext() ? getLast((Iterator)iterator) : defaultValue;
  }







  
  @CanIgnoreReturnValue
  public static int advance(Iterator<?> iterator, int numberToAdvance) {
    Preconditions.checkNotNull(iterator);
    Preconditions.checkArgument((numberToAdvance >= 0), "numberToAdvance must be nonnegative");
    
    int i;
    for (i = 0; i < numberToAdvance && iterator.hasNext(); i++) {
      iterator.next();
    }
    return i;
  }










  
  public static <T> Iterator<T> limit(final Iterator<T> iterator, final int limitSize) {
    Preconditions.checkNotNull(iterator);
    Preconditions.checkArgument((limitSize >= 0), "limit is negative");
    return new Iterator<T>()
      {
        private int count;
        
        public boolean hasNext() {
          return (this.count < limitSize && iterator.hasNext());
        }

        
        public T next() {
          if (!hasNext()) {
            throw new NoSuchElementException();
          }
          this.count++;
          return iterator.next();
        }

        
        public void remove() {
          iterator.remove();
        }
      };
  }











  
  public static <T> Iterator<T> consumingIterator(final Iterator<T> iterator) {
    Preconditions.checkNotNull(iterator);
    return new UnmodifiableIterator<T>()
      {
        public boolean hasNext() {
          return iterator.hasNext();
        }

        
        public T next() {
          T next = iterator.next();
          iterator.remove();
          return next;
        }

        
        public String toString() {
          return "Iterators.consumingIterator(...)";
        }
      };
  }




  
  static <T> T pollNext(Iterator<T> iterator) {
    if (iterator.hasNext()) {
      T result = iterator.next();
      iterator.remove();
      return result;
    } 
    return null;
  }




  
  static void clear(Iterator<?> iterator) {
    Preconditions.checkNotNull(iterator);
    while (iterator.hasNext()) {
      iterator.next();
      iterator.remove();
    } 
  }










  
  @SafeVarargs
  public static <T> UnmodifiableIterator<T> forArray(T... array) {
    return forArray(array, 0, array.length, 0);
  }








  
  static <T> UnmodifiableListIterator<T> forArray(T[] array, int offset, int length, int index) {
    Preconditions.checkArgument((length >= 0));
    int end = offset + length;

    
    Preconditions.checkPositionIndexes(offset, end, array.length);
    Preconditions.checkPositionIndex(index, length);
    if (length == 0) {
      return emptyListIterator();
    }
    return new ArrayItr<>(array, offset, length, index);
  }
  
  private static final class ArrayItr<T> extends AbstractIndexedListIterator<T> {
    static final UnmodifiableListIterator<Object> EMPTY = new ArrayItr((T[])new Object[0], 0, 0, 0);
    
    private final T[] array;
    private final int offset;
    
    ArrayItr(T[] array, int offset, int length, int index) {
      super(length, index);
      this.array = array;
      this.offset = offset;
    }

    
    protected T get(int index) {
      return this.array[this.offset + index];
    }
  }





  
  public static <T> UnmodifiableIterator<T> singletonIterator(final T value) {
    return new UnmodifiableIterator<T>()
      {
        boolean done;
        
        public boolean hasNext() {
          return !this.done;
        }

        
        public T next() {
          if (this.done) {
            throw new NoSuchElementException();
          }
          this.done = true;
          return (T)value;
        }
      };
  }







  
  public static <T> UnmodifiableIterator<T> forEnumeration(final Enumeration<T> enumeration) {
    Preconditions.checkNotNull(enumeration);
    return new UnmodifiableIterator<T>()
      {
        public boolean hasNext() {
          return enumeration.hasMoreElements();
        }

        
        public T next() {
          return enumeration.nextElement();
        }
      };
  }






  
  public static <T> Enumeration<T> asEnumeration(final Iterator<T> iterator) {
    Preconditions.checkNotNull(iterator);
    return new Enumeration<T>()
      {
        public boolean hasMoreElements() {
          return iterator.hasNext();
        }

        
        public T nextElement() {
          return iterator.next();
        }
      };
  }
  
  private static class PeekingImpl<E>
    implements PeekingIterator<E>
  {
    private final Iterator<? extends E> iterator;
    private boolean hasPeeked;
    private E peekedElement;
    
    public PeekingImpl(Iterator<? extends E> iterator) {
      this.iterator = (Iterator<? extends E>)Preconditions.checkNotNull(iterator);
    }

    
    public boolean hasNext() {
      return (this.hasPeeked || this.iterator.hasNext());
    }

    
    public E next() {
      if (!this.hasPeeked) {
        return this.iterator.next();
      }
      E result = this.peekedElement;
      this.hasPeeked = false;
      this.peekedElement = null;
      return result;
    }

    
    public void remove() {
      Preconditions.checkState(!this.hasPeeked, "Can't remove after you've peeked at next");
      this.iterator.remove();
    }

    
    public E peek() {
      if (!this.hasPeeked) {
        this.peekedElement = this.iterator.next();
        this.hasPeeked = true;
      } 
      return this.peekedElement;
    }
  }




































  
  public static <T> PeekingIterator<T> peekingIterator(Iterator<? extends T> iterator) {
    if (iterator instanceof PeekingImpl) {


      
      PeekingImpl<T> peeking = (PeekingImpl)iterator;
      return peeking;
    } 
    return new PeekingImpl<>(iterator);
  }






  
  @Deprecated
  public static <T> PeekingIterator<T> peekingIterator(PeekingIterator<T> iterator) {
    return (PeekingIterator<T>)Preconditions.checkNotNull(iterator);
  }













  
  @Beta
  public static <T> UnmodifiableIterator<T> mergeSorted(Iterable<? extends Iterator<? extends T>> iterators, Comparator<? super T> comparator) {
    Preconditions.checkNotNull(iterators, "iterators");
    Preconditions.checkNotNull(comparator, "comparator");
    
    return new MergingIterator<>(iterators, comparator);
  }






  
  private static class MergingIterator<T>
    extends UnmodifiableIterator<T>
  {
    final Queue<PeekingIterator<T>> queue;





    
    public MergingIterator(Iterable<? extends Iterator<? extends T>> iterators, final Comparator<? super T> itemComparator) {
      Comparator<PeekingIterator<T>> heapComparator = (Comparator)new Comparator<PeekingIterator<PeekingIterator<T>>>()
        {
          public int compare(PeekingIterator<T> o1, PeekingIterator<T> o2)
          {
            return itemComparator.compare(o1.peek(), o2.peek());
          }
        };
      
      this.queue = new PriorityQueue<>(2, heapComparator);
      
      for (Iterator<? extends T> iterator : iterators) {
        if (iterator.hasNext()) {
          this.queue.add(Iterators.peekingIterator(iterator));
        }
      } 
    }

    
    public boolean hasNext() {
      return !this.queue.isEmpty();
    }

    
    public T next() {
      PeekingIterator<T> nextIter = this.queue.remove();
      T next = nextIter.next();
      if (nextIter.hasNext()) {
        this.queue.add(nextIter);
      }
      return next;
    }
  }



  
  private static class ConcatenatedIterator<T>
    implements Iterator<T>
  {
    private Iterator<? extends T> toRemove;

    
    private Iterator<? extends T> iterator;

    
    private Iterator<? extends Iterator<? extends T>> topMetaIterator;

    
    private Deque<Iterator<? extends Iterator<? extends T>>> metaIterators;


    
    ConcatenatedIterator(Iterator<? extends Iterator<? extends T>> metaIterator) {
      this.iterator = Iterators.emptyIterator();
      this.topMetaIterator = (Iterator<? extends Iterator<? extends T>>)Preconditions.checkNotNull(metaIterator);
    }

    
    private Iterator<? extends Iterator<? extends T>> getTopMetaIterator() {
      while (this.topMetaIterator == null || !this.topMetaIterator.hasNext()) {
        if (this.metaIterators != null && !this.metaIterators.isEmpty()) {
          this.topMetaIterator = this.metaIterators.removeFirst(); continue;
        } 
        return null;
      } 
      
      return this.topMetaIterator;
    }

    
    public boolean hasNext() {
      while (!((Iterator)Preconditions.checkNotNull(this.iterator)).hasNext()) {


        
        this.topMetaIterator = getTopMetaIterator();
        if (this.topMetaIterator == null) {
          return false;
        }
        
        this.iterator = this.topMetaIterator.next();
        
        if (this.iterator instanceof ConcatenatedIterator) {


          
          ConcatenatedIterator<T> topConcat = (ConcatenatedIterator)this.iterator;
          this.iterator = topConcat.iterator;



          
          if (this.metaIterators == null) {
            this.metaIterators = new ArrayDeque<>();
          }
          this.metaIterators.addFirst(this.topMetaIterator);
          if (topConcat.metaIterators != null) {
            while (!topConcat.metaIterators.isEmpty()) {
              this.metaIterators.addFirst(topConcat.metaIterators.removeLast());
            }
          }
          this.topMetaIterator = topConcat.topMetaIterator;
        } 
      } 
      return true;
    }

    
    public T next() {
      if (hasNext()) {
        this.toRemove = this.iterator;
        return this.iterator.next();
      } 
      throw new NoSuchElementException();
    }


    
    public void remove() {
      CollectPreconditions.checkRemove((this.toRemove != null));
      this.toRemove.remove();
      this.toRemove = null;
    }
  }

  
  static <T> ListIterator<T> cast(Iterator<T> iterator) {
    return (ListIterator<T>)iterator;
  }
}
