package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

public class ParametersWithSalt implements CipherParameters {
  private byte[] salt;
  
  private CipherParameters parameters;
  
  public ParametersWithSalt(CipherParameters paramCipherParameters, byte[] paramArrayOfbyte) {
    this(paramCipherParameters, paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  public ParametersWithSalt(CipherParameters paramCipherParameters, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    this.salt = new byte[paramInt2];
    this.parameters = paramCipherParameters;
    System.arraycopy(paramArrayOfbyte, paramInt1, this.salt, 0, paramInt2);
  }
  
  public byte[] getSalt() {
    return this.salt;
  }
  
  public CipherParameters getParameters() {
    return this.parameters;
  }
}
