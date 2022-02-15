package org.springframework.cglib.proxy;

public interface Dispatcher extends Callback {
  Object loadObject() throws Exception;
}
