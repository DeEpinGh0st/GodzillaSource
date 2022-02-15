package com.formdev.flatlaf.extras.components;

import java.awt.Color;
import java.awt.Insets;
import javax.swing.UIManager;































public interface FlatComponentExtension
{
  Object getClientProperty(Object paramObject);
  
  void putClientProperty(Object paramObject1, Object paramObject2);
  
  default boolean getClientPropertyBoolean(Object key, String defaultValueKey) {
    Object value = getClientProperty(key);
    return (value instanceof Boolean) ? ((Boolean)value).booleanValue() : UIManager.getBoolean(defaultValueKey);
  }
  
  default boolean getClientPropertyBoolean(Object key, boolean defaultValue) {
    Object value = getClientProperty(key);
    return (value instanceof Boolean) ? ((Boolean)value).booleanValue() : defaultValue;
  }
  
  default void putClientPropertyBoolean(Object key, boolean value, boolean defaultValue) {
    putClientProperty(key, (value != defaultValue) ? Boolean.valueOf(value) : null);
  }

  
  default int getClientPropertyInt(Object key, String defaultValueKey) {
    Object value = getClientProperty(key);
    return (value instanceof Integer) ? ((Integer)value).intValue() : UIManager.getInt(defaultValueKey);
  }
  
  default int getClientPropertyInt(Object key, int defaultValue) {
    Object value = getClientProperty(key);
    return (value instanceof Integer) ? ((Integer)value).intValue() : defaultValue;
  }

  
  default Color getClientPropertyColor(Object key, String defaultValueKey) {
    Object value = getClientProperty(key);
    return (value instanceof Color) ? (Color)value : UIManager.getColor(defaultValueKey);
  }
  
  default Insets getClientPropertyInsets(Object key, String defaultValueKey) {
    Object value = getClientProperty(key);
    return (value instanceof Insets) ? (Insets)value : UIManager.getInsets(defaultValueKey);
  }



  
  default <T extends Enum<T>> T getClientPropertyEnumString(Object key, Class<T> enumType, String defaultValueKey, T defaultValue) {
    Object value = getClientProperty(key);
    if (!(value instanceof String) && defaultValueKey != null)
      value = UIManager.getString(defaultValueKey); 
    if (value instanceof String) {
      try {
        return Enum.valueOf(enumType, (String)value);
      } catch (IllegalArgumentException ex) {
        ex.printStackTrace();
      } 
    }
    return defaultValue;
  }
  
  default <T extends Enum<T>> void putClientPropertyEnumString(Object key, Enum<T> value) {
    putClientProperty(key, (value != null) ? value.toString() : null);
  }
}
