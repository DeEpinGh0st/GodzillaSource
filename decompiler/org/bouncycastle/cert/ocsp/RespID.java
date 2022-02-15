package org.bouncycastle.cert.ocsp;

import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ocsp.ResponderID;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.DigestCalculator;

public class RespID {
  public static final AlgorithmIdentifier HASH_SHA1 = new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, (ASN1Encodable)DERNull.INSTANCE);
  
  ResponderID id;
  
  public RespID(ResponderID paramResponderID) {
    this.id = paramResponderID;
  }
  
  public RespID(X500Name paramX500Name) {
    this.id = new ResponderID(paramX500Name);
  }
  
  public RespID(SubjectPublicKeyInfo paramSubjectPublicKeyInfo, DigestCalculator paramDigestCalculator) throws OCSPException {
    try {
      if (!paramDigestCalculator.getAlgorithmIdentifier().equals(HASH_SHA1))
        throw new IllegalArgumentException("only SHA-1 can be used with RespID - found: " + paramDigestCalculator.getAlgorithmIdentifier().getAlgorithm()); 
      OutputStream outputStream = paramDigestCalculator.getOutputStream();
      outputStream.write(paramSubjectPublicKeyInfo.getPublicKeyData().getBytes());
      outputStream.close();
      this.id = new ResponderID((ASN1OctetString)new DEROctetString(paramDigestCalculator.getDigest()));
    } catch (Exception exception) {
      throw new OCSPException("problem creating ID: " + exception, exception);
    } 
  }
  
  public ResponderID toASN1Primitive() {
    return this.id;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof RespID))
      return false; 
    RespID respID = (RespID)paramObject;
    return this.id.equals(respID.id);
  }
  
  public int hashCode() {
    return this.id.hashCode();
  }
}
