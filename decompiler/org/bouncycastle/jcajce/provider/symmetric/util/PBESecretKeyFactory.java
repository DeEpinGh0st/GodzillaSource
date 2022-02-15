package org.bouncycastle.jcajce.provider.symmetric.util;

import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.CipherParameters;

public class PBESecretKeyFactory extends BaseSecretKeyFactory implements PBE {
  private boolean forCipher;
  
  private int scheme;
  
  private int digest;
  
  private int keySize;
  
  private int ivSize;
  
  public PBESecretKeyFactory(String paramString, ASN1ObjectIdentifier paramASN1ObjectIdentifier, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    super(paramString, paramASN1ObjectIdentifier);
    this.forCipher = paramBoolean;
    this.scheme = paramInt1;
    this.digest = paramInt2;
    this.keySize = paramInt3;
    this.ivSize = paramInt4;
  }
  
  protected SecretKey engineGenerateSecret(KeySpec paramKeySpec) throws InvalidKeySpecException {
    if (paramKeySpec instanceof PBEKeySpec) {
      CipherParameters cipherParameters;
      PBEKeySpec pBEKeySpec = (PBEKeySpec)paramKeySpec;
      if (pBEKeySpec.getSalt() == null)
        return new BCPBEKey(this.algName, this.algOid, this.scheme, this.digest, this.keySize, this.ivSize, pBEKeySpec, null); 
      if (this.forCipher) {
        cipherParameters = PBE.Util.makePBEParameters(pBEKeySpec, this.scheme, this.digest, this.keySize, this.ivSize);
      } else {
        cipherParameters = PBE.Util.makePBEMacParameters(pBEKeySpec, this.scheme, this.digest, this.keySize);
      } 
      return new BCPBEKey(this.algName, this.algOid, this.scheme, this.digest, this.keySize, this.ivSize, pBEKeySpec, cipherParameters);
    } 
    throw new InvalidKeySpecException("Invalid KeySpec");
  }
}
