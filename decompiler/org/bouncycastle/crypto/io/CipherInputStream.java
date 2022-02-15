package org.bouncycastle.crypto.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.SkippingCipher;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.util.Arrays;

public class CipherInputStream extends FilterInputStream {
  private static final int INPUT_BUF_SIZE = 2048;
  
  private SkippingCipher skippingCipher;
  
  private byte[] inBuf;
  
  private BufferedBlockCipher bufferedBlockCipher;
  
  private StreamCipher streamCipher;
  
  private AEADBlockCipher aeadBlockCipher;
  
  private byte[] buf;
  
  private byte[] markBuf;
  
  private int bufOff;
  
  private int maxBuf;
  
  private boolean finalized;
  
  private long markPosition;
  
  private int markBufOff;
  
  public CipherInputStream(InputStream paramInputStream, BufferedBlockCipher paramBufferedBlockCipher) {
    this(paramInputStream, paramBufferedBlockCipher, 2048);
  }
  
  public CipherInputStream(InputStream paramInputStream, StreamCipher paramStreamCipher) {
    this(paramInputStream, paramStreamCipher, 2048);
  }
  
  public CipherInputStream(InputStream paramInputStream, AEADBlockCipher paramAEADBlockCipher) {
    this(paramInputStream, paramAEADBlockCipher, 2048);
  }
  
  public CipherInputStream(InputStream paramInputStream, BufferedBlockCipher paramBufferedBlockCipher, int paramInt) {
    super(paramInputStream);
    this.bufferedBlockCipher = paramBufferedBlockCipher;
    this.inBuf = new byte[paramInt];
    this.skippingCipher = (paramBufferedBlockCipher instanceof SkippingCipher) ? (SkippingCipher)paramBufferedBlockCipher : null;
  }
  
  public CipherInputStream(InputStream paramInputStream, StreamCipher paramStreamCipher, int paramInt) {
    super(paramInputStream);
    this.streamCipher = paramStreamCipher;
    this.inBuf = new byte[paramInt];
    this.skippingCipher = (paramStreamCipher instanceof SkippingCipher) ? (SkippingCipher)paramStreamCipher : null;
  }
  
  public CipherInputStream(InputStream paramInputStream, AEADBlockCipher paramAEADBlockCipher, int paramInt) {
    super(paramInputStream);
    this.aeadBlockCipher = paramAEADBlockCipher;
    this.inBuf = new byte[paramInt];
    this.skippingCipher = (paramAEADBlockCipher instanceof SkippingCipher) ? (SkippingCipher)paramAEADBlockCipher : null;
  }
  
  private int nextChunk() throws IOException {
    if (this.finalized)
      return -1; 
    this.bufOff = 0;
    this.maxBuf = 0;
    while (this.maxBuf == 0) {
      int i = this.in.read(this.inBuf);
      if (i == -1) {
        finaliseCipher();
        return (this.maxBuf == 0) ? -1 : this.maxBuf;
      } 
      try {
        ensureCapacity(i, false);
        if (this.bufferedBlockCipher != null) {
          this.maxBuf = this.bufferedBlockCipher.processBytes(this.inBuf, 0, i, this.buf, 0);
          continue;
        } 
        if (this.aeadBlockCipher != null) {
          this.maxBuf = this.aeadBlockCipher.processBytes(this.inBuf, 0, i, this.buf, 0);
          continue;
        } 
        this.streamCipher.processBytes(this.inBuf, 0, i, this.buf, 0);
        this.maxBuf = i;
      } catch (Exception exception) {
        throw new CipherIOException("Error processing stream ", exception);
      } 
    } 
    return this.maxBuf;
  }
  
  private void finaliseCipher() throws IOException {
    try {
      this.finalized = true;
      ensureCapacity(0, true);
      if (this.bufferedBlockCipher != null) {
        this.maxBuf = this.bufferedBlockCipher.doFinal(this.buf, 0);
      } else if (this.aeadBlockCipher != null) {
        this.maxBuf = this.aeadBlockCipher.doFinal(this.buf, 0);
      } else {
        this.maxBuf = 0;
      } 
    } catch (InvalidCipherTextException invalidCipherTextException) {
      throw new InvalidCipherTextIOException("Error finalising cipher", invalidCipherTextException);
    } catch (Exception exception) {
      throw new IOException("Error finalising cipher " + exception);
    } 
  }
  
  public int read() throws IOException {
    return (this.bufOff >= this.maxBuf && nextChunk() < 0) ? -1 : (this.buf[this.bufOff++] & 0xFF);
  }
  
  public int read(byte[] paramArrayOfbyte) throws IOException {
    return read(paramArrayOfbyte, 0, paramArrayOfbyte.length);
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
    if (this.skippingCipher != null) {
      int j = available();
      if (paramLong <= j) {
        this.bufOff = (int)(this.bufOff + paramLong);
        return paramLong;
      } 
      this.bufOff = this.maxBuf;
      long l1 = this.in.skip(paramLong - j);
      long l2 = this.skippingCipher.skip(l1);
      if (l1 != l2)
        throw new IOException("Unable to skip cipher " + l1 + " bytes."); 
      return l1 + j;
    } 
    int i = (int)Math.min(paramLong, available());
    this.bufOff += i;
    return i;
  }
  
  public int available() throws IOException {
    return this.maxBuf - this.bufOff;
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
  
  public void close() throws IOException {
    try {
      this.in.close();
    } finally {
      if (!this.finalized)
        finaliseCipher(); 
    } 
    this.maxBuf = this.bufOff = 0;
    this.markBufOff = 0;
    this.markPosition = 0L;
    if (this.markBuf != null) {
      Arrays.fill(this.markBuf, (byte)0);
      this.markBuf = null;
    } 
    if (this.buf != null) {
      Arrays.fill(this.buf, (byte)0);
      this.buf = null;
    } 
    Arrays.fill(this.inBuf, (byte)0);
  }
  
  public void mark(int paramInt) {
    this.in.mark(paramInt);
    if (this.skippingCipher != null)
      this.markPosition = this.skippingCipher.getPosition(); 
    if (this.buf != null) {
      this.markBuf = new byte[this.buf.length];
      System.arraycopy(this.buf, 0, this.markBuf, 0, this.buf.length);
    } 
    this.markBufOff = this.bufOff;
  }
  
  public void reset() throws IOException {
    if (this.skippingCipher == null)
      throw new IOException("cipher must implement SkippingCipher to be used with reset()"); 
    this.in.reset();
    this.skippingCipher.seekTo(this.markPosition);
    if (this.markBuf != null)
      this.buf = this.markBuf; 
    this.bufOff = this.markBufOff;
  }
  
  public boolean markSupported() {
    return (this.skippingCipher != null) ? this.in.markSupported() : false;
  }
}
