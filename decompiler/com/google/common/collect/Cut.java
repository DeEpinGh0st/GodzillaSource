package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Booleans;
import java.io.Serializable;
import java.util.NoSuchElementException;























@GwtCompatible
abstract class Cut<C extends Comparable>
  implements Comparable<Cut<C>>, Serializable
{
  final C endpoint;
  private static final long serialVersionUID = 0L;
  
  Cut(C endpoint) {
    this.endpoint = endpoint;
  }






















  
  Cut<C> canonical(DiscreteDomain<C> domain) {
    return this;
  }


  
  public int compareTo(Cut<C> that) {
    if (that == belowAll()) {
      return 1;
    }
    if (that == aboveAll()) {
      return -1;
    }
    int result = Range.compareOrThrow((Comparable)this.endpoint, (Comparable)that.endpoint);
    if (result != 0) {
      return result;
    }
    
    return Booleans.compare(this instanceof AboveValue, that instanceof AboveValue);
  }
  
  C endpoint() {
    return this.endpoint;
  }


  
  public boolean equals(Object obj) {
    if (obj instanceof Cut) {
      
      Cut<C> that = (Cut<C>)obj;
      try {
        int compareResult = compareTo(that);
        return (compareResult == 0);
      } catch (ClassCastException classCastException) {}
    } 
    
    return false;
  }









  
  static <C extends Comparable> Cut<C> belowAll() {
    return BelowAll.INSTANCE;
  }
  
  private static final class BelowAll
    extends Cut<Comparable<?>>
  {
    private static final BelowAll INSTANCE = new BelowAll(); private static final long serialVersionUID = 0L;
    
    private BelowAll() {
      super(null);
    }

    
    Comparable<?> endpoint() {
      throw new IllegalStateException("range unbounded on this side");
    }

    
    boolean isLessThan(Comparable<?> value) {
      return true;
    }

    
    BoundType typeAsLowerBound() {
      throw new IllegalStateException();
    }

    
    BoundType typeAsUpperBound() {
      throw new AssertionError("this statement should be unreachable");
    }


    
    Cut<Comparable<?>> withLowerBoundType(BoundType boundType, DiscreteDomain<Comparable<?>> domain) {
      throw new IllegalStateException();
    }


    
    Cut<Comparable<?>> withUpperBoundType(BoundType boundType, DiscreteDomain<Comparable<?>> domain) {
      throw new AssertionError("this statement should be unreachable");
    }

    
    void describeAsLowerBound(StringBuilder sb) {
      sb.append("(-∞");
    }

    
    void describeAsUpperBound(StringBuilder sb) {
      throw new AssertionError();
    }

    
    Comparable<?> leastValueAbove(DiscreteDomain<Comparable<?>> domain) {
      return domain.minValue();
    }

    
    Comparable<?> greatestValueBelow(DiscreteDomain<Comparable<?>> domain) {
      throw new AssertionError();
    }

    
    Cut<Comparable<?>> canonical(DiscreteDomain<Comparable<?>> domain) {
      try {
        return Cut.belowValue(domain.minValue());
      } catch (NoSuchElementException e) {
        return this;
      } 
    }

    
    public int compareTo(Cut<Comparable<?>> o) {
      return (o == this) ? 0 : -1;
    }

    
    public int hashCode() {
      return System.identityHashCode(this);
    }

    
    public String toString() {
      return "-∞";
    }
    
    private Object readResolve() {
      return INSTANCE;
    }
  }







  
  static <C extends Comparable> Cut<C> aboveAll() {
    return AboveAll.INSTANCE;
  }
  
  private static final class AboveAll extends Cut<Comparable<?>> {
    private static final AboveAll INSTANCE = new AboveAll(); private static final long serialVersionUID = 0L;
    
    private AboveAll() {
      super(null);
    }

    
    Comparable<?> endpoint() {
      throw new IllegalStateException("range unbounded on this side");
    }

    
    boolean isLessThan(Comparable<?> value) {
      return false;
    }

    
    BoundType typeAsLowerBound() {
      throw new AssertionError("this statement should be unreachable");
    }

    
    BoundType typeAsUpperBound() {
      throw new IllegalStateException();
    }


    
    Cut<Comparable<?>> withLowerBoundType(BoundType boundType, DiscreteDomain<Comparable<?>> domain) {
      throw new AssertionError("this statement should be unreachable");
    }


    
    Cut<Comparable<?>> withUpperBoundType(BoundType boundType, DiscreteDomain<Comparable<?>> domain) {
      throw new IllegalStateException();
    }

    
    void describeAsLowerBound(StringBuilder sb) {
      throw new AssertionError();
    }

    
    void describeAsUpperBound(StringBuilder sb) {
      sb.append("+∞)");
    }

    
    Comparable<?> leastValueAbove(DiscreteDomain<Comparable<?>> domain) {
      throw new AssertionError();
    }

    
    Comparable<?> greatestValueBelow(DiscreteDomain<Comparable<?>> domain) {
      return domain.maxValue();
    }

    
    public int compareTo(Cut<Comparable<?>> o) {
      return (o == this) ? 0 : 1;
    }

    
    public int hashCode() {
      return System.identityHashCode(this);
    }

    
    public String toString() {
      return "+∞";
    }
    
    private Object readResolve() {
      return INSTANCE;
    }
  }


  
  static <C extends Comparable> Cut<C> belowValue(C endpoint) {
    return new BelowValue<>(endpoint);
  }
  private static final class BelowValue<C extends Comparable> extends Cut<C> { private static final long serialVersionUID = 0L;
    
    BelowValue(C endpoint) {
      super((C)Preconditions.checkNotNull(endpoint));
    }

    
    boolean isLessThan(C value) {
      return (Range.compareOrThrow((Comparable)this.endpoint, (Comparable)value) <= 0);
    }

    
    BoundType typeAsLowerBound() {
      return BoundType.CLOSED;
    }

    
    BoundType typeAsUpperBound() {
      return BoundType.OPEN;
    }
    
    Cut<C> withLowerBoundType(BoundType boundType, DiscreteDomain<C> domain) {
      C previous;
      switch (boundType) {
        case CLOSED:
          return this;
        case OPEN:
          previous = domain.previous(this.endpoint);
          return (previous == null) ? Cut.<C>belowAll() : new Cut.AboveValue<>(previous);
      } 
      throw new AssertionError();
    }

    
    Cut<C> withUpperBoundType(BoundType boundType, DiscreteDomain<C> domain) {
      C previous;
      switch (boundType) {
        case CLOSED:
          previous = domain.previous(this.endpoint);
          return (previous == null) ? Cut.<C>aboveAll() : new Cut.AboveValue<>(previous);
        case OPEN:
          return this;
      } 
      throw new AssertionError();
    }


    
    void describeAsLowerBound(StringBuilder sb) {
      sb.append('[').append(this.endpoint);
    }

    
    void describeAsUpperBound(StringBuilder sb) {
      sb.append(this.endpoint).append(')');
    }

    
    C leastValueAbove(DiscreteDomain<C> domain) {
      return this.endpoint;
    }

    
    C greatestValueBelow(DiscreteDomain<C> domain) {
      return domain.previous(this.endpoint);
    }

    
    public int hashCode() {
      return this.endpoint.hashCode();
    }

    
    public String toString() {
      return "\\" + this.endpoint + "/";
    } } abstract boolean isLessThan(C paramC); abstract BoundType typeAsLowerBound(); abstract BoundType typeAsUpperBound(); abstract Cut<C> withLowerBoundType(BoundType paramBoundType, DiscreteDomain<C> paramDiscreteDomain);
  abstract Cut<C> withUpperBoundType(BoundType paramBoundType, DiscreteDomain<C> paramDiscreteDomain);
  abstract void describeAsLowerBound(StringBuilder paramStringBuilder);
  abstract void describeAsUpperBound(StringBuilder paramStringBuilder);
  abstract C leastValueAbove(DiscreteDomain<C> paramDiscreteDomain);
  static <C extends Comparable> Cut<C> aboveValue(C endpoint) {
    return new AboveValue<>(endpoint);
  }
  abstract C greatestValueBelow(DiscreteDomain<C> paramDiscreteDomain);
  public abstract int hashCode();
  private static final class AboveValue<C extends Comparable> extends Cut<C> { AboveValue(C endpoint) {
      super((C)Preconditions.checkNotNull(endpoint));
    }
    private static final long serialVersionUID = 0L;
    
    boolean isLessThan(C value) {
      return (Range.compareOrThrow((Comparable)this.endpoint, (Comparable)value) < 0);
    }

    
    BoundType typeAsLowerBound() {
      return BoundType.OPEN;
    }

    
    BoundType typeAsUpperBound() {
      return BoundType.CLOSED;
    }
    
    Cut<C> withLowerBoundType(BoundType boundType, DiscreteDomain<C> domain) {
      C next;
      switch (boundType) {
        case OPEN:
          return this;
        case CLOSED:
          next = domain.next(this.endpoint);
          return (next == null) ? Cut.<C>belowAll() : belowValue(next);
      } 
      throw new AssertionError();
    }

    
    Cut<C> withUpperBoundType(BoundType boundType, DiscreteDomain<C> domain) {
      C next;
      switch (boundType) {
        case OPEN:
          next = domain.next(this.endpoint);
          return (next == null) ? Cut.<C>aboveAll() : belowValue(next);
        case CLOSED:
          return this;
      } 
      throw new AssertionError();
    }


    
    void describeAsLowerBound(StringBuilder sb) {
      sb.append('(').append(this.endpoint);
    }

    
    void describeAsUpperBound(StringBuilder sb) {
      sb.append(this.endpoint).append(']');
    }

    
    C leastValueAbove(DiscreteDomain<C> domain) {
      return domain.next(this.endpoint);
    }

    
    C greatestValueBelow(DiscreteDomain<C> domain) {
      return this.endpoint;
    }

    
    Cut<C> canonical(DiscreteDomain<C> domain) {
      C next = leastValueAbove(domain);
      return (next != null) ? belowValue(next) : Cut.<C>aboveAll();
    }

    
    public int hashCode() {
      return this.endpoint.hashCode() ^ 0xFFFFFFFF;
    }

    
    public String toString() {
      return "/" + this.endpoint + "\\";
    } }

}
