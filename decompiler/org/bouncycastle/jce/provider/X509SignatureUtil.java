package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PSSParameterSpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;

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
        return getDigestAlgName(ASN1ObjectIdentifier.getInstance(aSN1Sequence.getObjectAt(0))) + "withECDSA";
      } 
    } 
    return paramAlgorithmIdentifier.getAlgorithm().getId();
  }
  
  private static String getDigestAlgName(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return PKCSObjectIdentifiers.md5.equals(paramASN1ObjectIdentifier) ? "MD5" : (OIWObjectIdentifiers.idSHA1.equals(paramASN1ObjectIdentifier) ? "SHA1" : (NISTObjectIdentifiers.id_sha224.equals(paramASN1ObjectIdentifier) ? "SHA224" : (NISTObjectIdentifiers.id_sha256.equals(paramASN1ObjectIdentifier) ? "SHA256" : (NISTObjectIdentifiers.id_sha384.equals(paramASN1ObjectIdentifier) ? "SHA384" : (NISTObjectIdentifiers.id_sha512.equals(paramASN1ObjectIdentifier) ? "SHA512" : (TeleTrusTObjectIdentifiers.ripemd128.equals(paramASN1ObjectIdentifier) ? "RIPEMD128" : (TeleTrusTObjectIdentifiers.ripemd160.equals(paramASN1ObjectIdentifier) ? "RIPEMD160" : (TeleTrusTObjectIdentifiers.ripemd256.equals(paramASN1ObjectIdentifier) ? "RIPEMD256" : (CryptoProObjectIdentifiers.gostR3411.equals(paramASN1ObjectIdentifier) ? "GOST3411" : paramASN1ObjectIdentifier.getId())))))))));
  }
}
