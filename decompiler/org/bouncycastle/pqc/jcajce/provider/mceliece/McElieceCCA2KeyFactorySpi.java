package org.bouncycastle.pqc.jcajce.provider.mceliece;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactorySpi;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.bouncycastle.pqc.asn1.McElieceCCA2PrivateKey;
import org.bouncycastle.pqc.asn1.McElieceCCA2PublicKey;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2PrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2PublicKeyParameters;

public class McElieceCCA2KeyFactorySpi extends KeyFactorySpi implements AsymmetricKeyInfoConverter {
  public static final String OID = "1.3.6.1.4.1.8301.3.1.3.4.2";
  
  protected PublicKey engineGeneratePublic(KeySpec paramKeySpec) throws InvalidKeySpecException {
    if (paramKeySpec instanceof X509EncodedKeySpec) {
      SubjectPublicKeyInfo subjectPublicKeyInfo;
      byte[] arrayOfByte = ((X509EncodedKeySpec)paramKeySpec).getEncoded();
      try {
        subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(arrayOfByte));
      } catch (IOException iOException) {
        throw new InvalidKeySpecException(iOException.toString());
      } 
      try {
        if (PQCObjectIdentifiers.mcElieceCca2.equals(subjectPublicKeyInfo.getAlgorithm().getAlgorithm())) {
          McElieceCCA2PublicKey mcElieceCCA2PublicKey = McElieceCCA2PublicKey.getInstance(subjectPublicKeyInfo.parsePublicKey());
          return new BCMcElieceCCA2PublicKey(new McElieceCCA2PublicKeyParameters(mcElieceCCA2PublicKey.getN(), mcElieceCCA2PublicKey.getT(), mcElieceCCA2PublicKey.getG(), Utils.getDigest(mcElieceCCA2PublicKey.getDigest()).getAlgorithmName()));
        } 
        throw new InvalidKeySpecException("Unable to recognise OID in McEliece private key");
      } catch (IOException iOException) {
        throw new InvalidKeySpecException("Unable to decode X509EncodedKeySpec: " + iOException.getMessage());
      } 
    } 
    throw new InvalidKeySpecException("Unsupported key specification: " + paramKeySpec.getClass() + ".");
  }
  
  protected PrivateKey engineGeneratePrivate(KeySpec paramKeySpec) throws InvalidKeySpecException {
    if (paramKeySpec instanceof PKCS8EncodedKeySpec) {
      PrivateKeyInfo privateKeyInfo;
      byte[] arrayOfByte = ((PKCS8EncodedKeySpec)paramKeySpec).getEncoded();
      try {
        privateKeyInfo = PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(arrayOfByte));
      } catch (IOException iOException) {
        throw new InvalidKeySpecException("Unable to decode PKCS8EncodedKeySpec: " + iOException);
      } 
      try {
        if (PQCObjectIdentifiers.mcElieceCca2.equals(privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm())) {
          McElieceCCA2PrivateKey mcElieceCCA2PrivateKey = McElieceCCA2PrivateKey.getInstance(privateKeyInfo.parsePrivateKey());
          return new BCMcElieceCCA2PrivateKey(new McElieceCCA2PrivateKeyParameters(mcElieceCCA2PrivateKey.getN(), mcElieceCCA2PrivateKey.getK(), mcElieceCCA2PrivateKey.getField(), mcElieceCCA2PrivateKey.getGoppaPoly(), mcElieceCCA2PrivateKey.getP(), Utils.getDigest(mcElieceCCA2PrivateKey.getDigest()).getAlgorithmName()));
        } 
        throw new InvalidKeySpecException("Unable to recognise OID in McEliece public key");
      } catch (IOException iOException) {
        throw new InvalidKeySpecException("Unable to decode PKCS8EncodedKeySpec.");
      } 
    } 
    throw new InvalidKeySpecException("Unsupported key specification: " + paramKeySpec.getClass() + ".");
  }
  
  public KeySpec getKeySpec(Key paramKey, Class<?> paramClass) throws InvalidKeySpecException {
    if (paramKey instanceof BCMcElieceCCA2PrivateKey) {
      if (PKCS8EncodedKeySpec.class.isAssignableFrom(paramClass))
        return new PKCS8EncodedKeySpec(paramKey.getEncoded()); 
    } else if (paramKey instanceof BCMcElieceCCA2PublicKey) {
      if (X509EncodedKeySpec.class.isAssignableFrom(paramClass))
        return new X509EncodedKeySpec(paramKey.getEncoded()); 
    } else {
      throw new InvalidKeySpecException("Unsupported key type: " + paramKey.getClass() + ".");
    } 
    throw new InvalidKeySpecException("Unknown key specification: " + paramClass + ".");
  }
  
  public Key translateKey(Key paramKey) throws InvalidKeyException {
    if (paramKey instanceof BCMcElieceCCA2PrivateKey || paramKey instanceof BCMcElieceCCA2PublicKey)
      return paramKey; 
    throw new InvalidKeyException("Unsupported key type.");
  }
  
  public PublicKey generatePublic(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) throws IOException {
    ASN1Primitive aSN1Primitive = paramSubjectPublicKeyInfo.parsePublicKey();
    McElieceCCA2PublicKey mcElieceCCA2PublicKey = McElieceCCA2PublicKey.getInstance(aSN1Primitive);
    return new BCMcElieceCCA2PublicKey(new McElieceCCA2PublicKeyParameters(mcElieceCCA2PublicKey.getN(), mcElieceCCA2PublicKey.getT(), mcElieceCCA2PublicKey.getG(), Utils.getDigest(mcElieceCCA2PublicKey.getDigest()).getAlgorithmName()));
  }
  
  public PrivateKey generatePrivate(PrivateKeyInfo paramPrivateKeyInfo) throws IOException {
    ASN1Primitive aSN1Primitive = paramPrivateKeyInfo.parsePrivateKey().toASN1Primitive();
    McElieceCCA2PrivateKey mcElieceCCA2PrivateKey = McElieceCCA2PrivateKey.getInstance(aSN1Primitive);
    return new BCMcElieceCCA2PrivateKey(new McElieceCCA2PrivateKeyParameters(mcElieceCCA2PrivateKey.getN(), mcElieceCCA2PrivateKey.getK(), mcElieceCCA2PrivateKey.getField(), mcElieceCCA2PrivateKey.getGoppaPoly(), mcElieceCCA2PrivateKey.getP(), null));
  }
  
  protected KeySpec engineGetKeySpec(Key paramKey, Class paramClass) throws InvalidKeySpecException {
    return null;
  }
  
  protected Key engineTranslateKey(Key paramKey) throws InvalidKeyException {
    return null;
  }
}
