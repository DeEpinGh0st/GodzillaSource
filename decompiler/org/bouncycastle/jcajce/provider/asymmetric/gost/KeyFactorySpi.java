package org.bouncycastle.jcajce.provider.asymmetric.gost;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;
import org.bouncycastle.jce.interfaces.GOST3410PrivateKey;
import org.bouncycastle.jce.interfaces.GOST3410PublicKey;
import org.bouncycastle.jce.spec.GOST3410PrivateKeySpec;
import org.bouncycastle.jce.spec.GOST3410PublicKeyParameterSetSpec;
import org.bouncycastle.jce.spec.GOST3410PublicKeySpec;

public class KeyFactorySpi extends BaseKeyFactorySpi {
  protected KeySpec engineGetKeySpec(Key paramKey, Class paramClass) throws InvalidKeySpecException {
    if (paramClass.isAssignableFrom(GOST3410PublicKeySpec.class) && paramKey instanceof GOST3410PublicKey) {
      GOST3410PublicKey gOST3410PublicKey = (GOST3410PublicKey)paramKey;
      GOST3410PublicKeyParameterSetSpec gOST3410PublicKeyParameterSetSpec = gOST3410PublicKey.getParameters().getPublicKeyParameters();
      return (KeySpec)new GOST3410PublicKeySpec(gOST3410PublicKey.getY(), gOST3410PublicKeyParameterSetSpec.getP(), gOST3410PublicKeyParameterSetSpec.getQ(), gOST3410PublicKeyParameterSetSpec.getA());
    } 
    if (paramClass.isAssignableFrom(GOST3410PrivateKeySpec.class) && paramKey instanceof GOST3410PrivateKey) {
      GOST3410PrivateKey gOST3410PrivateKey = (GOST3410PrivateKey)paramKey;
      GOST3410PublicKeyParameterSetSpec gOST3410PublicKeyParameterSetSpec = gOST3410PrivateKey.getParameters().getPublicKeyParameters();
      return (KeySpec)new GOST3410PrivateKeySpec(gOST3410PrivateKey.getX(), gOST3410PublicKeyParameterSetSpec.getP(), gOST3410PublicKeyParameterSetSpec.getQ(), gOST3410PublicKeyParameterSetSpec.getA());
    } 
    return super.engineGetKeySpec(paramKey, paramClass);
  }
  
  protected Key engineTranslateKey(Key paramKey) throws InvalidKeyException {
    if (paramKey instanceof GOST3410PublicKey)
      return (Key)new BCGOST3410PublicKey((GOST3410PublicKey)paramKey); 
    if (paramKey instanceof GOST3410PrivateKey)
      return (Key)new BCGOST3410PrivateKey((GOST3410PrivateKey)paramKey); 
    throw new InvalidKeyException("key type unknown");
  }
  
  protected PrivateKey engineGeneratePrivate(KeySpec paramKeySpec) throws InvalidKeySpecException {
    return (PrivateKey)((paramKeySpec instanceof GOST3410PrivateKeySpec) ? new BCGOST3410PrivateKey((GOST3410PrivateKeySpec)paramKeySpec) : super.engineGeneratePrivate(paramKeySpec));
  }
  
  protected PublicKey engineGeneratePublic(KeySpec paramKeySpec) throws InvalidKeySpecException {
    return (PublicKey)((paramKeySpec instanceof GOST3410PublicKeySpec) ? new BCGOST3410PublicKey((GOST3410PublicKeySpec)paramKeySpec) : super.engineGeneratePublic(paramKeySpec));
  }
  
  public PrivateKey generatePrivate(PrivateKeyInfo paramPrivateKeyInfo) throws IOException {
    ASN1ObjectIdentifier aSN1ObjectIdentifier = paramPrivateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm();
    if (aSN1ObjectIdentifier.equals(CryptoProObjectIdentifiers.gostR3410_94))
      return (PrivateKey)new BCGOST3410PrivateKey(paramPrivateKeyInfo); 
    throw new IOException("algorithm identifier " + aSN1ObjectIdentifier + " in key not recognised");
  }
  
  public PublicKey generatePublic(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) throws IOException {
    ASN1ObjectIdentifier aSN1ObjectIdentifier = paramSubjectPublicKeyInfo.getAlgorithm().getAlgorithm();
    if (aSN1ObjectIdentifier.equals(CryptoProObjectIdentifiers.gostR3410_94))
      return (PublicKey)new BCGOST3410PublicKey(paramSubjectPublicKeyInfo); 
    throw new IOException("algorithm identifier " + aSN1ObjectIdentifier + " in key not recognised");
  }
}
