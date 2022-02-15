package org.springframework.core.serializer.support;

import org.springframework.core.NestedRuntimeException;































public class SerializationFailedException
  extends NestedRuntimeException
{
  public SerializationFailedException(String message) {
    super(message);
  }






  
  public SerializationFailedException(String message, Throwable cause) {
    super(message, cause);
  }
}
