package org.bouncycastle.crypto.digests;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.Digest;

public class NullDigest implements Digest {
  private ByteArrayOutputStream bOut = new ByteArrayOutputStream();
  
  public String getAlgorithmName() {
    return "NULL";
  }
  
  public int getDigestSize() {
    return this.bOut.size();
  }
  
  public void update(byte paramByte) {
    this.bOut.write(paramByte);
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    this.bOut.write(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) {
    byte[] arrayOfByte = this.bOut.toByteArray();
    System.arraycopy(arrayOfByte, 0, paramArrayOfbyte, paramInt, arrayOfByte.length);
    reset();
    return arrayOfByte.length;
  }
  
  public void reset() {
    this.bOut.reset();
  }
}
