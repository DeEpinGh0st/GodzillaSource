package org.bouncycastle.jcajce.util;

import java.io.IOException;
import java.security.AlgorithmParameters;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;

public class JcaJceUtils {
  public static ASN1Encodable extractParameters(AlgorithmParameters paramAlgorithmParameters) throws IOException {
    ASN1Primitive aSN1Primitive;
    try {
      aSN1Primitive = ASN1Primitive.fromByteArray(paramAlgorithmParameters.getEncoded("ASN.1"));
    } catch (Exception exception) {
      aSN1Primitive = ASN1Primitive.fromByteArray(paramAlgorithmParameters.getEncoded());
    } 
    return (ASN1Encodable)aSN1Primitive;
  }
  
  public static void loadParameters(AlgorithmParameters paramAlgorithmParameters, ASN1Encodable paramASN1Encodable) throws IOException {
    try {
      paramAlgorithmParameters.init(paramASN1Encodable.toASN1Primitive().getEncoded(), "ASN.1");
    } catch (Exception exception) {
      paramAlgorithmParameters.init(paramASN1Encodable.toASN1Primitive().getEncoded());
    } 
  }
  
  public static String getDigestAlgName(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return PKCSObjectIdentifiers.md5.equals(paramASN1ObjectIdentifier) ? "MD5" : (OIWObjectIdentifiers.idSHA1.equals(paramASN1ObjectIdentifier) ? "SHA1" : (NISTObjectIdentifiers.id_sha224.equals(paramASN1ObjectIdentifier) ? "SHA224" : (NISTObjectIdentifiers.id_sha256.equals(paramASN1ObjectIdentifier) ? "SHA256" : (NISTObjectIdentifiers.id_sha384.equals(paramASN1ObjectIdentifier) ? "SHA384" : (NISTObjectIdentifiers.id_sha512.equals(paramASN1ObjectIdentifier) ? "SHA512" : (TeleTrusTObjectIdentifiers.ripemd128.equals(paramASN1ObjectIdentifier) ? "RIPEMD128" : (TeleTrusTObjectIdentifiers.ripemd160.equals(paramASN1ObjectIdentifier) ? "RIPEMD160" : (TeleTrusTObjectIdentifiers.ripemd256.equals(paramASN1ObjectIdentifier) ? "RIPEMD256" : (CryptoProObjectIdentifiers.gostR3411.equals(paramASN1ObjectIdentifier) ? "GOST3411" : paramASN1ObjectIdentifier.getId())))))))));
  }
}
