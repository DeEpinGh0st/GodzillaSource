package org.springframework.cglib.proxy;

public interface Factory {
  Object newInstance(Callback paramCallback);
  
  Object newInstance(Callback[] paramArrayOfCallback);
  
  Object newInstance(Class[] paramArrayOfClass, Object[] paramArrayOfObject, Callback[] paramArrayOfCallback);
  
  Callback getCallback(int paramInt);
  
  void setCallback(int paramInt, Callback paramCallback);
  
  void setCallbacks(Callback[] paramArrayOfCallback);
  
  Callback[] getCallbacks();
}
