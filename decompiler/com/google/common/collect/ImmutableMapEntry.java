package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;


































@GwtIncompatible
class ImmutableMapEntry<K, V>
  extends ImmutableEntry<K, V>
{
  static <K, V> ImmutableMapEntry<K, V>[] createEntryArray(int size) {
    return (ImmutableMapEntry<K, V>[])new ImmutableMapEntry[size];
  }
  
  ImmutableMapEntry(K key, V value) {
    super(key, value);
    CollectPreconditions.checkEntryNotNull(key, value);
  }
  
  ImmutableMapEntry(ImmutableMapEntry<K, V> contents) {
    super(contents.getKey(), contents.getValue());
  }


  
  ImmutableMapEntry<K, V> getNextInKeyBucket() {
    return null;
  }

  
  ImmutableMapEntry<K, V> getNextInValueBucket() {
    return null;
  }




  
  boolean isReusable() {
    return true;
  }
  
  static class NonTerminalImmutableMapEntry<K, V> extends ImmutableMapEntry<K, V> {
    private final transient ImmutableMapEntry<K, V> nextInKeyBucket;
    
    NonTerminalImmutableMapEntry(K key, V value, ImmutableMapEntry<K, V> nextInKeyBucket) {
      super(key, value);
      this.nextInKeyBucket = nextInKeyBucket;
    }

    
    final ImmutableMapEntry<K, V> getNextInKeyBucket() {
      return this.nextInKeyBucket;
    }

    
    final boolean isReusable() {
      return false;
    }
  }


  
  static final class NonTerminalImmutableBiMapEntry<K, V>
    extends NonTerminalImmutableMapEntry<K, V>
  {
    private final transient ImmutableMapEntry<K, V> nextInValueBucket;

    
    NonTerminalImmutableBiMapEntry(K key, V value, ImmutableMapEntry<K, V> nextInKeyBucket, ImmutableMapEntry<K, V> nextInValueBucket) {
      super(key, value, nextInKeyBucket);
      this.nextInValueBucket = nextInValueBucket;
    }


    
    ImmutableMapEntry<K, V> getNextInValueBucket() {
      return this.nextInValueBucket;
    }
  }
}
