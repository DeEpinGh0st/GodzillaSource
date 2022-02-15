package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Queue;


















@GwtCompatible
class ConsumingQueueIterator<T>
  extends AbstractIterator<T>
{
  private final Queue<T> queue;
  
  ConsumingQueueIterator(T... elements) {
    this.queue = new ArrayDeque<>(elements.length);
    Collections.addAll(this.queue, elements);
  }
  
  ConsumingQueueIterator(Queue<T> queue) {
    this.queue = (Queue<T>)Preconditions.checkNotNull(queue);
  }

  
  public T computeNext() {
    return this.queue.isEmpty() ? endOfData() : this.queue.remove();
  }
}
