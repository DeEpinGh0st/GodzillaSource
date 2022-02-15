package org.bouncycastle.jce.provider;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

class OldPKCS12ParametersGenerator extends PBEParametersGenerator {
  public static final int KEY_MATERIAL = 1;
  
  public static final int IV_MATERIAL = 2;
  
  public static final int MAC_MATERIAL = 3;
  
  private Digest digest;
  
  private int u;
  
  private int v;
  
  public OldPKCS12ParametersGenerator(Digest paramDigest) {
    this.digest = paramDigest;
    if (paramDigest instanceof org.bouncycastle.crypto.digests.MD5Digest) {
      this.u = 16;
      this.v = 64;
    } else if (paramDigest instanceof org.bouncycastle.crypto.digests.SHA1Digest) {
      this.u = 20;
      this.v = 64;
    } else if (paramDigest instanceof org.bouncycastle.crypto.digests.RIPEMD160Digest) {
      this.u = 20;
      this.v = 64;
    } else {
      throw new IllegalArgumentException("Digest " + paramDigest.getAlgorithmName() + " unsupported");
    } 
  }
  
  private void adjust(byte[] paramArrayOfbyte1, int paramInt, byte[] paramArrayOfbyte2) {
    int i = (paramArrayOfbyte2[paramArrayOfbyte2.length - 1] & 0xFF) + (paramArrayOfbyte1[paramInt + paramArrayOfbyte2.length - 1] & 0xFF) + 1;
    paramArrayOfbyte1[paramInt + paramArrayOfbyte2.length - 1] = (byte)i;
    i >>>= 8;
    for (int j = paramArrayOfbyte2.length - 2; j >= 0; j--) {
      i += (paramArrayOfbyte2[j] & 0xFF) + (paramArrayOfbyte1[paramInt + j] & 0xFF);
      paramArrayOfbyte1[paramInt + j] = (byte)i;
      i >>>= 8;
    } 
  }
  
  private byte[] generateDerivedKey(int paramInt1, int paramInt2) {
    byte[] arrayOfByte3;
    byte[] arrayOfByte4;
    byte[] arrayOfByte1 = new byte[this.v];
    byte[] arrayOfByte2 = new byte[paramInt2];
    for (byte b1 = 0; b1 != arrayOfByte1.length; b1++)
      arrayOfByte1[b1] = (byte)paramInt1; 
    if (this.salt != null && this.salt.length != 0) {
      arrayOfByte3 = new byte[this.v * (this.salt.length + this.v - 1) / this.v];
      for (byte b = 0; b != arrayOfByte3.length; b++)
        arrayOfByte3[b] = this.salt[b % this.salt.length]; 
    } else {
      arrayOfByte3 = new byte[0];
    } 
    if (this.password != null && this.password.length != 0) {
      arrayOfByte4 = new byte[this.v * (this.password.length + this.v - 1) / this.v];
      for (byte b = 0; b != arrayOfByte4.length; b++)
        arrayOfByte4[b] = this.password[b % this.password.length]; 
    } else {
      arrayOfByte4 = new byte[0];
    } 
    byte[] arrayOfByte5 = new byte[arrayOfByte3.length + arrayOfByte4.length];
    System.arraycopy(arrayOfByte3, 0, arrayOfByte5, 0, arrayOfByte3.length);
    System.arraycopy(arrayOfByte4, 0, arrayOfByte5, arrayOfByte3.length, arrayOfByte4.length);
    byte[] arrayOfByte6 = new byte[this.v];
    int i = (paramInt2 + this.u - 1) / this.u;
    for (byte b2 = 1; b2 <= i; b2++) {
      byte[] arrayOfByte = new byte[this.u];
      this.digest.update(arrayOfByte1, 0, arrayOfByte1.length);
      this.digest.update(arrayOfByte5, 0, arrayOfByte5.length);
      this.digest.doFinal(arrayOfByte, 0);
      byte b;
      for (b = 1; b != this.iterationCount; b++) {
        this.digest.update(arrayOfByte, 0, arrayOfByte.length);
        this.digest.doFinal(arrayOfByte, 0);
      } 
      for (b = 0; b != arrayOfByte6.length; b++)
        arrayOfByte6[b2] = arrayOfByte[b % arrayOfByte.length]; 
      for (b = 0; b != arrayOfByte5.length / this.v; b++)
        adjust(arrayOfByte5, b * this.v, arrayOfByte6); 
      if (b2 == i) {
        System.arraycopy(arrayOfByte, 0, arrayOfByte2, (b2 - 1) * this.u, arrayOfByte2.length - (b2 - 1) * this.u);
      } else {
        System.arraycopy(arrayOfByte, 0, arrayOfByte2, (b2 - 1) * this.u, arrayOfByte.length);
      } 
    } 
    return arrayOfByte2;
  }
  
  public CipherParameters generateDerivedParameters(int paramInt) {
    paramInt /= 8;
    byte[] arrayOfByte = generateDerivedKey(1, paramInt);
    return (CipherParameters)new KeyParameter(arrayOfByte, 0, paramInt);
  }
  
  public CipherParameters generateDerivedParameters(int paramInt1, int paramInt2) {
    paramInt1 /= 8;
    paramInt2 /= 8;
    byte[] arrayOfByte1 = generateDerivedKey(1, paramInt1);
    byte[] arrayOfByte2 = generateDerivedKey(2, paramInt2);
    return (CipherParameters)new ParametersWithIV((CipherParameters)new KeyParameter(arrayOfByte1, 0, paramInt1), arrayOfByte2, 0, paramInt2);
  }
  
  public CipherParameters generateDerivedMacParameters(int paramInt) {
    paramInt /= 8;
    byte[] arrayOfByte = generateDerivedKey(3, paramInt);
    return (CipherParameters)new KeyParameter(arrayOfByte, 0, paramInt);
  }
}
