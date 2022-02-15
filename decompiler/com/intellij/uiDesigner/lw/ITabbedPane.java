package com.intellij.uiDesigner.lw;

public interface ITabbedPane extends IContainer {
  public static final String TAB_TITLE_PROPERTY = "Tab Title";
  
  public static final String TAB_TOOLTIP_PROPERTY = "Tab Tooltip";
  
  StringDescriptor getTabProperty(IComponent paramIComponent, String paramString);
}
