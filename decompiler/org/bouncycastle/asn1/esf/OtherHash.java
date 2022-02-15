package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class OtherHash extends ASN1Object implements ASN1Choice {
  private ASN1OctetString sha1Hash;
  
  private OtherHashAlgAndValue otherHash;
  
  public static OtherHash getInstance(Object paramObject) {
    return (paramObject instanceof OtherHash) ? (OtherHash)paramObject : ((paramObject instanceof ASN1OctetString) ? new OtherHash((ASN1OctetString)paramObject) : new OtherHash(OtherHashAlgAndValue.getInstance(paramObject)));
  }
  
  private OtherHash(ASN1OctetString paramASN1OctetString) {
    this.sha1Hash = paramASN1OctetString;
  }
  
  public OtherHash(OtherHashAlgAndValue paramOtherHashAlgAndValue) {
    this.otherHash = paramOtherHashAlgAndValue;
  }
  
  public OtherHash(byte[] paramArrayOfbyte) {
    this.sha1Hash = (ASN1OctetString)new DEROctetString(paramArrayOfbyte);
  }
  
  public AlgorithmIdentifier getHashAlgorithm() {
    return (null == this.otherHash) ? new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1) : this.otherHash.getHashAlgorithm();
  }
  
  public byte[] getHashValue() {
    return (null == this.otherHash) ? this.sha1Hash.getOctets() : this.otherHash.getHashValue().getOctets();
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)((null == this.otherHash) ? this.sha1Hash : this.otherHash.toASN1Primitive());
  }
}
