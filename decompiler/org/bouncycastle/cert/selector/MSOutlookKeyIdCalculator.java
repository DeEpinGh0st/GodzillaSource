package org.bouncycastle.cert.selector;

import java.io.IOException;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.util.Pack;

class MSOutlookKeyIdCalculator {
  static byte[] calculateKeyId(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
    SHA1Digest sHA1Digest = new SHA1Digest();
    byte[] arrayOfByte1 = new byte[sHA1Digest.getDigestSize()];
    byte[] arrayOfByte2 = new byte[0];
    try {
      arrayOfByte2 = paramSubjectPublicKeyInfo.getEncoded("DER");
    } catch (IOException iOException) {
      return new byte[0];
    } 
    sHA1Digest.update(arrayOfByte2, 0, arrayOfByte2.length);
    sHA1Digest.doFinal(arrayOfByte1, 0);
    return arrayOfByte1;
  }
  
  private static abstract class GeneralDigest {
    private static final int BYTE_LENGTH = 64;
    
    private byte[] xBuf = new byte[4];
    
    private int xBufOff;
    
    private long byteCount;
    
    protected GeneralDigest() {
      this.xBufOff = 0;
    }
    
    protected GeneralDigest(GeneralDigest param1GeneralDigest) {
      copyIn(param1GeneralDigest);
    }
    
    protected void copyIn(GeneralDigest param1GeneralDigest) {
      System.arraycopy(param1GeneralDigest.xBuf, 0, this.xBuf, 0, param1GeneralDigest.xBuf.length);
      this.xBufOff = param1GeneralDigest.xBufOff;
      this.byteCount = param1GeneralDigest.byteCount;
    }
    
    public void update(byte param1Byte) {
      this.xBuf[this.xBufOff++] = param1Byte;
      if (this.xBufOff == this.xBuf.length) {
        processWord(this.xBuf, 0);
        this.xBufOff = 0;
      } 
      this.byteCount++;
    }
    
    public void update(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) {
      while (this.xBufOff != 0 && param1Int2 > 0) {
        update(param1ArrayOfbyte[param1Int1]);
        param1Int1++;
        param1Int2--;
      } 
      while (param1Int2 > this.xBuf.length) {
        processWord(param1ArrayOfbyte, param1Int1);
        param1Int1 += this.xBuf.length;
        param1Int2 -= this.xBuf.length;
        this.byteCount += this.xBuf.length;
      } 
      while (param1Int2 > 0) {
        update(param1ArrayOfbyte[param1Int1]);
        param1Int1++;
        param1Int2--;
      } 
    }
    
    public void finish() {
      long l = this.byteCount << 3L;
      update(-128);
      while (this.xBufOff != 0)
        update((byte)0); 
      processLength(l);
      processBlock();
    }
    
    public void reset() {
      this.byteCount = 0L;
      this.xBufOff = 0;
      for (byte b = 0; b < this.xBuf.length; b++)
        this.xBuf[b] = 0; 
    }
    
    protected abstract void processWord(byte[] param1ArrayOfbyte, int param1Int);
    
    protected abstract void processLength(long param1Long);
    
    protected abstract void processBlock();
  }
  
  private static class SHA1Digest extends GeneralDigest {
    private static final int DIGEST_LENGTH = 20;
    
    private int H1;
    
    private int H2;
    
    private int H3;
    
    private int H4;
    
    private int H5;
    
    private int[] X = new int[80];
    
    private int xOff;
    
    private static final int Y1 = 1518500249;
    
    private static final int Y2 = 1859775393;
    
    private static final int Y3 = -1894007588;
    
    private static final int Y4 = -899497514;
    
    public SHA1Digest() {
      reset();
    }
    
    public String getAlgorithmName() {
      return "SHA-1";
    }
    
    public int getDigestSize() {
      return 20;
    }
    
    protected void processWord(byte[] param1ArrayOfbyte, int param1Int) {
      int i = param1ArrayOfbyte[param1Int] << 24;
      i |= (param1ArrayOfbyte[++param1Int] & 0xFF) << 16;
      i |= (param1ArrayOfbyte[++param1Int] & 0xFF) << 8;
      i |= param1ArrayOfbyte[++param1Int] & 0xFF;
      this.X[this.xOff] = i;
      if (++this.xOff == 16)
        processBlock(); 
    }
    
    protected void processLength(long param1Long) {
      if (this.xOff > 14)
        processBlock(); 
      this.X[14] = (int)(param1Long >>> 32L);
      this.X[15] = (int)(param1Long & 0xFFFFFFFFFFFFFFFFL);
    }
    
    public int doFinal(byte[] param1ArrayOfbyte, int param1Int) {
      finish();
      Pack.intToBigEndian(this.H1, param1ArrayOfbyte, param1Int);
      Pack.intToBigEndian(this.H2, param1ArrayOfbyte, param1Int + 4);
      Pack.intToBigEndian(this.H3, param1ArrayOfbyte, param1Int + 8);
      Pack.intToBigEndian(this.H4, param1ArrayOfbyte, param1Int + 12);
      Pack.intToBigEndian(this.H5, param1ArrayOfbyte, param1Int + 16);
      reset();
      return 20;
    }
    
