package org.springframework.core.convert.support;

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.springframework.core.convert.converter.Converter;































final class ZonedDateTimeToCalendarConverter
  implements Converter<ZonedDateTime, Calendar>
{
  public Calendar convert(ZonedDateTime source) {
    return GregorianCalendar.from(source);
  }
}
