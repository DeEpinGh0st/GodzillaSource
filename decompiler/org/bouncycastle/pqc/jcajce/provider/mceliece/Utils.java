package org.bouncycastle.pqc.jcajce.provider.mceliece;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.util.DigestFactory;

class Utils {
  static AlgorithmIdentifier getDigAlgId(String paramString) {
    if (paramString.equals("SHA-1"))
      return new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, (ASN1Encodable)DERNull.INSTANCE); 
    if (paramString.equals("SHA-224"))
      return new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha224, (ASN1Encodable)DERNull.INSTANCE); 
    if (paramString.equals("SHA-256"))
      return new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256, (ASN1Encodable)DERNull.INSTANCE); 
    if (paramString.equals("SHA-384"))
      return new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384, (ASN1Encodable)DERNull.INSTANCE); 
    if (paramString.equals("SHA-512"))
      return new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512, (ASN1Encodable)DERNull.INSTANCE); 
    throw new IllegalArgumentException("unrecognised digest algorithm: " + paramString);
  }
  
  static Digest getDigest(AlgorithmIdentifier paramAlgorithmIdentifier) {
    if (paramAlgorithmIdentifier.getAlgorithm().equals(OIWObjectIdentifiers.idSHA1))
      return DigestFactory.createSHA1(); 
    if (paramAlgorithmIdentifier.getAlgorithm().equals(NISTObjectIdentifiers.id_sha224))
      return DigestFactory.createSHA224(); 
    if (paramAlgorithmIdentifier.getAlgorithm().equals(NISTObjectIdentifiers.id_sha256))
      return DigestFactory.createSHA256(); 
    if (paramAlgorithmIdentifier.getAlgorithm().equals(NISTObjectIdentifiers.id_sha384))
      return DigestFactory.createSHA384(); 
    if (paramAlgorithmIdentifier.getAlgorithm().equals(NISTObjectIdentifiers.id_sha512))
      return DigestFactory.createSHA512(); 
    throw new IllegalArgumentException("unrecognised OID in digest algorithm identifier: " + paramAlgorithmIdentifier.getAlgorithm());
  }
}
