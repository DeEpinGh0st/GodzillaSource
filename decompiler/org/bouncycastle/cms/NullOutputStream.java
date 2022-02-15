package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;

class NullOutputStream extends OutputStream {
  public void write(byte[] paramArrayOfbyte) throws IOException {}
  
  public void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {}
  
  public void write(int paramInt) throws IOException {}
}
