package com.jediterm.terminal.model;

public interface Tabulator {
  void clearTabStop(int paramInt);
  
  void clearAllTabStops();
  
  int getNextTabWidth(int paramInt);
  
  int getPreviousTabWidth(int paramInt);
  
  int nextTab(int paramInt);
  
  int previousTab(int paramInt);
  
  void setTabStop(int paramInt);
  
  void resize(int paramInt);
}
