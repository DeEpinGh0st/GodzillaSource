package org.springframework.util;































public class InvalidMimeTypeException
  extends IllegalArgumentException
{
  private final String mimeType;
  
  public InvalidMimeTypeException(String mimeType, String message) {
    super("Invalid mime type \"" + mimeType + "\": " + message);
    this.mimeType = mimeType;
  }




  
  public String getMimeType() {
    return this.mimeType;
  }
}
