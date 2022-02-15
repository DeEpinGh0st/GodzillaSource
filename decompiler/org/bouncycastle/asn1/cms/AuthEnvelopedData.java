package org.bouncycastle.asn1.cms;

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

public class AuthEnvelopedData extends ASN1Object {
  private ASN1Integer version;
  
  private OriginatorInfo originatorInfo;
  
  private ASN1Set recipientInfos;
  
  private EncryptedContentInfo authEncryptedContentInfo;
  
  private ASN1Set authAttrs;
  
  private ASN1OctetString mac;
  
  private ASN1Set unauthAttrs;
  
  public AuthEnvelopedData(OriginatorInfo paramOriginatorInfo, ASN1Set paramASN1Set1, EncryptedContentInfo paramEncryptedContentInfo, ASN1Set paramASN1Set2, ASN1OctetString paramASN1OctetString, ASN1Set paramASN1Set3) {
    this.version = new ASN1Integer(0L);
    this.originatorInfo = paramOriginatorInfo;
    this.recipientInfos = paramASN1Set1;
    if (this.recipientInfos.size() == 0)
      throw new IllegalArgumentException("AuthEnvelopedData requires at least 1 RecipientInfo"); 
    this.authEncryptedContentInfo = paramEncryptedContentInfo;
    this.authAttrs = paramASN1Set2;
    if (!paramEncryptedContentInfo.getContentType().equals(CMSObjectIdentifiers.data) && (paramASN1Set2 == null || paramASN1Set2.size() == 0))
      throw new IllegalArgumentException("authAttrs must be present with non-data content"); 
    this.mac = paramASN1OctetString;
    this.unauthAttrs = paramASN1Set3;
  }
  
  private AuthEnvelopedData(ASN1Sequence paramASN1Sequence) {
    byte b = 0;
    ASN1Primitive aSN1Primitive = paramASN1Sequence.getObjectAt(b++).toASN1Primitive();
    this.version = (ASN1Integer)aSN1Primitive;
    if (this.version.getValue().intValue() != 0)
      throw new IllegalArgumentException("AuthEnvelopedData version number must be 0"); 
    aSN1Primitive = paramASN1Sequence.getObjectAt(b++).toASN1Primitive();
    if (aSN1Primitive instanceof ASN1TaggedObject) {
      this.originatorInfo = OriginatorInfo.getInstance((ASN1TaggedObject)aSN1Primitive, false);
      aSN1Primitive = paramASN1Sequence.getObjectAt(b++).toASN1Primitive();
    } 
    this.recipientInfos = ASN1Set.getInstance(aSN1Primitive);
    if (this.recipientInfos.size() == 0)
      throw new IllegalArgumentException("AuthEnvelopedData requires at least 1 RecipientInfo"); 
    aSN1Primitive = paramASN1Sequence.getObjectAt(b++).toASN1Primitive();
    this.authEncryptedContentInfo = EncryptedContentInfo.getInstance(aSN1Primitive);
    aSN1Primitive = paramASN1Sequence.getObjectAt(b++).toASN1Primitive();
    if (aSN1Primitive instanceof ASN1TaggedObject) {
      this.authAttrs = ASN1Set.getInstance((ASN1TaggedObject)aSN1Primitive, false);
      aSN1Primitive = paramASN1Sequence.getObjectAt(b++).toASN1Primitive();
    } else if (!this.authEncryptedContentInfo.getContentType().equals(CMSObjectIdentifiers.data) && (this.authAttrs == null || this.authAttrs.size() == 0)) {
      throw new IllegalArgumentException("authAttrs must be present with non-data content");
    } 
    this.mac = ASN1OctetString.getInstance(aSN1Primitive);
    if (paramASN1Sequence.size() > b) {
      aSN1Primitive = paramASN1Sequence.getObjectAt(b).toASN1Primitive();
      this.unauthAttrs = ASN1Set.getInstance((ASN1TaggedObject)aSN1Primitive, false);
    } 
  }
  
  public static AuthEnvelopedData getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static AuthEnvelopedData getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof AuthEnvelopedData)
      return (AuthEnvelopedData)paramObject; 
    if (paramObject instanceof ASN1Sequence)
      return new AuthEnvelopedData((ASN1Sequence)paramObject); 
    throw new IllegalArgumentException("Invalid AuthEnvelopedData: " + paramObject.getClass().getName());
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
  
  public EncryptedContentInfo getAuthEncryptedContentInfo() {
    return this.authEncryptedContentInfo;
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
    aSN1EncodableVector.add((ASN1Encodable)this.authEncryptedContentInfo);
    if (this.authAttrs != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)this.authAttrs)); 
    aSN1EncodableVector.add((ASN1Encodable)this.mac);
    if (this.unauthAttrs != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 2, (ASN1Encodable)this.unauthAttrs)); 
    return (ASN1Primitive)new BERSequence(aSN1EncodableVector);
  }
}
