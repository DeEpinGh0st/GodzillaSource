package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public interface FutureCallback<V> {
  void onSuccess(V paramV);
  
  void onFailure(Throwable paramThrowable);
}
