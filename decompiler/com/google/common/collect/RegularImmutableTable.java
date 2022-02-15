package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


























@GwtCompatible
abstract class RegularImmutableTable<R, C, V>
  extends ImmutableTable<R, C, V>
{
  final ImmutableSet<Table.Cell<R, C, V>> createCellSet() {
    return isEmpty() ? ImmutableSet.<Table.Cell<R, C, V>>of() : new CellSet();
  }
  
  private final class CellSet extends IndexedImmutableSet<Table.Cell<R, C, V>> {
    private CellSet() {}
    
    public int size() {
      return RegularImmutableTable.this.size();
    }

    
    Table.Cell<R, C, V> get(int index) {
      return RegularImmutableTable.this.getCell(index);
    }

    
    public boolean contains(Object object) {
      if (object instanceof Table.Cell) {
        Table.Cell<?, ?, ?> cell = (Table.Cell<?, ?, ?>)object;
        Object value = RegularImmutableTable.this.get(cell.getRowKey(), cell.getColumnKey());
        return (value != null && value.equals(cell.getValue()));
      } 
      return false;
    }

    
    boolean isPartialView() {
      return false;
    }
  }



  
  final ImmutableCollection<V> createValues() {
    return isEmpty() ? ImmutableList.<V>of() : new Values();
  }
  
  private final class Values extends ImmutableList<V> {
    private Values() {}
    
    public int size() {
      return RegularImmutableTable.this.size();
    }

    
    public V get(int index) {
      return (V)RegularImmutableTable.this.getValue(index);
    }

    
    boolean isPartialView() {
      return true;
    }
  }



  
  static <R, C, V> RegularImmutableTable<R, C, V> forCells(List<Table.Cell<R, C, V>> cells, final Comparator<? super R> rowComparator, final Comparator<? super C> columnComparator) {
    Preconditions.checkNotNull(cells);
    if (rowComparator != null || columnComparator != null) {






      
      Comparator<Table.Cell<R, C, V>> comparator = new Comparator<Table.Cell<R, C, V>>()
        {


          
          public int compare(Table.Cell<R, C, V> cell1, Table.Cell<R, C, V> cell2)
          {
            int rowCompare = (rowComparator == null) ? 0 : rowComparator.compare(cell1.getRowKey(), cell2.getRowKey());
            if (rowCompare != 0) {
              return rowCompare;
            }
            return (columnComparator == null) ? 0 : columnComparator
              
              .compare(cell1.getColumnKey(), cell2.getColumnKey());
          }
        };
      Collections.sort(cells, comparator);
    } 
    return forCellsInternal(cells, rowComparator, columnComparator);
  }
  
  static <R, C, V> RegularImmutableTable<R, C, V> forCells(Iterable<Table.Cell<R, C, V>> cells) {
    return forCellsInternal(cells, (Comparator<? super R>)null, (Comparator<? super C>)null);
  }



  
  private static <R, C, V> RegularImmutableTable<R, C, V> forCellsInternal(Iterable<Table.Cell<R, C, V>> cells, Comparator<? super R> rowComparator, Comparator<? super C> columnComparator) {
    Set<R> rowSpaceBuilder = new LinkedHashSet<>();
    Set<C> columnSpaceBuilder = new LinkedHashSet<>();
    ImmutableList<Table.Cell<R, C, V>> cellList = ImmutableList.copyOf(cells);
    for (Table.Cell<R, C, V> cell : cells) {
      rowSpaceBuilder.add(cell.getRowKey());
      columnSpaceBuilder.add(cell.getColumnKey());
    } 



    
    ImmutableSet<R> rowSpace = (rowComparator == null) ? ImmutableSet.<R>copyOf(rowSpaceBuilder) : ImmutableSet.<R>copyOf(ImmutableList.sortedCopyOf(rowComparator, rowSpaceBuilder));


    
    ImmutableSet<C> columnSpace = (columnComparator == null) ? ImmutableSet.<C>copyOf(columnSpaceBuilder) : ImmutableSet.<C>copyOf(ImmutableList.sortedCopyOf(columnComparator, columnSpaceBuilder));
    
    return forOrderedComponents(cellList, rowSpace, columnSpace);
  }






  
  static <R, C, V> RegularImmutableTable<R, C, V> forOrderedComponents(ImmutableList<Table.Cell<R, C, V>> cellList, ImmutableSet<R> rowSpace, ImmutableSet<C> columnSpace) {
    return (cellList.size() > rowSpace.size() * columnSpace.size() / 2L) ? new DenseImmutableTable<>(cellList, rowSpace, columnSpace) : new SparseImmutableTable<>(cellList, rowSpace, columnSpace);
  }







  
  final void checkNoDuplicate(R rowKey, C columnKey, V existingValue, V newValue) {
    Preconditions.checkArgument((existingValue == null), "Duplicate key: (row=%s, column=%s), values: [%s, %s].", rowKey, columnKey, newValue, existingValue);
  }
  
  abstract Table.Cell<R, C, V> getCell(int paramInt);
  
  abstract V getValue(int paramInt);
}
