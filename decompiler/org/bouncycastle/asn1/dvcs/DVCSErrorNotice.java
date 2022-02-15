package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.x509.GeneralName;

public class DVCSErrorNotice extends ASN1Object {
  private PKIStatusInfo transactionStatus;
  
  private GeneralName transactionIdentifier;
  
  public DVCSErrorNotice(PKIStatusInfo paramPKIStatusInfo) {
    this(paramPKIStatusInfo, null);
  }
  
  public DVCSErrorNotice(PKIStatusInfo paramPKIStatusInfo, GeneralName paramGeneralName) {
    this.transactionStatus = paramPKIStatusInfo;
    this.transactionIdentifier = paramGeneralName;
  }
  
  private DVCSErrorNotice(ASN1Sequence paramASN1Sequence) {
    this.transactionStatus = PKIStatusInfo.getInstance(paramASN1Sequence.getObjectAt(0));
    if (paramASN1Sequence.size() > 1)
      this.transactionIdentifier = GeneralName.getInstance(paramASN1Sequence.getObjectAt(1)); 
  }
  
  public static DVCSErrorNotice getInstance(Object paramObject) {
    return (paramObject instanceof DVCSErrorNotice) ? (DVCSErrorNotice)paramObject : ((paramObject != null) ? new DVCSErrorNotice(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public static DVCSErrorNotice getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.transactionStatus);
    if (this.transactionIdentifier != null)
      aSN1EncodableVector.add((ASN1Encodable)this.transactionIdentifier); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  public String toString() {
    return "DVCSErrorNotice {\ntransactionStatus: " + this.transactionStatus + "\n" + ((this.transactionIdentifier != null) ? ("transactionIdentifier: " + this.transactionIdentifier + "\n") : "") + "}\n";
  }
  
  public PKIStatusInfo getTransactionStatus() {
    return this.transactionStatus;
  }
  
  public GeneralName getTransactionIdentifier() {
    return this.transactionIdentifier;
  }
}
