package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.Immutable;
import java.util.LinkedHashMap;
import java.util.Map;















@Immutable(containerOf = {"R", "C", "V"})
@GwtCompatible
final class SparseImmutableTable<R, C, V>
  extends RegularImmutableTable<R, C, V>
{
  static final ImmutableTable<Object, Object, Object> EMPTY = new SparseImmutableTable(
      
      ImmutableList.of(), ImmutableSet.of(), ImmutableSet.of());


  
  private final ImmutableMap<R, ImmutableMap<C, V>> rowMap;

  
  private final ImmutableMap<C, ImmutableMap<R, V>> columnMap;

  
  private final int[] cellRowIndices;

  
  private final int[] cellColumnInRowIndices;


  
  SparseImmutableTable(ImmutableList<Table.Cell<R, C, V>> cellList, ImmutableSet<R> rowSpace, ImmutableSet<C> columnSpace) {
    Map<R, Integer> rowIndex = Maps.indexMap(rowSpace);
    Map<R, Map<C, V>> rows = Maps.newLinkedHashMap();
    for (UnmodifiableIterator<R> unmodifiableIterator = rowSpace.iterator(); unmodifiableIterator.hasNext(); ) { R row = unmodifiableIterator.next();
      rows.put(row, new LinkedHashMap<>()); }
    
    Map<C, Map<R, V>> columns = Maps.newLinkedHashMap();
    for (UnmodifiableIterator<C> unmodifiableIterator1 = columnSpace.iterator(); unmodifiableIterator1.hasNext(); ) { C col = unmodifiableIterator1.next();
      columns.put(col, new LinkedHashMap<>()); }
    
    int[] cellRowIndices = new int[cellList.size()];
    int[] cellColumnInRowIndices = new int[cellList.size()];
    for (int i = 0; i < cellList.size(); i++) {
      Table.Cell<R, C, V> cell = cellList.get(i);
      R rowKey = cell.getRowKey();
      C columnKey = cell.getColumnKey();
      V value = cell.getValue();
      
      cellRowIndices[i] = ((Integer)rowIndex.get(rowKey)).intValue();
      Map<C, V> thisRow = rows.get(rowKey);
      cellColumnInRowIndices[i] = thisRow.size();
      V oldValue = thisRow.put(columnKey, value);
      checkNoDuplicate(rowKey, columnKey, oldValue, value);
      ((Map<R, V>)columns.get(columnKey)).put(rowKey, value);
    } 
    this.cellRowIndices = cellRowIndices;
    this.cellColumnInRowIndices = cellColumnInRowIndices;
    
    ImmutableMap.Builder<R, ImmutableMap<C, V>> rowBuilder = new ImmutableMap.Builder<>(rows.size());
    for (Map.Entry<R, Map<C, V>> row : rows.entrySet()) {
      rowBuilder.put(row.getKey(), ImmutableMap.copyOf(row.getValue()));
    }
    this.rowMap = rowBuilder.build();

    
    ImmutableMap.Builder<C, ImmutableMap<R, V>> columnBuilder = new ImmutableMap.Builder<>(columns.size());
    for (Map.Entry<C, Map<R, V>> col : columns.entrySet()) {
      columnBuilder.put(col.getKey(), ImmutableMap.copyOf(col.getValue()));
    }
    this.columnMap = columnBuilder.build();
  }


  
  public ImmutableMap<C, Map<R, V>> columnMap() {
    ImmutableMap<C, ImmutableMap<R, V>> columnMap = this.columnMap;
    return ImmutableMap.copyOf((Map)columnMap);
  }


  
  public ImmutableMap<R, Map<C, V>> rowMap() {
    ImmutableMap<R, ImmutableMap<C, V>> rowMap = this.rowMap;
    return ImmutableMap.copyOf((Map)rowMap);
  }

  
  public int size() {
    return this.cellRowIndices.length;
  }

  
  Table.Cell<R, C, V> getCell(int index) {
    int rowIndex = this.cellRowIndices[index];
    Map.Entry<R, ImmutableMap<C, V>> rowEntry = this.rowMap.entrySet().asList().get(rowIndex);
    ImmutableMap<C, V> row = rowEntry.getValue();
    int columnIndex = this.cellColumnInRowIndices[index];
    Map.Entry<C, V> colEntry = row.entrySet().asList().get(columnIndex);
    return cellOf(rowEntry.getKey(), colEntry.getKey(), colEntry.getValue());
  }

  
  V getValue(int index) {
    int rowIndex = this.cellRowIndices[index];
    ImmutableMap<C, V> row = this.rowMap.values().asList().get(rowIndex);
    int columnIndex = this.cellColumnInRowIndices[index];
    return row.values().asList().get(columnIndex);
  }

  
  ImmutableTable.SerializedForm createSerializedForm() {
    Map<C, Integer> columnKeyToIndex = Maps.indexMap(columnKeySet());
    int[] cellColumnIndices = new int[cellSet().size()];
    int i = 0;
    for (UnmodifiableIterator<Table.Cell<R, C, V>> unmodifiableIterator = cellSet().iterator(); unmodifiableIterator.hasNext(); ) { Table.Cell<R, C, V> cell = unmodifiableIterator.next();
      cellColumnIndices[i++] = ((Integer)columnKeyToIndex.get(cell.getColumnKey())).intValue(); }
    
    return ImmutableTable.SerializedForm.create(this, this.cellRowIndices, cellColumnIndices);
  }
}
