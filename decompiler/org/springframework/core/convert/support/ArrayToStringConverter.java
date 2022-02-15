package org.springframework.core.convert.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

























final class ArrayToStringConverter
  implements ConditionalGenericConverter
{
  private final CollectionToStringConverter helperConverter;
  
  public ArrayToStringConverter(ConversionService conversionService) {
    this.helperConverter = new CollectionToStringConverter(conversionService);
  }


  
  public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
    return Collections.singleton(new GenericConverter.ConvertiblePair(Object[].class, String.class));
  }

  
  public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
    return this.helperConverter.matches(sourceType, targetType);
  }

  
  @Nullable
  public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
    return this.helperConverter.convert(Arrays.asList(ObjectUtils.toObjectArray(source)), sourceType, targetType);
  }
}
