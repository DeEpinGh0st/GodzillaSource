package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;

@FunctionalInterface
@GwtCompatible
public interface Weigher<K, V> {
  int weigh(K paramK, V paramV);
}
