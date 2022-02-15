package com.intellij.uiDesigner.lw;

import com.intellij.uiDesigner.core.GridConstraints;

public interface IComponent {
  Object getClientProperty(Object paramObject);
  
  void putClientProperty(Object paramObject1, Object paramObject2);
  
  String getBinding();
  
  String getComponentClassName();
  
  String getId();
  
  boolean isCustomCreate();
  
  IProperty[] getModifiedProperties();
  
  IContainer getParentContainer();
  
  GridConstraints getConstraints();
  
  Object getCustomLayoutConstraints();
  
  boolean accept(ComponentVisitor paramComponentVisitor);
  
  boolean areChildrenExclusive();
}
