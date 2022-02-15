package org.bouncycastle.jcajce.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import org.bouncycastle.crypto.io.InvalidCipherTextIOException;

public class CipherInputStream extends FilterInputStream {
  private final Cipher cipher;
  
  private final byte[] inputBuffer = new byte[512];
  
  private boolean finalized = false;
  
  private byte[] buf;
  
  private int maxBuf;
  
  private int bufOff;
  
  public CipherInputStream(InputStream paramInputStream, Cipher paramCipher) {
    super(paramInputStream);
    this.cipher = paramCipher;
  }
  
  private int nextChunk() throws IOException {
    if (this.finalized)
      return -1; 
    this.bufOff = 0;
    this.maxBuf = 0;
    while (this.maxBuf == 0) {
      int i = this.in.read(this.inputBuffer);
      if (i == -1) {
        this.buf = finaliseCipher();
        if (this.buf == null || this.buf.length == 0)
          return -1; 
        this.maxBuf = this.buf.length;
        return this.maxBuf;
      } 
      this.buf = this.cipher.update(this.inputBuffer, 0, i);
      if (this.buf != null)
        this.maxBuf = this.buf.length; 
    } 
    return this.maxBuf;
  }
  
  private byte[] finaliseCipher() throws InvalidCipherTextIOException {
    try {
      this.finalized = true;
      return this.cipher.doFinal();
    } catch (GeneralSecurityException generalSecurityException) {
      throw new InvalidCipherTextIOException("Error finalising cipher", generalSecurityException);
    } 
  }
  
  public int read() throws IOException {
    return (this.bufOff >= this.maxBuf && nextChunk() < 0) ? -1 : (this.buf[this.bufOff++] & 0xFF);
  }
  
  public int read(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    if (this.bufOff >= this.maxBuf && nextChunk() < 0)
      return -1; 
    int i = Math.min(paramInt2, available());
    System.arraycopy(this.buf, this.bufOff, paramArrayOfbyte, paramInt1, i);
    this.bufOff += i;
    return i;
  }
  
  public long skip(long paramLong) throws IOException {
    if (paramLong <= 0L)
      return 0L; 
    int i = (int)Math.min(paramLong, available());
    this.bufOff += i;
    return i;
  }
  
  public int available() throws IOException {
    return this.maxBuf - this.bufOff;
  }
  
  public void close() throws IOException {
    try {
      this.in.close();
    } finally {
      if (!this.finalized)
        finaliseCipher(); 
    } 
    this.maxBuf = this.bufOff = 0;
  }
  
  public void mark(int paramInt) {}
  
  public void reset() throws IOException {}
  
  public boolean markSupported() {
    return false;
  }
}
