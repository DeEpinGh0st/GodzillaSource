package com.jgoodies.common.bean;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.Serializable;





























































































public abstract class Bean
  implements Serializable, ObservableBean2
{
  protected transient PropertyChangeSupport changeSupport;
  private transient VetoableChangeSupport vetoSupport;
  
  public final synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
    if (listener == null) {
      return;
    }
    if (this.changeSupport == null) {
      this.changeSupport = createPropertyChangeSupport(this);
    }
    this.changeSupport.addPropertyChangeListener(listener);
  }
















  
  public final synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
    if (listener == null || this.changeSupport == null) {
      return;
    }
    this.changeSupport.removePropertyChangeListener(listener);
  }




















  
  public final synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    if (listener == null) {
      return;
    }
    if (this.changeSupport == null) {
      this.changeSupport = createPropertyChangeSupport(this);
    }
    this.changeSupport.addPropertyChangeListener(propertyName, listener);
  }


















  
  public final synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    if (listener == null || this.changeSupport == null) {
      return;
    }
    this.changeSupport.removePropertyChangeListener(propertyName, listener);
  }

















  
  public final synchronized void addVetoableChangeListener(VetoableChangeListener listener) {
    if (listener == null) {
      return;
    }
    if (this.vetoSupport == null) {
      this.vetoSupport = new VetoableChangeSupport(this);
    }
    this.vetoSupport.addVetoableChangeListener(listener);
  }















  
  public final synchronized void removeVetoableChangeListener(VetoableChangeListener listener) {
    if (listener == null || this.vetoSupport == null) {
      return;
    }
    this.vetoSupport.removeVetoableChangeListener(listener);
  }



















  
  public final synchronized void addVetoableChangeListener(String propertyName, VetoableChangeListener listener) {
    if (listener == null) {
      return;
    }
    if (this.vetoSupport == null) {
      this.vetoSupport = new VetoableChangeSupport(this);
    }
    this.vetoSupport.addVetoableChangeListener(propertyName, listener);
  }

















  
  public final synchronized void removeVetoableChangeListener(String propertyName, VetoableChangeListener listener) {
    if (listener == null || this.vetoSupport == null) {
      return;
    }
    this.vetoSupport.removeVetoableChangeListener(propertyName, listener);
  }

















  
  public final synchronized PropertyChangeListener[] getPropertyChangeListeners() {
    if (this.changeSupport == null) {
      return new PropertyChangeListener[0];
    }
    return this.changeSupport.getPropertyChangeListeners();
  }















  
  public final synchronized PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
    if (this.changeSupport == null) {
      return new PropertyChangeListener[0];
    }
    return this.changeSupport.getPropertyChangeListeners(propertyName);
  }














  
  public final synchronized VetoableChangeListener[] getVetoableChangeListeners() {
    if (this.vetoSupport == null) {
      return new VetoableChangeListener[0];
    }
    return this.vetoSupport.getVetoableChangeListeners();
  }














  
  public final synchronized VetoableChangeListener[] getVetoableChangeListeners(String propertyName) {
    if (this.vetoSupport == null) {
      return new VetoableChangeListener[0];
    }
    return this.vetoSupport.getVetoableChangeListeners(propertyName);
  }


















  
  protected PropertyChangeSupport createPropertyChangeSupport(Object bean) {
    return new PropertyChangeSupport(bean);
  }

















  
  protected final void firePropertyChange(PropertyChangeEvent event) {
    PropertyChangeSupport aChangeSupport = this.changeSupport;
    if (aChangeSupport == null) {
      return;
    }
    aChangeSupport.firePropertyChange(event);
  }













  
  protected final void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    PropertyChangeSupport aChangeSupport = this.changeSupport;
    if (aChangeSupport == null) {
      return;
    }
    aChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
  }













  
  protected final void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
    PropertyChangeSupport aChangeSupport = this.changeSupport;
    if (aChangeSupport == null) {
      return;
    }
    aChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
  }













  
  protected final void firePropertyChange(String propertyName, double oldValue, double newValue) {
    firePropertyChange(propertyName, Double.valueOf(oldValue), Double.valueOf(newValue));
  }













  
  protected final void firePropertyChange(String propertyName, float oldValue, float newValue) {
    firePropertyChange(propertyName, Float.valueOf(oldValue), Float.valueOf(newValue));
  }













  
  protected final void firePropertyChange(String propertyName, int oldValue, int newValue) {
    PropertyChangeSupport aChangeSupport = this.changeSupport;
    if (aChangeSupport == null) {
      return;
    }
    aChangeSupport.firePropertyChange(propertyName, Integer.valueOf(oldValue), Integer.valueOf(newValue));
  }














  
  protected final void firePropertyChange(String propertyName, long oldValue, long newValue) {
    firePropertyChange(propertyName, Long.valueOf(oldValue), Long.valueOf(newValue));
  }









  
  protected final void fireMultiplePropertiesChanged() {
    firePropertyChange((String)null, (Object)null, (Object)null);
  }

















  
  protected final void fireIndexedPropertyChange(String propertyName, int index, Object oldValue, Object newValue) {
    PropertyChangeSupport aChangeSupport = this.changeSupport;
    if (aChangeSupport == null) {
      return;
    }
    aChangeSupport.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
  }




















  
  protected final void fireIndexedPropertyChange(String propertyName, int index, int oldValue, int newValue) {
    if (oldValue == newValue) {
      return;
    }
    fireIndexedPropertyChange(propertyName, index, Integer.valueOf(oldValue), Integer.valueOf(newValue));
  }





















  
  protected final void fireIndexedPropertyChange(String propertyName, int index, boolean oldValue, boolean newValue) {
    if (oldValue == newValue) {
      return;
    }
    fireIndexedPropertyChange(propertyName, index, Boolean.valueOf(oldValue), Boolean.valueOf(newValue));
  }




















  
  protected final void fireVetoableChange(PropertyChangeEvent event) throws PropertyVetoException {
    VetoableChangeSupport aVetoSupport = this.vetoSupport;
    if (aVetoSupport == null) {
      return;
    }
    aVetoSupport.fireVetoableChange(event);
  }















  
  protected final void fireVetoableChange(String propertyName, Object oldValue, Object newValue) throws PropertyVetoException {
    VetoableChangeSupport aVetoSupport = this.vetoSupport;
    if (aVetoSupport == null) {
      return;
    }
    aVetoSupport.fireVetoableChange(propertyName, oldValue, newValue);
  }















  
  protected final void fireVetoableChange(String propertyName, boolean oldValue, boolean newValue) throws PropertyVetoException {
    VetoableChangeSupport aVetoSupport = this.vetoSupport;
    if (aVetoSupport == null) {
      return;
    }
    aVetoSupport.fireVetoableChange(propertyName, oldValue, newValue);
  }















  
  protected final void fireVetoableChange(String propertyName, double oldValue, double newValue) throws PropertyVetoException {
    fireVetoableChange(propertyName, Double.valueOf(oldValue), Double.valueOf(newValue));
  }















  
  protected final void fireVetoableChange(String propertyName, int oldValue, int newValue) throws PropertyVetoException {
    VetoableChangeSupport aVetoSupport = this.vetoSupport;
    if (aVetoSupport == null) {
      return;
    }
    aVetoSupport.fireVetoableChange(propertyName, Integer.valueOf(oldValue), Integer.valueOf(newValue));
  }
















  
  protected final void fireVetoableChange(String propertyName, float oldValue, float newValue) throws PropertyVetoException {
    fireVetoableChange(propertyName, Float.valueOf(oldValue), Float.valueOf(newValue));
  }















  
  protected final void fireVetoableChange(String propertyName, long oldValue, long newValue) throws PropertyVetoException {
    fireVetoableChange(propertyName, Long.valueOf(oldValue), Long.valueOf(newValue));
  }
}
