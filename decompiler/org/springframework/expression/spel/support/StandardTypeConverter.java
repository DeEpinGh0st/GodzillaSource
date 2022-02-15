package org.springframework.expression.spel.support;

import java.util.function.Supplier;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;































public class StandardTypeConverter
  implements TypeConverter
{
  private final Supplier<ConversionService> conversionService;
  
  public StandardTypeConverter() {
    this.conversionService = DefaultConversionService::getSharedInstance;
  }




  
  public StandardTypeConverter(ConversionService conversionService) {
    Assert.notNull(conversionService, "ConversionService must not be null");
    this.conversionService = (() -> conversionService);
  }





  
  public StandardTypeConverter(Supplier<ConversionService> conversionService) {
    Assert.notNull(conversionService, "Supplier must not be null");
    this.conversionService = conversionService;
  }


  
  public boolean canConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
    return ((ConversionService)this.conversionService.get()).canConvert(sourceType, targetType);
  }

  
  @Nullable
  public Object convertValue(@Nullable Object value, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
    try {
      return ((ConversionService)this.conversionService.get()).convert(value, sourceType, targetType);
    }
    catch (ConversionException ex) {
      throw new SpelEvaluationException(ex, SpelMessage.TYPE_CONVERSION_ERROR, new Object[] { (sourceType != null) ? sourceType
            .toString() : ((value != null) ? value.getClass().getName() : "null"), targetType
            .toString() });
    } 
  }
}
