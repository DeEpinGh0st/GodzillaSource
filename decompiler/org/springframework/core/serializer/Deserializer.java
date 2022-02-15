package org.springframework.core.serializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;











































@FunctionalInterface
public interface Deserializer<T>
{
  T deserialize(InputStream paramInputStream) throws IOException;
  
  default T deserializeFromByteArray(byte[] serialized) throws IOException {
    return deserialize(new ByteArrayInputStream(serialized));
  }
}
