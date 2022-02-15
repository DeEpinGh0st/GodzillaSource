package org.springframework.cglib.proxy;

public interface ProxyRefDispatcher extends Callback {
  Object loadObject(Object paramObject) throws Exception;
}
