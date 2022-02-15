package org.springframework.expression;

import java.lang.reflect.Method;
import java.util.List;

@FunctionalInterface
public interface MethodFilter {
  List<Method> filter(List<Method> paramList);
}
