package com.google.common.base;

import com.google.common.annotations.GwtIncompatible;
import java.lang.ref.SoftReference;




























@GwtIncompatible
public abstract class FinalizableSoftReference<T>
  extends SoftReference<T>
  implements FinalizableReference
{
  protected FinalizableSoftReference(T referent, FinalizableReferenceQueue queue) {
    super(referent, queue.queue);
    queue.cleanUp();
  }
}
