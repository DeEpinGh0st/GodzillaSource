package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Iterator;
import java.util.NoSuchElementException;



















@GwtCompatible
abstract class AbstractIterator<T>
  implements Iterator<T>
{
  private T next;
  private State state = State.NOT_READY;
  
  protected abstract T computeNext();
  
  private enum State {
    READY,
    NOT_READY,
    DONE,
    FAILED;
  }




  
  @CanIgnoreReturnValue
  protected final T endOfData() {
    this.state = State.DONE;
    return null;
  }

  
  public final boolean hasNext() {
    Preconditions.checkState((this.state != State.FAILED));
    switch (this.state) {
      case READY:
        return true;
      case DONE:
        return false;
    } 
    
    return tryToComputeNext();
  }
  
  private boolean tryToComputeNext() {
    this.state = State.FAILED;
    this.next = computeNext();
    if (this.state != State.DONE) {
      this.state = State.READY;
      return true;
    } 
    return false;
  }

  
  public final T next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    this.state = State.NOT_READY;
    T result = this.next;
    this.next = null;
    return result;
  }

  
  public final void remove() {
    throw new UnsupportedOperationException();
  }
}
