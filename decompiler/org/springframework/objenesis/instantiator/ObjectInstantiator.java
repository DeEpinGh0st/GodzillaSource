package org.springframework.objenesis.instantiator;

public interface ObjectInstantiator<T> {
  T newInstance();
}
