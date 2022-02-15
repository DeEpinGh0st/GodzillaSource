package com.intellij.uiDesigner.lw;

public interface IRootContainer extends IContainer {
  String getClassToBind();
  
  String getButtonGroupName(IComponent paramIComponent);
  
  String[] getButtonGroupComponentIds(String paramString);
  
  boolean isInspectionSuppressed(String paramString1, String paramString2);
  
  IButtonGroup[] getButtonGroups();
}
