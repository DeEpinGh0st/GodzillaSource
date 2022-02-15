package org.bouncycastle.util;

import java.util.Iterator;

public interface Iterable<T> extends java.lang.Iterable<T> {
  Iterator<T> iterator();
}
