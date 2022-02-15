package org.springframework.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.springframework.lang.Nullable;

public interface ParameterNameDiscoverer {
  @Nullable
  String[] getParameterNames(Method paramMethod);
  
  @Nullable
  String[] getParameterNames(Constructor<?> paramConstructor);
}
