package org.springframework.core.convert.support;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;























final class EnumToStringConverter
  extends AbstractConditionalEnumConverter
  implements Converter<Enum<?>, String>
{
  public EnumToStringConverter(ConversionService conversionService) {
    super(conversionService);
  }

  
  public String convert(Enum<?> source) {
    return source.name();
  }
}
