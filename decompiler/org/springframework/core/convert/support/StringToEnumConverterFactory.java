package org.springframework.core.convert.support;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.lang.Nullable;

























final class StringToEnumConverterFactory
  implements ConverterFactory<String, Enum>
{
  public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
    return new StringToEnum<>((Class)ConversionUtils.getEnumType(targetType));
  }
  
  private static class StringToEnum<T extends Enum>
    implements Converter<String, T>
  {
    private final Class<T> enumType;
    
    StringToEnum(Class<T> enumType) {
      this.enumType = enumType;
    }

    
    @Nullable
    public T convert(String source) {
      if (source.isEmpty())
      {
        return null;
      }
      return (T)Enum.valueOf(this.enumType, source.trim());
    }
  }
}
