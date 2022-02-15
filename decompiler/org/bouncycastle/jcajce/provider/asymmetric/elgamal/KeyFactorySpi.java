package org.bouncycastle.jcajce.provider.asymmetric.elgamal;

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
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;
import org.bouncycastle.jce.interfaces.ElGamalPrivateKey;
import org.bouncycastle.jce.interfaces.ElGamalPublicKey;
import org.bouncycastle.jce.spec.ElGamalPrivateKeySpec;
import org.bouncycastle.jce.spec.ElGamalPublicKeySpec;

public class KeyFactorySpi extends BaseKeyFactorySpi {
  protected PrivateKey engineGeneratePrivate(KeySpec paramKeySpec) throws InvalidKeySpecException {
    return (paramKeySpec instanceof ElGamalPrivateKeySpec) ? new BCElGamalPrivateKey((ElGamalPrivateKeySpec)paramKeySpec) : ((paramKeySpec instanceof DHPrivateKeySpec) ? new BCElGamalPrivateKey((DHPrivateKeySpec)paramKeySpec) : super.engineGeneratePrivate(paramKeySpec));
  }
  
  protected PublicKey engineGeneratePublic(KeySpec paramKeySpec) throws InvalidKeySpecException {
    return (paramKeySpec instanceof ElGamalPublicKeySpec) ? new BCElGamalPublicKey((ElGamalPublicKeySpec)paramKeySpec) : ((paramKeySpec instanceof DHPublicKeySpec) ? new BCElGamalPublicKey((DHPublicKeySpec)paramKeySpec) : super.engineGeneratePublic(paramKeySpec));
  }
  
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
      return new BCElGamalPublicKey((DHPublicKey)paramKey); 
    if (paramKey instanceof DHPrivateKey)
      return new BCElGamalPrivateKey((DHPrivateKey)paramKey); 
    if (paramKey instanceof ElGamalPublicKey)
      return new BCElGamalPublicKey((ElGamalPublicKey)paramKey); 
    if (paramKey instanceof ElGamalPrivateKey)
      return new BCElGamalPrivateKey((ElGamalPrivateKey)paramKey); 
    throw new InvalidKeyException("key type unknown");
  }
  
  public PrivateKey generatePrivate(PrivateKeyInfo paramPrivateKeyInfo) throws IOException {
    ASN1ObjectIdentifier aSN1ObjectIdentifier = paramPrivateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm();
    if (aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.dhKeyAgreement))
      return new BCElGamalPrivateKey(paramPrivateKeyInfo); 
    if (aSN1ObjectIdentifier.equals(X9ObjectIdentifiers.dhpublicnumber))
      return new BCElGamalPrivateKey(paramPrivateKeyInfo); 
    if (aSN1ObjectIdentifier.equals(OIWObjectIdentifiers.elGamalAlgorithm))
      return new BCElGamalPrivateKey(paramPrivateKeyInfo); 
    throw new IOException("algorithm identifier " + aSN1ObjectIdentifier + " in key not recognised");
  }
  
  public PublicKey generatePublic(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) throws IOException {
    ASN1ObjectIdentifier aSN1ObjectIdentifier = paramSubjectPublicKeyInfo.getAlgorithm().getAlgorithm();
    if (aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.dhKeyAgreement))
      return new BCElGamalPublicKey(paramSubjectPublicKeyInfo); 
    if (aSN1ObjectIdentifier.equals(X9ObjectIdentifiers.dhpublicnumber))
      return new BCElGamalPublicKey(paramSubjectPublicKeyInfo); 
    if (aSN1ObjectIdentifier.equals(OIWObjectIdentifiers.elGamalAlgorithm))
      return new BCElGamalPublicKey(paramSubjectPublicKeyInfo); 
    throw new IOException("algorithm identifier " + aSN1ObjectIdentifier + " in key not recognised");
  }
}
