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
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.bouncycastle.pqc.asn1.McEliecePrivateKey;
import org.bouncycastle.pqc.asn1.McEliecePublicKey;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePublicKeyParameters;

public class McElieceKeyFactorySpi extends KeyFactorySpi implements AsymmetricKeyInfoConverter {
  public static final String OID = "1.3.6.1.4.1.8301.3.1.3.4.1";
  
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
        if (PQCObjectIdentifiers.mcEliece.equals(subjectPublicKeyInfo.getAlgorithm().getAlgorithm())) {
          McEliecePublicKey mcEliecePublicKey = McEliecePublicKey.getInstance(subjectPublicKeyInfo.parsePublicKey());
          return new BCMcEliecePublicKey(new McEliecePublicKeyParameters(mcEliecePublicKey.getN(), mcEliecePublicKey.getT(), mcEliecePublicKey.getG()));
        } 
        throw new InvalidKeySpecException("Unable to recognise OID in McEliece public key");
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
        if (PQCObjectIdentifiers.mcEliece.equals(privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm())) {
          McEliecePrivateKey mcEliecePrivateKey = McEliecePrivateKey.getInstance(privateKeyInfo.parsePrivateKey());
          return new BCMcEliecePrivateKey(new McEliecePrivateKeyParameters(mcEliecePrivateKey.getN(), mcEliecePrivateKey.getK(), mcEliecePrivateKey.getField(), mcEliecePrivateKey.getGoppaPoly(), mcEliecePrivateKey.getP1(), mcEliecePrivateKey.getP2(), mcEliecePrivateKey.getSInv()));
        } 
        throw new InvalidKeySpecException("Unable to recognise OID in McEliece private key");
      } catch (IOException iOException) {
        throw new InvalidKeySpecException("Unable to decode PKCS8EncodedKeySpec.");
      } 
    } 
    throw new InvalidKeySpecException("Unsupported key specification: " + paramKeySpec.getClass() + ".");
  }
  
  public KeySpec getKeySpec(Key paramKey, Class<?> paramClass) throws InvalidKeySpecException {
    if (paramKey instanceof BCMcEliecePrivateKey) {
      if (PKCS8EncodedKeySpec.class.isAssignableFrom(paramClass))
        return new PKCS8EncodedKeySpec(paramKey.getEncoded()); 
    } else if (paramKey instanceof BCMcEliecePublicKey) {
      if (X509EncodedKeySpec.class.isAssignableFrom(paramClass))
        return new X509EncodedKeySpec(paramKey.getEncoded()); 
    } else {
      throw new InvalidKeySpecException("Unsupported key type: " + paramKey.getClass() + ".");
    } 
    throw new InvalidKeySpecException("Unknown key specification: " + paramClass + ".");
  }
  
  public Key translateKey(Key paramKey) throws InvalidKeyException {
    if (paramKey instanceof BCMcEliecePrivateKey || paramKey instanceof BCMcEliecePublicKey)
      return paramKey; 
    throw new InvalidKeyException("Unsupported key type.");
  }
  
  public PublicKey generatePublic(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) throws IOException {
    ASN1Primitive aSN1Primitive = paramSubjectPublicKeyInfo.parsePublicKey();
    McEliecePublicKey mcEliecePublicKey = McEliecePublicKey.getInstance(aSN1Primitive);
    return new BCMcEliecePublicKey(new McEliecePublicKeyParameters(mcEliecePublicKey.getN(), mcEliecePublicKey.getT(), mcEliecePublicKey.getG()));
  }
  
  public PrivateKey generatePrivate(PrivateKeyInfo paramPrivateKeyInfo) throws IOException {
    ASN1Primitive aSN1Primitive = paramPrivateKeyInfo.parsePrivateKey().toASN1Primitive();
    McEliecePrivateKey mcEliecePrivateKey = McEliecePrivateKey.getInstance(aSN1Primitive);
    return new BCMcEliecePrivateKey(new McEliecePrivateKeyParameters(mcEliecePrivateKey.getN(), mcEliecePrivateKey.getK(), mcEliecePrivateKey.getField(), mcEliecePrivateKey.getGoppaPoly(), mcEliecePrivateKey.getP1(), mcEliecePrivateKey.getP2(), mcEliecePrivateKey.getSInv()));
  }
  
  protected KeySpec engineGetKeySpec(Key paramKey, Class paramClass) throws InvalidKeySpecException {
    return null;
  }
  
  protected Key engineTranslateKey(Key paramKey) throws InvalidKeyException {
    return null;
  }
  
  private static Digest getDigest(AlgorithmIdentifier paramAlgorithmIdentifier) {
    return (Digest)new SHA256Digest();
  }
}
