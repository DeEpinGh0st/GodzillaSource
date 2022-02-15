package org.springframework.objenesis.instantiator.android;

import java.io.ObjectStreamClass;
import java.lang.reflect.Method;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;






















@Instantiator(Typology.SERIALIZATION)
public class AndroidSerializationInstantiator<T>
  implements ObjectInstantiator<T>
{
  private final Class<T> type;
  private final ObjectStreamClass objectStreamClass;
  private final Method newInstanceMethod;
  
  public AndroidSerializationInstantiator(Class<T> type) {
    Method m;
    this.type = type;
    this.newInstanceMethod = getNewInstanceMethod();
    
    try {
      m = ObjectStreamClass.class.getMethod("lookupAny", new Class[] { Class.class });
    } catch (NoSuchMethodException e) {
      throw new ObjenesisException(e);
    } 
    try {
      this.objectStreamClass = (ObjectStreamClass)m.invoke((Object)null, new Object[] { type });
    } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
      throw new ObjenesisException(e);
    } 
  }
  
  public T newInstance() {
    try {
      return this.type.cast(this.newInstanceMethod.invoke(this.objectStreamClass, new Object[] { this.type }));
    }
    catch (IllegalAccessException|IllegalArgumentException|java.lang.reflect.InvocationTargetException e) {
      throw new ObjenesisException(e);
    } 
  }
  
  private static Method getNewInstanceMethod() {
    try {
      Method newInstanceMethod = ObjectStreamClass.class.getDeclaredMethod("newInstance", new Class[] { Class.class });
      
      newInstanceMethod.setAccessible(true);
      return newInstanceMethod;
    }
    catch (RuntimeException|NoSuchMethodException e) {
      throw new ObjenesisException(e);
    } 
  }
}
