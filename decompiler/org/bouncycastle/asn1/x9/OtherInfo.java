package org.bouncycastle.asn1.x9;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class OtherInfo extends ASN1Object {
  private KeySpecificInfo keyInfo;
  
  private ASN1OctetString partyAInfo;
  
  private ASN1OctetString suppPubInfo;
  
  public OtherInfo(KeySpecificInfo paramKeySpecificInfo, ASN1OctetString paramASN1OctetString1, ASN1OctetString paramASN1OctetString2) {
    this.keyInfo = paramKeySpecificInfo;
    this.partyAInfo = paramASN1OctetString1;
    this.suppPubInfo = paramASN1OctetString2;
  }
  
  public static OtherInfo getInstance(Object paramObject) {
    return (paramObject instanceof OtherInfo) ? (OtherInfo)paramObject : ((paramObject != null) ? new OtherInfo(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private OtherInfo(ASN1Sequence paramASN1Sequence) {
    Enumeration<ASN1TaggedObject> enumeration = paramASN1Sequence.getObjects();
    this.keyInfo = KeySpecificInfo.getInstance(enumeration.nextElement());
    while (enumeration.hasMoreElements()) {
      ASN1TaggedObject aSN1TaggedObject = enumeration.nextElement();
      if (aSN1TaggedObject.getTagNo() == 0) {
        this.partyAInfo = (ASN1OctetString)aSN1TaggedObject.getObject();
        continue;
      } 
      if (aSN1TaggedObject.getTagNo() == 2)
        this.suppPubInfo = (ASN1OctetString)aSN1TaggedObject.getObject(); 
    } 
  }
  
  public KeySpecificInfo getKeyInfo() {
    return this.keyInfo;
  }
  
  public ASN1OctetString getPartyAInfo() {
    return this.partyAInfo;
  }
  
  public ASN1OctetString getSuppPubInfo() {
    return this.suppPubInfo;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.keyInfo);
    if (this.partyAInfo != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(0, (ASN1Encodable)this.partyAInfo)); 
    aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(2, (ASN1Encodable)this.suppPubInfo));
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
