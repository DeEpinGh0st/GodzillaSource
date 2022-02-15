package org.springframework.cglib.transform.impl;

public interface FieldProvider {
  String[] getFieldNames();
  
  Class[] getFieldTypes();
  
  void setField(int paramInt, Object paramObject);
  
  Object getField(int paramInt);
  
  void setField(String paramString, Object paramObject);
  
  Object getField(String paramString);
}
