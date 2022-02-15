package org.springframework.core.io;

import org.springframework.lang.Nullable;

@FunctionalInterface
public interface ProtocolResolver {
  @Nullable
  Resource resolve(String paramString, ResourceLoader paramResourceLoader);
}
