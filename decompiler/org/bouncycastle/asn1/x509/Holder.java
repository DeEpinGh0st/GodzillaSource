package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class Holder extends ASN1Object {
  public static final int V1_CERTIFICATE_HOLDER = 0;
  
  public static final int V2_CERTIFICATE_HOLDER = 1;
  
  IssuerSerial baseCertificateID;
  
  GeneralNames entityName;
  
  ObjectDigestInfo objectDigestInfo;
  
  private int version = 1;
  
  public static Holder getInstance(Object paramObject) {
    return (paramObject instanceof Holder) ? (Holder)paramObject : ((paramObject instanceof ASN1TaggedObject) ? new Holder(ASN1TaggedObject.getInstance(paramObject)) : ((paramObject != null) ? new Holder(ASN1Sequence.getInstance(paramObject)) : null));
  }
  
  private Holder(ASN1TaggedObject paramASN1TaggedObject) {
    switch (paramASN1TaggedObject.getTagNo()) {
      case 0:
        this.baseCertificateID = IssuerSerial.getInstance(paramASN1TaggedObject, true);
        break;
      case 1:
        this.entityName = GeneralNames.getInstance(paramASN1TaggedObject, true);
        break;
      default:
        throw new IllegalArgumentException("unknown tag in Holder");
    } 
    this.version = 0;
  }
  
  private Holder(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() > 3)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    for (byte b = 0; b != paramASN1Sequence.size(); b++) {
      ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(paramASN1Sequence.getObjectAt(b));
      switch (aSN1TaggedObject.getTagNo()) {
        case 0:
          this.baseCertificateID = IssuerSerial.getInstance(aSN1TaggedObject, false);
          break;
        case 1:
          this.entityName = GeneralNames.getInstance(aSN1TaggedObject, false);
          break;
        case 2:
          this.objectDigestInfo = ObjectDigestInfo.getInstance(aSN1TaggedObject, false);
          break;
        default:
          throw new IllegalArgumentException("unknown tag in Holder");
      } 
    } 
    this.version = 1;
  }
  
  public Holder(IssuerSerial paramIssuerSerial) {
    this(paramIssuerSerial, 1);
  }
  
  public Holder(IssuerSerial paramIssuerSerial, int paramInt) {
    this.baseCertificateID = paramIssuerSerial;
    this.version = paramInt;
  }
  
  public int getVersion() {
    return this.version;
  }
  
  public Holder(GeneralNames paramGeneralNames) {
    this(paramGeneralNames, 1);
  }
  
  public Holder(GeneralNames paramGeneralNames, int paramInt) {
    this.entityName = paramGeneralNames;
    this.version = paramInt;
  }
  
  public Holder(ObjectDigestInfo paramObjectDigestInfo) {
    this.objectDigestInfo = paramObjectDigestInfo;
  }
  
  public IssuerSerial getBaseCertificateID() {
    return this.baseCertificateID;
  }
  
  public GeneralNames getEntityName() {
    return this.entityName;
  }
  
  public ObjectDigestInfo getObjectDigestInfo() {
    return this.objectDigestInfo;
  }
  
  public ASN1Primitive toASN1Primitive() {
    if (this.version == 1) {
      ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
      if (this.baseCertificateID != null)
        aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.baseCertificateID)); 
      if (this.entityName != null)
        aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)this.entityName)); 
      if (this.objectDigestInfo != null)
        aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 2, (ASN1Encodable)this.objectDigestInfo)); 
      return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
    } 
    return (ASN1Primitive)((this.entityName != null) ? new DERTaggedObject(true, 1, (ASN1Encodable)this.entityName) : new DERTaggedObject(true, 0, (ASN1Encodable)this.baseCertificateID));
  }
}
