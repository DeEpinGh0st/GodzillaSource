package org.springframework.objenesis.instantiator.basic;

import java.io.ObjectStreamClass;
import java.lang.reflect.Method;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;

























@Instantiator(Typology.SERIALIZATION)
public class ObjectStreamClassInstantiator<T>
  implements ObjectInstantiator<T>
{
  private static Method newInstanceMethod;
  private final ObjectStreamClass objStreamClass;
  
  private static void initialize() {
    if (newInstanceMethod == null) {
      try {
        newInstanceMethod = ObjectStreamClass.class.getDeclaredMethod("newInstance", new Class[0]);
        newInstanceMethod.setAccessible(true);
      }
      catch (RuntimeException|NoSuchMethodException e) {
        throw new ObjenesisException(e);
      } 
    }
  }


  
  public ObjectStreamClassInstantiator(Class<T> type) {
    initialize();
    this.objStreamClass = ObjectStreamClass.lookup(type);
  }


  
  public T newInstance() {
    try {
      return (T)newInstanceMethod.invoke(this.objStreamClass, new Object[0]);
    }
    catch (Exception e) {
      throw new ObjenesisException(e);
    } 
  }
}
