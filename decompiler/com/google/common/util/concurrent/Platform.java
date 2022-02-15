package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;

















@GwtCompatible(emulated = true)
final class Platform
{
  static boolean isInstanceOfThrowableClass(Throwable t, Class<? extends Throwable> expectedClass) {
    return expectedClass.isInstance(t);
  }
}
