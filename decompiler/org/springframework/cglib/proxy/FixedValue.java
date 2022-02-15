package org.springframework.cglib.proxy;

public interface FixedValue extends Callback {
  Object loadObject() throws Exception;
}
