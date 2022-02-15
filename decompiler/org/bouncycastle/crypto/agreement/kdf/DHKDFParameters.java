package org.bouncycastle.crypto.agreement.kdf;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.DerivationParameters;

public class DHKDFParameters implements DerivationParameters {
  private ASN1ObjectIdentifier algorithm;
  
  private int keySize;
  
  private byte[] z;
  
  private byte[] extraInfo;
  
  public DHKDFParameters(ASN1ObjectIdentifier paramASN1ObjectIdentifier, int paramInt, byte[] paramArrayOfbyte) {
    this(paramASN1ObjectIdentifier, paramInt, paramArrayOfbyte, null);
  }
  
  public DHKDFParameters(ASN1ObjectIdentifier paramASN1ObjectIdentifier, int paramInt, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    this.algorithm = paramASN1ObjectIdentifier;
    this.keySize = paramInt;
    this.z = paramArrayOfbyte1;
    this.extraInfo = paramArrayOfbyte2;
  }
  
  public ASN1ObjectIdentifier getAlgorithm() {
    return this.algorithm;
  }
  
  public int getKeySize() {
    return this.keySize;
  }
  
  public byte[] getZ() {
    return this.z;
  }
  
  public byte[] getExtraInfo() {
    return this.extraInfo;
  }
}
