package org.springframework.objenesis.instantiator.android;

import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;






















@Instantiator(Typology.STANDARD)
public class Android10Instantiator<T>
  implements ObjectInstantiator<T>
{
  private final Class<T> type;
  private final Method newStaticMethod;
  
  public Android10Instantiator(Class<T> type) {
    this.type = type;
    this.newStaticMethod = getNewStaticMethod();
  }
  
  public T newInstance() {
    try {
      return this.type.cast(this.newStaticMethod.invoke((Object)null, new Object[] { this.type, Object.class }));
    }
    catch (Exception e) {
      throw new ObjenesisException(e);
    } 
  }
  
  private static Method getNewStaticMethod() {
    try {
      Method newStaticMethod = ObjectInputStream.class.getDeclaredMethod("newInstance", new Class[] { Class.class, Class.class });
      
      newStaticMethod.setAccessible(true);
      return newStaticMethod;
    }
    catch (RuntimeException|NoSuchMethodException e) {
      throw new ObjenesisException(e);
    } 
  }
}
