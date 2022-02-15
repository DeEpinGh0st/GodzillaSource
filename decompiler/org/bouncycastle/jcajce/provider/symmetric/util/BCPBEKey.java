package org.bouncycastle.jcajce.provider.symmetric.util;

import javax.crypto.interfaces.PBEKey;
import javax.crypto.spec.PBEKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class BCPBEKey implements PBEKey {
  String algorithm;
  
  ASN1ObjectIdentifier oid;
  
  int type;
  
  int digest;
  
  int keySize;
  
  int ivSize;
  
  CipherParameters param;
  
  PBEKeySpec pbeKeySpec;
  
  boolean tryWrong = false;
  
  public BCPBEKey(String paramString, ASN1ObjectIdentifier paramASN1ObjectIdentifier, int paramInt1, int paramInt2, int paramInt3, int paramInt4, PBEKeySpec paramPBEKeySpec, CipherParameters paramCipherParameters) {
    this.algorithm = paramString;
    this.oid = paramASN1ObjectIdentifier;
    this.type = paramInt1;
    this.digest = paramInt2;
    this.keySize = paramInt3;
    this.ivSize = paramInt4;
    this.pbeKeySpec = paramPBEKeySpec;
    this.param = paramCipherParameters;
  }
  
  public String getAlgorithm() {
    return this.algorithm;
  }
  
  public String getFormat() {
    return "RAW";
  }
  
  public byte[] getEncoded() {
    if (this.param != null) {
      KeyParameter keyParameter;
      if (this.param instanceof ParametersWithIV) {
        keyParameter = (KeyParameter)((ParametersWithIV)this.param).getParameters();
      } else {
        keyParameter = (KeyParameter)this.param;
      } 
      return keyParameter.getKey();
    } 
    return (this.type == 2) ? PBEParametersGenerator.PKCS12PasswordToBytes(this.pbeKeySpec.getPassword()) : ((this.type == 5) ? PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(this.pbeKeySpec.getPassword()) : PBEParametersGenerator.PKCS5PasswordToBytes(this.pbeKeySpec.getPassword()));
  }
  
  int getType() {
    return this.type;
  }
  
  int getDigest() {
    return this.digest;
  }
  
  int getKeySize() {
    return this.keySize;
  }
  
  public int getIvSize() {
    return this.ivSize;
  }
  
  public CipherParameters getParam() {
    return this.param;
  }
  
  public char[] getPassword() {
    return this.pbeKeySpec.getPassword();
  }
  
  public byte[] getSalt() {
    return this.pbeKeySpec.getSalt();
  }
  
  public int getIterationCount() {
    return this.pbeKeySpec.getIterationCount();
  }
  
  public ASN1ObjectIdentifier getOID() {
    return this.oid;
  }
  
  public void setTryWrongPKCS12Zero(boolean paramBoolean) {
    this.tryWrong = paramBoolean;
  }
  
  boolean shouldTryWrongPKCS12() {
    return this.tryWrong;
  }
}
