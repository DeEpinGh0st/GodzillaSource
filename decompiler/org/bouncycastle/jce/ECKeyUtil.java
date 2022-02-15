package org.bouncycastle.jce;

import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class ECKeyUtil {
  public static PublicKey publicToExplicitParameters(PublicKey paramPublicKey, String paramString) throws IllegalArgumentException, NoSuchAlgorithmException, NoSuchProviderException {
    Provider provider = Security.getProvider(paramString);
    if (provider == null)
      throw new NoSuchProviderException("cannot find provider: " + paramString); 
    return publicToExplicitParameters(paramPublicKey, provider);
  }
  
  public static PublicKey publicToExplicitParameters(PublicKey paramPublicKey, Provider paramProvider) throws IllegalArgumentException, NoSuchAlgorithmException {
    try {
      X9ECParameters x9ECParameters;
      SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(paramPublicKey.getEncoded()));
      if (subjectPublicKeyInfo.getAlgorithmId().getAlgorithm().equals(CryptoProObjectIdentifiers.gostR3410_2001))
        throw new IllegalArgumentException("cannot convert GOST key to explicit parameters."); 
      X962Parameters x962Parameters = X962Parameters.getInstance(subjectPublicKeyInfo.getAlgorithmId().getParameters());
      if (x962Parameters.isNamedCurve()) {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = ASN1ObjectIdentifier.getInstance(x962Parameters.getParameters());
        x9ECParameters = ECUtil.getNamedCurveByOid(aSN1ObjectIdentifier);
        x9ECParameters = new X9ECParameters(x9ECParameters.getCurve(), x9ECParameters.getG(), x9ECParameters.getN(), x9ECParameters.getH());
      } else if (x962Parameters.isImplicitlyCA()) {
        x9ECParameters = new X9ECParameters(BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getCurve(), BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getG(), BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getN(), BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getH());
      } else {
        return paramPublicKey;
      } 
      x962Parameters = new X962Parameters(x9ECParameters);
      subjectPublicKeyInfo = new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, (ASN1Encodable)x962Parameters), subjectPublicKeyInfo.getPublicKeyData().getBytes());
      KeyFactory keyFactory = KeyFactory.getInstance(paramPublicKey.getAlgorithm(), paramProvider);
      return keyFactory.generatePublic(new X509EncodedKeySpec(subjectPublicKeyInfo.getEncoded()));
    } catch (IllegalArgumentException illegalArgumentException) {
      throw illegalArgumentException;
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw noSuchAlgorithmException;
    } catch (Exception exception) {
      throw new UnexpectedException(exception);
    } 
  }
  
  public static PrivateKey privateToExplicitParameters(PrivateKey paramPrivateKey, String paramString) throws IllegalArgumentException, NoSuchAlgorithmException, NoSuchProviderException {
    Provider provider = Security.getProvider(paramString);
    if (provider == null)
      throw new NoSuchProviderException("cannot find provider: " + paramString); 
    return privateToExplicitParameters(paramPrivateKey, provider);
  }
  
  public static PrivateKey privateToExplicitParameters(PrivateKey paramPrivateKey, Provider paramProvider) throws IllegalArgumentException, NoSuchAlgorithmException {
    try {
      X9ECParameters x9ECParameters;
      PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(paramPrivateKey.getEncoded()));
      if (privateKeyInfo.getAlgorithmId().getAlgorithm().equals(CryptoProObjectIdentifiers.gostR3410_2001))
        throw new UnsupportedEncodingException("cannot convert GOST key to explicit parameters."); 
      X962Parameters x962Parameters = X962Parameters.getInstance(privateKeyInfo.getAlgorithmId().getParameters());
      if (x962Parameters.isNamedCurve()) {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = ASN1ObjectIdentifier.getInstance(x962Parameters.getParameters());
        x9ECParameters = ECUtil.getNamedCurveByOid(aSN1ObjectIdentifier);
        x9ECParameters = new X9ECParameters(x9ECParameters.getCurve(), x9ECParameters.getG(), x9ECParameters.getN(), x9ECParameters.getH());
      } else if (x962Parameters.isImplicitlyCA()) {
        x9ECParameters = new X9ECParameters(BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getCurve(), BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getG(), BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getN(), BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getH());
      } else {
        return paramPrivateKey;
      } 
      x962Parameters = new X962Parameters(x9ECParameters);
      privateKeyInfo = new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, (ASN1Encodable)x962Parameters), privateKeyInfo.parsePrivateKey());
      KeyFactory keyFactory = KeyFactory.getInstance(paramPrivateKey.getAlgorithm(), paramProvider);
      return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyInfo.getEncoded()));
    } catch (IllegalArgumentException illegalArgumentException) {
      throw illegalArgumentException;
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw noSuchAlgorithmException;
    } catch (Exception exception) {
      throw new UnexpectedException(exception);
    } 
  }
  
  private static class UnexpectedException extends RuntimeException {
    private Throwable cause;
    
    UnexpectedException(Throwable param1Throwable) {
      super(param1Throwable.toString());
      this.cause = param1Throwable;
    }
    
    public Throwable getCause() {
      return this.cause;
    }
  }
}
