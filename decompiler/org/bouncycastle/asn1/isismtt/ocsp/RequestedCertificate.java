package org.bouncycastle.asn1.isismtt.ocsp;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.Certificate;

public class RequestedCertificate extends ASN1Object implements ASN1Choice {
  public static final int certificate = -1;
  
  public static final int publicKeyCertificate = 0;
  
  public static final int attributeCertificate = 1;
  
  private Certificate cert;
  
  private byte[] publicKeyCert;
  
  private byte[] attributeCert;
  
  public static RequestedCertificate getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof RequestedCertificate)
      return (RequestedCertificate)paramObject; 
    if (paramObject instanceof org.bouncycastle.asn1.ASN1Sequence)
      return new RequestedCertificate(Certificate.getInstance(paramObject)); 
    if (paramObject instanceof ASN1TaggedObject)
      return new RequestedCertificate((ASN1TaggedObject)paramObject); 
    throw new IllegalArgumentException("illegal object in getInstance: " + paramObject.getClass().getName());
  }
  
  public static RequestedCertificate getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    if (!paramBoolean)
      throw new IllegalArgumentException("choice item must be explicitly tagged"); 
    return getInstance(paramASN1TaggedObject.getObject());
  }
  
  private RequestedCertificate(ASN1TaggedObject paramASN1TaggedObject) {
    if (paramASN1TaggedObject.getTagNo() == 0) {
      this.publicKeyCert = ASN1OctetString.getInstance(paramASN1TaggedObject, true).getOctets();
    } else if (paramASN1TaggedObject.getTagNo() == 1) {
      this.attributeCert = ASN1OctetString.getInstance(paramASN1TaggedObject, true).getOctets();
    } else {
      throw new IllegalArgumentException("unknown tag number: " + paramASN1TaggedObject.getTagNo());
    } 
  }
  
  public RequestedCertificate(Certificate paramCertificate) {
    this.cert = paramCertificate;
  }
  
  public RequestedCertificate(int paramInt, byte[] paramArrayOfbyte) {
    this((ASN1TaggedObject)new DERTaggedObject(paramInt, (ASN1Encodable)new DEROctetString(paramArrayOfbyte)));
  }
  
  public int getType() {
    return (this.cert != null) ? -1 : ((this.publicKeyCert != null) ? 0 : 1);
  }
  
  public byte[] getCertificateBytes() {
    if (this.cert != null)
      try {
        return this.cert.getEncoded();
      } catch (IOException iOException) {
        throw new IllegalStateException("can't decode certificate: " + iOException);
      }  
    return (this.publicKeyCert != null) ? this.publicKeyCert : this.attributeCert;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)((this.publicKeyCert != null) ? new DERTaggedObject(0, (ASN1Encodable)new DEROctetString(this.publicKeyCert)) : ((this.attributeCert != null) ? new DERTaggedObject(1, (ASN1Encodable)new DEROctetString(this.attributeCert)) : this.cert.toASN1Primitive()));
  }
}
