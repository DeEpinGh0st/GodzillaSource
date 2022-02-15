package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;

public class HKDFBytesGenerator implements DerivationFunction {
  private HMac hMacHash;
  
  private int hashLen;
  
  private byte[] info;
  
  private byte[] currentT;
  
  private int generatedBytes;
  
  public HKDFBytesGenerator(Digest paramDigest) {
    this.hMacHash = new HMac(paramDigest);
    this.hashLen = paramDigest.getDigestSize();
  }
  
  public void init(DerivationParameters paramDerivationParameters) {
    if (!(paramDerivationParameters instanceof HKDFParameters))
      throw new IllegalArgumentException("HKDF parameters required for HKDFBytesGenerator"); 
    HKDFParameters hKDFParameters = (HKDFParameters)paramDerivationParameters;
    if (hKDFParameters.skipExtract()) {
      this.hMacHash.init((CipherParameters)new KeyParameter(hKDFParameters.getIKM()));
    } else {
      this.hMacHash.init((CipherParameters)extract(hKDFParameters.getSalt(), hKDFParameters.getIKM()));
    } 
    this.info = hKDFParameters.getInfo();
    this.generatedBytes = 0;
    this.currentT = new byte[this.hashLen];
  }
  
  private KeyParameter extract(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    if (paramArrayOfbyte1 == null) {
      this.hMacHash.init((CipherParameters)new KeyParameter(new byte[this.hashLen]));
    } else {
      this.hMacHash.init((CipherParameters)new KeyParameter(paramArrayOfbyte1));
    } 
    this.hMacHash.update(paramArrayOfbyte2, 0, paramArrayOfbyte2.length);
    byte[] arrayOfByte = new byte[this.hashLen];
    this.hMacHash.doFinal(arrayOfByte, 0);
    return new KeyParameter(arrayOfByte);
  }
  
  private void expandNext() throws DataLengthException {
    int i = this.generatedBytes / this.hashLen + 1;
    if (i >= 256)
      throw new DataLengthException("HKDF cannot generate more than 255 blocks of HashLen size"); 
    if (this.generatedBytes != 0)
      this.hMacHash.update(this.currentT, 0, this.hashLen); 
    this.hMacHash.update(this.info, 0, this.info.length);
    this.hMacHash.update((byte)i);
    this.hMacHash.doFinal(this.currentT, 0);
  }
  
  public Digest getDigest() {
    return this.hMacHash.getUnderlyingDigest();
  }
  
  public int generateBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws DataLengthException, IllegalArgumentException {
    if (this.generatedBytes + paramInt2 > 255 * this.hashLen)
      throw new DataLengthException("HKDF may only be used for 255 * HashLen bytes of output"); 
    if (this.generatedBytes % this.hashLen == 0)
      expandNext(); 
    int i = paramInt2;
    int j = this.generatedBytes % this.hashLen;
    int k = this.hashLen - this.generatedBytes % this.hashLen;
    int m = Math.min(k, i);
    System.arraycopy(this.currentT, j, paramArrayOfbyte, paramInt1, m);
    this.generatedBytes += m;
    i -= m;
    for (paramInt1 += m; i > 0; paramInt1 += m) {
      expandNext();
      m = Math.min(this.hashLen, i);
      System.arraycopy(this.currentT, 0, paramArrayOfbyte, paramInt1, m);
      this.generatedBytes += m;
      i -= m;
    } 
    return paramInt2;
  }
}
