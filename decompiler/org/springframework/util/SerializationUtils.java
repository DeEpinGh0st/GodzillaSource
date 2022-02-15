package org.springframework.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.springframework.lang.Nullable;





























public abstract class SerializationUtils
{
  @Nullable
  public static byte[] serialize(@Nullable Object object) {
    if (object == null) {
      return null;
    }
    ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
    try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
      oos.writeObject(object);
      oos.flush();
    }
    catch (IOException ex) {
      throw new IllegalArgumentException("Failed to serialize object of type: " + object.getClass(), ex);
    } 
    return baos.toByteArray();
  }





  
  @Nullable
  public static Object deserialize(@Nullable byte[] bytes) {
    if (bytes == null) {
      return null;
    }
    try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
      return ois.readObject();
    }
    catch (IOException ex) {
      throw new IllegalArgumentException("Failed to deserialize object", ex);
    }
    catch (ClassNotFoundException ex) {
      throw new IllegalStateException("Failed to deserialize object type", ex);
    } 
  }
}
