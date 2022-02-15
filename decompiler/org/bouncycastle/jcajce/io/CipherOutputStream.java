package org.bouncycastle.jcajce.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import org.bouncycastle.crypto.io.InvalidCipherTextIOException;

public class CipherOutputStream extends FilterOutputStream {
  private final Cipher cipher;
  
  private final byte[] oneByte = new byte[1];
  
  public CipherOutputStream(OutputStream paramOutputStream, Cipher paramCipher) {
    super(paramOutputStream);
    this.cipher = paramCipher;
  }
  
  public void write(int paramInt) throws IOException {
    this.oneByte[0] = (byte)paramInt;
    write(this.oneByte, 0, 1);
  }
  
  public void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    byte[] arrayOfByte = this.cipher.update(paramArrayOfbyte, paramInt1, paramInt2);
    if (arrayOfByte != null)
      this.out.write(arrayOfByte); 
  }
  
  public void flush() throws IOException {
    this.out.flush();
  }
  
  public void close() throws IOException {
    IOException iOException;
    InvalidCipherTextIOException invalidCipherTextIOException = null;
    try {
      byte[] arrayOfByte = this.cipher.doFinal();
      if (arrayOfByte != null)
        this.out.write(arrayOfByte); 
    } catch (GeneralSecurityException generalSecurityException) {
      invalidCipherTextIOException = new InvalidCipherTextIOException("Error during cipher finalisation", generalSecurityException);
    } catch (Exception exception) {
      iOException = new IOException("Error closing stream: " + exception);
    } 
    try {
      flush();
      this.out.close();
    } catch (IOException iOException1) {
      if (iOException == null)
        iOException = iOException1; 
    } 
    if (iOException != null)
      throw iOException; 
  }
}
