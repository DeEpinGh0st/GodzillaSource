package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PSSParameterSpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.jcajce.util.MessageDigestUtils;

class X509SignatureUtil {
  private static final ASN1Null derNull = (ASN1Null)DERNull.INSTANCE;
  
  static void setSignatureParameters(Signature paramSignature, ASN1Encodable paramASN1Encodable) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
    if (paramASN1Encodable != null && !derNull.equals(paramASN1Encodable)) {
      AlgorithmParameters algorithmParameters = AlgorithmParameters.getInstance(paramSignature.getAlgorithm(), paramSignature.getProvider());
      try {
        algorithmParameters.init(paramASN1Encodable.toASN1Primitive().getEncoded());
      } catch (IOException iOException) {
        throw new SignatureException("IOException decoding parameters: " + iOException.getMessage());
      } 
      if (paramSignature.getAlgorithm().endsWith("MGF1"))
        try {
          paramSignature.setParameter(algorithmParameters.getParameterSpec((Class)PSSParameterSpec.class));
        } catch (GeneralSecurityException generalSecurityException) {
          throw new SignatureException("Exception extracting parameters: " + generalSecurityException.getMessage());
        }  
    } 
  }
  
  static String getSignatureName(AlgorithmIdentifier paramAlgorithmIdentifier) {
    ASN1Encodable aSN1Encodable = paramAlgorithmIdentifier.getParameters();
    if (aSN1Encodable != null && !derNull.equals(aSN1Encodable)) {
      if (paramAlgorithmIdentifier.getAlgorithm().equals(PKCSObjectIdentifiers.id_RSASSA_PSS)) {
        RSASSAPSSparams rSASSAPSSparams = RSASSAPSSparams.getInstance(aSN1Encodable);
        return getDigestAlgName(rSASSAPSSparams.getHashAlgorithm().getAlgorithm()) + "withRSAandMGF1";
      } 
      if (paramAlgorithmIdentifier.getAlgorithm().equals(X9ObjectIdentifiers.ecdsa_with_SHA2)) {
        ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(aSN1Encodable);
        return getDigestAlgName((ASN1ObjectIdentifier)aSN1Sequence.getObjectAt(0)) + "withECDSA";
      } 
    } 
    Provider provider = Security.getProvider("BC");
    if (provider != null) {
      String str = provider.getProperty("Alg.Alias.Signature." + paramAlgorithmIdentifier.getAlgorithm().getId());
      if (str != null)
        return str; 
    } 
    Provider[] arrayOfProvider = Security.getProviders();
    for (byte b = 0; b != arrayOfProvider.length; b++) {
      String str = arrayOfProvider[b].getProperty("Alg.Alias.Signature." + paramAlgorithmIdentifier.getAlgorithm().getId());
      if (str != null)
        return str; 
    } 
    return paramAlgorithmIdentifier.getAlgorithm().getId();
  }
  
  private static String getDigestAlgName(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    String str = MessageDigestUtils.getDigestName(paramASN1ObjectIdentifier);
    int i = str.indexOf('-');
    return (i > 0 && !str.startsWith("SHA3")) ? (str.substring(0, i) + str.substring(i + 1)) : MessageDigestUtils.getDigestName(paramASN1ObjectIdentifier);
  }
}
