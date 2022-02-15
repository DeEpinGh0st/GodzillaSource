package org.bouncycastle.pqc.crypto.gmss.util;

import org.bouncycastle.crypto.Digest;

public class GMSSRandom {
  private Digest messDigestTree;
  
  public GMSSRandom(Digest paramDigest) {
    this.messDigestTree = paramDigest;
  }
  
  public byte[] nextSeed(byte[] paramArrayOfbyte) {
    byte[] arrayOfByte = new byte[paramArrayOfbyte.length];
    this.messDigestTree.update(paramArrayOfbyte, 0, paramArrayOfbyte.length);
    arrayOfByte = new byte[this.messDigestTree.getDigestSize()];
    this.messDigestTree.doFinal(arrayOfByte, 0);
    addByteArrays(paramArrayOfbyte, arrayOfByte);
    addOne(paramArrayOfbyte);
    return arrayOfByte;
  }
  
  private void addByteArrays(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    byte b = 0;
    for (byte b1 = 0; b1 < paramArrayOfbyte1.length; b1++) {
      int i = (0xFF & paramArrayOfbyte1[b1]) + (0xFF & paramArrayOfbyte2[b1]) + b;
      paramArrayOfbyte1[b1] = (byte)i;
      b = (byte)(i >> 8);
    } 
  }
  
  private void addOne(byte[] paramArrayOfbyte) {
    byte b = 1;
    for (byte b1 = 0; b1 < paramArrayOfbyte.length; b1++) {
      int i = (0xFF & paramArrayOfbyte[b1]) + b;
      paramArrayOfbyte[b1] = (byte)i;
      b = (byte)(i >> 8);
    } 
  }
}
