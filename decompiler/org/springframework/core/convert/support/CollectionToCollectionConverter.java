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





























final class CollectionToCollectionConverter
  implements ConditionalGenericConverter
{
  private final ConversionService conversionService;
  
  public CollectionToCollectionConverter(ConversionService conversionService) {
    this.conversionService = conversionService;
  }


  
  public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
    return Collections.singleton(new GenericConverter.ConvertiblePair(Collection.class, Collection.class));
  }

  
  public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
    return ConversionUtils.canConvertElements(sourceType
        .getElementTypeDescriptor(), targetType.getElementTypeDescriptor(), this.conversionService);
  }

  
  @Nullable
  public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
    if (source == null) {
      return null;
    }
    Collection<?> sourceCollection = (Collection)source;

    
    boolean copyRequired = !targetType.getType().isInstance(source);
    if (!copyRequired && sourceCollection.isEmpty()) {
      return source;
    }
    TypeDescriptor elementDesc = targetType.getElementTypeDescriptor();
    if (elementDesc == null && !copyRequired) {
      return source;
    }

    
    Collection<Object> target = CollectionFactory.createCollection(targetType.getType(), (elementDesc != null) ? elementDesc
        .getType() : null, sourceCollection.size());
    
    if (elementDesc == null) {
      target.addAll(sourceCollection);
    } else {
      
      for (Object sourceElement : sourceCollection) {
        Object targetElement = this.conversionService.convert(sourceElement, sourceType
            .elementTypeDescriptor(sourceElement), elementDesc);
        target.add(targetElement);
        if (sourceElement != targetElement) {
          copyRequired = true;
        }
      } 
    } 
    
    return copyRequired ? target : source;
  }
}
