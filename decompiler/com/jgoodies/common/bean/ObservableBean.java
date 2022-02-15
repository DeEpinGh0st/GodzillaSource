package com.jgoodies.common.bean;

import java.beans.PropertyChangeListener;

public interface ObservableBean {
  void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener);
  
  void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener);
}
