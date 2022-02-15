package org.springframework.core.convert.support;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;






















final class EnumToIntegerConverter
  extends AbstractConditionalEnumConverter
  implements Converter<Enum<?>, Integer>
{
  public EnumToIntegerConverter(ConversionService conversionService) {
    super(conversionService);
  }

  
  public Integer convert(Enum<?> source) {
    return Integer.valueOf(source.ordinal());
  }
}
