package org.bouncycastle.asn1.cmp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class PKIMessage extends ASN1Object {
  private PKIHeader header;
  
  private PKIBody body;
  
  private DERBitString protection;
  
  private ASN1Sequence extraCerts;
  
  private PKIMessage(ASN1Sequence paramASN1Sequence) {
    Enumeration<ASN1TaggedObject> enumeration = paramASN1Sequence.getObjects();
    this.header = PKIHeader.getInstance(enumeration.nextElement());
    this.body = PKIBody.getInstance(enumeration.nextElement());
    while (enumeration.hasMoreElements()) {
      ASN1TaggedObject aSN1TaggedObject = enumeration.nextElement();
      if (aSN1TaggedObject.getTagNo() == 0) {
        this.protection = DERBitString.getInstance(aSN1TaggedObject, true);
        continue;
      } 
      this.extraCerts = ASN1Sequence.getInstance(aSN1TaggedObject, true);
    } 
  }
  
  public static PKIMessage getInstance(Object paramObject) {
    return (paramObject instanceof PKIMessage) ? (PKIMessage)paramObject : ((paramObject != null) ? new PKIMessage(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public PKIMessage(PKIHeader paramPKIHeader, PKIBody paramPKIBody, DERBitString paramDERBitString, CMPCertificate[] paramArrayOfCMPCertificate) {
    this.header = paramPKIHeader;
    this.body = paramPKIBody;
    this.protection = paramDERBitString;
    if (paramArrayOfCMPCertificate != null) {
      ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
      for (byte b = 0; b < paramArrayOfCMPCertificate.length; b++)
        aSN1EncodableVector.add((ASN1Encodable)paramArrayOfCMPCertificate[b]); 
      this.extraCerts = (ASN1Sequence)new DERSequence(aSN1EncodableVector);
    } 
  }
  
  public PKIMessage(PKIHeader paramPKIHeader, PKIBody paramPKIBody, DERBitString paramDERBitString) {
    this(paramPKIHeader, paramPKIBody, paramDERBitString, null);
  }
  
  public PKIMessage(PKIHeader paramPKIHeader, PKIBody paramPKIBody) {
    this(paramPKIHeader, paramPKIBody, null, null);
  }
  
  public PKIHeader getHeader() {
    return this.header;
  }
  
  public PKIBody getBody() {
    return this.body;
  }
  
  public DERBitString getProtection() {
    return this.protection;
  }
  
  public CMPCertificate[] getExtraCerts() {
    if (this.extraCerts == null)
      return null; 
    CMPCertificate[] arrayOfCMPCertificate = new CMPCertificate[this.extraCerts.size()];
    for (byte b = 0; b < arrayOfCMPCertificate.length; b++)
      arrayOfCMPCertificate[b] = CMPCertificate.getInstance(this.extraCerts.getObjectAt(b)); 
    return arrayOfCMPCertificate;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.header);
    aSN1EncodableVector.add((ASN1Encodable)this.body);
    addOptional(aSN1EncodableVector, 0, (ASN1Encodable)this.protection);
    addOptional(aSN1EncodableVector, 1, (ASN1Encodable)this.extraCerts);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  private void addOptional(ASN1EncodableVector paramASN1EncodableVector, int paramInt, ASN1Encodable paramASN1Encodable) {
    if (paramASN1Encodable != null)
      paramASN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, paramInt, paramASN1Encodable)); 
  }
}
