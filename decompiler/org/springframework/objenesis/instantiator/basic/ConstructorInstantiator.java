package org.springframework.objenesis.instantiator.basic;

import java.lang.reflect.Constructor;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;
























@Instantiator(Typology.NOT_COMPLIANT)
public class ConstructorInstantiator<T>
  implements ObjectInstantiator<T>
{
  protected Constructor<T> constructor;
  
  public ConstructorInstantiator(Class<T> type) {
    try {
      this.constructor = type.getDeclaredConstructor((Class[])null);
    }
    catch (Exception e) {
      throw new ObjenesisException(e);
    } 
  }
  
  public T newInstance() {
    try {
      return this.constructor.newInstance((Object[])null);
    }
    catch (Exception e) {
      throw new ObjenesisException(e);
    } 
  }
}
