package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class ExtendedFailInfo extends ASN1Object {
  private final ASN1ObjectIdentifier failInfoOID;
  
  private final ASN1Encodable failInfoValue;
  
  public ExtendedFailInfo(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Encodable paramASN1Encodable) {
    this.failInfoOID = paramASN1ObjectIdentifier;
    this.failInfoValue = paramASN1Encodable;
  }
  
  private ExtendedFailInfo(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 2)
      throw new IllegalArgumentException("Sequence must be 2 elements."); 
    this.failInfoOID = ASN1ObjectIdentifier.getInstance(paramASN1Sequence.getObjectAt(0));
    this.failInfoValue = paramASN1Sequence.getObjectAt(1);
  }
  
  public static ExtendedFailInfo getInstance(Object paramObject) {
    if (paramObject instanceof ExtendedFailInfo)
      return (ExtendedFailInfo)paramObject; 
    if (paramObject instanceof ASN1Encodable) {
      ASN1Primitive aSN1Primitive = ((ASN1Encodable)paramObject).toASN1Primitive();
      if (aSN1Primitive instanceof ASN1Sequence)
        return new ExtendedFailInfo((ASN1Sequence)aSN1Primitive); 
    } else if (paramObject instanceof byte[]) {
      return getInstance(ASN1Sequence.getInstance(paramObject));
    } 
    return null;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)new DERSequence(new ASN1Encodable[] { (ASN1Encodable)this.failInfoOID, this.failInfoValue });
  }
  
  public ASN1ObjectIdentifier getFailInfoOID() {
    return this.failInfoOID;
  }
  
  public ASN1Encodable getFailInfoValue() {
    return this.failInfoValue;
  }
}
