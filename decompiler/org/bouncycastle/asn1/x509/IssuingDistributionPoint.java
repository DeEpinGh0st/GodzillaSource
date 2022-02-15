package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.util.Strings;

public class IssuingDistributionPoint extends ASN1Object {
  private DistributionPointName distributionPoint;
  
  private boolean onlyContainsUserCerts;
  
  private boolean onlyContainsCACerts;
  
  private ReasonFlags onlySomeReasons;
  
  private boolean indirectCRL;
  
  private boolean onlyContainsAttributeCerts;
  
  private ASN1Sequence seq;
  
  public static IssuingDistributionPoint getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static IssuingDistributionPoint getInstance(Object paramObject) {
    return (paramObject instanceof IssuingDistributionPoint) ? (IssuingDistributionPoint)paramObject : ((paramObject != null) ? new IssuingDistributionPoint(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public IssuingDistributionPoint(DistributionPointName paramDistributionPointName, boolean paramBoolean1, boolean paramBoolean2, ReasonFlags paramReasonFlags, boolean paramBoolean3, boolean paramBoolean4) {
    this.distributionPoint = paramDistributionPointName;
    this.indirectCRL = paramBoolean3;
    this.onlyContainsAttributeCerts = paramBoolean4;
    this.onlyContainsCACerts = paramBoolean2;
    this.onlyContainsUserCerts = paramBoolean1;
    this.onlySomeReasons = paramReasonFlags;
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (paramDistributionPointName != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)paramDistributionPointName)); 
    if (paramBoolean1)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)ASN1Boolean.getInstance(true))); 
    if (paramBoolean2)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 2, (ASN1Encodable)ASN1Boolean.getInstance(true))); 
    if (paramReasonFlags != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 3, (ASN1Encodable)paramReasonFlags)); 
    if (paramBoolean3)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 4, (ASN1Encodable)ASN1Boolean.getInstance(true))); 
    if (paramBoolean4)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 5, (ASN1Encodable)ASN1Boolean.getInstance(true))); 
    this.seq = (ASN1Sequence)new DERSequence(aSN1EncodableVector);
  }
  
  public IssuingDistributionPoint(DistributionPointName paramDistributionPointName, boolean paramBoolean1, boolean paramBoolean2) {
    this(paramDistributionPointName, false, false, null, paramBoolean1, paramBoolean2);
  }
  
  private IssuingDistributionPoint(ASN1Sequence paramASN1Sequence) {
    this.seq = paramASN1Sequence;
    for (byte b = 0; b != paramASN1Sequence.size(); b++) {
      ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(paramASN1Sequence.getObjectAt(b));
      switch (aSN1TaggedObject.getTagNo()) {
        case 0:
          this.distributionPoint = DistributionPointName.getInstance(aSN1TaggedObject, true);
          break;
        case 1:
          this.onlyContainsUserCerts = ASN1Boolean.getInstance(aSN1TaggedObject, false).isTrue();
          break;
        case 2:
          this.onlyContainsCACerts = ASN1Boolean.getInstance(aSN1TaggedObject, false).isTrue();
          break;
        case 3:
          this.onlySomeReasons = new ReasonFlags(ReasonFlags.getInstance(aSN1TaggedObject, false));
          break;
        case 4:
          this.indirectCRL = ASN1Boolean.getInstance(aSN1TaggedObject, false).isTrue();
          break;
        case 5:
          this.onlyContainsAttributeCerts = ASN1Boolean.getInstance(aSN1TaggedObject, false).isTrue();
          break;
        default:
          throw new IllegalArgumentException("unknown tag in IssuingDistributionPoint");
      } 
    } 
  }
  
  public boolean onlyContainsUserCerts() {
    return this.onlyContainsUserCerts;
  }
  
  public boolean onlyContainsCACerts() {
    return this.onlyContainsCACerts;
  }
  
  public boolean isIndirectCRL() {
    return this.indirectCRL;
  }
  
  public boolean onlyContainsAttributeCerts() {
    return this.onlyContainsAttributeCerts;
  }
  
  public DistributionPointName getDistributionPoint() {
    return this.distributionPoint;
  }
  
  public ReasonFlags getOnlySomeReasons() {
    return this.onlySomeReasons;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.seq;
  }
  
  public String toString() {
    String str = Strings.lineSeparator();
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("IssuingDistributionPoint: [");
    stringBuffer.append(str);
    if (this.distributionPoint != null)
      appendObject(stringBuffer, str, "distributionPoint", this.distributionPoint.toString()); 
    if (this.onlyContainsUserCerts)
      appendObject(stringBuffer, str, "onlyContainsUserCerts", booleanToString(this.onlyContainsUserCerts)); 
    if (this.onlyContainsCACerts)
      appendObject(stringBuffer, str, "onlyContainsCACerts", booleanToString(this.onlyContainsCACerts)); 
    if (this.onlySomeReasons != null)
      appendObject(stringBuffer, str, "onlySomeReasons", this.onlySomeReasons.toString()); 
    if (this.onlyContainsAttributeCerts)
      appendObject(stringBuffer, str, "onlyContainsAttributeCerts", booleanToString(this.onlyContainsAttributeCerts)); 
    if (this.indirectCRL)
      appendObject(stringBuffer, str, "indirectCRL", booleanToString(this.indirectCRL)); 
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
  
  private String booleanToString(boolean paramBoolean) {
    return paramBoolean ? "true" : "false";
  }
}
