package com.google.common.cache;

import com.google.common.annotations.GwtIncompatible;

@GwtIncompatible
interface ReferenceEntry<K, V> {
  LocalCache.ValueReference<K, V> getValueReference();
  
  void setValueReference(LocalCache.ValueReference<K, V> paramValueReference);
  
  ReferenceEntry<K, V> getNext();
  
  int getHash();
  
  K getKey();
  
  long getAccessTime();
  
  void setAccessTime(long paramLong);
  
  ReferenceEntry<K, V> getNextInAccessQueue();
  
  void setNextInAccessQueue(ReferenceEntry<K, V> paramReferenceEntry);
  
  ReferenceEntry<K, V> getPreviousInAccessQueue();
  
  void setPreviousInAccessQueue(ReferenceEntry<K, V> paramReferenceEntry);
  
  long getWriteTime();
  
  void setWriteTime(long paramLong);
  
  ReferenceEntry<K, V> getNextInWriteQueue();
  
  void setNextInWriteQueue(ReferenceEntry<K, V> paramReferenceEntry);
  
  ReferenceEntry<K, V> getPreviousInWriteQueue();
  
  void setPreviousInWriteQueue(ReferenceEntry<K, V> paramReferenceEntry);
}
