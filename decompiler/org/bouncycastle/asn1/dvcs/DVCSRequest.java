package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.GeneralName;

public class DVCSRequest extends ASN1Object {
  private DVCSRequestInformation requestInformation;
  
  private Data data;
  
  private GeneralName transactionIdentifier;
  
  public DVCSRequest(DVCSRequestInformation paramDVCSRequestInformation, Data paramData) {
    this(paramDVCSRequestInformation, paramData, null);
  }
  
  public DVCSRequest(DVCSRequestInformation paramDVCSRequestInformation, Data paramData, GeneralName paramGeneralName) {
    this.requestInformation = paramDVCSRequestInformation;
    this.data = paramData;
    this.transactionIdentifier = paramGeneralName;
  }
  
  private DVCSRequest(ASN1Sequence paramASN1Sequence) {
    this.requestInformation = DVCSRequestInformation.getInstance(paramASN1Sequence.getObjectAt(0));
    this.data = Data.getInstance(paramASN1Sequence.getObjectAt(1));
    if (paramASN1Sequence.size() > 2)
      this.transactionIdentifier = GeneralName.getInstance(paramASN1Sequence.getObjectAt(2)); 
  }
  
  public static DVCSRequest getInstance(Object paramObject) {
    return (paramObject instanceof DVCSRequest) ? (DVCSRequest)paramObject : ((paramObject != null) ? new DVCSRequest(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public static DVCSRequest getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.requestInformation);
    aSN1EncodableVector.add((ASN1Encodable)this.data);
    if (this.transactionIdentifier != null)
      aSN1EncodableVector.add((ASN1Encodable)this.transactionIdentifier); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  public String toString() {
    return "DVCSRequest {\nrequestInformation: " + this.requestInformation + "\n" + "data: " + this.data + "\n" + ((this.transactionIdentifier != null) ? ("transactionIdentifier: " + this.transactionIdentifier + "\n") : "") + "}\n";
  }
  
  public Data getData() {
    return this.data;
  }
  
  public DVCSRequestInformation getRequestInformation() {
    return this.requestInformation;
  }
  
  public GeneralName getTransactionIdentifier() {
    return this.transactionIdentifier;
  }
}
