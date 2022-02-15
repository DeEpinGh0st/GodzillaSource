package org.bouncycastle.asn1.cms;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;

public class SignedData extends ASN1Object {
  private static final ASN1Integer VERSION_1 = new ASN1Integer(1L);
  
  private static final ASN1Integer VERSION_3 = new ASN1Integer(3L);
  
  private static final ASN1Integer VERSION_4 = new ASN1Integer(4L);
  
  private static final ASN1Integer VERSION_5 = new ASN1Integer(5L);
  
  private ASN1Integer version;
  
  private ASN1Set digestAlgorithms;
  
  private ContentInfo contentInfo;
  
  private ASN1Set certificates;
  
  private ASN1Set crls;
  
  private ASN1Set signerInfos;
  
  private boolean certsBer;
  
  private boolean crlsBer;
  
  public static SignedData getInstance(Object paramObject) {
    return (paramObject instanceof SignedData) ? (SignedData)paramObject : ((paramObject != null) ? new SignedData(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public SignedData(ASN1Set paramASN1Set1, ContentInfo paramContentInfo, ASN1Set paramASN1Set2, ASN1Set paramASN1Set3, ASN1Set paramASN1Set4) {
    this.version = calculateVersion(paramContentInfo.getContentType(), paramASN1Set2, paramASN1Set3, paramASN1Set4);
    this.digestAlgorithms = paramASN1Set1;
    this.contentInfo = paramContentInfo;
    this.certificates = paramASN1Set2;
    this.crls = paramASN1Set3;
    this.signerInfos = paramASN1Set4;
    this.crlsBer = paramASN1Set3 instanceof org.bouncycastle.asn1.BERSet;
    this.certsBer = paramASN1Set2 instanceof org.bouncycastle.asn1.BERSet;
  }
  
  private ASN1Integer calculateVersion(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Set paramASN1Set1, ASN1Set paramASN1Set2, ASN1Set paramASN1Set3) {
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    boolean bool4 = false;
    if (paramASN1Set1 != null) {
      Enumeration<Object> enumeration = paramASN1Set1.getObjects();
      while (enumeration.hasMoreElements()) {
        Object object = enumeration.nextElement();
        if (object instanceof ASN1TaggedObject) {
          ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(object);
          if (aSN1TaggedObject.getTagNo() == 1) {
            bool3 = true;
            continue;
          } 
          if (aSN1TaggedObject.getTagNo() == 2) {
            bool4 = true;
            continue;
          } 
          if (aSN1TaggedObject.getTagNo() == 3)
            bool1 = true; 
        } 
      } 
    } 
    if (bool1)
      return new ASN1Integer(5L); 
    if (paramASN1Set2 != null) {
      Enumeration<Object> enumeration = paramASN1Set2.getObjects();
      while (enumeration.hasMoreElements()) {
        Object object = enumeration.nextElement();
        if (object instanceof ASN1TaggedObject)
          bool2 = true; 
      } 
    } 
    return bool2 ? VERSION_5 : (bool4 ? VERSION_4 : (bool3 ? VERSION_3 : (checkForVersion3(paramASN1Set3) ? VERSION_3 : (!CMSObjectIdentifiers.data.equals(paramASN1ObjectIdentifier) ? VERSION_3 : VERSION_1))));
  }
  
  private boolean checkForVersion3(ASN1Set paramASN1Set) {
    Enumeration enumeration = paramASN1Set.getObjects();
    while (enumeration.hasMoreElements()) {
      SignerInfo signerInfo = SignerInfo.getInstance(enumeration.nextElement());
      if (signerInfo.getVersion().getValue().intValue() == 3)
        return true; 
    } 
    return false;
  }
  
  private SignedData(ASN1Sequence paramASN1Sequence) {
    Enumeration<ASN1Set> enumeration = paramASN1Sequence.getObjects();
    this.version = ASN1Integer.getInstance(enumeration.nextElement());
    this.digestAlgorithms = enumeration.nextElement();
    this.contentInfo = ContentInfo.getInstance(enumeration.nextElement());
    while (enumeration.hasMoreElements()) {
      ASN1Primitive aSN1Primitive = (ASN1Primitive)enumeration.nextElement();
      if (aSN1Primitive instanceof ASN1TaggedObject) {
        ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)aSN1Primitive;
        switch (aSN1TaggedObject.getTagNo()) {
          case 0:
            this.certsBer = aSN1TaggedObject instanceof BERTaggedObject;
            this.certificates = ASN1Set.getInstance(aSN1TaggedObject, false);
            continue;
          case 1:
            this.crlsBer = aSN1TaggedObject instanceof BERTaggedObject;
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
  
  public ContentInfo getEncapContentInfo() {
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
      if (this.certsBer) {
        aSN1EncodableVector.add((ASN1Encodable)new BERTaggedObject(false, 0, (ASN1Encodable)this.certificates));
      } else {
        aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.certificates));
      }  
    if (this.crls != null)
      if (this.crlsBer) {
        aSN1EncodableVector.add((ASN1Encodable)new BERTaggedObject(false, 1, (ASN1Encodable)this.crls));
      } else {
        aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)this.crls));
      }  
    aSN1EncodableVector.add((ASN1Encodable)this.signerInfos);
    return (ASN1Primitive)new BERSequence(aSN1EncodableVector);
  }
}
