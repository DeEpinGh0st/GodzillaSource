package org.bouncycastle.asn1.cmp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;

public class PKIHeader extends ASN1Object {
  public static final GeneralName NULL_NAME = new GeneralName(X500Name.getInstance(new DERSequence()));
  
  public static final int CMP_1999 = 1;
  
  public static final int CMP_2000 = 2;
  
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
  
  private PKIHeader(ASN1Sequence paramASN1Sequence) {
    Enumeration<ASN1TaggedObject> enumeration = paramASN1Sequence.getObjects();
    this.pvno = ASN1Integer.getInstance(enumeration.nextElement());
    this.sender = GeneralName.getInstance(enumeration.nextElement());
    this.recipient = GeneralName.getInstance(enumeration.nextElement());
    while (enumeration.hasMoreElements()) {
      ASN1TaggedObject aSN1TaggedObject = enumeration.nextElement();
      switch (aSN1TaggedObject.getTagNo()) {
        case 0:
          this.messageTime = ASN1GeneralizedTime.getInstance(aSN1TaggedObject, true);
          continue;
        case 1:
          this.protectionAlg = AlgorithmIdentifier.getInstance(aSN1TaggedObject, true);
          continue;
        case 2:
          this.senderKID = ASN1OctetString.getInstance(aSN1TaggedObject, true);
          continue;
        case 3:
          this.recipKID = ASN1OctetString.getInstance(aSN1TaggedObject, true);
          continue;
        case 4:
          this.transactionID = ASN1OctetString.getInstance(aSN1TaggedObject, true);
          continue;
        case 5:
          this.senderNonce = ASN1OctetString.getInstance(aSN1TaggedObject, true);
          continue;
        case 6:
          this.recipNonce = ASN1OctetString.getInstance(aSN1TaggedObject, true);
          continue;
        case 7:
          this.freeText = PKIFreeText.getInstance(aSN1TaggedObject, true);
          continue;
        case 8:
          this.generalInfo = ASN1Sequence.getInstance(aSN1TaggedObject, true);
          continue;
      } 
      throw new IllegalArgumentException("unknown tag number: " + aSN1TaggedObject.getTagNo());
    } 
  }
  
  public static PKIHeader getInstance(Object paramObject) {
    return (paramObject instanceof PKIHeader) ? (PKIHeader)paramObject : ((paramObject != null) ? new PKIHeader(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public PKIHeader(int paramInt, GeneralName paramGeneralName1, GeneralName paramGeneralName2) {
    this(new ASN1Integer(paramInt), paramGeneralName1, paramGeneralName2);
  }
  
  private PKIHeader(ASN1Integer paramASN1Integer, GeneralName paramGeneralName1, GeneralName paramGeneralName2) {
    this.pvno = paramASN1Integer;
    this.sender = paramGeneralName1;
    this.recipient = paramGeneralName2;
  }
  
  public ASN1Integer getPvno() {
    return this.pvno;
  }
  
  public GeneralName getSender() {
    return this.sender;
  }
  
  public GeneralName getRecipient() {
    return this.recipient;
  }
  
  public ASN1GeneralizedTime getMessageTime() {
    return this.messageTime;
  }
  
  public AlgorithmIdentifier getProtectionAlg() {
    return this.protectionAlg;
  }
  
  public ASN1OctetString getSenderKID() {
    return this.senderKID;
  }
  
  public ASN1OctetString getRecipKID() {
    return this.recipKID;
  }
  
  public ASN1OctetString getTransactionID() {
    return this.transactionID;
  }
  
  public ASN1OctetString getSenderNonce() {
    return this.senderNonce;
  }
  
  public ASN1OctetString getRecipNonce() {
    return this.recipNonce;
  }
  
  public PKIFreeText getFreeText() {
    return this.freeText;
  }
  
  public InfoTypeAndValue[] getGeneralInfo() {
    if (this.generalInfo == null)
      return null; 
    InfoTypeAndValue[] arrayOfInfoTypeAndValue = new InfoTypeAndValue[this.generalInfo.size()];
    for (byte b = 0; b < arrayOfInfoTypeAndValue.length; b++)
      arrayOfInfoTypeAndValue[b] = InfoTypeAndValue.getInstance(this.generalInfo.getObjectAt(b)); 
    return arrayOfInfoTypeAndValue;
  }
  
  public ASN1Primitive toASN1Primitive() {
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
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  private void addOptional(ASN1EncodableVector paramASN1EncodableVector, int paramInt, ASN1Encodable paramASN1Encodable) {
    if (paramASN1Encodable != null)
      paramASN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, paramInt, paramASN1Encodable)); 
  }
}
