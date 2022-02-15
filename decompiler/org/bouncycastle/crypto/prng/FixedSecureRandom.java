package org.bouncycastle.crypto.prng;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

public class FixedSecureRandom extends SecureRandom {
  private byte[] _data;
  
  private int _index;
  
  private int _intPad;
  
  public FixedSecureRandom(byte[] paramArrayOfbyte) {
    this(false, new byte[][] { paramArrayOfbyte });
  }
  
  public FixedSecureRandom(byte[][] paramArrayOfbyte) {
    this(false, paramArrayOfbyte);
  }
  
  public FixedSecureRandom(boolean paramBoolean, byte[] paramArrayOfbyte) {
    this(paramBoolean, new byte[][] { paramArrayOfbyte });
  }
  
  public FixedSecureRandom(boolean paramBoolean, byte[][] paramArrayOfbyte) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    for (byte b = 0; b != paramArrayOfbyte.length; b++) {
      try {
        byteArrayOutputStream.write(paramArrayOfbyte[b]);
      } catch (IOException iOException) {
        throw new IllegalArgumentException("can't save value array.");
      } 
    } 
    this._data = byteArrayOutputStream.toByteArray();
    if (paramBoolean)
      this._intPad = this._data.length % 4; 
  }
  
  public void nextBytes(byte[] paramArrayOfbyte) {
    System.arraycopy(this._data, this._index, paramArrayOfbyte, 0, paramArrayOfbyte.length);
    this._index += paramArrayOfbyte.length;
  }
  
  public byte[] generateSeed(int paramInt) {
    byte[] arrayOfByte = new byte[paramInt];
    nextBytes(arrayOfByte);
    return arrayOfByte;
  }
  
  public int nextInt() {
    int i = 0;
    i |= nextValue() << 24;
    i |= nextValue() << 16;
    if (this._intPad == 2) {
      this._intPad--;
    } else {
      i |= nextValue() << 8;
    } 
    if (this._intPad == 1) {
      this._intPad--;
    } else {
      i |= nextValue();
    } 
    return i;
  }
  
  public long nextLong() {
    long l = 0L;
    l |= nextValue() << 56L;
    l |= nextValue() << 48L;
    l |= nextValue() << 40L;
    l |= nextValue() << 32L;
    l |= nextValue() << 24L;
    l |= nextValue() << 16L;
    l |= nextValue() << 8L;
    l |= nextValue();
    return l;
  }
  
  public boolean isExhausted() {
    return (this._index == this._data.length);
  }
  
  private int nextValue() {
    return this._data[this._index++] & 0xFF;
  }
}
