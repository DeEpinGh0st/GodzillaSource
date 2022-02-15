package org.bouncycastle.jcajce.provider.asymmetric.dh;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHPrivateKeySpec;
import javax.crypto.spec.DHPublicKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;
import org.bouncycastle.jcajce.provider.asymmetric.util.ExtendedInvalidKeySpecException;

public class KeyFactorySpi extends BaseKeyFactorySpi {
  protected KeySpec engineGetKeySpec(Key paramKey, Class paramClass) throws InvalidKeySpecException {
    if (paramClass.isAssignableFrom(DHPrivateKeySpec.class) && paramKey instanceof DHPrivateKey) {
      DHPrivateKey dHPrivateKey = (DHPrivateKey)paramKey;
      return new DHPrivateKeySpec(dHPrivateKey.getX(), dHPrivateKey.getParams().getP(), dHPrivateKey.getParams().getG());
    } 
    if (paramClass.isAssignableFrom(DHPublicKeySpec.class) && paramKey instanceof DHPublicKey) {
      DHPublicKey dHPublicKey = (DHPublicKey)paramKey;
      return new DHPublicKeySpec(dHPublicKey.getY(), dHPublicKey.getParams().getP(), dHPublicKey.getParams().getG());
    } 
    return super.engineGetKeySpec(paramKey, paramClass);
  }
  
  protected Key engineTranslateKey(Key paramKey) throws InvalidKeyException {
    if (paramKey instanceof DHPublicKey)
      return new BCDHPublicKey((DHPublicKey)paramKey); 
    if (paramKey instanceof DHPrivateKey)
      return new BCDHPrivateKey((DHPrivateKey)paramKey); 
    throw new InvalidKeyException("key type unknown");
  }
  
  protected PrivateKey engineGeneratePrivate(KeySpec paramKeySpec) throws InvalidKeySpecException {
    return (paramKeySpec instanceof DHPrivateKeySpec) ? new BCDHPrivateKey((DHPrivateKeySpec)paramKeySpec) : super.engineGeneratePrivate(paramKeySpec);
  }
  
  protected PublicKey engineGeneratePublic(KeySpec paramKeySpec) throws InvalidKeySpecException {
    if (paramKeySpec instanceof DHPublicKeySpec)
      try {
        return new BCDHPublicKey((DHPublicKeySpec)paramKeySpec);
      } catch (IllegalArgumentException illegalArgumentException) {
        throw new ExtendedInvalidKeySpecException(illegalArgumentException.getMessage(), illegalArgumentException);
      }  
    return super.engineGeneratePublic(paramKeySpec);
  }
  
  public PrivateKey generatePrivate(PrivateKeyInfo paramPrivateKeyInfo) throws IOException {
    ASN1ObjectIdentifier aSN1ObjectIdentifier = paramPrivateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm();
    if (aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.dhKeyAgreement))
      return new BCDHPrivateKey(paramPrivateKeyInfo); 
    if (aSN1ObjectIdentifier.equals(X9ObjectIdentifiers.dhpublicnumber))
      return new BCDHPrivateKey(paramPrivateKeyInfo); 
    throw new IOException("algorithm identifier " + aSN1ObjectIdentifier + " in key not recognised");
  }
  
  public PublicKey generatePublic(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) throws IOException {
    ASN1ObjectIdentifier aSN1ObjectIdentifier = paramSubjectPublicKeyInfo.getAlgorithm().getAlgorithm();
    if (aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.dhKeyAgreement))
      return new BCDHPublicKey(paramSubjectPublicKeyInfo); 
    if (aSN1ObjectIdentifier.equals(X9ObjectIdentifiers.dhpublicnumber))
      return new BCDHPublicKey(paramSubjectPublicKeyInfo); 
    throw new IOException("algorithm identifier " + aSN1ObjectIdentifier + " in key not recognised");
  }
}
