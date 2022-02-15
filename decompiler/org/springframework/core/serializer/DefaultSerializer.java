package org.springframework.core.serializer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;































public class DefaultSerializer
  implements Serializer<Object>
{
  public void serialize(Object object, OutputStream outputStream) throws IOException {
    if (!(object instanceof java.io.Serializable)) {
      throw new IllegalArgumentException(getClass().getSimpleName() + " requires a Serializable payload but received an object of type [" + object
          .getClass().getName() + "]");
    }
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
    objectOutputStream.writeObject(object);
    objectOutputStream.flush();
  }
}
