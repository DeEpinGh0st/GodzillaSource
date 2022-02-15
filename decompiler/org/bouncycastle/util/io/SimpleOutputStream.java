package org.bouncycastle.util.io;

import java.io.IOException;
import java.io.OutputStream;

public abstract class SimpleOutputStream extends OutputStream {
  public void close() {}
  
  public void flush() {}
  
  public void write(int paramInt) throws IOException {
    byte[] arrayOfByte = { (byte)paramInt };
    write(arrayOfByte, 0, 1);
  }
}
