package com.intellij.uiDesigner.core;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;

public final class Util {
  private static final Dimension MAX_SIZE = new Dimension(2147483647, 2147483647);
  public static final int DEFAULT_INDENT = 10;
  
  public static Dimension getMinimumSize(Component component, GridConstraints constraints, boolean addIndent) {
    try {
      Dimension size = getSize(constraints.myMinimumSize, component.getMinimumSize());
      if (addIndent) {
        size.width += 10 * constraints.getIndent();
      }
      return size;
    }
    catch (NullPointerException npe) {
      return new Dimension(0, 0);
    } 
  }



  
  public static Dimension getMaximumSize(GridConstraints constraints, boolean addIndent) {
    try {
      Dimension size = getSize(constraints.myMaximumSize, MAX_SIZE);
      if (addIndent && size.width < MAX_SIZE.width) {
        size.width += 10 * constraints.getIndent();
      }
      return size;
    }
    catch (NullPointerException e) {
      return new Dimension(0, 0);
    } 
  }
  
  public static Dimension getPreferredSize(Component component, GridConstraints constraints, boolean addIndent) {
    try {
      Dimension size = getSize(constraints.myPreferredSize, component.getPreferredSize());
      if (addIndent) {
        size.width += 10 * constraints.getIndent();
      }
      return size;
    }
    catch (NullPointerException e) {
      return new Dimension(0, 0);
    } 
  }
  
  private static Dimension getSize(Dimension overridenSize, Dimension ownSize) {
    int overridenWidth = (overridenSize.width >= 0) ? overridenSize.width : ownSize.width;
    int overridenHeight = (overridenSize.height >= 0) ? overridenSize.height : ownSize.height;
    return new Dimension(overridenWidth, overridenHeight);
  }
  
  public static void adjustSize(Component component, GridConstraints constraints, Dimension size) {
    Dimension minimumSize = getMinimumSize(component, constraints, false);
    Dimension maximumSize = getMaximumSize(constraints, false);
    
    size.width = Math.max(size.width, minimumSize.width);
    size.height = Math.max(size.height, minimumSize.height);
    
    size.width = Math.min(size.width, maximumSize.width);
    size.height = Math.min(size.height, maximumSize.height);
  }




  
  public static int eliminate(int[] cellIndices, int[] spans, ArrayList<Integer> eliminated) {
    int size = cellIndices.length;
    if (size != spans.length) {
      throw new IllegalArgumentException("size mismatch: " + size + ", " + spans.length);
    }
    if (eliminated != null && eliminated.size() != 0) {
      throw new IllegalArgumentException("eliminated must be empty");
    }
    
    int cellCount = 0;
    for (int i = 0; i < size; i++) {
      cellCount = Math.max(cellCount, cellIndices[i] + spans[i]);
    }
    
    for (int cell = cellCount - 1; cell >= 0; cell--) {

      
      boolean starts = false;
      boolean ends = false;
      int j;
      for (j = 0; j < size; j++) {
        if (cellIndices[j] == cell) {
          starts = true;
        }
        if (cellIndices[j] + spans[j] - 1 == cell) {
          ends = true;
        }
      } 
      
      if (!starts || !ends) {


        
        if (eliminated != null) {
          eliminated.add(new Integer(cell));
        }

        
        for (j = 0; j < size; j++) {
          boolean decreaseSpan = (cellIndices[j] <= cell && cell < cellIndices[j] + spans[j]);
          boolean decreaseIndex = (cellIndices[j] > cell);
          
          if (decreaseSpan) {
            spans[j] = spans[j] - 1;
          }
          
          if (decreaseIndex) {
            cellIndices[j] = cellIndices[j] - 1;
          }
        } 
        
        cellCount--;
      } 
    } 
    return cellCount;
  }
}
