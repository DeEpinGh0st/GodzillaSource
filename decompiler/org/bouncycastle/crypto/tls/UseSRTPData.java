package org.bouncycastle.crypto.tls;

public class UseSRTPData {
  protected int[] protectionProfiles;
  
  protected byte[] mki;
  
  public UseSRTPData(int[] paramArrayOfint, byte[] paramArrayOfbyte) {
    if (paramArrayOfint == null || paramArrayOfint.length < 1 || paramArrayOfint.length >= 32768)
      throw new IllegalArgumentException("'protectionProfiles' must have length from 1 to (2^15 - 1)"); 
    if (paramArrayOfbyte == null) {
      paramArrayOfbyte = TlsUtils.EMPTY_BYTES;
    } else if (paramArrayOfbyte.length > 255) {
      throw new IllegalArgumentException("'mki' cannot be longer than 255 bytes");
    } 
    this.protectionProfiles = paramArrayOfint;
    this.mki = paramArrayOfbyte;
  }
  
  public int[] getProtectionProfiles() {
    return this.protectionProfiles;
  }
  
  public byte[] getMki() {
    return this.mki;
  }
}
