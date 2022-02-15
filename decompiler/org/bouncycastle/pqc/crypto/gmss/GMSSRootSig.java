package org.bouncycastle.pqc.crypto.gmss;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.pqc.crypto.gmss.util.GMSSRandom;
import org.bouncycastle.util.encoders.Hex;

public class GMSSRootSig {
  private Digest messDigestOTS;
  
  private int mdsize;
  
  private int keysize;
  
  private byte[] privateKeyOTS;
  
  private byte[] hash;
  
  private byte[] sign;
  
  private int w;
  
  private GMSSRandom gmssRandom;
  
  private int messagesize;
  
  private int k;
  
  private int r;
  
  private int test;
  
  private int counter;
  
  private int ii;
  
  private long test8;
  
  private long big8;
  
  private int steps;
  
  private int checksum;
  
  private int height;
  
  private byte[] seed;
  
  public GMSSRootSig(Digest paramDigest, byte[][] paramArrayOfbyte, int[] paramArrayOfint) {
    this.messDigestOTS = paramDigest;
    this.gmssRandom = new GMSSRandom(this.messDigestOTS);
    this.counter = paramArrayOfint[0];
    this.test = paramArrayOfint[1];
    this.ii = paramArrayOfint[2];
    this.r = paramArrayOfint[3];
    this.steps = paramArrayOfint[4];
    this.keysize = paramArrayOfint[5];
    this.height = paramArrayOfint[6];
    this.w = paramArrayOfint[7];
    this.checksum = paramArrayOfint[8];
    this.mdsize = this.messDigestOTS.getDigestSize();
    this.k = (1 << this.w) - 1;
    int i = this.mdsize << 3;
    this.messagesize = (int)Math.ceil(i / this.w);
    this.privateKeyOTS = paramArrayOfbyte[0];
    this.seed = paramArrayOfbyte[1];
    this.hash = paramArrayOfbyte[2];
    this.sign = paramArrayOfbyte[3];
    this.test8 = (paramArrayOfbyte[4][0] & 0xFF) | (paramArrayOfbyte[4][1] & 0xFF) << 8L | (paramArrayOfbyte[4][2] & 0xFF) << 16L | (paramArrayOfbyte[4][3] & 0xFF) << 24L | (paramArrayOfbyte[4][4] & 0xFF) << 32L | (paramArrayOfbyte[4][5] & 0xFF) << 40L | (paramArrayOfbyte[4][6] & 0xFF) << 48L | (paramArrayOfbyte[4][7] & 0xFF) << 56L;
    this.big8 = (paramArrayOfbyte[4][8] & 0xFF) | (paramArrayOfbyte[4][9] & 0xFF) << 8L | (paramArrayOfbyte[4][10] & 0xFF) << 16L | (paramArrayOfbyte[4][11] & 0xFF) << 24L | (paramArrayOfbyte[4][12] & 0xFF) << 32L | (paramArrayOfbyte[4][13] & 0xFF) << 40L | (paramArrayOfbyte[4][14] & 0xFF) << 48L | (paramArrayOfbyte[4][15] & 0xFF) << 56L;
  }
  
  public GMSSRootSig(Digest paramDigest, int paramInt1, int paramInt2) {
    this.messDigestOTS = paramDigest;
    this.gmssRandom = new GMSSRandom(this.messDigestOTS);
    this.mdsize = this.messDigestOTS.getDigestSize();
    this.w = paramInt1;
    this.height = paramInt2;
    this.k = (1 << paramInt1) - 1;
    int i = this.mdsize << 3;
    this.messagesize = (int)Math.ceil(i / paramInt1);
  }
  
