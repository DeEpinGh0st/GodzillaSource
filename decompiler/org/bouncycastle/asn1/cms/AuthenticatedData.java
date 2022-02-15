package org.bouncycastle.asn1.cms;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class AuthenticatedData extends ASN1Object {
  private ASN1Integer version;
  
  private OriginatorInfo originatorInfo;
  
  private ASN1Set recipientInfos;
  
  private AlgorithmIdentifier macAlgorithm;
  
  private AlgorithmIdentifier digestAlgorithm;
  
  private ContentInfo encapsulatedContentInfo;
  
  private ASN1Set authAttrs;
  
  private ASN1OctetString mac;
  
  private ASN1Set unauthAttrs;
  
  public AuthenticatedData(OriginatorInfo paramOriginatorInfo, ASN1Set paramASN1Set1, AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2, ContentInfo paramContentInfo, ASN1Set paramASN1Set2, ASN1OctetString paramASN1OctetString, ASN1Set paramASN1Set3) {
    if ((paramAlgorithmIdentifier2 != null || paramASN1Set2 != null) && (paramAlgorithmIdentifier2 == null || paramASN1Set2 == null))
      throw new IllegalArgumentException("digestAlgorithm and authAttrs must be set together"); 
    this.version = new ASN1Integer(calculateVersion(paramOriginatorInfo));
    this.originatorInfo = paramOriginatorInfo;
    this.macAlgorithm = paramAlgorithmIdentifier1;
    this.digestAlgorithm = paramAlgorithmIdentifier2;
    this.recipientInfos = paramASN1Set1;
    this.encapsulatedContentInfo = paramContentInfo;
    this.authAttrs = paramASN1Set2;
    this.mac = paramASN1OctetString;
    this.unauthAttrs = paramASN1Set3;
  }
  
  private AuthenticatedData(ASN1Sequence paramASN1Sequence) {
    byte b = 0;
    this.version = (ASN1Integer)paramASN1Sequence.getObjectAt(b++);
    ASN1Encodable aSN1Encodable = paramASN1Sequence.getObjectAt(b++);
    if (aSN1Encodable instanceof ASN1TaggedObject) {
      this.originatorInfo = OriginatorInfo.getInstance((ASN1TaggedObject)aSN1Encodable, false);
      aSN1Encodable = paramASN1Sequence.getObjectAt(b++);
    } 
    this.recipientInfos = ASN1Set.getInstance(aSN1Encodable);
    this.macAlgorithm = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(b++));
    aSN1Encodable = paramASN1Sequence.getObjectAt(b++);
    if (aSN1Encodable instanceof ASN1TaggedObject) {
      this.digestAlgorithm = AlgorithmIdentifier.getInstance((ASN1TaggedObject)aSN1Encodable, false);
      aSN1Encodable = paramASN1Sequence.getObjectAt(b++);
    } 
    this.encapsulatedContentInfo = ContentInfo.getInstance(aSN1Encodable);
    aSN1Encodable = paramASN1Sequence.getObjectAt(b++);
    if (aSN1Encodable instanceof ASN1TaggedObject) {
      this.authAttrs = ASN1Set.getInstance((ASN1TaggedObject)aSN1Encodable, false);
      aSN1Encodable = paramASN1Sequence.getObjectAt(b++);
    } 
    this.mac = ASN1OctetString.getInstance(aSN1Encodable);
    if (paramASN1Sequence.size() > b)
      this.unauthAttrs = ASN1Set.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(b), false); 
  }
  
  public static AuthenticatedData getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static AuthenticatedData getInstance(Object paramObject) {
    return (paramObject instanceof AuthenticatedData) ? (AuthenticatedData)paramObject : ((paramObject != null) ? new AuthenticatedData(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1Integer getVersion() {
    return this.version;
  }
  
  public OriginatorInfo getOriginatorInfo() {
    return this.originatorInfo;
  }
  
  public ASN1Set getRecipientInfos() {
    return this.recipientInfos;
  }
  
  public AlgorithmIdentifier getMacAlgorithm() {
    return this.macAlgorithm;
  }
  
  public AlgorithmIdentifier getDigestAlgorithm() {
    return this.digestAlgorithm;
  }
  
  public ContentInfo getEncapsulatedContentInfo() {
    return this.encapsulatedContentInfo;
  }
  
  public ASN1Set getAuthAttrs() {
    return this.authAttrs;
  }
  
  public ASN1OctetString getMac() {
    return this.mac;
  }
  
  public ASN1Set getUnauthAttrs() {
    return this.unauthAttrs;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.version);
    if (this.originatorInfo != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.originatorInfo)); 
    aSN1EncodableVector.add((ASN1Encodable)this.recipientInfos);
    aSN1EncodableVector.add((ASN1Encodable)this.macAlgorithm);
    if (this.digestAlgorithm != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)this.digestAlgorithm)); 
    aSN1EncodableVector.add((ASN1Encodable)this.encapsulatedContentInfo);
    if (this.authAttrs != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 2, (ASN1Encodable)this.authAttrs)); 
    aSN1EncodableVector.add((ASN1Encodable)this.mac);
    if (this.unauthAttrs != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 3, (ASN1Encodable)this.unauthAttrs)); 
    return (ASN1Primitive)new BERSequence(aSN1EncodableVector);
  }
  
  public static int calculateVersion(OriginatorInfo paramOriginatorInfo) {
    if (paramOriginatorInfo == null)
      return 0; 
    byte b = 0;
    Enumeration<Object> enumeration = paramOriginatorInfo.getCertificates().getObjects();
    while (enumeration.hasMoreElements()) {
      ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)enumeration.nextElement();
      if (aSN1TaggedObject instanceof ASN1TaggedObject) {
        ASN1TaggedObject aSN1TaggedObject1 = aSN1TaggedObject;
        if (aSN1TaggedObject1.getTagNo() == 2) {
          b = 1;
          continue;
        } 
        if (aSN1TaggedObject1.getTagNo() == 3) {
          b = 3;
          break;
        } 
      } 
    } 
    if (paramOriginatorInfo.getCRLs() != null) {
      enumeration = paramOriginatorInfo.getCRLs().getObjects();
      while (enumeration.hasMoreElements()) {
        ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)enumeration.nextElement();
        if (aSN1TaggedObject instanceof ASN1TaggedObject) {
          ASN1TaggedObject aSN1TaggedObject1 = aSN1TaggedObject;
          if (aSN1TaggedObject1.getTagNo() == 1) {
            b = 3;
            break;
          } 
        } 
      } 
    } 
    return b;
  }
}
