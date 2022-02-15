package org.springframework.cglib.proxy;

public interface LazyLoader extends Callback {
  Object loadObject() throws Exception;
}
