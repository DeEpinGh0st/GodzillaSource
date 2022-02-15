package org.springframework.cglib.core;

import org.springframework.asm.Type;

















public abstract class MethodInfo
{
  public abstract ClassInfo getClassInfo();
  
  public abstract int getModifiers();
  
  public abstract Signature getSignature();
  
  public abstract Type[] getExceptionTypes();
  
  public boolean equals(Object o) {
    if (o == null)
      return false; 
    if (!(o instanceof MethodInfo))
      return false; 
    return getSignature().equals(((MethodInfo)o).getSignature());
  }
  
  public int hashCode() {
    return getSignature().hashCode();
  }

  
  public String toString() {
    return getSignature().toString();
  }
}
