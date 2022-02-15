package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.ForOverride;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.io.Serializable;
import java.util.Iterator;











































































































@GwtCompatible
public abstract class Converter<A, B>
  implements Function<A, B>
{
  private final boolean handleNullAutomatically;
  @LazyInit
  private transient Converter<B, A> reverse;
  
  protected Converter() {
    this(true);
  }

  
  Converter(boolean handleNullAutomatically) {
    this.handleNullAutomatically = handleNullAutomatically;
  }










  
  @ForOverride
  protected abstract B doForward(A paramA);









  
  @ForOverride
  protected abstract A doBackward(B paramB);









  
  @CanIgnoreReturnValue
  public final B convert(A a) {
    return correctedDoForward(a);
  }

  
  B correctedDoForward(A a) {
    if (this.handleNullAutomatically)
    {
      return (a == null) ? null : Preconditions.<B>checkNotNull(doForward(a));
    }
    return doForward(a);
  }


  
  A correctedDoBackward(B b) {
    if (this.handleNullAutomatically)
    {
      return (b == null) ? null : Preconditions.<A>checkNotNull(doBackward(b));
    }
    return doBackward(b);
  }









  
  @CanIgnoreReturnValue
  public Iterable<B> convertAll(final Iterable<? extends A> fromIterable) {
    Preconditions.checkNotNull(fromIterable, "fromIterable");
    return new Iterable<B>()
      {
        public Iterator<B> iterator() {
          return new Iterator<B>() {
              private final Iterator<? extends A> fromIterator = fromIterable.iterator();

              
              public boolean hasNext() {
                return this.fromIterator.hasNext();
              }

              
              public B next() {
                return (B)Converter.this.convert(this.fromIterator.next());
              }

              
              public void remove() {
                this.fromIterator.remove();
              }
            };
        }
      };
  }








  
  @CanIgnoreReturnValue
  public Converter<B, A> reverse() {
    Converter<B, A> result = this.reverse;
    return (result == null) ? (this.reverse = new ReverseConverter<>(this)) : result;
  }
  
  private static final class ReverseConverter<A, B> extends Converter<B, A> implements Serializable {
    final Converter<A, B> original;
    private static final long serialVersionUID = 0L;
    
    ReverseConverter(Converter<A, B> original) {
      this.original = original;
    }








    
    protected A doForward(B b) {
      throw new AssertionError();
    }

    
    protected B doBackward(A a) {
      throw new AssertionError();
    }


    
    A correctedDoForward(B b) {
      return this.original.correctedDoBackward(b);
    }


    
    B correctedDoBackward(A a) {
      return this.original.correctedDoForward(a);
    }

    
    public Converter<A, B> reverse() {
      return this.original;
    }

    
    public boolean equals(Object object) {
      if (object instanceof ReverseConverter) {
        ReverseConverter<?, ?> that = (ReverseConverter<?, ?>)object;
        return this.original.equals(that.original);
      } 
      return false;
    }

    
    public int hashCode() {
      return this.original.hashCode() ^ 0xFFFFFFFF;
    }

    
    public String toString() {
      return this.original + ".reverse()";
    }
  }









  
  public final <C> Converter<A, C> andThen(Converter<B, C> secondConverter) {
    return doAndThen(secondConverter);
  }

  
  <C> Converter<A, C> doAndThen(Converter<B, C> secondConverter) {
    return new ConverterComposition<>(this, Preconditions.<Converter<B, C>>checkNotNull(secondConverter));
  }
  
  private static final class ConverterComposition<A, B, C> extends Converter<A, C> implements Serializable {
    final Converter<A, B> first;
    final Converter<B, C> second;
    private static final long serialVersionUID = 0L;
    
    ConverterComposition(Converter<A, B> first, Converter<B, C> second) {
      this.first = first;
      this.second = second;
    }








    
    protected C doForward(A a) {
      throw new AssertionError();
    }

    
    protected A doBackward(C c) {
      throw new AssertionError();
    }


    
    C correctedDoForward(A a) {
      return this.second.correctedDoForward(this.first.correctedDoForward(a));
    }


    
    A correctedDoBackward(C c) {
      return this.first.correctedDoBackward(this.second.correctedDoBackward(c));
    }

    
    public boolean equals(Object object) {
      if (object instanceof ConverterComposition) {
        ConverterComposition<?, ?, ?> that = (ConverterComposition<?, ?, ?>)object;
        return (this.first.equals(that.first) && this.second.equals(that.second));
      } 
      return false;
    }

    
    public int hashCode() {
      return 31 * this.first.hashCode() + this.second.hashCode();
    }

    
    public String toString() {
      return this.first + ".andThen(" + this.second + ")";
    }
  }






  
  @Deprecated
  @CanIgnoreReturnValue
  public final B apply(A a) {
    return convert(a);
  }












  
  public boolean equals(Object object) {
    return super.equals(object);
  }


















  
  public static <A, B> Converter<A, B> from(Function<? super A, ? extends B> forwardFunction, Function<? super B, ? extends A> backwardFunction) {
    return new FunctionBasedConverter<>(forwardFunction, backwardFunction);
  }
  
  private static final class FunctionBasedConverter<A, B>
    extends Converter<A, B>
    implements Serializable
  {
    private final Function<? super A, ? extends B> forwardFunction;
    private final Function<? super B, ? extends A> backwardFunction;
    
    private FunctionBasedConverter(Function<? super A, ? extends B> forwardFunction, Function<? super B, ? extends A> backwardFunction) {
      this.forwardFunction = Preconditions.<Function<? super A, ? extends B>>checkNotNull(forwardFunction);
      this.backwardFunction = Preconditions.<Function<? super B, ? extends A>>checkNotNull(backwardFunction);
    }

    
    protected B doForward(A a) {
      return this.forwardFunction.apply(a);
    }

    
    protected A doBackward(B b) {
      return this.backwardFunction.apply(b);
    }

    
    public boolean equals(Object object) {
      if (object instanceof FunctionBasedConverter) {
        FunctionBasedConverter<?, ?> that = (FunctionBasedConverter<?, ?>)object;
        return (this.forwardFunction.equals(that.forwardFunction) && this.backwardFunction
          .equals(that.backwardFunction));
      } 
      return false;
    }

    
    public int hashCode() {
      return this.forwardFunction.hashCode() * 31 + this.backwardFunction.hashCode();
    }

    
    public String toString() {
      return "Converter.from(" + this.forwardFunction + ", " + this.backwardFunction + ")";
    }
  }


  
  public static <T> Converter<T, T> identity() {
    return IdentityConverter.INSTANCE;
  }

  
  private static final class IdentityConverter<T>
    extends Converter<T, T>
    implements Serializable
  {
    static final IdentityConverter INSTANCE = new IdentityConverter();
    private static final long serialVersionUID = 0L;
    
    protected T doForward(T t) {
      return t;
    }

    
    protected T doBackward(T t) {
      return t;
    }

    
    public IdentityConverter<T> reverse() {
      return this;
    }

    
    <S> Converter<T, S> doAndThen(Converter<T, S> otherConverter) {
      return Preconditions.<Converter<T, S>>checkNotNull(otherConverter, "otherConverter");
    }






    
    public String toString() {
      return "Converter.identity()";
    }
    
    private Object readResolve() {
      return INSTANCE;
    }
  }
}
