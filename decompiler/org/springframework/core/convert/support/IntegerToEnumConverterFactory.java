package org.springframework.core.convert.support;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

























final class IntegerToEnumConverterFactory
  implements ConverterFactory<Integer, Enum>
{
  public <T extends Enum> Converter<Integer, T> getConverter(Class<T> targetType) {
    return new IntegerToEnum<>((Class)ConversionUtils.getEnumType(targetType));
  }
  
  private static class IntegerToEnum<T extends Enum>
    implements Converter<Integer, T>
  {
    private final Class<T> enumType;
    
    public IntegerToEnum(Class<T> enumType) {
      this.enumType = enumType;
    }

    
    public T convert(Integer source) {
      return (T)((Enum[])this.enumType.getEnumConstants())[source.intValue()];
    }
  }
}
