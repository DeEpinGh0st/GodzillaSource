package org.bouncycastle.asn1.esf;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;

public class CrlIdentifier extends ASN1Object {
  private X500Name crlIssuer;
  
  private ASN1UTCTime crlIssuedTime;
  
  private ASN1Integer crlNumber;
  
  public static CrlIdentifier getInstance(Object paramObject) {
    return (paramObject instanceof CrlIdentifier) ? (CrlIdentifier)paramObject : ((paramObject != null) ? new CrlIdentifier(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private CrlIdentifier(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() < 2 || paramASN1Sequence.size() > 3)
      throw new IllegalArgumentException(); 
    this.crlIssuer = X500Name.getInstance(paramASN1Sequence.getObjectAt(0));
    this.crlIssuedTime = ASN1UTCTime.getInstance(paramASN1Sequence.getObjectAt(1));
    if (paramASN1Sequence.size() > 2)
      this.crlNumber = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(2)); 
  }
  
  public CrlIdentifier(X500Name paramX500Name, ASN1UTCTime paramASN1UTCTime) {
    this(paramX500Name, paramASN1UTCTime, null);
  }
  
  public CrlIdentifier(X500Name paramX500Name, ASN1UTCTime paramASN1UTCTime, BigInteger paramBigInteger) {
    this.crlIssuer = paramX500Name;
    this.crlIssuedTime = paramASN1UTCTime;
    if (null != paramBigInteger)
      this.crlNumber = new ASN1Integer(paramBigInteger); 
  }
  
  public X500Name getCrlIssuer() {
    return this.crlIssuer;
  }
  
  public ASN1UTCTime getCrlIssuedTime() {
    return this.crlIssuedTime;
  }
  
  public BigInteger getCrlNumber() {
    return (null == this.crlNumber) ? null : this.crlNumber.getValue();
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.crlIssuer.toASN1Primitive());
    aSN1EncodableVector.add((ASN1Encodable)this.crlIssuedTime);
    if (null != this.crlNumber)
      aSN1EncodableVector.add((ASN1Encodable)this.crlNumber); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
