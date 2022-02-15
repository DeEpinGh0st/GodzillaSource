package org.bouncycastle.asn1.pkcs;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class SignedData extends ASN1Object implements PKCSObjectIdentifiers {
  private ASN1Integer version;
  
  private ASN1Set digestAlgorithms;
  
  private ContentInfo contentInfo;
  
  private ASN1Set certificates;
  
  private ASN1Set crls;
  
  private ASN1Set signerInfos;
  
  public static SignedData getInstance(Object paramObject) {
    return (paramObject instanceof SignedData) ? (SignedData)paramObject : ((paramObject != null) ? new SignedData(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public SignedData(ASN1Integer paramASN1Integer, ASN1Set paramASN1Set1, ContentInfo paramContentInfo, ASN1Set paramASN1Set2, ASN1Set paramASN1Set3, ASN1Set paramASN1Set4) {
    this.version = paramASN1Integer;
    this.digestAlgorithms = paramASN1Set1;
    this.contentInfo = paramContentInfo;
    this.certificates = paramASN1Set2;
    this.crls = paramASN1Set3;
    this.signerInfos = paramASN1Set4;
  }
  
  public SignedData(ASN1Sequence paramASN1Sequence) {
    Enumeration<ASN1Integer> enumeration = paramASN1Sequence.getObjects();
    this.version = enumeration.nextElement();
    this.digestAlgorithms = (ASN1Set)enumeration.nextElement();
    this.contentInfo = ContentInfo.getInstance(enumeration.nextElement());
    while (enumeration.hasMoreElements()) {
      ASN1Primitive aSN1Primitive = (ASN1Primitive)enumeration.nextElement();
      if (aSN1Primitive instanceof ASN1TaggedObject) {
        ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)aSN1Primitive;
        switch (aSN1TaggedObject.getTagNo()) {
          case 0:
            this.certificates = ASN1Set.getInstance(aSN1TaggedObject, false);
            continue;
          case 1:
            this.crls = ASN1Set.getInstance(aSN1TaggedObject, false);
            continue;
        } 
        throw new IllegalArgumentException("unknown tag value " + aSN1TaggedObject.getTagNo());
      } 
      this.signerInfos = (ASN1Set)aSN1Primitive;
    } 
  }
  
  public ASN1Integer getVersion() {
    return this.version;
  }
  
  public ASN1Set getDigestAlgorithms() {
    return this.digestAlgorithms;
  }
  
  public ContentInfo getContentInfo() {
    return this.contentInfo;
  }
  
  public ASN1Set getCertificates() {
    return this.certificates;
  }
  
  public ASN1Set getCRLs() {
    return this.crls;
  }
  
  public ASN1Set getSignerInfos() {
    return this.signerInfos;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.version);
    aSN1EncodableVector.add((ASN1Encodable)this.digestAlgorithms);
    aSN1EncodableVector.add((ASN1Encodable)this.contentInfo);
    if (this.certificates != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.certificates)); 
    if (this.crls != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)this.crls)); 
    aSN1EncodableVector.add((ASN1Encodable)this.signerInfos);
    return (ASN1Primitive)new BERSequence(aSN1EncodableVector);
  }
}
