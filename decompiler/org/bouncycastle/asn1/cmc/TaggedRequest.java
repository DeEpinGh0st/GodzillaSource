package org.bouncycastle.asn1.cmc;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.crmf.CertReqMsg;

public class TaggedRequest extends ASN1Object implements ASN1Choice {
  public static final int TCR = 0;
  
  public static final int CRM = 1;
  
  public static final int ORM = 2;
  
  private final int tagNo = 0;
  
  private final ASN1Encodable value;
  
  public TaggedRequest(TaggedCertificationRequest paramTaggedCertificationRequest) {
    this.value = (ASN1Encodable)paramTaggedCertificationRequest;
  }
  
  public TaggedRequest(CertReqMsg paramCertReqMsg) {
    this.value = (ASN1Encodable)paramCertReqMsg;
  }
  
  private TaggedRequest(ASN1Sequence paramASN1Sequence) {
    this.value = (ASN1Encodable)paramASN1Sequence;
  }
  
  public static TaggedRequest getInstance(Object paramObject) {
    if (paramObject instanceof TaggedRequest)
      return (TaggedRequest)paramObject; 
    if (paramObject != null) {
      if (paramObject instanceof ASN1Encodable) {
        ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(((ASN1Encodable)paramObject).toASN1Primitive());
        switch (aSN1TaggedObject.getTagNo()) {
          case 0:
            return new TaggedRequest(TaggedCertificationRequest.getInstance(aSN1TaggedObject, false));
          case 1:
            return new TaggedRequest(CertReqMsg.getInstance(aSN1TaggedObject, false));
          case 2:
            return new TaggedRequest(ASN1Sequence.getInstance(aSN1TaggedObject, false));
        } 
        throw new IllegalArgumentException("unknown tag in getInstance(): " + aSN1TaggedObject.getTagNo());
      } 
      if (paramObject instanceof byte[])
        try {
          return getInstance(ASN1Primitive.fromByteArray((byte[])paramObject));
        } catch (IOException iOException) {
          throw new IllegalArgumentException("unknown encoding in getInstance()");
        }  
      throw new IllegalArgumentException("unknown object in getInstance(): " + paramObject.getClass().getName());
    } 
    return null;
  }
  
  public int getTagNo() {
    return this.tagNo;
  }
  
  public ASN1Encodable getValue() {
    return this.value;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)new DERTaggedObject(false, this.tagNo, this.value);
  }
}
