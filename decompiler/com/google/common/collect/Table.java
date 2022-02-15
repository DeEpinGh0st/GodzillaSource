package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CompatibleWith;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@GwtCompatible
public interface Table<R, C, V> {
  boolean contains(@CompatibleWith("R") Object paramObject1, @CompatibleWith("C") Object paramObject2);
  
  boolean containsRow(@CompatibleWith("R") Object paramObject);
  
  boolean containsColumn(@CompatibleWith("C") Object paramObject);
  
  boolean containsValue(@CompatibleWith("V") Object paramObject);
  
  V get(@CompatibleWith("R") Object paramObject1, @CompatibleWith("C") Object paramObject2);
  
  boolean isEmpty();
  
  int size();
  
  boolean equals(Object paramObject);
  
  int hashCode();
  
  void clear();
  
  @CanIgnoreReturnValue
  V put(R paramR, C paramC, V paramV);
  
  void putAll(Table<? extends R, ? extends C, ? extends V> paramTable);
  
  @CanIgnoreReturnValue
  V remove(@CompatibleWith("R") Object paramObject1, @CompatibleWith("C") Object paramObject2);
  
  Map<C, V> row(R paramR);
  
  Map<R, V> column(C paramC);
  
  Set<Cell<R, C, V>> cellSet();
  
  Set<R> rowKeySet();
  
  Set<C> columnKeySet();
  
  Collection<V> values();
  
  Map<R, Map<C, V>> rowMap();
  
  Map<C, Map<R, V>> columnMap();
  
  public static interface Cell<R, C, V> {
    R getRowKey();
    
    C getColumnKey();
    
    V getValue();
    
    boolean equals(Object param1Object);
    
    int hashCode();
  }
}
