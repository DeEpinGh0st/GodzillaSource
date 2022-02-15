package org.springframework.objenesis.instantiator.sun;

import java.io.NotSerializableException;
import java.lang.reflect.Constructor;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.instantiator.SerializationInstantiatorHelper;
import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;


























@Instantiator(Typology.SERIALIZATION)
public class SunReflectionFactorySerializationInstantiator<T>
  implements ObjectInstantiator<T>
{
  private final Constructor<T> mungedConstructor;
  
  public SunReflectionFactorySerializationInstantiator(Class<T> type) {
    Constructor<? super T> nonSerializableAncestorConstructor;
    Class<? super T> nonSerializableAncestor = SerializationInstantiatorHelper.getNonSerializableSuperClass(type);


    
    try {
      nonSerializableAncestorConstructor = nonSerializableAncestor.getDeclaredConstructor((Class[])null);
    }
    catch (NoSuchMethodException e) {
      throw new ObjenesisException(new NotSerializableException(type + " has no suitable superclass constructor"));
    } 
    
    this.mungedConstructor = SunReflectionFactoryHelper.newConstructorForSerialization(type, nonSerializableAncestorConstructor);
    
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
}
