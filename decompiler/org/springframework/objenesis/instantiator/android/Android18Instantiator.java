package org.springframework.objenesis.instantiator.android;

import java.io.ObjectStreamClass;
import java.lang.reflect.Method;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;






















@Instantiator(Typology.STANDARD)
public class Android18Instantiator<T>
  implements ObjectInstantiator<T>
{
  private final Class<T> type;
  private final Method newInstanceMethod;
  private final Long objectConstructorId;
  
  public Android18Instantiator(Class<T> type) {
    this.type = type;
    this.newInstanceMethod = getNewInstanceMethod();
    this.objectConstructorId = findConstructorIdForJavaLangObjectConstructor();
  }
  
  public T newInstance() {
    try {
      return this.type.cast(this.newInstanceMethod.invoke((Object)null, new Object[] { this.type, this.objectConstructorId }));
    }
    catch (Exception e) {
      throw new ObjenesisException(e);
    } 
  }
  
  private static Method getNewInstanceMethod() {
    try {
      Method newInstanceMethod = ObjectStreamClass.class.getDeclaredMethod("newInstance", new Class[] { Class.class, long.class });
      
      newInstanceMethod.setAccessible(true);
      return newInstanceMethod;
    }
    catch (RuntimeException|NoSuchMethodException e) {
      throw new ObjenesisException(e);
    } 
  }
  
  private static Long findConstructorIdForJavaLangObjectConstructor() {
    try {
      Method newInstanceMethod = ObjectStreamClass.class.getDeclaredMethod("getConstructorId", new Class[] { Class.class });
      
      newInstanceMethod.setAccessible(true);
      
      return (Long)newInstanceMethod.invoke((Object)null, new Object[] { Object.class });
    }
    catch (RuntimeException|NoSuchMethodException|java.lang.reflect.InvocationTargetException|IllegalAccessException e) {
      throw new ObjenesisException(e);
    } 
  }
}
