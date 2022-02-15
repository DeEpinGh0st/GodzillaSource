package org.springframework.objenesis.instantiator.basic;

import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;
























@Instantiator(Typology.NOT_COMPLIANT)
public class NullInstantiator<T>
  implements ObjectInstantiator<T>
{
  public NullInstantiator(Class<T> type) {}
  
  public T newInstance() {
    return null;
  }
}
