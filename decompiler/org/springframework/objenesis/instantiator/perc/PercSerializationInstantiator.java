package org.springframework.objenesis.instantiator.perc;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;






























@Instantiator(Typology.SERIALIZATION)
public class PercSerializationInstantiator<T>
  implements ObjectInstantiator<T>
{
  private final Object[] typeArgs;
  private final Method newInstanceMethod;
  
  public PercSerializationInstantiator(Class<T> type) {
    Class<? super T> unserializableType = type;
    
    while (Serializable.class.isAssignableFrom(unserializableType)) {
      unserializableType = unserializableType.getSuperclass();
    }

    
    try {
      Class<?> percMethodClass = Class.forName("COM.newmonics.PercClassLoader.Method");
      
      this.newInstanceMethod = ObjectInputStream.class.getDeclaredMethod("noArgConstruct", new Class[] { Class.class, Object.class, percMethodClass });
      
      this.newInstanceMethod.setAccessible(true);

      
      Class<?> percClassClass = Class.forName("COM.newmonics.PercClassLoader.PercClass");
      Method getPercClassMethod = percClassClass.getDeclaredMethod("getPercClass", new Class[] { Class.class });
      Object someObject = getPercClassMethod.invoke((Object)null, new Object[] { unserializableType });
      Method findMethodMethod = someObject.getClass().getDeclaredMethod("findMethod", new Class[] { String.class });
      
      Object percMethod = findMethodMethod.invoke(someObject, new Object[] { "<init>()V" });
      
      this.typeArgs = new Object[] { unserializableType, type, percMethod };
    
    }
    catch (ClassNotFoundException|NoSuchMethodException|IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
      throw new ObjenesisException(e);
    } 
  }

  
  public T newInstance() {
    try {
      return (T)this.newInstanceMethod.invoke((Object)null, this.typeArgs);
    }
    catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
      throw new ObjenesisException(e);
    } 
  }
}
