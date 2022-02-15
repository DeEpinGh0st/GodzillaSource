package org.springframework.expression;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;

public interface TypeConverter {
  boolean canConvert(@Nullable TypeDescriptor paramTypeDescriptor1, TypeDescriptor paramTypeDescriptor2);
  
  @Nullable
  Object convertValue(@Nullable Object paramObject, @Nullable TypeDescriptor paramTypeDescriptor1, TypeDescriptor paramTypeDescriptor2);
}
