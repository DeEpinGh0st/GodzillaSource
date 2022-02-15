package org.bouncycastle.est;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ESTException extends IOException {
  private Throwable cause;
  
  private InputStream body;
  
  private int statusCode;
  
  private static final long MAX_ERROR_BODY = 8192L;
  
  public ESTException(String paramString) {
    this(paramString, null);
  }
  
  public ESTException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
    this.body = null;
    this.statusCode = 0;
  }
  
  public ESTException(String paramString, Throwable paramThrowable, int paramInt, InputStream paramInputStream) {
    super(paramString);
    this.cause = paramThrowable;
    this.statusCode = paramInt;
    if (paramInputStream != null) {
      byte[] arrayOfByte = new byte[8192];
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      try {
        int i;
        for (i = paramInputStream.read(arrayOfByte); i >= 0; i = paramInputStream.read(arrayOfByte)) {
          if ((byteArrayOutputStream.size() + i) > 8192L) {
            i = 8192 - byteArrayOutputStream.size();
            byteArrayOutputStream.write(arrayOfByte, 0, i);
            break;
          } 
          byteArrayOutputStream.write(arrayOfByte, 0, i);
        } 
        byteArrayOutputStream.flush();
        byteArrayOutputStream.close();
        this.body = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        paramInputStream.close();
      } catch (Exception exception) {}
    } else {
      this.body = null;
    } 
  }
  
  public Throwable getCause() {
    return this.cause;
  }
  
  public String getMessage() {
    return super.getMessage() + " HTTP Status Code: " + this.statusCode;
  }
  
  public InputStream getBody() {
    return this.body;
  }
  
  public int getStatusCode() {
    return this.statusCode;
  }
}
