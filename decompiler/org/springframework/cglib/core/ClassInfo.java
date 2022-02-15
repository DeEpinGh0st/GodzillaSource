package org.springframework.cglib.core;

import org.springframework.asm.Type;

















public abstract class ClassInfo
{
  public abstract Type getType();
  
  public abstract Type getSuperType();
  
  public abstract Type[] getInterfaces();
  
  public abstract int getModifiers();
  
  public boolean equals(Object o) {
    if (o == null)
      return false; 
    if (!(o instanceof ClassInfo))
      return false; 
    return getType().equals(((ClassInfo)o).getType());
  }
  
  public int hashCode() {
    return getType().hashCode();
  }

  
  public String toString() {
    return getType().getClassName();
  }
}
