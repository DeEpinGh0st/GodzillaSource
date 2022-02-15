package org.springframework.core.convert.support;

import java.util.Locale;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;





























final class StringToLocaleConverter
  implements Converter<String, Locale>
{
  @Nullable
  public Locale convert(String source) {
    return StringUtils.parseLocale(source);
  }
}
