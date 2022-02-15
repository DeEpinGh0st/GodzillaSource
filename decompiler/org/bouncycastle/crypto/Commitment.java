package org.bouncycastle.crypto;

public class Commitment {
  private final byte[] secret;
  
  private final byte[] commitment;
  
  public Commitment(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    this.secret = paramArrayOfbyte1;
    this.commitment = paramArrayOfbyte2;
  }
  
  public byte[] getSecret() {
    return this.secret;
  }
  
  public byte[] getCommitment() {
    return this.commitment;
  }
}
