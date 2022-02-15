package org.bouncycastle.pqc.crypto.gmss.util;

import org.bouncycastle.crypto.Digest;

public class WinternitzOTSVerify {
  private Digest messDigestOTS;
  
  private int w;
  
  public WinternitzOTSVerify(Digest paramDigest, int paramInt) {
    this.w = paramInt;
    this.messDigestOTS = paramDigest;
  }
  
  public int getSignatureLength() {
    int i = this.messDigestOTS.getDigestSize();
    int j = ((i << 3) + this.w - 1) / this.w;
    int k = getLog((j << this.w) + 1);
    j += (k + this.w - 1) / this.w;
    return i * j;
  }
  
  public byte[] Verify(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    int i = this.messDigestOTS.getDigestSize();
    byte[] arrayOfByte1 = new byte[i];
    this.messDigestOTS.update(paramArrayOfbyte1, 0, paramArrayOfbyte1.length);
    arrayOfByte1 = new byte[this.messDigestOTS.getDigestSize()];
    this.messDigestOTS.doFinal(arrayOfByte1, 0);
    int j = ((i << 3) + this.w - 1) / this.w;
    int k = getLog((j << this.w) + 1);
    int m = j + (k + this.w - 1) / this.w;
    int n = i * m;
    if (n != paramArrayOfbyte2.length)
      return null; 
    byte[] arrayOfByte2 = new byte[n];
    int i1 = 0;
    byte b = 0;
    if (8 % this.w == 0) {
      int i2 = 8 / this.w;
      int i3 = (1 << this.w) - 1;
      byte[] arrayOfByte = new byte[i];
      int i4;
      for (i4 = 0; i4 < arrayOfByte1.length; i4++) {
        for (byte b1 = 0; b1 < i2; b1++) {
          int i5 = arrayOfByte1[i4] & i3;
          i1 += i5;
          System.arraycopy(paramArrayOfbyte2, b * i, arrayOfByte, 0, i);
          while (i5 < i3) {
            this.messDigestOTS.update(arrayOfByte, 0, arrayOfByte.length);
            arrayOfByte = new byte[this.messDigestOTS.getDigestSize()];
            this.messDigestOTS.doFinal(arrayOfByte, 0);
            i5++;
          } 
          System.arraycopy(arrayOfByte, 0, arrayOfByte2, b * i, i);
          arrayOfByte1[i4] = (byte)(arrayOfByte1[i4] >>> this.w);
          b++;
        } 
      } 
      i1 = (j << this.w) - i1;
      for (i4 = 0; i4 < k; i4 += this.w) {
        int i5 = i1 & i3;
        System.arraycopy(paramArrayOfbyte2, b * i, arrayOfByte, 0, i);
        while (i5 < i3) {
          this.messDigestOTS.update(arrayOfByte, 0, arrayOfByte.length);
          arrayOfByte = new byte[this.messDigestOTS.getDigestSize()];
          this.messDigestOTS.doFinal(arrayOfByte, 0);
          i5++;
        } 
        System.arraycopy(arrayOfByte, 0, arrayOfByte2, b * i, i);
        i1 >>>= this.w;
        b++;
      } 
    } else if (this.w < 8) {
      int i2 = i / this.w;
      int i3 = (1 << this.w) - 1;
      byte[] arrayOfByte = new byte[i];
      byte b1 = 0;
      int i4;
      for (i4 = 0; i4 < i2; i4++) {
        long l1 = 0L;
        byte b2;
        for (b2 = 0; b2 < this.w; b2++) {
          l1 ^= ((arrayOfByte1[b1] & 0xFF) << b2 << 3);
          b1++;
        } 
        for (b2 = 0; b2 < 8; b2++) {
          int i5 = (int)(l1 & i3);
          i1 += i5;
          System.arraycopy(paramArrayOfbyte2, b * i, arrayOfByte, 0, i);
          while (i5 < i3) {
            this.messDigestOTS.update(arrayOfByte, 0, arrayOfByte.length);
            arrayOfByte = new byte[this.messDigestOTS.getDigestSize()];
            this.messDigestOTS.doFinal(arrayOfByte, 0);
            i5++;
          } 
          System.arraycopy(arrayOfByte, 0, arrayOfByte2, b * i, i);
          l1 >>>= this.w;
          b++;
        } 
      } 
      i2 = i % this.w;
      long l = 0L;
      for (i4 = 0; i4 < i2; i4++) {
        l ^= ((arrayOfByte1[b1] & 0xFF) << i4 << 3);
        b1++;
      } 
      i2 <<= 3;
      for (i4 = 0; i4 < i2; i4 += this.w) {
        int i5 = (int)(l & i3);
        i1 += i5;
        System.arraycopy(paramArrayOfbyte2, b * i, arrayOfByte, 0, i);
        while (i5 < i3) {
          this.messDigestOTS.update(arrayOfByte, 0, arrayOfByte.length);
          arrayOfByte = new byte[this.messDigestOTS.getDigestSize()];
          this.messDigestOTS.doFinal(arrayOfByte, 0);
          i5++;
        } 
        System.arraycopy(arrayOfByte, 0, arrayOfByte2, b * i, i);
        l >>>= this.w;
        b++;
      } 
      i1 = (j << this.w) - i1;
      for (i4 = 0; i4 < k; i4 += this.w) {
        int i5 = i1 & i3;
        System.arraycopy(paramArrayOfbyte2, b * i, arrayOfByte, 0, i);
        while (i5 < i3) {
          this.messDigestOTS.update(arrayOfByte, 0, arrayOfByte.length);
          arrayOfByte = new byte[this.messDigestOTS.getDigestSize()];
          this.messDigestOTS.doFinal(arrayOfByte, 0);
          i5++;
        } 
        System.arraycopy(arrayOfByte, 0, arrayOfByte2, b * i, i);
        i1 >>>= this.w;
        b++;
      } 
    } else if (this.w < 57) {
      int i2 = (i << 3) - this.w;
      int i3 = (1 << this.w) - 1;
      byte[] arrayOfByte = new byte[i];
      int i4 = 0;
      while (i4 <= i2) {
        int i7 = i4 >>> 3;
        int i9 = i4 % 8;
        i4 += this.w;
        int i8 = i4 + 7 >>> 3;
        long l1 = 0L;
        byte b1 = 0;
        for (int i10 = i7; i10 < i8; i10++) {
          l1 ^= ((arrayOfByte1[i10] & 0xFF) << b1 << 3);
          b1++;
        } 
        l1 >>>= i9;
        long l2 = l1 & i3;
        i1 = (int)(i1 + l2);
        System.arraycopy(paramArrayOfbyte2, b * i, arrayOfByte, 0, i);
        while (l2 < i3) {
          this.messDigestOTS.update(arrayOfByte, 0, arrayOfByte.length);
          arrayOfByte = new byte[this.messDigestOTS.getDigestSize()];
          this.messDigestOTS.doFinal(arrayOfByte, 0);
          l2++;
        } 
        System.arraycopy(arrayOfByte, 0, arrayOfByte2, b * i, i);
        b++;
      } 
      int i5 = i4 >>> 3;
      if (i5 < i) {
        int i7 = i4 % 8;
        long l1 = 0L;
        byte b1 = 0;
        for (int i8 = i5; i8 < i; i8++) {
          l1 ^= ((arrayOfByte1[i8] & 0xFF) << b1 << 3);
          b1++;
        } 
        l1 >>>= i7;
        long l2 = l1 & i3;
        i1 = (int)(i1 + l2);
        System.arraycopy(paramArrayOfbyte2, b * i, arrayOfByte, 0, i);
        while (l2 < i3) {
          this.messDigestOTS.update(arrayOfByte, 0, arrayOfByte.length);
          arrayOfByte = new byte[this.messDigestOTS.getDigestSize()];
          this.messDigestOTS.doFinal(arrayOfByte, 0);
          l2++;
        } 
        System.arraycopy(arrayOfByte, 0, arrayOfByte2, b * i, i);
        b++;
      } 
      i1 = (j << this.w) - i1;
      int i6;
      for (i6 = 0; i6 < k; i6 += this.w) {
        long l = (i1 & i3);
        System.arraycopy(paramArrayOfbyte2, b * i, arrayOfByte, 0, i);
        while (l < i3) {
          this.messDigestOTS.update(arrayOfByte, 0, arrayOfByte.length);
          arrayOfByte = new byte[this.messDigestOTS.getDigestSize()];
          this.messDigestOTS.doFinal(arrayOfByte, 0);
          l++;
        } 
        System.arraycopy(arrayOfByte, 0, arrayOfByte2, b * i, i);
        i1 >>>= this.w;
        b++;
      } 
    } 
    byte[] arrayOfByte3 = new byte[i];
    this.messDigestOTS.update(arrayOfByte2, 0, arrayOfByte2.length);
    arrayOfByte3 = new byte[this.messDigestOTS.getDigestSize()];
    this.messDigestOTS.doFinal(arrayOfByte3, 0);
    return arrayOfByte3;
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
