package com.jgoodies.common.base;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


























































public final class Objects
{
  public static <T extends java.io.Serializable> T deepCopy(T original) {
    if (original == null) {
      return null;
    }
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
      ObjectOutputStream oas = new ObjectOutputStream(baos);
      oas.writeObject(original);
      oas.flush();
      
      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      ObjectInputStream ois = new ObjectInputStream(bais);
      return (T)ois.readObject();
    } catch (Throwable e) {
      throw new RuntimeException("Deep copy failed", e);
    } 
  }



















  
  public static boolean equals(Object o1, Object o2) {
    return (o1 == o2 || (o1 != null && o1.equals(o2)));
  }
}
