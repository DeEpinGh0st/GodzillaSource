package org.springframework.core.convert.support;

import java.nio.charset.Charset;
import org.springframework.core.convert.converter.Converter;
























class StringToCharsetConverter
  implements Converter<String, Charset>
{
  public Charset convert(String source) {
    return Charset.forName(source);
  }
}
