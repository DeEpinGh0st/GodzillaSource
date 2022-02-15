package com.jgoodies.common.bean;

import java.beans.PropertyChangeListener;

public interface ObservableBean2 extends ObservableBean {
  void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener);
  
  void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener);
  
  PropertyChangeListener[] getPropertyChangeListeners();
  
  PropertyChangeListener[] getPropertyChangeListeners(String paramString);
}
