package org.springframework.core.convert.support;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import org.springframework.core.CollectionFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.lang.Nullable;

























final class ObjectToCollectionConverter
  implements ConditionalGenericConverter
{
  private final ConversionService conversionService;
  
  public ObjectToCollectionConverter(ConversionService conversionService) {
    this.conversionService = conversionService;
  }


  
  public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
    return Collections.singleton(new GenericConverter.ConvertiblePair(Object.class, Collection.class));
  }

  
  public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
    return ConversionUtils.canConvertElements(sourceType, targetType.getElementTypeDescriptor(), this.conversionService);
  }

  
  @Nullable
  public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
    if (source == null) {
      return null;
    }
    
    TypeDescriptor elementDesc = targetType.getElementTypeDescriptor();
    Collection<Object> target = CollectionFactory.createCollection(targetType.getType(), (elementDesc != null) ? elementDesc
        .getType() : null, 1);
    
    if (elementDesc == null || elementDesc.isCollection()) {
      target.add(source);
    } else {
      
      Object singleElement = this.conversionService.convert(source, sourceType, elementDesc);
      target.add(singleElement);
    } 
    return target;
  }
}
