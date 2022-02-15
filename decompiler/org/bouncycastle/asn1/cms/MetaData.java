package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;

public class MetaData extends ASN1Object {
  private ASN1Boolean hashProtected;
  
  private DERUTF8String fileName;
  
  private DERIA5String mediaType;
  
  private Attributes otherMetaData;
  
  public MetaData(ASN1Boolean paramASN1Boolean, DERUTF8String paramDERUTF8String, DERIA5String paramDERIA5String, Attributes paramAttributes) {
    this.hashProtected = paramASN1Boolean;
    this.fileName = paramDERUTF8String;
    this.mediaType = paramDERIA5String;
    this.otherMetaData = paramAttributes;
  }
  
  private MetaData(ASN1Sequence paramASN1Sequence) {
    this.hashProtected = ASN1Boolean.getInstance(paramASN1Sequence.getObjectAt(0));
    byte b = 1;
    if (b < paramASN1Sequence.size() && paramASN1Sequence.getObjectAt(b) instanceof DERUTF8String)
      this.fileName = DERUTF8String.getInstance(paramASN1Sequence.getObjectAt(b++)); 
    if (b < paramASN1Sequence.size() && paramASN1Sequence.getObjectAt(b) instanceof DERIA5String)
      this.mediaType = DERIA5String.getInstance(paramASN1Sequence.getObjectAt(b++)); 
    if (b < paramASN1Sequence.size())
      this.otherMetaData = Attributes.getInstance(paramASN1Sequence.getObjectAt(b++)); 
  }
  
  public static MetaData getInstance(Object paramObject) {
    return (paramObject instanceof MetaData) ? (MetaData)paramObject : ((paramObject != null) ? new MetaData(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.hashProtected);
    if (this.fileName != null)
      aSN1EncodableVector.add((ASN1Encodable)this.fileName); 
    if (this.mediaType != null)
      aSN1EncodableVector.add((ASN1Encodable)this.mediaType); 
    if (this.otherMetaData != null)
      aSN1EncodableVector.add((ASN1Encodable)this.otherMetaData); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  public boolean isHashProtected() {
    return this.hashProtected.isTrue();
  }
  
  public DERUTF8String getFileName() {
    return this.fileName;
  }
  
  public DERIA5String getMediaType() {
    return this.mediaType;
  }
  
  public Attributes getOtherMetaData() {
    return this.otherMetaData;
  }
}
