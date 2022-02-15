package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class V2Form extends ASN1Object {
  GeneralNames issuerName;
  
  IssuerSerial baseCertificateID;
  
  ObjectDigestInfo objectDigestInfo;
  
  public static V2Form getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static V2Form getInstance(Object paramObject) {
    return (paramObject instanceof V2Form) ? (V2Form)paramObject : ((paramObject != null) ? new V2Form(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public V2Form(GeneralNames paramGeneralNames) {
    this(paramGeneralNames, null, null);
  }
  
  public V2Form(GeneralNames paramGeneralNames, IssuerSerial paramIssuerSerial) {
    this(paramGeneralNames, paramIssuerSerial, null);
  }
  
  public V2Form(GeneralNames paramGeneralNames, ObjectDigestInfo paramObjectDigestInfo) {
    this(paramGeneralNames, null, paramObjectDigestInfo);
  }
  
  public V2Form(GeneralNames paramGeneralNames, IssuerSerial paramIssuerSerial, ObjectDigestInfo paramObjectDigestInfo) {
    this.issuerName = paramGeneralNames;
    this.baseCertificateID = paramIssuerSerial;
    this.objectDigestInfo = paramObjectDigestInfo;
  }
  
  public V2Form(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() > 3)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    byte b1 = 0;
    if (!(paramASN1Sequence.getObjectAt(0) instanceof ASN1TaggedObject)) {
      b1++;
      this.issuerName = GeneralNames.getInstance(paramASN1Sequence.getObjectAt(0));
    } 
    for (byte b2 = b1; b2 != paramASN1Sequence.size(); b2++) {
      ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(paramASN1Sequence.getObjectAt(b2));
      if (aSN1TaggedObject.getTagNo() == 0) {
        this.baseCertificateID = IssuerSerial.getInstance(aSN1TaggedObject, false);
      } else if (aSN1TaggedObject.getTagNo() == 1) {
        this.objectDigestInfo = ObjectDigestInfo.getInstance(aSN1TaggedObject, false);
      } else {
        throw new IllegalArgumentException("Bad tag number: " + aSN1TaggedObject.getTagNo());
      } 
    } 
  }
  
  public GeneralNames getIssuerName() {
    return this.issuerName;
  }
  
  public IssuerSerial getBaseCertificateID() {
    return this.baseCertificateID;
  }
  
  public ObjectDigestInfo getObjectDigestInfo() {
    return this.objectDigestInfo;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (this.issuerName != null)
      aSN1EncodableVector.add((ASN1Encodable)this.issuerName); 
    if (this.baseCertificateID != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.baseCertificateID)); 
    if (this.objectDigestInfo != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)this.objectDigestInfo)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
