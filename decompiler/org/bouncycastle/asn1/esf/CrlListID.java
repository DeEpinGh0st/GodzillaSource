package org.bouncycastle.asn1.esf;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class CrlListID extends ASN1Object {
  private ASN1Sequence crls;
  
  public static CrlListID getInstance(Object paramObject) {
    return (paramObject instanceof CrlListID) ? (CrlListID)paramObject : ((paramObject != null) ? new CrlListID(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private CrlListID(ASN1Sequence paramASN1Sequence) {
    this.crls = (ASN1Sequence)paramASN1Sequence.getObjectAt(0);
    Enumeration enumeration = this.crls.getObjects();
    while (enumeration.hasMoreElements())
      CrlValidatedID.getInstance(enumeration.nextElement()); 
  }
  
  public CrlListID(CrlValidatedID[] paramArrayOfCrlValidatedID) {
    this.crls = (ASN1Sequence)new DERSequence((ASN1Encodable[])paramArrayOfCrlValidatedID);
  }
  
  public CrlValidatedID[] getCrls() {
    CrlValidatedID[] arrayOfCrlValidatedID = new CrlValidatedID[this.crls.size()];
    for (byte b = 0; b < arrayOfCrlValidatedID.length; b++)
      arrayOfCrlValidatedID[b] = CrlValidatedID.getInstance(this.crls.getObjectAt(b)); 
    return arrayOfCrlValidatedID;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)new DERSequence((ASN1Encodable)this.crls);
  }
}
