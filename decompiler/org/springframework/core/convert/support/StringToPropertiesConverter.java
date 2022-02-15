package org.springframework.core.convert.support;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import org.springframework.core.convert.converter.Converter;

























final class StringToPropertiesConverter
  implements Converter<String, Properties>
{
  public Properties convert(String source) {
    try {
      Properties props = new Properties();
      
      props.load(new ByteArrayInputStream(source.getBytes(StandardCharsets.ISO_8859_1)));
      return props;
    }
    catch (Exception ex) {
      
      throw new IllegalArgumentException("Failed to parse [" + source + "] into Properties", ex);
    } 
  }
}
