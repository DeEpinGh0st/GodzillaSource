package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

public class POPOSigningKeyInput extends ASN1Object {
  private GeneralName sender;
  
  private PKMACValue publicKeyMAC;
  
  private SubjectPublicKeyInfo publicKey;
  
  private POPOSigningKeyInput(ASN1Sequence paramASN1Sequence) {
    ASN1Encodable aSN1Encodable = paramASN1Sequence.getObjectAt(0);
    if (aSN1Encodable instanceof ASN1TaggedObject) {
      ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)aSN1Encodable;
      if (aSN1TaggedObject.getTagNo() != 0)
        throw new IllegalArgumentException("Unknown authInfo tag: " + aSN1TaggedObject.getTagNo()); 
      this.sender = GeneralName.getInstance(aSN1TaggedObject.getObject());
    } else {
      this.publicKeyMAC = PKMACValue.getInstance(aSN1Encodable);
    } 
    this.publicKey = SubjectPublicKeyInfo.getInstance(paramASN1Sequence.getObjectAt(1));
  }
  
  public static POPOSigningKeyInput getInstance(Object paramObject) {
    return (paramObject instanceof POPOSigningKeyInput) ? (POPOSigningKeyInput)paramObject : ((paramObject != null) ? new POPOSigningKeyInput(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public POPOSigningKeyInput(GeneralName paramGeneralName, SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
    this.sender = paramGeneralName;
    this.publicKey = paramSubjectPublicKeyInfo;
  }
  
  public POPOSigningKeyInput(PKMACValue paramPKMACValue, SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
    this.publicKeyMAC = paramPKMACValue;
    this.publicKey = paramSubjectPublicKeyInfo;
  }
  
  public GeneralName getSender() {
    return this.sender;
  }
  
  public PKMACValue getPublicKeyMAC() {
    return this.publicKeyMAC;
  }
  
  public SubjectPublicKeyInfo getPublicKey() {
    return this.publicKey;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (this.sender != null) {
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.sender));
    } else {
      aSN1EncodableVector.add((ASN1Encodable)this.publicKeyMAC);
    } 
    aSN1EncodableVector.add((ASN1Encodable)this.publicKey);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
