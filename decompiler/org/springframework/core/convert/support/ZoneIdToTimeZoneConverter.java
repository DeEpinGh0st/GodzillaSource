package org.springframework.core.convert.support;

import java.time.ZoneId;
import java.util.TimeZone;
import org.springframework.core.convert.converter.Converter;































final class ZoneIdToTimeZoneConverter
  implements Converter<ZoneId, TimeZone>
{
  public TimeZone convert(ZoneId source) {
    return TimeZone.getTimeZone(source);
  }
}
