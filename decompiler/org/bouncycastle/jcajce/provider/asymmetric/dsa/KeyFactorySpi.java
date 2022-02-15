package org.bouncycastle.jcajce.provider.asymmetric.dsa;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPrivateKeySpec;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;

public class KeyFactorySpi extends BaseKeyFactorySpi {
  protected KeySpec engineGetKeySpec(Key paramKey, Class paramClass) throws InvalidKeySpecException {
    if (paramClass.isAssignableFrom(DSAPublicKeySpec.class) && paramKey instanceof DSAPublicKey) {
      DSAPublicKey dSAPublicKey = (DSAPublicKey)paramKey;
      return new DSAPublicKeySpec(dSAPublicKey.getY(), dSAPublicKey.getParams().getP(), dSAPublicKey.getParams().getQ(), dSAPublicKey.getParams().getG());
    } 
    if (paramClass.isAssignableFrom(DSAPrivateKeySpec.class) && paramKey instanceof DSAPrivateKey) {
      DSAPrivateKey dSAPrivateKey = (DSAPrivateKey)paramKey;
      return new DSAPrivateKeySpec(dSAPrivateKey.getX(), dSAPrivateKey.getParams().getP(), dSAPrivateKey.getParams().getQ(), dSAPrivateKey.getParams().getG());
    } 
    return super.engineGetKeySpec(paramKey, paramClass);
  }
  
  protected Key engineTranslateKey(Key paramKey) throws InvalidKeyException {
    if (paramKey instanceof DSAPublicKey)
      return new BCDSAPublicKey((DSAPublicKey)paramKey); 
    if (paramKey instanceof DSAPrivateKey)
      return new BCDSAPrivateKey((DSAPrivateKey)paramKey); 
    throw new InvalidKeyException("key type unknown");
  }
  
  public PrivateKey generatePrivate(PrivateKeyInfo paramPrivateKeyInfo) throws IOException {
    ASN1ObjectIdentifier aSN1ObjectIdentifier = paramPrivateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm();
    if (DSAUtil.isDsaOid(aSN1ObjectIdentifier))
      return new BCDSAPrivateKey(paramPrivateKeyInfo); 
    throw new IOException("algorithm identifier " + aSN1ObjectIdentifier + " in key not recognised");
  }
  
  public PublicKey generatePublic(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) throws IOException {
    ASN1ObjectIdentifier aSN1ObjectIdentifier = paramSubjectPublicKeyInfo.getAlgorithm().getAlgorithm();
    if (DSAUtil.isDsaOid(aSN1ObjectIdentifier))
      return new BCDSAPublicKey(paramSubjectPublicKeyInfo); 
    throw new IOException("algorithm identifier " + aSN1ObjectIdentifier + " in key not recognised");
  }
  
  protected PrivateKey engineGeneratePrivate(KeySpec paramKeySpec) throws InvalidKeySpecException {
    return (paramKeySpec instanceof DSAPrivateKeySpec) ? new BCDSAPrivateKey((DSAPrivateKeySpec)paramKeySpec) : super.engineGeneratePrivate(paramKeySpec);
  }
  
  protected PublicKey engineGeneratePublic(KeySpec paramKeySpec) throws InvalidKeySpecException {
    if (paramKeySpec instanceof DSAPublicKeySpec)
      try {
        return new BCDSAPublicKey((DSAPublicKeySpec)paramKeySpec);
      } catch (Exception exception) {
        throw new InvalidKeySpecException("invalid KeySpec: " + exception.getMessage()) {
            public Throwable getCause() {
              return e;
            }
          };
      }  
    return super.engineGeneratePublic(paramKeySpec);
  }
}
