package org.springframework.core.serializer.support;

import java.io.ByteArrayInputStream;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.DefaultDeserializer;
import org.springframework.core.serializer.Deserializer;
import org.springframework.util.Assert;

































public class DeserializingConverter
  implements Converter<byte[], Object>
{
  private final Deserializer<Object> deserializer;
  
  public DeserializingConverter() {
    this.deserializer = (Deserializer<Object>)new DefaultDeserializer();
  }






  
  public DeserializingConverter(ClassLoader classLoader) {
    this.deserializer = (Deserializer<Object>)new DefaultDeserializer(classLoader);
  }



  
  public DeserializingConverter(Deserializer<Object> deserializer) {
    Assert.notNull(deserializer, "Deserializer must not be null");
    this.deserializer = deserializer;
  }


  
  public Object convert(byte[] source) {
    ByteArrayInputStream byteStream = new ByteArrayInputStream(source);
    try {
      return this.deserializer.deserialize(byteStream);
    }
    catch (Throwable ex) {
      throw new SerializationFailedException("Failed to deserialize payload. Is the byte array a result of corresponding serialization for " + this.deserializer
          
          .getClass().getSimpleName() + "?", ex);
    } 
  }
}
