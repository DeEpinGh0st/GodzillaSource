package org.bouncycastle.pqc.crypto.gmss;

public class GMSSPublicKeyParameters extends GMSSKeyParameters {
  private byte[] gmssPublicKey;
  
  public GMSSPublicKeyParameters(byte[] paramArrayOfbyte, GMSSParameters paramGMSSParameters) {
    super(false, paramGMSSParameters);
    this.gmssPublicKey = paramArrayOfbyte;
  }
  
  public byte[] getPublicKey() {
    return this.gmssPublicKey;
  }
}
