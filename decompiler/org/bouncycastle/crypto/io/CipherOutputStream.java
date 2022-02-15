package org.bouncycastle.crypto.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.modes.AEADBlockCipher;

public class CipherOutputStream extends FilterOutputStream {
  private BufferedBlockCipher bufferedBlockCipher;
  
  private StreamCipher streamCipher;
  
  private AEADBlockCipher aeadBlockCipher;
  
  private final byte[] oneByte = new byte[1];
  
  private byte[] buf;
  
  public CipherOutputStream(OutputStream paramOutputStream, BufferedBlockCipher paramBufferedBlockCipher) {
    super(paramOutputStream);
    this.bufferedBlockCipher = paramBufferedBlockCipher;
  }
  
  public CipherOutputStream(OutputStream paramOutputStream, StreamCipher paramStreamCipher) {
    super(paramOutputStream);
    this.streamCipher = paramStreamCipher;
  }
  
  public CipherOutputStream(OutputStream paramOutputStream, AEADBlockCipher paramAEADBlockCipher) {
    super(paramOutputStream);
    this.aeadBlockCipher = paramAEADBlockCipher;
  }
  
  public void write(int paramInt) throws IOException {
    this.oneByte[0] = (byte)paramInt;
    if (this.streamCipher != null) {
      this.out.write(this.streamCipher.returnByte((byte)paramInt));
    } else {
      write(this.oneByte, 0, 1);
    } 
  }
  
  public void write(byte[] paramArrayOfbyte) throws IOException {
    write(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  public void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    ensureCapacity(paramInt2, false);
    if (this.bufferedBlockCipher != null) {
      int i = this.bufferedBlockCipher.processBytes(paramArrayOfbyte, paramInt1, paramInt2, this.buf, 0);
      if (i != 0)
        this.out.write(this.buf, 0, i); 
    } else if (this.aeadBlockCipher != null) {
      int i = this.aeadBlockCipher.processBytes(paramArrayOfbyte, paramInt1, paramInt2, this.buf, 0);
      if (i != 0)
        this.out.write(this.buf, 0, i); 
    } else {
      this.streamCipher.processBytes(paramArrayOfbyte, paramInt1, paramInt2, this.buf, 0);
      this.out.write(this.buf, 0, paramInt2);
    } 
  }
  
  private void ensureCapacity(int paramInt, boolean paramBoolean) {
    int i = paramInt;
    if (paramBoolean) {
      if (this.bufferedBlockCipher != null) {
        i = this.bufferedBlockCipher.getOutputSize(paramInt);
      } else if (this.aeadBlockCipher != null) {
        i = this.aeadBlockCipher.getOutputSize(paramInt);
      } 
    } else if (this.bufferedBlockCipher != null) {
      i = this.bufferedBlockCipher.getUpdateOutputSize(paramInt);
    } else if (this.aeadBlockCipher != null) {
      i = this.aeadBlockCipher.getUpdateOutputSize(paramInt);
    } 
    if (this.buf == null || this.buf.length < i)
      this.buf = new byte[i]; 
  }
  
  public void flush() throws IOException {
    this.out.flush();
  }
  
  public void close() throws IOException {
    ensureCapacity(0, true);
    IOException iOException = null;
    try {
      if (this.bufferedBlockCipher != null) {
        int i = this.bufferedBlockCipher.doFinal(this.buf, 0);
        if (i != 0)
          this.out.write(this.buf, 0, i); 
      } else if (this.aeadBlockCipher != null) {
        int i = this.aeadBlockCipher.doFinal(this.buf, 0);
        if (i != 0)
          this.out.write(this.buf, 0, i); 
      } else if (this.streamCipher != null) {
        this.streamCipher.reset();
      } 
    } catch (InvalidCipherTextException invalidCipherTextException) {
      iOException = new InvalidCipherTextIOException("Error finalising cipher data", (Throwable)invalidCipherTextException);
    } catch (Exception exception) {
      iOException = new CipherIOException("Error closing stream: ", exception);
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