  public void initSign(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    this.hash = new byte[this.mdsize];
    this.messDigestOTS.update(paramArrayOfbyte2, 0, paramArrayOfbyte2.length);
    this.hash = new byte[this.messDigestOTS.getDigestSize()];
    this.messDigestOTS.doFinal(this.hash, 0);
    byte[] arrayOfByte = new byte[this.mdsize];
    System.arraycopy(this.hash, 0, arrayOfByte, 0, this.mdsize);
    int i = 0;
    int j = 0;
    int k = getLog((this.messagesize << this.w) + 1);
    if (8 % this.w == 0) {
      int m = 8 / this.w;
      int n;
      for (n = 0; n < this.mdsize; n++) {
        for (byte b = 0; b < m; b++) {
          j += arrayOfByte[n] & this.k;
          arrayOfByte[n] = (byte)(arrayOfByte[n] >>> this.w);
        } 
      } 
      this.checksum = (this.messagesize << this.w) - j;
      i = this.checksum;
      for (n = 0; n < k; n += this.w) {
        j += i & this.k;
        i >>>= this.w;
      } 
    } else if (this.w < 8) {
      byte b = 0;
      int m = this.mdsize / this.w;
      int n;
      for (n = 0; n < m; n++) {
        long l1 = 0L;
        byte b1;
        for (b1 = 0; b1 < this.w; b1++) {
          l1 ^= ((arrayOfByte[b] & 0xFF) << b1 << 3);
          b++;
        } 
        for (b1 = 0; b1 < 8; b1++) {
          j += (int)(l1 & this.k);
          l1 >>>= this.w;
        } 
      } 
      m = this.mdsize % this.w;
      long l = 0L;
      for (n = 0; n < m; n++) {
        l ^= ((arrayOfByte[b] & 0xFF) << n << 3);
        b++;
      } 
      m <<= 3;
      for (n = 0; n < m; n += this.w) {
        j += (int)(l & this.k);
        l >>>= this.w;
      } 
      this.checksum = (this.messagesize << this.w) - j;
      i = this.checksum;
      for (n = 0; n < k; n += this.w) {
        j += i & this.k;
        i >>>= this.w;
      } 
    } else if (this.w < 57) {
      int m = 0;
      while (m <= (this.mdsize << 3) - this.w) {
        int i2 = m >>> 3;
        int i4 = m % 8;
        m += this.w;
        int i3 = m + 7 >>> 3;
        long l = 0L;
        byte b = 0;
        for (int i5 = i2; i5 < i3; i5++) {
          l ^= ((arrayOfByte[i5] & 0xFF) << b << 3);
          b++;
        } 
        l >>>= i4;
        j = (int)(j + (l & this.k));
      } 
      int n = m >>> 3;
      if (n < this.mdsize) {
        int i2 = m % 8;
        long l = 0L;
        byte b = 0;
        for (int i3 = n; i3 < this.mdsize; i3++) {
          l ^= ((arrayOfByte[i3] & 0xFF) << b << 3);
          b++;
        } 
        l >>>= i2;
        j = (int)(j + (l & this.k));
      } 
      this.checksum = (this.messagesize << this.w) - j;
      i = this.checksum;
      int i1;
      for (i1 = 0; i1 < k; i1 += this.w) {
        j += i & this.k;
        i >>>= this.w;
      } 
    } 
    this.keysize = this.messagesize + (int)Math.ceil(k / this.w);
    this.steps = (int)Math.ceil((this.keysize + j) / (1 << this.height));
    this.sign = new byte[this.keysize * this.mdsize];
    this.counter = 0;
    this.test = 0;
    this.ii = 0;
    this.test8 = 0L;
    this.r = 0;
    this.privateKeyOTS = new byte[this.mdsize];
    this.seed = new byte[this.mdsize];
    System.arraycopy(paramArrayOfbyte1, 0, this.seed, 0, this.mdsize);
  }
  
  public boolean updateSign() {
    for (byte b = 0; b < this.steps; b++) {
      if (this.counter < this.keysize)
        oneStep(); 
      if (this.counter == this.keysize)
        return true; 
    } 
    return false;
  }
  
  public byte[] getSig() {
    return this.sign;
  }
  
