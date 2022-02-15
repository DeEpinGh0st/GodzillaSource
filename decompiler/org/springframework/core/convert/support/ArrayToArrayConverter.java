package org.springframework.core.convert.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;



























final class ArrayToArrayConverter
  implements ConditionalGenericConverter
{
  private final CollectionToArrayConverter helperConverter;
  private final ConversionService conversionService;
  
  public ArrayToArrayConverter(ConversionService conversionService) {
    this.helperConverter = new CollectionToArrayConverter(conversionService);
    this.conversionService = conversionService;
  }


  
  public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
    return Collections.singleton(new GenericConverter.ConvertiblePair(Object[].class, Object[].class));
  }

  
  public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
    return this.helperConverter.matches(sourceType, targetType);
  }

  
  @Nullable
  public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
    if (this.conversionService instanceof GenericConversionService) {
      TypeDescriptor targetElement = targetType.getElementTypeDescriptor();
      if (targetElement != null && ((GenericConversionService)this.conversionService)
        .canBypassConvert(sourceType
          .getElementTypeDescriptor(), targetElement)) {
        return source;
      }
    } 
    List<Object> sourceList = Arrays.asList(ObjectUtils.toObjectArray(source));
    return this.helperConverter.convert(sourceList, sourceType, targetType);
  }
}
