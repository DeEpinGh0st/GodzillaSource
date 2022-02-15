package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.security.InvalidKeyException;
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
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class KeyFactory extends KeyFactorySpi {
  protected PrivateKey engineGeneratePrivate(KeySpec paramKeySpec) throws InvalidKeySpecException {
    if (paramKeySpec instanceof PKCS8EncodedKeySpec)
      try {
        PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(((PKCS8EncodedKeySpec)paramKeySpec).getEncoded());
        PrivateKey privateKey = BouncyCastleProvider.getPrivateKey(privateKeyInfo);
        if (privateKey != null)
          return privateKey; 
        throw new InvalidKeySpecException("no factory found for OID: " + privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm());
      } catch (Exception exception) {
        throw new InvalidKeySpecException(exception.toString());
      }  
    throw new InvalidKeySpecException("Unknown KeySpec type: " + paramKeySpec.getClass().getName());
  }
  
  protected PublicKey engineGeneratePublic(KeySpec paramKeySpec) throws InvalidKeySpecException {
    if (paramKeySpec instanceof X509EncodedKeySpec)
      try {
        SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(((X509EncodedKeySpec)paramKeySpec).getEncoded());
        PublicKey publicKey = BouncyCastleProvider.getPublicKey(subjectPublicKeyInfo);
        if (publicKey != null)
          return publicKey; 
        throw new InvalidKeySpecException("no factory found for OID: " + subjectPublicKeyInfo.getAlgorithm().getAlgorithm());
      } catch (Exception exception) {
        throw new InvalidKeySpecException(exception.toString());
      }  
    throw new InvalidKeySpecException("Unknown KeySpec type: " + paramKeySpec.getClass().getName());
  }
  
  protected KeySpec engineGetKeySpec(Key paramKey, Class paramClass) throws InvalidKeySpecException {
    if (paramClass.isAssignableFrom(PKCS8EncodedKeySpec.class) && paramKey.getFormat().equals("PKCS#8"))
      return new PKCS8EncodedKeySpec(paramKey.getEncoded()); 
    if (paramClass.isAssignableFrom(X509EncodedKeySpec.class) && paramKey.getFormat().equals("X.509"))
      return new X509EncodedKeySpec(paramKey.getEncoded()); 
    throw new InvalidKeySpecException("not implemented yet " + paramKey + " " + paramClass);
  }
  
  protected Key engineTranslateKey(Key paramKey) throws InvalidKeyException {
    throw new InvalidKeyException("not implemented yet " + paramKey);
  }
}
