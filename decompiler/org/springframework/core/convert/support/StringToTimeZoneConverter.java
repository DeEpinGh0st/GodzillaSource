package org.springframework.core.convert.support;

import java.util.TimeZone;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;
























class StringToTimeZoneConverter
  implements Converter<String, TimeZone>
{
  public TimeZone convert(String source) {
    return StringUtils.parseTimeZoneString(source);
  }
}
