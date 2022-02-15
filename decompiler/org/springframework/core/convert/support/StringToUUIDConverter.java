package org.springframework.core.convert.support;

import java.util.UUID;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

























final class StringToUUIDConverter
  implements Converter<String, UUID>
{
  @Nullable
  public UUID convert(String source) {
    return StringUtils.hasText(source) ? UUID.fromString(source.trim()) : null;
  }
}
