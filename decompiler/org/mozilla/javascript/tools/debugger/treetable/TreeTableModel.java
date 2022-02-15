package org.mozilla.javascript.tools.debugger.treetable;

import javax.swing.tree.TreeModel;

public interface TreeTableModel extends TreeModel {
  int getColumnCount();
  
  String getColumnName(int paramInt);
  
  Class<?> getColumnClass(int paramInt);
  
  Object getValueAt(Object paramObject, int paramInt);
  
  boolean isCellEditable(Object paramObject, int paramInt);
  
  void setValueAt(Object paramObject1, Object paramObject2, int paramInt);
}
