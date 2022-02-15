package org.springframework.objenesis.instantiator.sun;

import java.lang.reflect.Constructor;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;

























@Instantiator(Typology.STANDARD)
public class SunReflectionFactoryInstantiator<T>
  implements ObjectInstantiator<T>
{
  private final Constructor<T> mungedConstructor;
  
  public SunReflectionFactoryInstantiator(Class<T> type) {
    Constructor<Object> javaLangObjectConstructor = getJavaLangObjectConstructor();
    this.mungedConstructor = SunReflectionFactoryHelper.newConstructorForSerialization(type, javaLangObjectConstructor);
    
    this.mungedConstructor.setAccessible(true);
  }
  
  public T newInstance() {
    try {
      return this.mungedConstructor.newInstance((Object[])null);
    }
    catch (Exception e) {
      throw new ObjenesisException(e);
    } 
  }
  
  private static Constructor<Object> getJavaLangObjectConstructor() {
    try {
      return Object.class.getConstructor((Class[])null);
    }
    catch (NoSuchMethodException e) {
      throw new ObjenesisException(e);
    } 
  }
}
