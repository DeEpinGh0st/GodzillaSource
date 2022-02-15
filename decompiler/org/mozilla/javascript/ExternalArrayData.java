package org.mozilla.javascript;

public interface ExternalArrayData {
  Object getArrayElement(int paramInt);
  
  void setArrayElement(int paramInt, Object paramObject);
  
  int getArrayLength();
}
