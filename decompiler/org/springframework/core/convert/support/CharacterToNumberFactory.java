package org.springframework.core.convert.support;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.util.NumberUtils;



































final class CharacterToNumberFactory
  implements ConverterFactory<Character, Number>
{
  public <T extends Number> Converter<Character, T> getConverter(Class<T> targetType) {
    return new CharacterToNumber<>(targetType);
  }
  
  private static final class CharacterToNumber<T extends Number>
    implements Converter<Character, T> {
    private final Class<T> targetType;
    
    public CharacterToNumber(Class<T> targetType) {
      this.targetType = targetType;
    }

    
    public T convert(Character source) {
      return (T)NumberUtils.convertNumberToTargetClass(Short.valueOf((short)source.charValue()), this.targetType);
    }
  }
}
