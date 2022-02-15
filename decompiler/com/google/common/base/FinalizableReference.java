package com.google.common.base;

import com.google.common.annotations.GwtIncompatible;

@GwtIncompatible
public interface FinalizableReference {
  void finalizeReferent();
}
