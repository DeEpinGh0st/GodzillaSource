package org.springframework.objenesis.instantiator.gcj;

import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;























@Instantiator(Typology.STANDARD)
public class GCJInstantiator<T>
  extends GCJInstantiatorBase<T>
{
  public GCJInstantiator(Class<T> type) {
    super(type);
  }

  
  public T newInstance() {
    try {
      return this.type.cast(newObjectMethod.invoke(dummyStream, new Object[] { this.type, Object.class }));
    }
    catch (RuntimeException|IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
      throw new ObjenesisException(e);
    } 
  }
}
