package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;






























































@Beta
@GwtCompatible(emulated = true)
public final class ArrayTable<R, C, V>
  extends AbstractTable<R, C, V>
  implements Serializable
{
  private final ImmutableList<R> rowList;
  private final ImmutableList<C> columnList;
  private final ImmutableMap<R, Integer> rowKeyToIndex;
  private final ImmutableMap<C, Integer> columnKeyToIndex;
  private final V[][] array;
  private transient ColumnMap columnMap;
  private transient RowMap rowMap;
  private static final long serialVersionUID = 0L;
  
  public static <R, C, V> ArrayTable<R, C, V> create(Iterable<? extends R> rowKeys, Iterable<? extends C> columnKeys) {
    return new ArrayTable<>(rowKeys, columnKeys);
  }
























  
  public static <R, C, V> ArrayTable<R, C, V> create(Table<R, C, V> table) {
    return (table instanceof ArrayTable) ? new ArrayTable<>((ArrayTable<R, C, V>)table) : new ArrayTable<>(table);
  }










  
  private ArrayTable(Iterable<? extends R> rowKeys, Iterable<? extends C> columnKeys) {
    this.rowList = ImmutableList.copyOf(rowKeys);
    this.columnList = ImmutableList.copyOf(columnKeys);
    Preconditions.checkArgument((this.rowList.isEmpty() == this.columnList.isEmpty()));






    
    this.rowKeyToIndex = Maps.indexMap(this.rowList);
    this.columnKeyToIndex = Maps.indexMap(this.columnList);

    
    V[][] tmpArray = (V[][])new Object[this.rowList.size()][this.columnList.size()];
    this.array = tmpArray;
    
    eraseAll();
  }
  
  private ArrayTable(Table<R, C, V> table) {
    this(table.rowKeySet(), table.columnKeySet());
    putAll(table);
  }
  
  private ArrayTable(ArrayTable<R, C, V> table) {
    this.rowList = table.rowList;
    this.columnList = table.columnList;
    this.rowKeyToIndex = table.rowKeyToIndex;
    this.columnKeyToIndex = table.columnKeyToIndex;
    
    V[][] copy = (V[][])new Object[this.rowList.size()][this.columnList.size()];
    this.array = copy;
    for (int i = 0; i < this.rowList.size(); i++)
      System.arraycopy(table.array[i], 0, copy[i], 0, (table.array[i]).length); 
  }
  
  private static abstract class ArrayMap<K, V>
    extends Maps.IteratorBasedAbstractMap<K, V> {
    private final ImmutableMap<K, Integer> keyIndex;
    
    private ArrayMap(ImmutableMap<K, Integer> keyIndex) {
      this.keyIndex = keyIndex;
    }

    
    public Set<K> keySet() {
      return this.keyIndex.keySet();
    }
    
    K getKey(int index) {
      return this.keyIndex.keySet().asList().get(index);
    }







    
    public int size() {
      return this.keyIndex.size();
    }

    
    public boolean isEmpty() {
      return this.keyIndex.isEmpty();
    }
    
    Map.Entry<K, V> getEntry(final int index) {
      Preconditions.checkElementIndex(index, size());
      return new AbstractMapEntry<K, V>()
        {
          public K getKey() {
            return (K)ArrayTable.ArrayMap.this.getKey(index);
          }

          
          public V getValue() {
            return (V)ArrayTable.ArrayMap.this.getValue(index);
          }

          
          public V setValue(V value) {
            return (V)ArrayTable.ArrayMap.this.setValue(index, value);
          }
        };
    }

    
    Iterator<Map.Entry<K, V>> entryIterator() {
      return new AbstractIndexedListIterator<Map.Entry<K, V>>(size())
        {
          protected Map.Entry<K, V> get(int index) {
            return ArrayTable.ArrayMap.this.getEntry(index);
          }
        };
    }

    
    Spliterator<Map.Entry<K, V>> entrySpliterator() {
      return CollectSpliterators.indexed(size(), 16, this::getEntry);
    }



    
    public boolean containsKey(Object key) {
      return this.keyIndex.containsKey(key);
    }

    
    public V get(Object key) {
      Integer index = this.keyIndex.get(key);
      if (index == null) {
        return null;
      }
      return getValue(index.intValue());
    }


    
    public V put(K key, V value) {
      Integer index = this.keyIndex.get(key);
      if (index == null) {
        throw new IllegalArgumentException(
            getKeyRole() + " " + key + " not in " + this.keyIndex.keySet());
      }
      return setValue(index.intValue(), value);
    }

    
    public V remove(Object key) {
      throw new UnsupportedOperationException();
    }
    abstract String getKeyRole();
    abstract V getValue(int param1Int);
    public void clear() {
      throw new UnsupportedOperationException();
    }

    
    abstract V setValue(int param1Int, V param1V);
  }

  
  public ImmutableList<R> rowKeyList() {
    return this.rowList;
  }




  
  public ImmutableList<C> columnKeyList() {
    return this.columnList;
  }













  
  public V at(int rowIndex, int columnIndex) {
    Preconditions.checkElementIndex(rowIndex, this.rowList.size());
    Preconditions.checkElementIndex(columnIndex, this.columnList.size());
    return this.array[rowIndex][columnIndex];
  }














  
  @CanIgnoreReturnValue
  public V set(int rowIndex, int columnIndex, V value) {
    Preconditions.checkElementIndex(rowIndex, this.rowList.size());
    Preconditions.checkElementIndex(columnIndex, this.columnList.size());
    V oldValue = this.array[rowIndex][columnIndex];
    this.array[rowIndex][columnIndex] = value;
    return oldValue;
  }










  
  @GwtIncompatible
  public V[][] toArray(Class<V> valueClass) {
    V[][] copy = (V[][])Array.newInstance(valueClass, new int[] { this.rowList.size(), this.columnList.size() });
    for (int i = 0; i < this.rowList.size(); i++) {
      System.arraycopy(this.array[i], 0, copy[i], 0, (this.array[i]).length);
    }
    return copy;
  }







  
  @Deprecated
  public void clear() {
    throw new UnsupportedOperationException();
  }

  
  public void eraseAll() {
    for (V[] row : this.array) {
      Arrays.fill((Object[])row, (Object)null);
    }
  }





  
  public boolean contains(Object rowKey, Object columnKey) {
    return (containsRow(rowKey) && containsColumn(columnKey));
  }





  
  public boolean containsColumn(Object columnKey) {
    return this.columnKeyToIndex.containsKey(columnKey);
  }





  
  public boolean containsRow(Object rowKey) {
    return this.rowKeyToIndex.containsKey(rowKey);
  }

  
  public boolean containsValue(Object value) {
    for (V[] row : this.array) {
      for (V element : row) {
        if (Objects.equal(value, element)) {
          return true;
        }
      } 
    } 
    return false;
  }

  
  public V get(Object rowKey, Object columnKey) {
    Integer rowIndex = this.rowKeyToIndex.get(rowKey);
    Integer columnIndex = this.columnKeyToIndex.get(columnKey);
    return (rowIndex == null || columnIndex == null) ? null : at(rowIndex.intValue(), columnIndex.intValue());
  }




  
  public boolean isEmpty() {
    return (this.rowList.isEmpty() || this.columnList.isEmpty());
  }







  
  @CanIgnoreReturnValue
  public V put(R rowKey, C columnKey, V value) {
    Preconditions.checkNotNull(rowKey);
    Preconditions.checkNotNull(columnKey);
    Integer rowIndex = this.rowKeyToIndex.get(rowKey);
    Preconditions.checkArgument((rowIndex != null), "Row %s not in %s", rowKey, this.rowList);
    Integer columnIndex = this.columnKeyToIndex.get(columnKey);
    Preconditions.checkArgument((columnIndex != null), "Column %s not in %s", columnKey, this.columnList);
    return set(rowIndex.intValue(), columnIndex.intValue(), value);
  }
















  
  public void putAll(Table<? extends R, ? extends C, ? extends V> table) {
    super.putAll(table);
  }







  
  @Deprecated
  @CanIgnoreReturnValue
  public V remove(Object rowKey, Object columnKey) {
    throw new UnsupportedOperationException();
  }













  
  @CanIgnoreReturnValue
  public V erase(Object rowKey, Object columnKey) {
    Integer rowIndex = this.rowKeyToIndex.get(rowKey);
    Integer columnIndex = this.columnKeyToIndex.get(columnKey);
    if (rowIndex == null || columnIndex == null) {
      return null;
    }
    return set(rowIndex.intValue(), columnIndex.intValue(), null);
  }



  
  public int size() {
    return this.rowList.size() * this.columnList.size();
  }












  
  public Set<Table.Cell<R, C, V>> cellSet() {
    return super.cellSet();
  }

  
  Iterator<Table.Cell<R, C, V>> cellIterator() {
    return new AbstractIndexedListIterator<Table.Cell<R, C, V>>(size())
      {
        protected Table.Cell<R, C, V> get(int index) {
          return ArrayTable.this.getCell(index);
        }
      };
  }

  
  Spliterator<Table.Cell<R, C, V>> cellSpliterator() {
    return CollectSpliterators.indexed(
        size(), 273, this::getCell);
  }
  
  private Table.Cell<R, C, V> getCell(final int index) {
    return new Tables.AbstractCell<R, C, V>() {
        final int rowIndex = index / ArrayTable.this.columnList.size();
        final int columnIndex = index % ArrayTable.this.columnList.size();

        
        public R getRowKey() {
          return (R)ArrayTable.this.rowList.get(this.rowIndex);
        }

        
        public C getColumnKey() {
          return (C)ArrayTable.this.columnList.get(this.columnIndex);
        }

        
        public V getValue() {
          return (V)ArrayTable.this.at(this.rowIndex, this.columnIndex);
        }
      };
  }
  
  private V getValue(int index) {
    int rowIndex = index / this.columnList.size();
    int columnIndex = index % this.columnList.size();
    return at(rowIndex, columnIndex);
  }












  
  public Map<R, V> column(C columnKey) {
    Preconditions.checkNotNull(columnKey);
    Integer columnIndex = this.columnKeyToIndex.get(columnKey);
    return (columnIndex == null) ? ImmutableMap.<R, V>of() : new Column(columnIndex.intValue());
  }
  
  private class Column extends ArrayMap<R, V> {
    final int columnIndex;
    
    Column(int columnIndex) {
      super(ArrayTable.this.rowKeyToIndex);
      this.columnIndex = columnIndex;
    }

    
    String getKeyRole() {
      return "Row";
    }

    
    V getValue(int index) {
      return (V)ArrayTable.this.at(index, this.columnIndex);
    }

    
    V setValue(int index, V newValue) {
      return (V)ArrayTable.this.set(index, this.columnIndex, newValue);
    }
  }







  
  public ImmutableSet<C> columnKeySet() {
    return this.columnKeyToIndex.keySet();
  }



  
  public Map<C, Map<R, V>> columnMap() {
    ColumnMap map = this.columnMap;
    return (map == null) ? (this.columnMap = new ColumnMap()) : map;
  }
  
  private class ColumnMap
    extends ArrayMap<C, Map<R, V>> {
    private ColumnMap() {
      super(ArrayTable.this.columnKeyToIndex);
    }

    
    String getKeyRole() {
      return "Column";
    }

    
    Map<R, V> getValue(int index) {
      return new ArrayTable.Column(index);
    }

    
    Map<R, V> setValue(int index, Map<R, V> newValue) {
      throw new UnsupportedOperationException();
    }

    
    public Map<R, V> put(C key, Map<R, V> value) {
      throw new UnsupportedOperationException();
    }
  }












  
  public Map<C, V> row(R rowKey) {
    Preconditions.checkNotNull(rowKey);
    Integer rowIndex = this.rowKeyToIndex.get(rowKey);
    return (rowIndex == null) ? ImmutableMap.<C, V>of() : new Row(rowIndex.intValue());
  }
  
  private class Row extends ArrayMap<C, V> {
    final int rowIndex;
    
    Row(int rowIndex) {
      super(ArrayTable.this.columnKeyToIndex);
      this.rowIndex = rowIndex;
    }

    
    String getKeyRole() {
      return "Column";
    }

    
    V getValue(int index) {
      return (V)ArrayTable.this.at(this.rowIndex, index);
    }

    
    V setValue(int index, V newValue) {
      return (V)ArrayTable.this.set(this.rowIndex, index, newValue);
    }
  }







  
  public ImmutableSet<R> rowKeySet() {
    return this.rowKeyToIndex.keySet();
  }



  
  public Map<R, Map<C, V>> rowMap() {
    RowMap map = this.rowMap;
    return (map == null) ? (this.rowMap = new RowMap()) : map;
  }
  
  private class RowMap
    extends ArrayMap<R, Map<C, V>> {
    private RowMap() {
      super(ArrayTable.this.rowKeyToIndex);
    }

    
    String getKeyRole() {
      return "Row";
    }

    
    Map<C, V> getValue(int index) {
      return new ArrayTable.Row(index);
    }

    
    Map<C, V> setValue(int index, Map<C, V> newValue) {
      throw new UnsupportedOperationException();
    }

    
    public Map<C, V> put(R key, Map<C, V> value) {
      throw new UnsupportedOperationException();
    }
  }










  
  public Collection<V> values() {
    return super.values();
  }

  
  Iterator<V> valuesIterator() {
    return new AbstractIndexedListIterator<V>(size())
      {
        protected V get(int index) {
          return ArrayTable.this.getValue(index);
        }
      };
  }

  
  Spliterator<V> valuesSpliterator() {
    return CollectSpliterators.indexed(size(), 16, this::getValue);
  }
}
