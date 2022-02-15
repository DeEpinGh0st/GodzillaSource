package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.util.Strings;

public class DistributionPoint extends ASN1Object {
  DistributionPointName distributionPoint;
  
  ReasonFlags reasons;
  
  GeneralNames cRLIssuer;
  
  public static DistributionPoint getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static DistributionPoint getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof DistributionPoint)
      return (DistributionPoint)paramObject; 
    if (paramObject instanceof ASN1Sequence)
      return new DistributionPoint((ASN1Sequence)paramObject); 
    throw new IllegalArgumentException("Invalid DistributionPoint: " + paramObject.getClass().getName());
  }
  
  public DistributionPoint(ASN1Sequence paramASN1Sequence) {
    for (byte b = 0; b != paramASN1Sequence.size(); b++) {
      ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(paramASN1Sequence.getObjectAt(b));
      switch (aSN1TaggedObject.getTagNo()) {
        case 0:
          this.distributionPoint = DistributionPointName.getInstance(aSN1TaggedObject, true);
          break;
        case 1:
          this.reasons = new ReasonFlags(DERBitString.getInstance(aSN1TaggedObject, false));
          break;
        case 2:
          this.cRLIssuer = GeneralNames.getInstance(aSN1TaggedObject, false);
          break;
        default:
          throw new IllegalArgumentException("Unknown tag encountered in structure: " + aSN1TaggedObject.getTagNo());
      } 
    } 
  }
  
  public DistributionPoint(DistributionPointName paramDistributionPointName, ReasonFlags paramReasonFlags, GeneralNames paramGeneralNames) {
    this.distributionPoint = paramDistributionPointName;
    this.reasons = paramReasonFlags;
    this.cRLIssuer = paramGeneralNames;
  }
  
  public DistributionPointName getDistributionPoint() {
    return this.distributionPoint;
  }
  
  public ReasonFlags getReasons() {
    return this.reasons;
  }
  
  public GeneralNames getCRLIssuer() {
    return this.cRLIssuer;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (this.distributionPoint != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(0, (ASN1Encodable)this.distributionPoint)); 
    if (this.reasons != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)this.reasons)); 
    if (this.cRLIssuer != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 2, (ASN1Encodable)this.cRLIssuer)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  public String toString() {
    String str = Strings.lineSeparator();
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("DistributionPoint: [");
    stringBuffer.append(str);
    if (this.distributionPoint != null)
      appendObject(stringBuffer, str, "distributionPoint", this.distributionPoint.toString()); 
    if (this.reasons != null)
      appendObject(stringBuffer, str, "reasons", this.reasons.toString()); 
    if (this.cRLIssuer != null)
      appendObject(stringBuffer, str, "cRLIssuer", this.cRLIssuer.toString()); 
    stringBuffer.append("]");
    stringBuffer.append(str);
    return stringBuffer.toString();
  }
  
  private void appendObject(StringBuffer paramStringBuffer, String paramString1, String paramString2, String paramString3) {
    String str = "    ";
    paramStringBuffer.append(str);
    paramStringBuffer.append(paramString2);
    paramStringBuffer.append(":");
    paramStringBuffer.append(paramString1);
    paramStringBuffer.append(str);
    paramStringBuffer.append(str);
    paramStringBuffer.append(paramString3);
    paramStringBuffer.append(paramString1);
  }
}