    public void reset() {
      super.reset();
      this.H1 = 1732584193;
      this.H2 = -271733879;
      this.H3 = -1732584194;
      this.H4 = 271733878;
      this.H5 = -1009589776;
      this.xOff = 0;
      for (byte b = 0; b != this.X.length; b++)
        this.X[b] = 0; 
    }
    
    private int f(int param1Int1, int param1Int2, int param1Int3) {
      return param1Int1 & param1Int2 | (param1Int1 ^ 0xFFFFFFFF) & param1Int3;
    }
    
    private int h(int param1Int1, int param1Int2, int param1Int3) {
      return param1Int1 ^ param1Int2 ^ param1Int3;
    }
    
    private int g(int param1Int1, int param1Int2, int param1Int3) {
      return param1Int1 & param1Int2 | param1Int1 & param1Int3 | param1Int2 & param1Int3;
    }
    
    protected void processBlock() {
      int i;
      for (i = 16; i < 80; i++) {
        int i1 = this.X[i - 3] ^ this.X[i - 8] ^ this.X[i - 14] ^ this.X[i - 16];
        this.X[i] = i1 << 1 | i1 >>> 31;
      } 
      i = this.H1;
      int j = this.H2;
      int k = this.H3;
      int m = this.H4;
      int n = this.H5;
      byte b1 = 0;
      byte b2;
      for (b2 = 0; b2 < 4; b2++) {
        n += (i << 5 | i >>> 27) + f(j, k, m) + this.X[b1++] + 1518500249;
        j = j << 30 | j >>> 2;
        m += (n << 5 | n >>> 27) + f(i, j, k) + this.X[b1++] + 1518500249;
        i = i << 30 | i >>> 2;
        k += (m << 5 | m >>> 27) + f(n, i, j) + this.X[b1++] + 1518500249;
        n = n << 30 | n >>> 2;
        j += (k << 5 | k >>> 27) + f(m, n, i) + this.X[b1++] + 1518500249;
        m = m << 30 | m >>> 2;
        i += (j << 5 | j >>> 27) + f(k, m, n) + this.X[b1++] + 1518500249;
        k = k << 30 | k >>> 2;
      } 
      for (b2 = 0; b2 < 4; b2++) {
        n += (i << 5 | i >>> 27) + h(j, k, m) + this.X[b1++] + 1859775393;
        j = j << 30 | j >>> 2;
        m += (n << 5 | n >>> 27) + h(i, j, k) + this.X[b1++] + 1859775393;
        i = i << 30 | i >>> 2;
        k += (m << 5 | m >>> 27) + h(n, i, j) + this.X[b1++] + 1859775393;
        n = n << 30 | n >>> 2;
        j += (k << 5 | k >>> 27) + h(m, n, i) + this.X[b1++] + 1859775393;
        m = m << 30 | m >>> 2;
        i += (j << 5 | j >>> 27) + h(k, m, n) + this.X[b1++] + 1859775393;
        k = k << 30 | k >>> 2;
      } 
      for (b2 = 0; b2 < 4; b2++) {
        n += (i << 5 | i >>> 27) + g(j, k, m) + this.X[b1++] + -1894007588;
        j = j << 30 | j >>> 2;
        m += (n << 5 | n >>> 27) + g(i, j, k) + this.X[b1++] + -1894007588;
        i = i << 30 | i >>> 2;
        k += (m << 5 | m >>> 27) + g(n, i, j) + this.X[b1++] + -1894007588;
        n = n << 30 | n >>> 2;
        j += (k << 5 | k >>> 27) + g(m, n, i) + this.X[b1++] + -1894007588;
        m = m << 30 | m >>> 2;
        i += (j << 5 | j >>> 27) + g(k, m, n) + this.X[b1++] + -1894007588;
        k = k << 30 | k >>> 2;
      } 
      for (b2 = 0; b2 <= 3; b2++) {
        n += (i << 5 | i >>> 27) + h(j, k, m) + this.X[b1++] + -899497514;
        j = j << 30 | j >>> 2;
        m += (n << 5 | n >>> 27) + h(i, j, k) + this.X[b1++] + -899497514;
        i = i << 30 | i >>> 2;
        k += (m << 5 | m >>> 27) + h(n, i, j) + this.X[b1++] + -899497514;
        n = n << 30 | n >>> 2;
        j += (k << 5 | k >>> 27) + h(m, n, i) + this.X[b1++] + -899497514;
        m = m << 30 | m >>> 2;
        i += (j << 5 | j >>> 27) + h(k, m, n) + this.X[b1++] + -899497514;
        k = k << 30 | k >>> 2;
      } 
      this.H1 += i;
      this.H2 += j;
      this.H3 += k;
      this.H4 += m;
      this.H5 += n;
      this.xOff = 0;
      for (b2 = 0; b2 < 16; b2++)
        this.X[b2] = 0; 
    }
  }
}
