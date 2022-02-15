package org.springframework.core.convert.support;

import java.util.HashSet;
import java.util.Set;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
























final class StringToBooleanConverter
  implements Converter<String, Boolean>
{
  private static final Set<String> trueValues = new HashSet<>(8);
  
  private static final Set<String> falseValues = new HashSet<>(8);
  
  static {
    trueValues.add("true");
    trueValues.add("on");
    trueValues.add("yes");
    trueValues.add("1");
    
    falseValues.add("false");
    falseValues.add("off");
    falseValues.add("no");
    falseValues.add("0");
  }


  
  @Nullable
  public Boolean convert(String source) {
    String value = source.trim();
    if (value.isEmpty()) {
      return null;
    }
    value = value.toLowerCase();
    if (trueValues.contains(value)) {
      return Boolean.TRUE;
    }
    if (falseValues.contains(value)) {
      return Boolean.FALSE;
    }
    
    throw new IllegalArgumentException("Invalid boolean value '" + source + "'");
  }
}
