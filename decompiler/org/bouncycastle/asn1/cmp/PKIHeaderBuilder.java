package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;

public class PKIHeaderBuilder {
  private ASN1Integer pvno;
  
  private GeneralName sender;
  
  private GeneralName recipient;
  
  private ASN1GeneralizedTime messageTime;
  
  private AlgorithmIdentifier protectionAlg;
  
  private ASN1OctetString senderKID;
  
  private ASN1OctetString recipKID;
  
  private ASN1OctetString transactionID;
  
  private ASN1OctetString senderNonce;
  
  private ASN1OctetString recipNonce;
  
  private PKIFreeText freeText;
  
  private ASN1Sequence generalInfo;
  
  public PKIHeaderBuilder(int paramInt, GeneralName paramGeneralName1, GeneralName paramGeneralName2) {
    this(new ASN1Integer(paramInt), paramGeneralName1, paramGeneralName2);
  }
  
  private PKIHeaderBuilder(ASN1Integer paramASN1Integer, GeneralName paramGeneralName1, GeneralName paramGeneralName2) {
    this.pvno = paramASN1Integer;
    this.sender = paramGeneralName1;
    this.recipient = paramGeneralName2;
  }
  
  public PKIHeaderBuilder setMessageTime(ASN1GeneralizedTime paramASN1GeneralizedTime) {
    this.messageTime = paramASN1GeneralizedTime;
    return this;
  }
  
  public PKIHeaderBuilder setProtectionAlg(AlgorithmIdentifier paramAlgorithmIdentifier) {
    this.protectionAlg = paramAlgorithmIdentifier;
    return this;
  }
  
  public PKIHeaderBuilder setSenderKID(byte[] paramArrayOfbyte) {
    return setSenderKID((paramArrayOfbyte == null) ? null : (ASN1OctetString)new DEROctetString(paramArrayOfbyte));
  }
  
  public PKIHeaderBuilder setSenderKID(ASN1OctetString paramASN1OctetString) {
    this.senderKID = paramASN1OctetString;
    return this;
  }
  
  public PKIHeaderBuilder setRecipKID(byte[] paramArrayOfbyte) {
    return setRecipKID((paramArrayOfbyte == null) ? null : new DEROctetString(paramArrayOfbyte));
  }
  
  public PKIHeaderBuilder setRecipKID(DEROctetString paramDEROctetString) {
    this.recipKID = (ASN1OctetString)paramDEROctetString;
    return this;
  }
  
  public PKIHeaderBuilder setTransactionID(byte[] paramArrayOfbyte) {
    return setTransactionID((paramArrayOfbyte == null) ? null : (ASN1OctetString)new DEROctetString(paramArrayOfbyte));
  }
  
  public PKIHeaderBuilder setTransactionID(ASN1OctetString paramASN1OctetString) {
    this.transactionID = paramASN1OctetString;
    return this;
  }
  
  public PKIHeaderBuilder setSenderNonce(byte[] paramArrayOfbyte) {
    return setSenderNonce((paramArrayOfbyte == null) ? null : (ASN1OctetString)new DEROctetString(paramArrayOfbyte));
  }
  
  public PKIHeaderBuilder setSenderNonce(ASN1OctetString paramASN1OctetString) {
    this.senderNonce = paramASN1OctetString;
    return this;
  }
  
  public PKIHeaderBuilder setRecipNonce(byte[] paramArrayOfbyte) {
    return setRecipNonce((paramArrayOfbyte == null) ? null : (ASN1OctetString)new DEROctetString(paramArrayOfbyte));
  }
  
  public PKIHeaderBuilder setRecipNonce(ASN1OctetString paramASN1OctetString) {
    this.recipNonce = paramASN1OctetString;
    return this;
  }
  
  public PKIHeaderBuilder setFreeText(PKIFreeText paramPKIFreeText) {
    this.freeText = paramPKIFreeText;
    return this;
  }
  
  public PKIHeaderBuilder setGeneralInfo(InfoTypeAndValue paramInfoTypeAndValue) {
    return setGeneralInfo(makeGeneralInfoSeq(paramInfoTypeAndValue));
  }
  
  public PKIHeaderBuilder setGeneralInfo(InfoTypeAndValue[] paramArrayOfInfoTypeAndValue) {
    return setGeneralInfo(makeGeneralInfoSeq(paramArrayOfInfoTypeAndValue));
  }
  
  public PKIHeaderBuilder setGeneralInfo(ASN1Sequence paramASN1Sequence) {
    this.generalInfo = paramASN1Sequence;
    return this;
  }
  
  private static ASN1Sequence makeGeneralInfoSeq(InfoTypeAndValue paramInfoTypeAndValue) {
    return (ASN1Sequence)new DERSequence((ASN1Encodable)paramInfoTypeAndValue);
  }
  
  private static ASN1Sequence makeGeneralInfoSeq(InfoTypeAndValue[] paramArrayOfInfoTypeAndValue) {
    DERSequence dERSequence = null;
    if (paramArrayOfInfoTypeAndValue != null) {
      ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
      for (byte b = 0; b < paramArrayOfInfoTypeAndValue.length; b++)
        aSN1EncodableVector.add((ASN1Encodable)paramArrayOfInfoTypeAndValue[b]); 
      dERSequence = new DERSequence(aSN1EncodableVector);
    } 
    return (ASN1Sequence)dERSequence;
  }
  
  public PKIHeader build() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.pvno);
    aSN1EncodableVector.add((ASN1Encodable)this.sender);
    aSN1EncodableVector.add((ASN1Encodable)this.recipient);
    addOptional(aSN1EncodableVector, 0, (ASN1Encodable)this.messageTime);
    addOptional(aSN1EncodableVector, 1, (ASN1Encodable)this.protectionAlg);
    addOptional(aSN1EncodableVector, 2, (ASN1Encodable)this.senderKID);
    addOptional(aSN1EncodableVector, 3, (ASN1Encodable)this.recipKID);
    addOptional(aSN1EncodableVector, 4, (ASN1Encodable)this.transactionID);
    addOptional(aSN1EncodableVector, 5, (ASN1Encodable)this.senderNonce);
    addOptional(aSN1EncodableVector, 6, (ASN1Encodable)this.recipNonce);
    addOptional(aSN1EncodableVector, 7, (ASN1Encodable)this.freeText);
    addOptional(aSN1EncodableVector, 8, (ASN1Encodable)this.generalInfo);
    this.messageTime = null;
    this.protectionAlg = null;
    this.senderKID = null;
    this.recipKID = null;
    this.transactionID = null;
    this.senderNonce = null;
    this.recipNonce = null;
    this.freeText = null;
    this.generalInfo = null;
    return PKIHeader.getInstance(new DERSequence(aSN1EncodableVector));
  }
  
  private void addOptional(ASN1EncodableVector paramASN1EncodableVector, int paramInt, ASN1Encodable paramASN1Encodable) {
    if (paramASN1Encodable != null)
      paramASN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, paramInt, paramASN1Encodable)); 
  }
}
