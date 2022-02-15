package org.bouncycastle.asn1.esf;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class OcspListID extends ASN1Object {
  private ASN1Sequence ocspResponses;
  
  public static OcspListID getInstance(Object paramObject) {
    return (paramObject instanceof OcspListID) ? (OcspListID)paramObject : ((paramObject != null) ? new OcspListID(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private OcspListID(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 1)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    this.ocspResponses = (ASN1Sequence)paramASN1Sequence.getObjectAt(0);
    Enumeration enumeration = this.ocspResponses.getObjects();
    while (enumeration.hasMoreElements())
      OcspResponsesID.getInstance(enumeration.nextElement()); 
  }
  
  public OcspListID(OcspResponsesID[] paramArrayOfOcspResponsesID) {
    this.ocspResponses = (ASN1Sequence)new DERSequence((ASN1Encodable[])paramArrayOfOcspResponsesID);
  }
  
  public OcspResponsesID[] getOcspResponses() {
    OcspResponsesID[] arrayOfOcspResponsesID = new OcspResponsesID[this.ocspResponses.size()];
    for (byte b = 0; b < arrayOfOcspResponsesID.length; b++)
      arrayOfOcspResponsesID[b] = OcspResponsesID.getInstance(this.ocspResponses.getObjectAt(b)); 
    return arrayOfOcspResponsesID;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)new DERSequence((ASN1Encodable)this.ocspResponses);
  }
}
