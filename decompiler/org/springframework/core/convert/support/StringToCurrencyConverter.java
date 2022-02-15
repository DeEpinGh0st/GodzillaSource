package org.springframework.core.convert.support;

import java.util.Currency;
import org.springframework.core.convert.converter.Converter;
























class StringToCurrencyConverter
  implements Converter<String, Currency>
{
  public Currency convert(String source) {
    return Currency.getInstance(source);
  }
}
