package org.springframework.core.serializer;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.core.ConfigurableObjectInputStream;
import org.springframework.core.NestedIOException;
import org.springframework.lang.Nullable;

































public class DefaultDeserializer
  implements Deserializer<Object>
{
  @Nullable
  private final ClassLoader classLoader;
  
  public DefaultDeserializer() {
    this.classLoader = null;
  }






  
  public DefaultDeserializer(@Nullable ClassLoader classLoader) {
    this.classLoader = classLoader;
  }








  
  public Object deserialize(InputStream inputStream) throws IOException {
    ConfigurableObjectInputStream configurableObjectInputStream = new ConfigurableObjectInputStream(inputStream, this.classLoader);
    try {
      return configurableObjectInputStream.readObject();
    }
    catch (ClassNotFoundException ex) {
      throw new NestedIOException("Failed to deserialize object type", ex);
    } 
  }
}
