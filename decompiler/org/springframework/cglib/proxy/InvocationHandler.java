package org.springframework.cglib.proxy;

import java.lang.reflect.Method;

public interface InvocationHandler extends Callback {
  Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject) throws Throwable;
}
