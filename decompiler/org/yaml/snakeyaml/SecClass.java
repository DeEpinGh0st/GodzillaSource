package org.yaml.snakeyaml;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SecClass {
  public static final ArrayList<Class> whiteList = new ArrayList<>();
  static {
    whiteList.add(CharSequence.class);
    whiteList.add(Map.class);
    whiteList.add(List.class);
    whiteList.add(Number.class);
    whiteList.add(Set.class);
  }
  
  public static Class forName(String name) throws ClassNotFoundException {
    return forName(name, false, Thread.currentThread().getContextClassLoader());
  }
  
  public static Class forName(String name, boolean initialize, ClassLoader loader) throws ClassNotFoundException {
    Class<?> type = Class.forName(name, initialize, loader);
    if (!type.equals(Object.class) && 
      Object.class.isAssignableFrom(type) && 
      type.getAnnotation(YamlClass.class) == null && 
      !type.isArray()) {
      boolean ok = false;
      Iterator<Class<?>> classIterator = whiteList.iterator();
      while (classIterator.hasNext()) {
        Class whiteClass = classIterator.next();
        if (whiteClass.isAssignableFrom(type)) {
          ok = true;
          break;
        } 
      } 
      if (!ok) {
        throw new ClassNotFoundException(name);
      }
    } 


    
    return type;
  }
}
