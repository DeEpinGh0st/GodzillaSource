package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;









































@GwtCompatible
public abstract class ImmutableTable<R, C, V>
  extends AbstractTable<R, C, V>
  implements Serializable
{
  public static <T, R, C, V> Collector<T, ?, ImmutableTable<R, C, V>> toImmutableTable(Function<? super T, ? extends R> rowFunction, Function<? super T, ? extends C> columnFunction, Function<? super T, ? extends V> valueFunction) {
    Preconditions.checkNotNull(rowFunction, "rowFunction");
    Preconditions.checkNotNull(columnFunction, "columnFunction");
    Preconditions.checkNotNull(valueFunction, "valueFunction");
    return Collector.of(() -> new Builder<>(), (builder, t) -> builder.put(rowFunction.apply(t), columnFunction.apply(t), valueFunction.apply(t)), (b1, b2) -> b1.combine(b2), b -> b.build(), new Collector.Characteristics[0]);
  }





















  
  public static <T, R, C, V> Collector<T, ?, ImmutableTable<R, C, V>> toImmutableTable(Function<? super T, ? extends R> rowFunction, Function<? super T, ? extends C> columnFunction, Function<? super T, ? extends V> valueFunction, BinaryOperator<V> mergeFunction) {
    Preconditions.checkNotNull(rowFunction, "rowFunction");
    Preconditions.checkNotNull(columnFunction, "columnFunction");
    Preconditions.checkNotNull(valueFunction, "valueFunction");
    Preconditions.checkNotNull(mergeFunction, "mergeFunction");






    
    return Collector.of(() -> new CollectorState<>(), (state, input) -> state.put(rowFunction.apply(input), columnFunction.apply(input), valueFunction.apply(input), mergeFunction), (s1, s2) -> s1.combine(s2, mergeFunction), state -> state.toTable(), new Collector.Characteristics[0]);
  }









  
  private static final class CollectorState<R, C, V>
  {
    final List<ImmutableTable.MutableCell<R, C, V>> insertionOrder = new ArrayList<>();
    final Table<R, C, ImmutableTable.MutableCell<R, C, V>> table = HashBasedTable.create();
    
    void put(R row, C column, V value, BinaryOperator<V> merger) {
      ImmutableTable.MutableCell<R, C, V> oldCell = this.table.get(row, column);
      if (oldCell == null) {
        ImmutableTable.MutableCell<R, C, V> cell = new ImmutableTable.MutableCell<>(row, column, value);
        this.insertionOrder.add(cell);
        this.table.put(row, column, cell);
      } else {
        oldCell.merge(value, merger);
      } 
    }
    
    CollectorState<R, C, V> combine(CollectorState<R, C, V> other, BinaryOperator<V> merger) {
      for (ImmutableTable.MutableCell<R, C, V> cell : other.insertionOrder) {
        put(cell.getRowKey(), cell.getColumnKey(), cell.getValue(), merger);
      }
      return this;
    }
    
    ImmutableTable<R, C, V> toTable() {
      return ImmutableTable.copyOf((Iterable)this.insertionOrder);
    }
    
    private CollectorState() {}
  }
  
  private static final class MutableCell<R, C, V> extends Tables.AbstractCell<R, C, V> {
    private final R row;
    
    MutableCell(R row, C column, V value) {
      this.row = (R)Preconditions.checkNotNull(row, "row");
      this.column = (C)Preconditions.checkNotNull(column, "column");
      this.value = (V)Preconditions.checkNotNull(value, "value");
    }
    private final C column;
    
    public R getRowKey() {
      return this.row;
    }
    private V value;
    
    public C getColumnKey() {
      return this.column;
    }

    
    public V getValue() {
      return this.value;
    }
    
    void merge(V value, BinaryOperator<V> mergeFunction) {
      Preconditions.checkNotNull(value, "value");
      this.value = (V)Preconditions.checkNotNull(mergeFunction.apply(this.value, value), "mergeFunction.apply");
    }
  }


  
  public static <R, C, V> ImmutableTable<R, C, V> of() {
    return (ImmutableTable)SparseImmutableTable.EMPTY;
  }

  
  public static <R, C, V> ImmutableTable<R, C, V> of(R rowKey, C columnKey, V value) {
    return new SingletonImmutableTable<>(rowKey, columnKey, value);
  }














  
  public static <R, C, V> ImmutableTable<R, C, V> copyOf(Table<? extends R, ? extends C, ? extends V> table) {
    if (table instanceof ImmutableTable) {
      
      ImmutableTable<R, C, V> parameterizedTable = (ImmutableTable)table;
      return parameterizedTable;
    } 
    return copyOf(table.cellSet());
  }


  
  private static <R, C, V> ImmutableTable<R, C, V> copyOf(Iterable<? extends Table.Cell<? extends R, ? extends C, ? extends V>> cells) {
    Builder<R, C, V> builder = builder();
    for (Table.Cell<? extends R, ? extends C, ? extends V> cell : cells) {
      builder.put(cell);
    }
    return builder.build();
  }




  
  public static <R, C, V> Builder<R, C, V> builder() {
    return new Builder<>();
  }




  
  static <R, C, V> Table.Cell<R, C, V> cellOf(R rowKey, C columnKey, V value) {
    return Tables.immutableCell(
        (R)Preconditions.checkNotNull(rowKey, "rowKey"), 
        (C)Preconditions.checkNotNull(columnKey, "columnKey"), 
        (V)Preconditions.checkNotNull(value, "value"));
  }

























  
  public static final class Builder<R, C, V>
  {
    private final List<Table.Cell<R, C, V>> cells = Lists.newArrayList();


    
    private Comparator<? super R> rowComparator;

    
    private Comparator<? super C> columnComparator;


    
    @CanIgnoreReturnValue
    public Builder<R, C, V> orderRowsBy(Comparator<? super R> rowComparator) {
      this.rowComparator = (Comparator<? super R>)Preconditions.checkNotNull(rowComparator, "rowComparator");
      return this;
    }

    
    @CanIgnoreReturnValue
    public Builder<R, C, V> orderColumnsBy(Comparator<? super C> columnComparator) {
      this.columnComparator = (Comparator<? super C>)Preconditions.checkNotNull(columnComparator, "columnComparator");
      return this;
    }




    
    @CanIgnoreReturnValue
    public Builder<R, C, V> put(R rowKey, C columnKey, V value) {
      this.cells.add(ImmutableTable.cellOf(rowKey, columnKey, value));
      return this;
    }




    
    @CanIgnoreReturnValue
    public Builder<R, C, V> put(Table.Cell<? extends R, ? extends C, ? extends V> cell) {
      if (cell instanceof Tables.ImmutableCell) {
        Preconditions.checkNotNull(cell.getRowKey(), "row");
        Preconditions.checkNotNull(cell.getColumnKey(), "column");
        Preconditions.checkNotNull(cell.getValue(), "value");
        
        Table.Cell<? extends R, ? extends C, ? extends V> cell1 = cell;
        this.cells.add(cell1);
      } else {
        put(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
      } 
      return this;
    }






    
    @CanIgnoreReturnValue
    public Builder<R, C, V> putAll(Table<? extends R, ? extends C, ? extends V> table) {
      for (Table.Cell<? extends R, ? extends C, ? extends V> cell : table.cellSet()) {
        put(cell);
      }
      return this;
    }
    
    Builder<R, C, V> combine(Builder<R, C, V> other) {
      this.cells.addAll(other.cells);
      return this;
    }





    
    public ImmutableTable<R, C, V> build() {
      int size = this.cells.size();
      switch (size) {
        case 0:
          return ImmutableTable.of();
        case 1:
          return new SingletonImmutableTable<>(Iterables.<Table.Cell<R, C, V>>getOnlyElement(this.cells));
      } 
      return RegularImmutableTable.forCells(this.cells, this.rowComparator, this.columnComparator);
    }
  }




  
  public ImmutableSet<Table.Cell<R, C, V>> cellSet() {
    return (ImmutableSet<Table.Cell<R, C, V>>)super.cellSet();
  }




  
  final UnmodifiableIterator<Table.Cell<R, C, V>> cellIterator() {
    throw new AssertionError("should never be called");
  }

  
  final Spliterator<Table.Cell<R, C, V>> cellSpliterator() {
    throw new AssertionError("should never be called");
  }

  
  public ImmutableCollection<V> values() {
    return (ImmutableCollection<V>)super.values();
  }




  
  final Iterator<V> valuesIterator() {
    throw new AssertionError("should never be called");
  }






  
  public ImmutableMap<R, V> column(C columnKey) {
    Preconditions.checkNotNull(columnKey, "columnKey");
    return (ImmutableMap<R, V>)MoreObjects.firstNonNull(
        columnMap().get(columnKey), ImmutableMap.of());
  }

  
  public ImmutableSet<C> columnKeySet() {
    return columnMap().keySet();
  }















  
  public ImmutableMap<C, V> row(R rowKey) {
    Preconditions.checkNotNull(rowKey, "rowKey");
    return (ImmutableMap<C, V>)MoreObjects.firstNonNull(
        rowMap().get(rowKey), ImmutableMap.of());
  }

  
  public ImmutableSet<R> rowKeySet() {
    return rowMap().keySet();
  }










  
  public boolean contains(Object rowKey, Object columnKey) {
    return (get(rowKey, columnKey) != null);
  }

  
  public boolean containsValue(Object value) {
    return values().contains(value);
  }







  
  @Deprecated
  public final void clear() {
    throw new UnsupportedOperationException();
  }







  
  @Deprecated
  @CanIgnoreReturnValue
  public final V put(R rowKey, C columnKey, V value) {
    throw new UnsupportedOperationException();
  }







  
  @Deprecated
  public final void putAll(Table<? extends R, ? extends C, ? extends V> table) {
    throw new UnsupportedOperationException();
  }







  
  @Deprecated
  @CanIgnoreReturnValue
  public final V remove(Object rowKey, Object columnKey) {
    throw new UnsupportedOperationException();
  }


  
  static final class SerializedForm
    implements Serializable
  {
    private final Object[] rowKeys;

    
    private final Object[] columnKeys;

    
    private final Object[] cellValues;
    
    private final int[] cellRowIndices;
    
    private final int[] cellColumnIndices;
    
    private static final long serialVersionUID = 0L;

    
    private SerializedForm(Object[] rowKeys, Object[] columnKeys, Object[] cellValues, int[] cellRowIndices, int[] cellColumnIndices) {
      this.rowKeys = rowKeys;
      this.columnKeys = columnKeys;
      this.cellValues = cellValues;
      this.cellRowIndices = cellRowIndices;
      this.cellColumnIndices = cellColumnIndices;
    }

    
    static SerializedForm create(ImmutableTable<?, ?, ?> table, int[] cellRowIndices, int[] cellColumnIndices) {
      return new SerializedForm(table
          .rowKeySet().toArray(), table
          .columnKeySet().toArray(), table
          .values().toArray(), cellRowIndices, cellColumnIndices);
    }


    
    Object readResolve() {
      if (this.cellValues.length == 0) {
        return ImmutableTable.of();
      }
      if (this.cellValues.length == 1) {
        return ImmutableTable.of(this.rowKeys[0], this.columnKeys[0], this.cellValues[0]);
      }
      ImmutableList.Builder<Table.Cell<Object, Object, Object>> cellListBuilder = new ImmutableList.Builder<>(this.cellValues.length);
      
      for (int i = 0; i < this.cellValues.length; i++) {
        cellListBuilder.add(
            ImmutableTable.cellOf(this.rowKeys[this.cellRowIndices[i]], this.columnKeys[this.cellColumnIndices[i]], this.cellValues[i]));
      }
      return RegularImmutableTable.forOrderedComponents(cellListBuilder
          .build(), ImmutableSet.copyOf(this.rowKeys), ImmutableSet.copyOf(this.columnKeys));
    }
  }


  
  final Object writeReplace() {
    return createSerializedForm();
  }
  
  abstract ImmutableSet<Table.Cell<R, C, V>> createCellSet();
  
  abstract ImmutableCollection<V> createValues();
  
  public abstract ImmutableMap<C, Map<R, V>> columnMap();
  
  public abstract ImmutableMap<R, Map<C, V>> rowMap();
  
  abstract SerializedForm createSerializedForm();
}
