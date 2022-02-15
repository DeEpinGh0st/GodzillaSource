package org.springframework.core.convert.support;

import java.io.StringWriter;
import java.util.Collections;
import java.util.Set;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.lang.Nullable;


































final class FallbackObjectToStringConverter
  implements ConditionalGenericConverter
{
  public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
    return Collections.singleton(new GenericConverter.ConvertiblePair(Object.class, String.class));
  }

  
  public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
    Class<?> sourceClass = sourceType.getObjectType();
    if (String.class == sourceClass)
    {
      return false;
    }
    return (CharSequence.class.isAssignableFrom(sourceClass) || StringWriter.class
      .isAssignableFrom(sourceClass) || 
      ObjectToObjectConverter.hasConversionMethodOrConstructor(sourceClass, String.class));
  }

  
  @Nullable
  public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
    return (source != null) ? source.toString() : null;
  }
}
