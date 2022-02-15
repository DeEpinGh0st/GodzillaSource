package com.jgoodies.forms.layout;

import com.jgoodies.common.base.Objects;
import com.jgoodies.common.base.Preconditions;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JComponent;













































































































































































































public final class FormLayout
  implements LayoutManager2, Serializable
{
  private final List<ColumnSpec> colSpecs;
  private final List<RowSpec> rowSpecs;
  private int[][] colGroupIndices;
  private int[][] rowGroupIndices;
  private final Map<Component, CellConstraints> constraintMap;
  private boolean honorsVisibility = true;
  private transient List<Component>[] colComponents;
  private transient List<Component>[] rowComponents;
  private final ComponentSizeCache componentSizeCache;
  private final Measure minimumWidthMeasure;
  private final Measure minimumHeightMeasure;
  private final Measure preferredWidthMeasure;
  private final Measure preferredHeightMeasure;
  
  public FormLayout() {
    this(new ColumnSpec[0], new RowSpec[0]);
  }






























  
  public FormLayout(String encodedColumnSpecs) {
    this(encodedColumnSpecs, LayoutMap.getRoot());
  }




































  
  public FormLayout(String encodedColumnSpecs, LayoutMap layoutMap) {
    this(ColumnSpec.decodeSpecs(encodedColumnSpecs, layoutMap), new RowSpec[0]);
  }

































  
  public FormLayout(String encodedColumnSpecs, String encodedRowSpecs) {
    this(encodedColumnSpecs, encodedRowSpecs, LayoutMap.getRoot());
  }







































  
  public FormLayout(String encodedColumnSpecs, String encodedRowSpecs, LayoutMap layoutMap) {
    this(ColumnSpec.decodeSpecs(encodedColumnSpecs, layoutMap), RowSpec.decodeSpecs(encodedRowSpecs, layoutMap));
  }












  
  public FormLayout(ColumnSpec[] colSpecs) {
    this(colSpecs, new RowSpec[0]);
  }









  
  public FormLayout(ColumnSpec[] colSpecs, RowSpec[] rowSpecs) {
    Preconditions.checkNotNull(colSpecs, "The column specifications must not be null.");
    Preconditions.checkNotNull(rowSpecs, "The row specifications must not be null.");
    this.colSpecs = new ArrayList<ColumnSpec>(Arrays.asList(colSpecs));
    this.rowSpecs = new ArrayList<RowSpec>(Arrays.asList(rowSpecs));
    this.colGroupIndices = new int[0][];
    this.rowGroupIndices = new int[0][];
    int initialCapacity = colSpecs.length * rowSpecs.length / 4;
    this.constraintMap = new HashMap<Component, CellConstraints>(initialCapacity);
    this.componentSizeCache = new ComponentSizeCache(initialCapacity);
    this.minimumWidthMeasure = new MinimumWidthMeasure(this.componentSizeCache);
    this.minimumHeightMeasure = new MinimumHeightMeasure(this.componentSizeCache);
    this.preferredWidthMeasure = new PreferredWidthMeasure(this.componentSizeCache);
    this.preferredHeightMeasure = new PreferredHeightMeasure(this.componentSizeCache);
  }








  
  public int getColumnCount() {
    return this.colSpecs.size();
  }








  
  public ColumnSpec getColumnSpec(int columnIndex) {
    return this.colSpecs.get(columnIndex - 1);
  }









  
  public void setColumnSpec(int columnIndex, ColumnSpec columnSpec) {
    Preconditions.checkNotNull(columnSpec, "The column spec must not be null.");
    this.colSpecs.set(columnIndex - 1, columnSpec);
  }








  
  public void appendColumn(ColumnSpec columnSpec) {
    Preconditions.checkNotNull(columnSpec, "The column spec must not be null.");
    this.colSpecs.add(columnSpec);
  }

















  
  public void insertColumn(int columnIndex, ColumnSpec columnSpec) {
    if (columnIndex < 1 || columnIndex > getColumnCount()) {
      throw new IndexOutOfBoundsException("The column index " + columnIndex + "must be in the range [1, " + getColumnCount() + "].");
    }

    
    this.colSpecs.add(columnIndex - 1, columnSpec);
    shiftComponentsHorizontally(columnIndex, false);
    adjustGroupIndices(this.colGroupIndices, columnIndex, false);
  }


































  
  public void removeColumn(int columnIndex) {
    if (columnIndex < 1 || columnIndex > getColumnCount()) {
      throw new IndexOutOfBoundsException("The column index " + columnIndex + " must be in the range [1, " + getColumnCount() + "].");
    }

    
    this.colSpecs.remove(columnIndex - 1);
    shiftComponentsHorizontally(columnIndex, true);
    adjustGroupIndices(this.colGroupIndices, columnIndex, true);
  }






  
  public int getRowCount() {
    return this.rowSpecs.size();
  }








  
  public RowSpec getRowSpec(int rowIndex) {
    return this.rowSpecs.get(rowIndex - 1);
  }









  
  public void setRowSpec(int rowIndex, RowSpec rowSpec) {
    Preconditions.checkNotNull(rowSpec, "The row spec must not be null.");
    this.rowSpecs.set(rowIndex - 1, rowSpec);
  }







  
  public void appendRow(RowSpec rowSpec) {
    Preconditions.checkNotNull(rowSpec, "The row spec must not be null.");
    this.rowSpecs.add(rowSpec);
  }
















  
  public void insertRow(int rowIndex, RowSpec rowSpec) {
    if (rowIndex < 1 || rowIndex > getRowCount()) {
      throw new IndexOutOfBoundsException("The row index " + rowIndex + " must be in the range [1, " + getRowCount() + "].");
    }

    
    this.rowSpecs.add(rowIndex - 1, rowSpec);
    shiftComponentsVertically(rowIndex, false);
    adjustGroupIndices(this.rowGroupIndices, rowIndex, false);
  }































  
  public void removeRow(int rowIndex) {
    if (rowIndex < 1 || rowIndex > getRowCount()) {
      throw new IndexOutOfBoundsException("The row index " + rowIndex + "must be in the range [1, " + getRowCount() + "].");
    }

    
    this.rowSpecs.remove(rowIndex - 1);
    shiftComponentsVertically(rowIndex, true);
    adjustGroupIndices(this.rowGroupIndices, rowIndex, true);
  }









  
  private void shiftComponentsHorizontally(int columnIndex, boolean remove) {
    int offset = remove ? -1 : 1;
    for (Map.Entry<Component, CellConstraints> element : this.constraintMap.entrySet()) {
      Map.Entry entry = element;
      CellConstraints constraints = (CellConstraints)entry.getValue();
      int x1 = constraints.gridX;
      int w = constraints.gridWidth;
      int x2 = x1 + w - 1;
      if (x1 == columnIndex && remove) {
        throw new IllegalStateException("The removed column " + columnIndex + " must not contain component origins.\n" + "Illegal component=" + entry.getKey());
      }

      
      if (x1 >= columnIndex) {
        constraints.gridX += offset; continue;
      }  if (x2 >= columnIndex) {
        constraints.gridWidth += offset;
      }
    } 
  }








  
  private void shiftComponentsVertically(int rowIndex, boolean remove) {
    int offset = remove ? -1 : 1;
    for (Map.Entry<Component, CellConstraints> element : this.constraintMap.entrySet()) {
      Map.Entry entry = element;
      CellConstraints constraints = (CellConstraints)entry.getValue();
      int y1 = constraints.gridY;
      int h = constraints.gridHeight;
      int y2 = y1 + h - 1;
      if (y1 == rowIndex && remove) {
        throw new IllegalStateException("The removed row " + rowIndex + " must not contain component origins.\n" + "Illegal component=" + entry.getKey());
      }

      
      if (y1 >= rowIndex) {
        constraints.gridY += offset; continue;
      }  if (y2 >= rowIndex) {
        constraints.gridHeight += offset;
      }
    } 
  }










  
  private static void adjustGroupIndices(int[][] allGroupIndices, int modifiedIndex, boolean remove) {
    int offset = remove ? -1 : 1;
    for (int[] allGroupIndice : allGroupIndices) {
      int[] groupIndices = allGroupIndice;
      for (int i = 0; i < groupIndices.length; i++) {
        int index = groupIndices[i];
        if (index == modifiedIndex && remove) {
          throw new IllegalStateException("The removed index " + modifiedIndex + " must not be grouped.");
        }
        if (index >= modifiedIndex) {
          groupIndices[i] = groupIndices[i] + offset;
        }
      } 
    } 
  }













  
  public CellConstraints getConstraints(Component component) {
    return (CellConstraints)getConstraints0(component).clone();
  }

  
  private CellConstraints getConstraints0(Component component) {
    Preconditions.checkNotNull(component, "The component must not be null.");
    CellConstraints constraints = this.constraintMap.get(component);
    Preconditions.checkState((constraints != null), "The component has not been added to the container.");
    return constraints;
  }









  
  public void setConstraints(Component component, CellConstraints constraints) {
    Preconditions.checkNotNull(component, "The component must not be null.");
    Preconditions.checkNotNull(constraints, "The constraints must not be null.");
    constraints.ensureValidGridBounds(getColumnCount(), getRowCount());
    this.constraintMap.put(component, (CellConstraints)constraints.clone());
  }






  
  private void removeConstraints(Component component) {
    this.constraintMap.remove(component);
    this.componentSizeCache.removeEntry(component);
  }








  
  public int[][] getColumnGroups() {
    return deepClone(this.colGroupIndices);
  }




















  
  public void setColumnGroups(int[][] groupOfIndices) {
    setColumnGroupsImpl(groupOfIndices, true);
  }

  
  private void setColumnGroupsImpl(int[][] groupOfIndices, boolean checkIndices) {
    int maxColumn = getColumnCount();
    boolean[] usedIndices = new boolean[maxColumn + 1];
    for (int group = 0; group < groupOfIndices.length; group++) {
      int[] indices = groupOfIndices[group];
      if (checkIndices) {
        Preconditions.checkArgument((indices.length >= 2), "Each indice group must contain at least two indices.");
      }
      for (int indice : indices) {
        int colIndex = indice;
        if (colIndex < 1 || colIndex > maxColumn) {
          throw new IndexOutOfBoundsException("Invalid column group index " + colIndex + " in group " + (group + 1));
        }

        
        if (usedIndices[colIndex]) {
          throw new IllegalArgumentException("Column index " + colIndex + " must not be used in multiple column groups.");
        }
        
        usedIndices[colIndex] = true;
      } 
    } 
    this.colGroupIndices = deepClone(groupOfIndices);
  }



















  
  public void setColumnGroup(int... indices) {
    Preconditions.checkNotNull(indices, "The %1$s must not be null.", new Object[] { "column group indices" });
    Preconditions.checkArgument((indices.length >= 2), "You must specify at least two indices.");
    setColumnGroups(new int[][] { indices });
  }







  
  public void addGroupedColumn(int columnIndex) {
    int[][] newColGroups = getColumnGroups();
    
    if (newColGroups.length == 0) {
      newColGroups = new int[][] { { columnIndex } };
    } else {
      int lastGroupIndex = newColGroups.length - 1;
      int[] lastGroup = newColGroups[lastGroupIndex];
      int groupSize = lastGroup.length;
      int[] newLastGroup = new int[groupSize + 1];
      System.arraycopy(lastGroup, 0, newLastGroup, 0, groupSize);
      newLastGroup[groupSize] = columnIndex;
      newColGroups[lastGroupIndex] = newLastGroup;
    } 
    setColumnGroupsImpl(newColGroups, false);
  }





  
  public int[][] getRowGroups() {
    return deepClone(this.rowGroupIndices);
  }




















  
  public void setRowGroups(int[][] groupOfIndices) {
    setRowGroupsImpl(groupOfIndices, true);
  }

  
  private void setRowGroupsImpl(int[][] groupOfIndices, boolean checkIndices) {
    int rowCount = getRowCount();
    boolean[] usedIndices = new boolean[rowCount + 1];
    for (int group = 0; group < groupOfIndices.length; group++) {
      int[] indices = groupOfIndices[group];
      if (checkIndices) {
        Preconditions.checkArgument((indices.length >= 2), "Each indice group must contain at least two indices.");
      }
      for (int indice : indices) {
        int rowIndex = indice;
        if (rowIndex < 1 || rowIndex > rowCount) {
          throw new IndexOutOfBoundsException("Invalid row group index " + rowIndex + " in group " + (group + 1));
        }

        
        if (usedIndices[rowIndex]) {
          throw new IllegalArgumentException("Row index " + rowIndex + " must not be used in multiple row groups.");
        }
        
        usedIndices[rowIndex] = true;
      } 
    } 
    this.rowGroupIndices = deepClone(groupOfIndices);
  }



















  
  public void setRowGroup(int... indices) {
    Preconditions.checkNotNull(indices, "The %1$s must not be null.", new Object[] { "row group indices" });
    Preconditions.checkArgument((indices.length >= 2), "You must specify at least two indices.");
    setRowGroups(new int[][] { indices });
  }







  
  public void addGroupedRow(int rowIndex) {
    int[][] newRowGroups = getRowGroups();
    
    if (newRowGroups.length == 0) {
      newRowGroups = new int[][] { { rowIndex } };
    } else {
      int lastGroupIndex = newRowGroups.length - 1;
      int[] lastGroup = newRowGroups[lastGroupIndex];
      int groupSize = lastGroup.length;
      int[] newLastGroup = new int[groupSize + 1];
      System.arraycopy(lastGroup, 0, newLastGroup, 0, groupSize);
      newLastGroup[groupSize] = rowIndex;
      newRowGroups[lastGroupIndex] = newLastGroup;
    } 
    setRowGroupsImpl(newRowGroups, false);
  }















  
  public boolean getHonorsVisibility() {
    return this.honorsVisibility;
  }
































  
  public void setHonorsVisibility(boolean b) {
    boolean oldHonorsVisibility = getHonorsVisibility();
    if (oldHonorsVisibility == b) {
      return;
    }
    this.honorsVisibility = b;
    Set<Component> componentSet = this.constraintMap.keySet();
    if (componentSet.isEmpty()) {
      return;
    }
    Component firstComponent = componentSet.iterator().next();
    Container container = firstComponent.getParent();
    invalidateAndRepaint(container);
  }
















  
  public void setHonorsVisibility(Component component, Boolean b) {
    CellConstraints constraints = getConstraints0(component);
    if (Objects.equals(b, constraints.honorsVisibility)) {
      return;
    }
    constraints.honorsVisibility = b;
    invalidateAndRepaint(component.getParent());
  }












  
  public void addLayoutComponent(String name, Component component) {
    throw new UnsupportedOperationException("Use #addLayoutComponent(Component, Object) instead.");
  }














  
  public void addLayoutComponent(Component comp, Object constraints) {
    Preconditions.checkNotNull(constraints, "The constraints must not be null.");
    if (constraints instanceof String) {
      setConstraints(comp, new CellConstraints((String)constraints));
    } else if (constraints instanceof CellConstraints) {
      setConstraints(comp, (CellConstraints)constraints);
    } else {
      throw new IllegalArgumentException("Illegal constraint type " + constraints.getClass());
    } 
  }










  
  public void removeLayoutComponent(Component comp) {
    removeConstraints(comp);
  }















  
  public Dimension minimumLayoutSize(Container parent) {
    return computeLayoutSize(parent, this.minimumWidthMeasure, this.minimumHeightMeasure);
  }














  
  public Dimension preferredLayoutSize(Container parent) {
    return computeLayoutSize(parent, this.preferredWidthMeasure, this.preferredHeightMeasure);
  }













  
  public Dimension maximumLayoutSize(Container target) {
    return new Dimension(2147483647, 2147483647);
  }











  
  public float getLayoutAlignmentX(Container parent) {
    return 0.5F;
  }











  
  public float getLayoutAlignmentY(Container parent) {
    return 0.5F;
  }








  
  public void invalidateLayout(Container target) {
    invalidateCaches();
  }

























  
  public void layoutContainer(Container parent) {
    synchronized (parent.getTreeLock()) {
      initializeColAndRowComponentLists();
      Dimension size = parent.getSize();
      
      Insets insets = parent.getInsets();
      int totalWidth = size.width - insets.left - insets.right;
      int totalHeight = size.height - insets.top - insets.bottom;
      
      int[] x = computeGridOrigins(parent, totalWidth, insets.left, this.colSpecs, (List[])this.colComponents, this.colGroupIndices, this.minimumWidthMeasure, this.preferredWidthMeasure);






      
      int[] y = computeGridOrigins(parent, totalHeight, insets.top, this.rowSpecs, (List[])this.rowComponents, this.rowGroupIndices, this.minimumHeightMeasure, this.preferredHeightMeasure);







      
      layoutComponents(x, y);
    } 
  }










  
  private void initializeColAndRowComponentLists() {
    this.colComponents = (List<Component>[])new List[getColumnCount()]; int i;
    for (i = 0; i < getColumnCount(); i++) {
      this.colComponents[i] = new ArrayList<Component>();
    }
    
    this.rowComponents = (List<Component>[])new List[getRowCount()];
    for (i = 0; i < getRowCount(); i++) {
      this.rowComponents[i] = new ArrayList<Component>();
    }
    
    for (Map.Entry<Component, CellConstraints> element : this.constraintMap.entrySet()) {
      Map.Entry entry = element;
      Component component = (Component)entry.getKey();
      CellConstraints constraints = (CellConstraints)entry.getValue();
      if (takeIntoAccount(component, constraints)) {
        if (constraints.gridWidth == 1) {
          this.colComponents[constraints.gridX - 1].add(component);
        }
        
        if (constraints.gridHeight == 1) {
          this.rowComponents[constraints.gridY - 1].add(component);
        }
      } 
    } 
  }












  
  private Dimension computeLayoutSize(Container parent, Measure defaultWidthMeasure, Measure defaultHeightMeasure) {
    synchronized (parent.getTreeLock()) {
      initializeColAndRowComponentLists();
      int[] colWidths = maximumSizes(parent, this.colSpecs, (List[])this.colComponents, this.minimumWidthMeasure, this.preferredWidthMeasure, defaultWidthMeasure);


      
      int[] rowHeights = maximumSizes(parent, this.rowSpecs, (List[])this.rowComponents, this.minimumHeightMeasure, this.preferredHeightMeasure, defaultHeightMeasure);


      
      int[] groupedWidths = groupedSizes(this.colGroupIndices, colWidths);
      int[] groupedHeights = groupedSizes(this.rowGroupIndices, rowHeights);

      
      int[] xOrigins = computeOrigins(groupedWidths, 0);
      int[] yOrigins = computeOrigins(groupedHeights, 0);
      
      int width1 = sum(groupedWidths);
      int height1 = sum(groupedHeights);
      int maxWidth = width1;
      int maxHeight = height1;







      
      int[] maxFixedSizeColsTable = computeMaximumFixedSpanTable(this.colSpecs);
      int[] maxFixedSizeRowsTable = computeMaximumFixedSpanTable(this.rowSpecs);
      
      for (Map.Entry<Component, CellConstraints> element : this.constraintMap.entrySet()) {
        Map.Entry entry = element;
        Component component = (Component)entry.getKey();
        CellConstraints constraints = (CellConstraints)entry.getValue();
        if (!takeIntoAccount(component, constraints)) {
          continue;
        }
        
        if (constraints.gridWidth > 1 && constraints.gridWidth > maxFixedSizeColsTable[constraints.gridX - 1]) {

          
          int compWidth = defaultWidthMeasure.sizeOf(component);
          
          int gridX1 = constraints.gridX - 1;
          int gridX2 = gridX1 + constraints.gridWidth;
          int lead = xOrigins[gridX1];
          int trail = width1 - xOrigins[gridX2];
          int myWidth = lead + compWidth + trail;
          if (myWidth > maxWidth) {
            maxWidth = myWidth;
          }
        } 
        
        if (constraints.gridHeight > 1 && constraints.gridHeight > maxFixedSizeRowsTable[constraints.gridY - 1]) {

          
          int compHeight = defaultHeightMeasure.sizeOf(component);
          
          int gridY1 = constraints.gridY - 1;
          int gridY2 = gridY1 + constraints.gridHeight;
          int lead = yOrigins[gridY1];
          int trail = height1 - yOrigins[gridY2];
          int myHeight = lead + compHeight + trail;
          if (myHeight > maxHeight) {
            maxHeight = myHeight;
          }
        } 
      } 
      Insets insets = parent.getInsets();
      int width = maxWidth + insets.left + insets.right;
      int height = maxHeight + insets.top + insets.bottom;
      return new Dimension(width, height);
    } 
  }






















  
  private static int[] computeGridOrigins(Container container, int totalSize, int offset, List formSpecs, List[] componentLists, int[][] groupIndices, Measure minMeasure, Measure prefMeasure) {
    int[] minSizes = maximumSizes(container, formSpecs, componentLists, minMeasure, prefMeasure, minMeasure);
    
    int[] prefSizes = maximumSizes(container, formSpecs, componentLists, minMeasure, prefMeasure, prefMeasure);

    
    int[] groupedMinSizes = groupedSizes(groupIndices, minSizes);
    int[] groupedPrefSizes = groupedSizes(groupIndices, prefSizes);
    int totalMinSize = sum(groupedMinSizes);
    int totalPrefSize = sum(groupedPrefSizes);
    int[] compressedSizes = compressedSizes(formSpecs, totalSize, totalMinSize, totalPrefSize, groupedMinSizes, prefSizes);




    
    int[] groupedSizes = groupedSizes(groupIndices, compressedSizes);
    int totalGroupedSize = sum(groupedSizes);
    int[] sizes = distributedSizes(formSpecs, totalSize, totalGroupedSize, groupedSizes);


    
    return computeOrigins(sizes, offset);
  }








  
  private static int[] computeOrigins(int[] sizes, int offset) {
    int count = sizes.length;
    int[] origins = new int[count + 1];
    origins[0] = offset;
    for (int i = 1; i <= count; i++) {
      origins[i] = origins[i - 1] + sizes[i - 1];
    }
    return origins;
  }
















  
  private void layoutComponents(int[] x, int[] y) {
    Rectangle cellBounds = new Rectangle();
    for (Map.Entry<Component, CellConstraints> element : this.constraintMap.entrySet()) {
      Map.Entry entry = element;
      Component component = (Component)entry.getKey();
      CellConstraints constraints = (CellConstraints)entry.getValue();
      
      int gridX = constraints.gridX - 1;
      int gridY = constraints.gridY - 1;
      int gridWidth = constraints.gridWidth;
      int gridHeight = constraints.gridHeight;
      cellBounds.x = x[gridX];
      cellBounds.y = y[gridY];
      cellBounds.width = x[gridX + gridWidth] - cellBounds.x;
      cellBounds.height = y[gridY + gridHeight] - cellBounds.y;
      
      constraints.setBounds(component, this, cellBounds, this.minimumWidthMeasure, this.minimumHeightMeasure, this.preferredWidthMeasure, this.preferredHeightMeasure);
    } 
  }






  
  private void invalidateCaches() {
    this.componentSizeCache.invalidate();
  }



















  
  private static int[] maximumSizes(Container container, List<FormSpec> formSpecs, List[] componentLists, Measure minMeasure, Measure prefMeasure, Measure defaultMeasure) {
    int size = formSpecs.size();
    int[] result = new int[size];
    for (int i = 0; i < size; i++) {
      FormSpec formSpec = formSpecs.get(i);
      result[i] = formSpec.maximumSize(container, componentLists[i], minMeasure, prefMeasure, defaultMeasure);
    } 



    
    return result;
  }






















  
  private static int[] compressedSizes(List<FormSpec> formSpecs, int totalSize, int totalMinSize, int totalPrefSize, int[] minSizes, int[] prefSizes) {
    if (totalSize < totalMinSize) {
      return minSizes;
    }
    
    if (totalSize >= totalPrefSize) {
      return prefSizes;
    }
    
    int count = formSpecs.size();
    int[] sizes = new int[count];
    
    double totalCompressionSpace = (totalPrefSize - totalSize);
    double maxCompressionSpace = (totalPrefSize - totalMinSize);
    double compressionFactor = totalCompressionSpace / maxCompressionSpace;




    
    for (int i = 0; i < count; i++) {
      FormSpec formSpec = formSpecs.get(i);
      sizes[i] = prefSizes[i];
      if (formSpec.getSize().compressible()) {
        sizes[i] = sizes[i] - (int)Math.round((prefSizes[i] - minSizes[i]) * compressionFactor);
      }
    } 
    
    return sizes;
  }










  
  private static int[] groupedSizes(int[][] groups, int[] rawSizes) {
    if (groups == null || groups.length == 0) {
      return rawSizes;
    }

    
    int[] sizes = new int[rawSizes.length];
    for (int i = 0; i < sizes.length; i++) {
      sizes[i] = rawSizes[i];
    }

    
    for (int[] groupIndices : groups) {
      int groupMaxSize = 0;
      
      for (int groupIndice : groupIndices) {
        int index = groupIndice - 1;
        groupMaxSize = Math.max(groupMaxSize, sizes[index]);
      } 
      
      for (int groupIndice : groupIndices) {
        int index = groupIndice - 1;
        sizes[index] = groupMaxSize;
      } 
    } 
    return sizes;
  }













  
  private static int[] distributedSizes(List<FormSpec> formSpecs, int totalSize, int totalPrefSize, int[] inputSizes) {
    double totalFreeSpace = (totalSize - totalPrefSize);
    
    if (totalFreeSpace < 0.0D) {
      return inputSizes;
    }

    
    int count = formSpecs.size();
    double totalWeight = 0.0D;
    for (int i = 0; i < count; i++) {
      FormSpec formSpec = formSpecs.get(i);
      totalWeight += formSpec.getResizeWeight();
    } 

    
    if (totalWeight == 0.0D) {
      return inputSizes;
    }
    
    int[] sizes = new int[count];
    
    double restSpace = totalFreeSpace;
    int roundedRestSpace = (int)totalFreeSpace;
    for (int j = 0; j < count; j++) {
      FormSpec formSpec = formSpecs.get(j);
      double weight = formSpec.getResizeWeight();
      if (weight == 0.0D) {
        sizes[j] = inputSizes[j];
      } else {
        double roundingCorrection = restSpace - roundedRestSpace;
        double extraSpace = totalFreeSpace * weight / totalWeight;
        double correctedExtraSpace = extraSpace - roundingCorrection;
        int roundedExtraSpace = (int)Math.round(correctedExtraSpace);
        sizes[j] = inputSizes[j] + roundedExtraSpace;
        restSpace -= extraSpace;
        roundedRestSpace -= roundedExtraSpace;
      } 
    } 
    return sizes;
  }

























  
  private static int[] computeMaximumFixedSpanTable(List<FormSpec> formSpecs) {
    int size = formSpecs.size();
    int[] table = new int[size];
    int maximumFixedSpan = Integer.MAX_VALUE;
    for (int i = size - 1; i >= 0; i--) {
      FormSpec spec = formSpecs.get(i);
      if (spec.canGrow()) {
        maximumFixedSpan = 0;
      }
      table[i] = maximumFixedSpan;
      if (maximumFixedSpan < Integer.MAX_VALUE) {
        maximumFixedSpan++;
      }
    } 
    return table;
  }









  
  private static int sum(int[] sizes) {
    int sum = 0;
    for (int i = sizes.length - 1; i >= 0; i--) {
      sum += sizes[i];
    }
    return sum;
  }

  
  private static void invalidateAndRepaint(Container container) {
    if (container == null) {
      return;
    }
    if (container instanceof JComponent) {
      ((JComponent)container).revalidate();
    } else {
      container.invalidate();
    } 
    container.repaint();
  }














  
  private boolean takeIntoAccount(Component component, CellConstraints cc) {
    return (component.isVisible() || (cc.honorsVisibility == null && !getHonorsVisibility()) || Boolean.FALSE.equals(cc.honorsVisibility));
  }









  
  public static interface Measure
  {
    int sizeOf(Component param1Component);
  }









  
  private static abstract class CachingMeasure
    implements Measure, Serializable
  {
    protected final FormLayout.ComponentSizeCache cache;








    
    private CachingMeasure(FormLayout.ComponentSizeCache cache) {
      this.cache = cache;
    }
  }



  
  private static final class MinimumWidthMeasure
    extends CachingMeasure
  {
    private MinimumWidthMeasure(FormLayout.ComponentSizeCache cache) {
      super(cache);
    }
    
    public int sizeOf(Component c) {
      return (this.cache.getMinimumSize(c)).width;
    }
  }


  
  private static final class MinimumHeightMeasure
    extends CachingMeasure
  {
    private MinimumHeightMeasure(FormLayout.ComponentSizeCache cache) {
      super(cache);
    }
    
    public int sizeOf(Component c) {
      return (this.cache.getMinimumSize(c)).height;
    }
  }


  
  private static final class PreferredWidthMeasure
    extends CachingMeasure
  {
    private PreferredWidthMeasure(FormLayout.ComponentSizeCache cache) {
      super(cache);
    }
    
    public int sizeOf(Component c) {
      return (this.cache.getPreferredSize(c)).width;
    }
  }


  
  private static final class PreferredHeightMeasure
    extends CachingMeasure
  {
    private PreferredHeightMeasure(FormLayout.ComponentSizeCache cache) {
      super(cache);
    }
    
    public int sizeOf(Component c) {
      return (this.cache.getPreferredSize(c)).height;
    }
  }





  
  private static final class ComponentSizeCache
    implements Serializable
  {
    private final Map<Component, Dimension> minimumSizes;




    
    private final Map<Component, Dimension> preferredSizes;




    
    private ComponentSizeCache(int initialCapacity) {
      this.minimumSizes = new HashMap<Component, Dimension>(initialCapacity);
      this.preferredSizes = new HashMap<Component, Dimension>(initialCapacity);
    }



    
    void invalidate() {
      this.minimumSizes.clear();
      this.preferredSizes.clear();
    }








    
    Dimension getMinimumSize(Component component) {
      Dimension size = this.minimumSizes.get(component);
      if (size == null) {
        size = component.getMinimumSize();
        this.minimumSizes.put(component, size);
      } 
      return size;
    }








    
    Dimension getPreferredSize(Component component) {
      Dimension size = this.preferredSizes.get(component);
      if (size == null) {
        size = component.getPreferredSize();
        this.preferredSizes.put(component, size);
      } 
      return size;
    }
    
    void removeEntry(Component component) {
      this.minimumSizes.remove(component);
      this.preferredSizes.remove(component);
    }
  }
















  
  public LayoutInfo getLayoutInfo(Container parent) {
    synchronized (parent.getTreeLock()) {
      initializeColAndRowComponentLists();
      Dimension size = parent.getSize();
      
      Insets insets = parent.getInsets();
      int totalWidth = size.width - insets.left - insets.right;
      int totalHeight = size.height - insets.top - insets.bottom;
      
      int[] x = computeGridOrigins(parent, totalWidth, insets.left, this.colSpecs, (List[])this.colComponents, this.colGroupIndices, this.minimumWidthMeasure, this.preferredWidthMeasure);






      
      int[] y = computeGridOrigins(parent, totalHeight, insets.top, this.rowSpecs, (List[])this.rowComponents, this.rowGroupIndices, this.minimumHeightMeasure, this.preferredHeightMeasure);






      
      return new LayoutInfo(x, y);
    } 
  }




  
  public static final class LayoutInfo
  {
    public final int[] columnOrigins;



    
    public final int[] rowOrigins;



    
    private LayoutInfo(int[] xOrigins, int[] yOrigins) {
      this.columnOrigins = xOrigins;
      this.rowOrigins = yOrigins;
    }





    
    public int getX() {
      return this.columnOrigins[0];
    }





    
    public int getY() {
      return this.rowOrigins[0];
    }






    
    public int getWidth() {
      return this.columnOrigins[this.columnOrigins.length - 1] - this.columnOrigins[0];
    }





    
    public int getHeight() {
      return this.rowOrigins[this.rowOrigins.length - 1] - this.rowOrigins[0];
    }
  }














  
  private static int[][] deepClone(int[][] array) {
    int[][] result = new int[array.length][];
    for (int i = 0; i < result.length; i++) {
      result[i] = (int[])array[i].clone();
    }
    return result;
  }











  
  private void writeObject(ObjectOutputStream out) throws IOException {
    invalidateCaches();
    out.defaultWriteObject();
  }
}
