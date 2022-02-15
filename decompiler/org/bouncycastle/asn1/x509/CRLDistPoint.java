package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Strings;

public class CRLDistPoint extends ASN1Object {
  ASN1Sequence seq = null;
  
  public static CRLDistPoint getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static CRLDistPoint getInstance(Object paramObject) {
    return (paramObject instanceof CRLDistPoint) ? (CRLDistPoint)paramObject : ((paramObject != null) ? new CRLDistPoint(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private CRLDistPoint(ASN1Sequence paramASN1Sequence) {
    this.seq = paramASN1Sequence;
  }
  
  public CRLDistPoint(DistributionPoint[] paramArrayOfDistributionPoint) {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    for (byte b = 0; b != paramArrayOfDistributionPoint.length; b++)
      aSN1EncodableVector.add((ASN1Encodable)paramArrayOfDistributionPoint[b]); 
    this.seq = (ASN1Sequence)new DERSequence(aSN1EncodableVector);
  }
  
  public DistributionPoint[] getDistributionPoints() {
    DistributionPoint[] arrayOfDistributionPoint = new DistributionPoint[this.seq.size()];
    for (byte b = 0; b != this.seq.size(); b++)
      arrayOfDistributionPoint[b] = DistributionPoint.getInstance(this.seq.getObjectAt(b)); 
    return arrayOfDistributionPoint;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.seq;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    String str = Strings.lineSeparator();
    stringBuffer.append("CRLDistPoint:");
    stringBuffer.append(str);
    DistributionPoint[] arrayOfDistributionPoint = getDistributionPoints();
    for (byte b = 0; b != arrayOfDistributionPoint.length; b++) {
      stringBuffer.append("    ");
      stringBuffer.append(arrayOfDistributionPoint[b]);
      stringBuffer.append(str);
    } 
    return stringBuffer.toString();
  }
}
