package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import java.util.Set;
import java.util.SortedSet;

@GwtIncompatible
interface SortedMultisetBridge<E> extends Multiset<E> {
  SortedSet<E> elementSet();
}
