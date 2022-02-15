package org.bouncycastle.asn1.esf;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class CompleteRevocationRefs extends ASN1Object {
  private ASN1Sequence crlOcspRefs;
  
  public static CompleteRevocationRefs getInstance(Object paramObject) {
    return (paramObject instanceof CompleteRevocationRefs) ? (CompleteRevocationRefs)paramObject : ((paramObject != null) ? new CompleteRevocationRefs(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private CompleteRevocationRefs(ASN1Sequence paramASN1Sequence) {
    Enumeration enumeration = paramASN1Sequence.getObjects();
    while (enumeration.hasMoreElements())
      CrlOcspRef.getInstance(enumeration.nextElement()); 
    this.crlOcspRefs = paramASN1Sequence;
  }
  
  public CompleteRevocationRefs(CrlOcspRef[] paramArrayOfCrlOcspRef) {
    this.crlOcspRefs = (ASN1Sequence)new DERSequence((ASN1Encodable[])paramArrayOfCrlOcspRef);
  }
  
  public CrlOcspRef[] getCrlOcspRefs() {
    CrlOcspRef[] arrayOfCrlOcspRef = new CrlOcspRef[this.crlOcspRefs.size()];
    for (byte b = 0; b < arrayOfCrlOcspRef.length; b++)
      arrayOfCrlOcspRef[b] = CrlOcspRef.getInstance(this.crlOcspRefs.getObjectAt(b)); 
    return arrayOfCrlOcspRef;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.crlOcspRefs;
  }
}
