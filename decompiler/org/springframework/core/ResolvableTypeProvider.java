package org.springframework.core;

import org.springframework.lang.Nullable;

public interface ResolvableTypeProvider {
  @Nullable
  ResolvableType getResolvableType();
}
