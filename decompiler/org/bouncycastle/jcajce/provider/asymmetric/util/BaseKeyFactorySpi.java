package org.bouncycastle.jcajce.provider.asymmetric.util;

import java.security.Key;
import java.security.KeyFactorySpi;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;

public abstract class BaseKeyFactorySpi extends KeyFactorySpi implements AsymmetricKeyInfoConverter {
  protected PrivateKey engineGeneratePrivate(KeySpec paramKeySpec) throws InvalidKeySpecException {
    if (paramKeySpec instanceof PKCS8EncodedKeySpec)
      try {
        return generatePrivate(PrivateKeyInfo.getInstance(((PKCS8EncodedKeySpec)paramKeySpec).getEncoded()));
      } catch (Exception exception) {
        throw new InvalidKeySpecException("encoded key spec not recognized: " + exception.getMessage());
      }  
    throw new InvalidKeySpecException("key spec not recognized");
  }
  
  protected PublicKey engineGeneratePublic(KeySpec paramKeySpec) throws InvalidKeySpecException {
    if (paramKeySpec instanceof X509EncodedKeySpec)
      try {
        return generatePublic(SubjectPublicKeyInfo.getInstance(((X509EncodedKeySpec)paramKeySpec).getEncoded()));
      } catch (Exception exception) {
        throw new InvalidKeySpecException("encoded key spec not recognized: " + exception.getMessage());
      }  
    throw new InvalidKeySpecException("key spec not recognized");
  }
  
  protected KeySpec engineGetKeySpec(Key paramKey, Class paramClass) throws InvalidKeySpecException {
    if (paramClass.isAssignableFrom(PKCS8EncodedKeySpec.class) && paramKey.getFormat().equals("PKCS#8"))
      return new PKCS8EncodedKeySpec(paramKey.getEncoded()); 
    if (paramClass.isAssignableFrom(X509EncodedKeySpec.class) && paramKey.getFormat().equals("X.509"))
      return new X509EncodedKeySpec(paramKey.getEncoded()); 
    throw new InvalidKeySpecException("not implemented yet " + paramKey + " " + paramClass);
  }
}
