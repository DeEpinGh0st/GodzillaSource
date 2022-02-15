package org.springframework.core.convert.support;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.NumberUtils;



































final class StringToNumberConverterFactory
  implements ConverterFactory<String, Number>
{
  public <T extends Number> Converter<String, T> getConverter(Class<T> targetType) {
    return new StringToNumber<>(targetType);
  }
  
  private static final class StringToNumber<T extends Number>
    implements Converter<String, T>
  {
    private final Class<T> targetType;
    
    public StringToNumber(Class<T> targetType) {
      this.targetType = targetType;
    }

    
    @Nullable
    public T convert(String source) {
      if (source.isEmpty()) {
        return null;
      }
      return (T)NumberUtils.parseNumber(source, this.targetType);
    }
  }
}
