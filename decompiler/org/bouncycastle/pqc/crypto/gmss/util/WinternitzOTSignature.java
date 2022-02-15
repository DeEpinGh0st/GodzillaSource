package org.bouncycastle.pqc.crypto.gmss.util;

import org.bouncycastle.crypto.Digest;

public class WinternitzOTSignature {
  private Digest messDigestOTS;
  
  private int mdsize;
  
  private int keysize;
  
  private byte[][] privateKeyOTS;
  
  private int w;
  
  private GMSSRandom gmssRandom;
  
  private int messagesize;
  
  private int checksumsize;
  
  public WinternitzOTSignature(byte[] paramArrayOfbyte, Digest paramDigest, int paramInt) {
    this.w = paramInt;
    this.messDigestOTS = paramDigest;
    this.gmssRandom = new GMSSRandom(this.messDigestOTS);
    this.mdsize = this.messDigestOTS.getDigestSize();
    int i = this.mdsize << 3;
    this.messagesize = (int)Math.ceil(i / paramInt);
    this.checksumsize = getLog((this.messagesize << paramInt) + 1);
    this.keysize = this.messagesize + (int)Math.ceil(this.checksumsize / paramInt);
    this.privateKeyOTS = new byte[this.keysize][this.mdsize];
    byte[] arrayOfByte = new byte[this.mdsize];
    System.arraycopy(paramArrayOfbyte, 0, arrayOfByte, 0, arrayOfByte.length);
    for (byte b = 0; b < this.keysize; b++)
      this.privateKeyOTS[b] = this.gmssRandom.nextSeed(arrayOfByte); 
  }
  
  public byte[][] getPrivateKey() {
    return this.privateKeyOTS;
  }
  
  public byte[] getPublicKey() {
    byte[] arrayOfByte1 = new byte[this.keysize * this.mdsize];
    byte[] arrayOfByte2 = new byte[this.mdsize];
    int i = 1 << this.w;
    for (byte b = 0; b < this.keysize; b++) {
      this.messDigestOTS.update(this.privateKeyOTS[b], 0, (this.privateKeyOTS[b]).length);
      arrayOfByte2 = new byte[this.messDigestOTS.getDigestSize()];
      this.messDigestOTS.doFinal(arrayOfByte2, 0);
      for (byte b1 = 2; b1 < i; b1++) {
        this.messDigestOTS.update(arrayOfByte2, 0, arrayOfByte2.length);
        arrayOfByte2 = new byte[this.messDigestOTS.getDigestSize()];
        this.messDigestOTS.doFinal(arrayOfByte2, 0);
      } 
      System.arraycopy(arrayOfByte2, 0, arrayOfByte1, this.mdsize * b, this.mdsize);
    } 
    this.messDigestOTS.update(arrayOfByte1, 0, arrayOfByte1.length);
    byte[] arrayOfByte3 = new byte[this.messDigestOTS.getDigestSize()];
    this.messDigestOTS.doFinal(arrayOfByte3, 0);
    return arrayOfByte3;
  }
  
