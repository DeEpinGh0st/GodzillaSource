package org.springframework.core.annotation;

import java.lang.reflect.Method;
import org.springframework.lang.Nullable;

@FunctionalInterface
interface ValueExtractor {
  @Nullable
  Object extract(Method paramMethod, @Nullable Object paramObject);
}
