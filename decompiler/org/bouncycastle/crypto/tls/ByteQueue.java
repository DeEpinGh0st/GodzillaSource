package org.bouncycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ByteQueue {
  private static final int DEFAULT_CAPACITY = 1024;
  
  private byte[] databuf;
  
  private int skipped = 0;
  
  private int available = 0;
  
  private boolean readOnlyBuf = false;
  
  public static int nextTwoPow(int paramInt) {
    paramInt |= paramInt >> 1;
    paramInt |= paramInt >> 2;
    paramInt |= paramInt >> 4;
    paramInt |= paramInt >> 8;
    paramInt |= paramInt >> 16;
    return paramInt + 1;
  }
  
  public ByteQueue() {
    this(1024);
  }
  
  public ByteQueue(int paramInt) {
    this.databuf = (paramInt == 0) ? TlsUtils.EMPTY_BYTES : new byte[paramInt];
  }
  
  public ByteQueue(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    this.databuf = paramArrayOfbyte;
    this.skipped = paramInt1;
    this.available = paramInt2;
    this.readOnlyBuf = true;
  }
  
  public void addData(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (this.readOnlyBuf)
      throw new IllegalStateException("Cannot add data to read-only buffer"); 
    if (this.skipped + this.available + paramInt2 > this.databuf.length) {
      int i = nextTwoPow(this.available + paramInt2);
      if (i > this.databuf.length) {
        byte[] arrayOfByte = new byte[i];
        System.arraycopy(this.databuf, this.skipped, arrayOfByte, 0, this.available);
        this.databuf = arrayOfByte;
      } else {
        System.arraycopy(this.databuf, this.skipped, this.databuf, 0, this.available);
      } 
      this.skipped = 0;
    } 
    System.arraycopy(paramArrayOfbyte, paramInt1, this.databuf, this.skipped + this.available, paramInt2);
    this.available += paramInt2;
  }
  
  public int available() {
    return this.available;
  }
  
  public void copyTo(OutputStream paramOutputStream, int paramInt) throws IOException {
    if (paramInt > this.available)
      throw new IllegalStateException("Cannot copy " + paramInt + " bytes, only got " + this.available); 
    paramOutputStream.write(this.databuf, this.skipped, paramInt);
  }
  
  public void read(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3) {
    if (paramArrayOfbyte.length - paramInt1 < paramInt2)
      throw new IllegalArgumentException("Buffer size of " + paramArrayOfbyte.length + " is too small for a read of " + paramInt2 + " bytes"); 
    if (this.available - paramInt3 < paramInt2)
      throw new IllegalStateException("Not enough data to read"); 
    System.arraycopy(this.databuf, this.skipped + paramInt3, paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public ByteArrayInputStream readFrom(int paramInt) {
    if (paramInt > this.available)
      throw new IllegalStateException("Cannot read " + paramInt + " bytes, only got " + this.available); 
    int i = this.skipped;
    this.available -= paramInt;
    this.skipped += paramInt;
    return new ByteArrayInputStream(this.databuf, i, paramInt);
  }
  
  public void removeData(int paramInt) {
    if (paramInt > this.available)
      throw new IllegalStateException("Cannot remove " + paramInt + " bytes, only got " + this.available); 
    this.available -= paramInt;
    this.skipped += paramInt;
  }
  
  public void removeData(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3) {
    read(paramArrayOfbyte, paramInt1, paramInt2, paramInt3);
    removeData(paramInt3 + paramInt2);
  }
  
  public byte[] removeData(int paramInt1, int paramInt2) {
    byte[] arrayOfByte = new byte[paramInt1];
    removeData(arrayOfByte, 0, paramInt1, paramInt2);
    return arrayOfByte;
  }
  
  public void shrink() {
    if (this.available == 0) {
      this.databuf = TlsUtils.EMPTY_BYTES;
      this.skipped = 0;
    } else {
      int i = nextTwoPow(this.available);
      if (i < this.databuf.length) {
        byte[] arrayOfByte = new byte[i];
        System.arraycopy(this.databuf, this.skipped, arrayOfByte, 0, this.available);
        this.databuf = arrayOfByte;
        this.skipped = 0;
      } 
    } 
  }
}
