package org.springframework.objenesis.instantiator.basic;

import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;
























@Instantiator(Typology.NOT_COMPLIANT)
public class FailingInstantiator<T>
  implements ObjectInstantiator<T>
{
  public FailingInstantiator(Class<T> type) {}
  
  public T newInstance() {
    throw new ObjenesisException("Always failing");
  }
}
