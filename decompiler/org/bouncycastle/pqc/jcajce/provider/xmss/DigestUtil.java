package org.bouncycastle.pqc.jcajce.provider.xmss;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;

class DigestUtil {
  static Digest getDigest(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    if (paramASN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_sha256))
      return (Digest)new SHA256Digest(); 
    if (paramASN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_sha512))
      return (Digest)new SHA512Digest(); 
    if (paramASN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_shake128))
      return (Digest)new SHAKEDigest(128); 
    if (paramASN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_shake256))
      return (Digest)new SHAKEDigest(256); 
    throw new IllegalArgumentException("unrecognized digest OID: " + paramASN1ObjectIdentifier);
  }
  
  public static byte[] getDigestResult(Digest paramDigest) {
    byte[] arrayOfByte = new byte[getDigestSize(paramDigest)];
    if (paramDigest instanceof Xof) {
      ((Xof)paramDigest).doFinal(arrayOfByte, 0, arrayOfByte.length);
    } else {
      paramDigest.doFinal(arrayOfByte, 0);
    } 
    return arrayOfByte;
  }
  
  public static int getDigestSize(Digest paramDigest) {
    return (paramDigest instanceof Xof) ? (paramDigest.getDigestSize() * 2) : paramDigest.getDigestSize();
  }
  
  public static String getXMSSDigestName(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    if (paramASN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_sha256))
      return "SHA256"; 
    if (paramASN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_sha512))
      return "SHA512"; 
    if (paramASN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_shake128))
      return "SHAKE128"; 
    if (paramASN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_shake256))
      return "SHAKE256"; 
    throw new IllegalArgumentException("unrecognized digest OID: " + paramASN1ObjectIdentifier);
  }
}