  private void oneStep() {
    if (8 % this.w == 0) {
      if (this.test == 0) {
        this.privateKeyOTS = this.gmssRandom.nextSeed(this.seed);
        if (this.ii < this.mdsize) {
          this.test = this.hash[this.ii] & this.k;
          this.hash[this.ii] = (byte)(this.hash[this.ii] >>> this.w);
        } else {
          this.test = this.checksum & this.k;
          this.checksum >>>= this.w;
        } 
      } else if (this.test > 0) {
        this.messDigestOTS.update(this.privateKeyOTS, 0, this.privateKeyOTS.length);
        this.privateKeyOTS = new byte[this.messDigestOTS.getDigestSize()];
        this.messDigestOTS.doFinal(this.privateKeyOTS, 0);
        this.test--;
      } 
      if (this.test == 0) {
        System.arraycopy(this.privateKeyOTS, 0, this.sign, this.counter * this.mdsize, this.mdsize);
        this.counter++;
        if (this.counter % 8 / this.w == 0)
          this.ii++; 
      } 
    } else if (this.w < 8) {
      if (this.test == 0) {
        if (this.counter % 8 == 0 && this.ii < this.mdsize) {
          this.big8 = 0L;
          if (this.counter < this.mdsize / this.w << 3) {
            for (byte b = 0; b < this.w; b++) {
              this.big8 ^= ((this.hash[this.ii] & 0xFF) << b << 3);
              this.ii++;
            } 
          } else {
            for (byte b = 0; b < this.mdsize % this.w; b++) {
              this.big8 ^= ((this.hash[this.ii] & 0xFF) << b << 3);
              this.ii++;
            } 
          } 
        } 
        if (this.counter == this.messagesize)
          this.big8 = this.checksum; 
        this.test = (int)(this.big8 & this.k);
        this.privateKeyOTS = this.gmssRandom.nextSeed(this.seed);
      } else if (this.test > 0) {
        this.messDigestOTS.update(this.privateKeyOTS, 0, this.privateKeyOTS.length);
        this.privateKeyOTS = new byte[this.messDigestOTS.getDigestSize()];
        this.messDigestOTS.doFinal(this.privateKeyOTS, 0);
        this.test--;
      } 
      if (this.test == 0) {
        System.arraycopy(this.privateKeyOTS, 0, this.sign, this.counter * this.mdsize, this.mdsize);
        this.big8 >>>= this.w;
        this.counter++;
      } 
    } else if (this.w < 57) {
      if (this.test8 == 0L) {
        this.big8 = 0L;
        this.ii = 0;
        int j = this.r % 8;
        int i = this.r >>> 3;
        if (i < this.mdsize) {
          int k;
          if (this.r <= (this.mdsize << 3) - this.w) {
            this.r += this.w;
            k = this.r + 7 >>> 3;
          } else {
            k = this.mdsize;
            this.r += this.w;
          } 
          for (int m = i; m < k; m++) {
            this.big8 ^= ((this.hash[m] & 0xFF) << this.ii << 3);
            this.ii++;
          } 
          this.big8 >>>= j;
          this.test8 = this.big8 & this.k;
        } else {
          this.test8 = (this.checksum & this.k);
          this.checksum >>>= this.w;
        } 
        this.privateKeyOTS = this.gmssRandom.nextSeed(this.seed);
      } else if (this.test8 > 0L) {
        this.messDigestOTS.update(this.privateKeyOTS, 0, this.privateKeyOTS.length);
        this.privateKeyOTS = new byte[this.messDigestOTS.getDigestSize()];
        this.messDigestOTS.doFinal(this.privateKeyOTS, 0);
        this.test8--;
      } 
      if (this.test8 == 0L) {
        System.arraycopy(this.privateKeyOTS, 0, this.sign, this.counter * this.mdsize, this.mdsize);
        this.counter++;
      } 
    } 
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
  
  public byte[][] getStatByte() {
    byte[][] arrayOfByte = new byte[5][this.mdsize];
    arrayOfByte[0] = this.privateKeyOTS;
    arrayOfByte[1] = this.seed;
    arrayOfByte[2] = this.hash;
    arrayOfByte[3] = this.sign;
    arrayOfByte[4] = getStatLong();
    return arrayOfByte;
  }
  
  public int[] getStatInt() {
    int[] arrayOfInt = new int[9];
    arrayOfInt[0] = this.counter;
    arrayOfInt[1] = this.test;
    arrayOfInt[2] = this.ii;
    arrayOfInt[3] = this.r;
    arrayOfInt[4] = this.steps;
    arrayOfInt[5] = this.keysize;
    arrayOfInt[6] = this.height;
    arrayOfInt[7] = this.w;
    arrayOfInt[8] = this.checksum;
    return arrayOfInt;
  }
  
  public byte[] getStatLong() {
    byte[] arrayOfByte = new byte[16];
    arrayOfByte[0] = (byte)(int)(this.test8 & 0xFFL);
    arrayOfByte[1] = (byte)(int)(this.test8 >> 8L & 0xFFL);
    arrayOfByte[2] = (byte)(int)(this.test8 >> 16L & 0xFFL);
    arrayOfByte[3] = (byte)(int)(this.test8 >> 24L & 0xFFL);
    arrayOfByte[4] = (byte)(int)(this.test8 >> 32L & 0xFFL);
    arrayOfByte[5] = (byte)(int)(this.test8 >> 40L & 0xFFL);
    arrayOfByte[6] = (byte)(int)(this.test8 >> 48L & 0xFFL);
    arrayOfByte[7] = (byte)(int)(this.test8 >> 56L & 0xFFL);
    arrayOfByte[8] = (byte)(int)(this.big8 & 0xFFL);
    arrayOfByte[9] = (byte)(int)(this.big8 >> 8L & 0xFFL);
    arrayOfByte[10] = (byte)(int)(this.big8 >> 16L & 0xFFL);
    arrayOfByte[11] = (byte)(int)(this.big8 >> 24L & 0xFFL);
    arrayOfByte[12] = (byte)(int)(this.big8 >> 32L & 0xFFL);
    arrayOfByte[13] = (byte)(int)(this.big8 >> 40L & 0xFFL);
    arrayOfByte[14] = (byte)(int)(this.big8 >> 48L & 0xFFL);
    arrayOfByte[15] = (byte)(int)(this.big8 >> 56L & 0xFFL);
    return arrayOfByte;
  }
  
  public String toString() {
    String str = "" + this.big8 + "  ";
    int[] arrayOfInt = new int[9];
    arrayOfInt = getStatInt();
    byte[][] arrayOfByte = new byte[5][this.mdsize];
    arrayOfByte = getStatByte();
    byte b;
    for (b = 0; b < 9; b++)
      str = str + arrayOfInt[b] + " "; 
    for (b = 0; b < 5; b++)
      str = str + new String(Hex.encode(arrayOfByte[b])) + " "; 
    return str;
  }
}
