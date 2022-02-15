package org.springframework.objenesis.instantiator.perc;

import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;

























@Instantiator(Typology.STANDARD)
public class PercInstantiator<T>
  implements ObjectInstantiator<T>
{
  private final Method newInstanceMethod;
  private final Object[] typeArgs = new Object[] { null, Boolean.FALSE };

  
  public PercInstantiator(Class<T> type) {
    this.typeArgs[0] = type;
    
    try {
      this.newInstanceMethod = ObjectInputStream.class.getDeclaredMethod("newInstance", new Class[] { Class.class, boolean.class });
      
      this.newInstanceMethod.setAccessible(true);
    }
    catch (RuntimeException|NoSuchMethodException e) {
      throw new ObjenesisException(e);
    } 
  }

  
  public T newInstance() {
    try {
      return (T)this.newInstanceMethod.invoke((Object)null, this.typeArgs);
    } catch (Exception e) {
      throw new ObjenesisException(e);
    } 
  }
}
