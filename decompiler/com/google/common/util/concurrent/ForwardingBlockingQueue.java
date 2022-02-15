package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ForwardingQueue;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


































@CanIgnoreReturnValue
@GwtIncompatible
public abstract class ForwardingBlockingQueue<E>
  extends ForwardingQueue<E>
  implements BlockingQueue<E>
{
  public int drainTo(Collection<? super E> c, int maxElements) {
    return delegate().drainTo(c, maxElements);
  }

  
  public int drainTo(Collection<? super E> c) {
    return delegate().drainTo(c);
  }

  
  public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
    return delegate().offer(e, timeout, unit);
  }

  
  public E poll(long timeout, TimeUnit unit) throws InterruptedException {
    return delegate().poll(timeout, unit);
  }

  
  public void put(E e) throws InterruptedException {
    delegate().put(e);
  }

  
  public int remainingCapacity() {
    return delegate().remainingCapacity();
  }

  
  public E take() throws InterruptedException {
    return delegate().take();
  }
  
  protected abstract BlockingQueue<E> delegate();
}
