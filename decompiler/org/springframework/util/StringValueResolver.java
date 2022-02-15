package org.springframework.util;

import org.springframework.lang.Nullable;

@FunctionalInterface
public interface StringValueResolver {
  @Nullable
  String resolveStringValue(String paramString);
}
