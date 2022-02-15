package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

public class ParametersWithID implements CipherParameters {
  private CipherParameters parameters;
  
  private byte[] id;
  
  public ParametersWithID(CipherParameters paramCipherParameters, byte[] paramArrayOfbyte) {
    this.parameters = paramCipherParameters;
    this.id = paramArrayOfbyte;
  }
  
  public byte[] getID() {
    return this.id;
  }
  
  public CipherParameters getParameters() {
    return this.parameters;
  }
}
