package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;
import org.bouncycastle.jcajce.provider.asymmetric.util.ExtendedInvalidKeySpecException;

public class KeyFactorySpi extends BaseKeyFactorySpi {
  protected KeySpec engineGetKeySpec(Key paramKey, Class paramClass) throws InvalidKeySpecException {
    if (paramClass.isAssignableFrom(RSAPublicKeySpec.class) && paramKey instanceof RSAPublicKey) {
      RSAPublicKey rSAPublicKey = (RSAPublicKey)paramKey;
      return new RSAPublicKeySpec(rSAPublicKey.getModulus(), rSAPublicKey.getPublicExponent());
    } 
    if (paramClass.isAssignableFrom(RSAPrivateKeySpec.class) && paramKey instanceof RSAPrivateKey) {
      RSAPrivateKey rSAPrivateKey = (RSAPrivateKey)paramKey;
      return new RSAPrivateKeySpec(rSAPrivateKey.getModulus(), rSAPrivateKey.getPrivateExponent());
    } 
    if (paramClass.isAssignableFrom(RSAPrivateCrtKeySpec.class) && paramKey instanceof RSAPrivateCrtKey) {
      RSAPrivateCrtKey rSAPrivateCrtKey = (RSAPrivateCrtKey)paramKey;
      return new RSAPrivateCrtKeySpec(rSAPrivateCrtKey.getModulus(), rSAPrivateCrtKey.getPublicExponent(), rSAPrivateCrtKey.getPrivateExponent(), rSAPrivateCrtKey.getPrimeP(), rSAPrivateCrtKey.getPrimeQ(), rSAPrivateCrtKey.getPrimeExponentP(), rSAPrivateCrtKey.getPrimeExponentQ(), rSAPrivateCrtKey.getCrtCoefficient());
    } 
    return super.engineGetKeySpec(paramKey, paramClass);
  }
  
  protected Key engineTranslateKey(Key paramKey) throws InvalidKeyException {
    if (paramKey instanceof RSAPublicKey)
      return new BCRSAPublicKey((RSAPublicKey)paramKey); 
    if (paramKey instanceof RSAPrivateCrtKey)
      return new BCRSAPrivateCrtKey((RSAPrivateCrtKey)paramKey); 
    if (paramKey instanceof RSAPrivateKey)
      return new BCRSAPrivateKey((RSAPrivateKey)paramKey); 
    throw new InvalidKeyException("key type unknown");
  }
  
  protected PrivateKey engineGeneratePrivate(KeySpec paramKeySpec) throws InvalidKeySpecException {
    if (paramKeySpec instanceof PKCS8EncodedKeySpec)
      try {
        return generatePrivate(PrivateKeyInfo.getInstance(((PKCS8EncodedKeySpec)paramKeySpec).getEncoded()));
      } catch (Exception exception) {
        try {
          return new BCRSAPrivateCrtKey(RSAPrivateKey.getInstance(((PKCS8EncodedKeySpec)paramKeySpec).getEncoded()));
        } catch (Exception exception1) {
          throw new ExtendedInvalidKeySpecException("unable to process key spec: " + exception.toString(), exception);
        } 
      }  
    if (paramKeySpec instanceof RSAPrivateCrtKeySpec)
      return new BCRSAPrivateCrtKey((RSAPrivateCrtKeySpec)paramKeySpec); 
    if (paramKeySpec instanceof RSAPrivateKeySpec)
      return new BCRSAPrivateKey((RSAPrivateKeySpec)paramKeySpec); 
    throw new InvalidKeySpecException("Unknown KeySpec type: " + paramKeySpec.getClass().getName());
  }
  
  protected PublicKey engineGeneratePublic(KeySpec paramKeySpec) throws InvalidKeySpecException {
    return (paramKeySpec instanceof RSAPublicKeySpec) ? new BCRSAPublicKey((RSAPublicKeySpec)paramKeySpec) : super.engineGeneratePublic(paramKeySpec);
  }
  
  public PrivateKey generatePrivate(PrivateKeyInfo paramPrivateKeyInfo) throws IOException {
    ASN1ObjectIdentifier aSN1ObjectIdentifier = paramPrivateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm();
    if (RSAUtil.isRsaOid(aSN1ObjectIdentifier)) {
      RSAPrivateKey rSAPrivateKey = RSAPrivateKey.getInstance(paramPrivateKeyInfo.parsePrivateKey());
      return (rSAPrivateKey.getCoefficient().intValue() == 0) ? new BCRSAPrivateKey(rSAPrivateKey) : new BCRSAPrivateCrtKey(paramPrivateKeyInfo);
    } 
    throw new IOException("algorithm identifier " + aSN1ObjectIdentifier + " in key not recognised");
  }
  
  public PublicKey generatePublic(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) throws IOException {
    ASN1ObjectIdentifier aSN1ObjectIdentifier = paramSubjectPublicKeyInfo.getAlgorithm().getAlgorithm();
    if (RSAUtil.isRsaOid(aSN1ObjectIdentifier))
      return new BCRSAPublicKey(paramSubjectPublicKeyInfo); 
    throw new IOException("algorithm identifier " + aSN1ObjectIdentifier + " in key not recognised");
  }
}
