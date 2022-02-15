package org.springframework.core.convert;

import org.springframework.lang.Nullable;

public interface ConversionService {
  boolean canConvert(@Nullable Class<?> paramClass1, Class<?> paramClass2);
  
  boolean canConvert(@Nullable TypeDescriptor paramTypeDescriptor1, TypeDescriptor paramTypeDescriptor2);
  
  @Nullable
  <T> T convert(@Nullable Object paramObject, Class<T> paramClass);
  
  @Nullable
  Object convert(@Nullable Object paramObject, @Nullable TypeDescriptor paramTypeDescriptor1, TypeDescriptor paramTypeDescriptor2);
}
