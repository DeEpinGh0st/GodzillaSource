package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Map;

@Beta
public interface TypeToInstanceMap<B> extends Map<TypeToken<? extends B>, B> {
  <T extends B> T getInstance(Class<T> paramClass);
  
  <T extends B> T getInstance(TypeToken<T> paramTypeToken);
  
  @CanIgnoreReturnValue
  <T extends B> T putInstance(Class<T> paramClass, T paramT);
  
  @CanIgnoreReturnValue
  <T extends B> T putInstance(TypeToken<T> paramTypeToken, T paramT);
}