  public byte[] getSignature(byte[] paramArrayOfbyte) {
    byte[] arrayOfByte1 = new byte[this.keysize * this.mdsize];
    byte[] arrayOfByte2 = new byte[this.mdsize];
    byte b = 0;
    int i = 0;
    int j = 0;
    this.messDigestOTS.update(paramArrayOfbyte, 0, paramArrayOfbyte.length);
    arrayOfByte2 = new byte[this.messDigestOTS.getDigestSize()];
    this.messDigestOTS.doFinal(arrayOfByte2, 0);
    if (8 % this.w == 0) {
      int k = 8 / this.w;
      int m = (1 << this.w) - 1;
      byte[] arrayOfByte = new byte[this.mdsize];
      int n;
      for (n = 0; n < arrayOfByte2.length; n++) {
        for (byte b1 = 0; b1 < k; b1++) {
          j = arrayOfByte2[n] & m;
          i += j;
          System.arraycopy(this.privateKeyOTS[b], 0, arrayOfByte, 0, this.mdsize);
          while (j > 0) {
            this.messDigestOTS.update(arrayOfByte, 0, arrayOfByte.length);
            arrayOfByte = new byte[this.messDigestOTS.getDigestSize()];
            this.messDigestOTS.doFinal(arrayOfByte, 0);
            j--;
          } 
          System.arraycopy(arrayOfByte, 0, arrayOfByte1, b * this.mdsize, this.mdsize);
          arrayOfByte2[n] = (byte)(arrayOfByte2[n] >>> this.w);
          b++;
        } 
      } 
      i = (this.messagesize << this.w) - i;
      for (n = 0; n < this.checksumsize; n += this.w) {
        j = i & m;
        System.arraycopy(this.privateKeyOTS[b], 0, arrayOfByte, 0, this.mdsize);
        while (j > 0) {
          this.messDigestOTS.update(arrayOfByte, 0, arrayOfByte.length);
          arrayOfByte = new byte[this.messDigestOTS.getDigestSize()];
          this.messDigestOTS.doFinal(arrayOfByte, 0);
          j--;
        } 
        System.arraycopy(arrayOfByte, 0, arrayOfByte1, b * this.mdsize, this.mdsize);
        i >>>= this.w;
        b++;
      } 
    } else if (this.w < 8) {
      int k = this.mdsize / this.w;
      int m = (1 << this.w) - 1;
      byte[] arrayOfByte = new byte[this.mdsize];
      byte b1 = 0;
      int n;
      for (n = 0; n < k; n++) {
        long l1 = 0L;
        byte b2;
        for (b2 = 0; b2 < this.w; b2++) {
          l1 ^= ((arrayOfByte2[b1] & 0xFF) << b2 << 3);
          b1++;
        } 
        for (b2 = 0; b2 < 8; b2++) {
          j = (int)(l1 & m);
          i += j;
          System.arraycopy(this.privateKeyOTS[b], 0, arrayOfByte, 0, this.mdsize);
          while (j > 0) {
            this.messDigestOTS.update(arrayOfByte, 0, arrayOfByte.length);
            arrayOfByte = new byte[this.messDigestOTS.getDigestSize()];
            this.messDigestOTS.doFinal(arrayOfByte, 0);
            j--;
          } 
          System.arraycopy(arrayOfByte, 0, arrayOfByte1, b * this.mdsize, this.mdsize);
          l1 >>>= this.w;
          b++;
        } 
      } 
      k = this.mdsize % this.w;
      long l = 0L;
      for (n = 0; n < k; n++) {
        l ^= ((arrayOfByte2[b1] & 0xFF) << n << 3);
        b1++;
      } 
      k <<= 3;
      for (n = 0; n < k; n += this.w) {
        j = (int)(l & m);
        i += j;
        System.arraycopy(this.privateKeyOTS[b], 0, arrayOfByte, 0, this.mdsize);
        while (j > 0) {
          this.messDigestOTS.update(arrayOfByte, 0, arrayOfByte.length);
          arrayOfByte = new byte[this.messDigestOTS.getDigestSize()];
          this.messDigestOTS.doFinal(arrayOfByte, 0);
          j--;
        } 
        System.arraycopy(arrayOfByte, 0, arrayOfByte1, b * this.mdsize, this.mdsize);
        l >>>= this.w;
        b++;
      } 
      i = (this.messagesize << this.w) - i;
      for (n = 0; n < this.checksumsize; n += this.w) {
        j = i & m;
        System.arraycopy(this.privateKeyOTS[b], 0, arrayOfByte, 0, this.mdsize);
        while (j > 0) {
          this.messDigestOTS.update(arrayOfByte, 0, arrayOfByte.length);
          arrayOfByte = new byte[this.messDigestOTS.getDigestSize()];
          this.messDigestOTS.doFinal(arrayOfByte, 0);
          j--;
        } 
        System.arraycopy(arrayOfByte, 0, arrayOfByte1, b * this.mdsize, this.mdsize);
        i >>>= this.w;
        b++;
      } 
    } else if (this.w < 57) {
      int k = (this.mdsize << 3) - this.w;
      int m = (1 << this.w) - 1;
      byte[] arrayOfByte = new byte[this.mdsize];
      int n = 0;
      while (n <= k) {
        int i3 = n >>> 3;
        int i5 = n % 8;
        n += this.w;
        int i4 = n + 7 >>> 3;
        long l1 = 0L;
        byte b1 = 0;
        for (int i6 = i3; i6 < i4; i6++) {
          l1 ^= ((arrayOfByte2[i6] & 0xFF) << b1 << 3);
          b1++;
        } 
        l1 >>>= i5;
        long l2 = l1 & m;
        i = (int)(i + l2);
        System.arraycopy(this.privateKeyOTS[b], 0, arrayOfByte, 0, this.mdsize);
        while (l2 > 0L) {
          this.messDigestOTS.update(arrayOfByte, 0, arrayOfByte.length);
          arrayOfByte = new byte[this.messDigestOTS.getDigestSize()];
          this.messDigestOTS.doFinal(arrayOfByte, 0);
          l2--;
        } 
        System.arraycopy(arrayOfByte, 0, arrayOfByte1, b * this.mdsize, this.mdsize);
        b++;
      } 
      int i1 = n >>> 3;
      if (i1 < this.mdsize) {
        int i3 = n % 8;
        long l1 = 0L;
        byte b1 = 0;
        for (int i4 = i1; i4 < this.mdsize; i4++) {
          l1 ^= ((arrayOfByte2[i4] & 0xFF) << b1 << 3);
          b1++;
        } 
        l1 >>>= i3;
        long l2 = l1 & m;
        i = (int)(i + l2);
        System.arraycopy(this.privateKeyOTS[b], 0, arrayOfByte, 0, this.mdsize);
        while (l2 > 0L) {
          this.messDigestOTS.update(arrayOfByte, 0, arrayOfByte.length);
          arrayOfByte = new byte[this.messDigestOTS.getDigestSize()];
          this.messDigestOTS.doFinal(arrayOfByte, 0);
          l2--;
        } 
        System.arraycopy(arrayOfByte, 0, arrayOfByte1, b * this.mdsize, this.mdsize);
        b++;
      } 
      i = (this.messagesize << this.w) - i;
      int i2;
      for (i2 = 0; i2 < this.checksumsize; i2 += this.w) {
        long l = (i & m);
        System.arraycopy(this.privateKeyOTS[b], 0, arrayOfByte, 0, this.mdsize);
        while (l > 0L) {
          this.messDigestOTS.update(arrayOfByte, 0, arrayOfByte.length);
          arrayOfByte = new byte[this.messDigestOTS.getDigestSize()];
          this.messDigestOTS.doFinal(arrayOfByte, 0);
          l--;
        } 
        System.arraycopy(arrayOfByte, 0, arrayOfByte1, b * this.mdsize, this.mdsize);
        i >>>= this.w;
        b++;
      } 
    } 
    return arrayOfByte1;
  }
  
  public int getLog(int paramInt) {
    byte b = 1;
    int i = 2;
    while (i < paramInt) {
      i <<= 1;
      b++;
    } 
    return b;
  }
}
