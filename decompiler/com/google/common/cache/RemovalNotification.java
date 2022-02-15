package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.AbstractMap;

































@GwtCompatible
public final class RemovalNotification<K, V>
  extends AbstractMap.SimpleImmutableEntry<K, V>
{
  private final RemovalCause cause;
  private static final long serialVersionUID = 0L;
  
  public static <K, V> RemovalNotification<K, V> create(K key, V value, RemovalCause cause) {
    return new RemovalNotification<>(key, value, cause);
  }
  
  private RemovalNotification(K key, V value, RemovalCause cause) {
    super(key, value);
    this.cause = (RemovalCause)Preconditions.checkNotNull(cause);
  }

  
  public RemovalCause getCause() {
    return this.cause;
  }




  
  public boolean wasEvicted() {
    return this.cause.wasEvicted();
  }
}
