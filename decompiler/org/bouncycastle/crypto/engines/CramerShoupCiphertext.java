package org.bouncycastle.crypto.engines;

import java.math.BigInteger;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class CramerShoupCiphertext {
  BigInteger u1;
  
  BigInteger u2;
  
  BigInteger e;
  
  BigInteger v;
  
  public CramerShoupCiphertext() {}
  
  public CramerShoupCiphertext(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4) {
    this.u1 = paramBigInteger1;
    this.u2 = paramBigInteger2;
    this.e = paramBigInteger3;
    this.v = paramBigInteger4;
  }
  
  public CramerShoupCiphertext(byte[] paramArrayOfbyte) {
    int i = 0;
    int j = Pack.bigEndianToInt(paramArrayOfbyte, i);
    i += true;
    byte[] arrayOfByte = Arrays.copyOfRange(paramArrayOfbyte, i, i + j);
    i += j;
    this.u1 = new BigInteger(arrayOfByte);
    j = Pack.bigEndianToInt(paramArrayOfbyte, i);
    i += 4;
    arrayOfByte = Arrays.copyOfRange(paramArrayOfbyte, i, i + j);
    i += j;
    this.u2 = new BigInteger(arrayOfByte);
    j = Pack.bigEndianToInt(paramArrayOfbyte, i);
    i += 4;
    arrayOfByte = Arrays.copyOfRange(paramArrayOfbyte, i, i + j);
    i += j;
    this.e = new BigInteger(arrayOfByte);
    j = Pack.bigEndianToInt(paramArrayOfbyte, i);
    i += 4;
    arrayOfByte = Arrays.copyOfRange(paramArrayOfbyte, i, i + j);
    i += j;
    this.v = new BigInteger(arrayOfByte);
  }
  
  public BigInteger getU1() {
    return this.u1;
  }
  
  public void setU1(BigInteger paramBigInteger) {
    this.u1 = paramBigInteger;
  }
  
  public BigInteger getU2() {
    return this.u2;
  }
  
  public void setU2(BigInteger paramBigInteger) {
    this.u2 = paramBigInteger;
  }
  
  public BigInteger getE() {
    return this.e;
  }
  
  public void setE(BigInteger paramBigInteger) {
    this.e = paramBigInteger;
  }
  
  public BigInteger getV() {
    return this.v;
  }
  
  public void setV(BigInteger paramBigInteger) {
    this.v = paramBigInteger;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("u1: " + this.u1.toString());
    stringBuffer.append("\nu2: " + this.u2.toString());
    stringBuffer.append("\ne: " + this.e.toString());
    stringBuffer.append("\nv: " + this.v.toString());
    return stringBuffer.toString();
  }
  
  public byte[] toByteArray() {
    byte[] arrayOfByte1 = this.u1.toByteArray();
    int i = arrayOfByte1.length;
    byte[] arrayOfByte2 = this.u2.toByteArray();
    int j = arrayOfByte2.length;
    byte[] arrayOfByte3 = this.e.toByteArray();
    int k = arrayOfByte3.length;
    byte[] arrayOfByte4 = this.v.toByteArray();
    int m = arrayOfByte4.length;
    int n = 0;
    byte[] arrayOfByte5 = new byte[i + j + k + m + 16];
    Pack.intToBigEndian(i, arrayOfByte5, n);
    n += true;
    System.arraycopy(arrayOfByte1, 0, arrayOfByte5, n, i);
    n += i;
    Pack.intToBigEndian(j, arrayOfByte5, n);
    n += 4;
    System.arraycopy(arrayOfByte2, 0, arrayOfByte5, n, j);
    n += j;
    Pack.intToBigEndian(k, arrayOfByte5, n);
    n += 4;
    System.arraycopy(arrayOfByte3, 0, arrayOfByte5, n, k);
    n += k;
    Pack.intToBigEndian(m, arrayOfByte5, n);
    n += 4;
    System.arraycopy(arrayOfByte4, 0, arrayOfByte5, n, m);
    n += m;
    return arrayOfByte5;
  }
}
