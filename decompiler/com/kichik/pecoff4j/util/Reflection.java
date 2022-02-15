package com.kichik.pecoff4j.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;









public class Reflection
{
  public static String toString(Object o) {
    StringBuilder sb = new StringBuilder();
    Field[] fields = o.getClass().getDeclaredFields();
    for (Field f : fields) {
      if (!Modifier.isStatic(f.getModifiers())) {
        
        f.setAccessible(true);
        sb.append(f.getName());
        sb.append(": ");
        try {
          Object val = f.get(o);
          if (val instanceof Integer) {
            sb.append(f.get(o));
            sb.append(" (0x");
            sb.append(Integer.toHexString(((Integer)val).intValue()));
            sb.append(")");
          } else if (val instanceof Long) {
            sb.append(f.get(o));
            sb.append(" (0x");
            sb.append(Long.toHexString(((Long)val).longValue()));
            sb.append(")");
          } else if (val != null && val.getClass().isArray()) {
            if (val instanceof int[]) {
              int[] arr = (int[])val;
              for (int i = 0; i < arr.length && i < 10; i++) {
                if (i != 0)
                  sb.append(", "); 
                sb.append(arr[i]);
              } 
            } else if (val instanceof byte[]) {
              byte[] arr = (byte[])val;
              for (int i = 0; i < arr.length && i < 10; i++) {
                if (i != 0)
                  sb.append(", "); 
                sb.append(Integer.toHexString(arr[i] & 0xFF));
              } 
            } else {
              Object[] arr = (Object[])val;
              for (int i = 0; i < arr.length && i < 10; i++) {
                if (i != 0)
                  sb.append(", "); 
                sb.append(arr[i]);
              } 
            } 
          } else {
            sb.append(f.get(o));
          } 
        } catch (Exception e) {
          sb.append(e.getMessage());
        } 
        sb.append("\n");
      } 
    }  return sb.toString();
  }

  
  public static String getConstantName(Class clazz, int value) throws Exception {
    Field[] fields = clazz.getDeclaredFields();
    Integer valObj = Integer.valueOf(value);
    for (int i = 0; i < fields.length; i++) {
      Field f = fields[i];
      if (Modifier.isStatic(f.getModifiers()) && 
        Modifier.isPublic(f.getModifiers()) && 
        f.get((Object)null).equals(valObj)) {
        return f.getName();
      }
    } 

    
    return null;
  }
  
  public static void println(Object o) {
    System.out.println(toString(o));
  }
}
