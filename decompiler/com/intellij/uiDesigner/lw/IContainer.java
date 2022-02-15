package com.intellij.uiDesigner.lw;

import com.intellij.uiDesigner.shared.BorderType;

public interface IContainer extends IComponent {
  int getComponentCount();
  
  IComponent getComponent(int paramInt);
  
  int indexOfComponent(IComponent paramIComponent);
  
  boolean isXY();
  
  StringDescriptor getBorderTitle();
  
  BorderType getBorderType();
}
