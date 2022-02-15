package org.bouncycastle.asn1.pkcs;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.X509Name;

public class IssuerAndSerialNumber extends ASN1Object {
  X500Name name;
  
  ASN1Integer certSerialNumber;
  
  public static IssuerAndSerialNumber getInstance(Object paramObject) {
    return (paramObject instanceof IssuerAndSerialNumber) ? (IssuerAndSerialNumber)paramObject : ((paramObject != null) ? new IssuerAndSerialNumber(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private IssuerAndSerialNumber(ASN1Sequence paramASN1Sequence) {
    this.name = X500Name.getInstance(paramASN1Sequence.getObjectAt(0));
    this.certSerialNumber = (ASN1Integer)paramASN1Sequence.getObjectAt(1);
  }
  
  public IssuerAndSerialNumber(X509Name paramX509Name, BigInteger paramBigInteger) {
    this.name = X500Name.getInstance(paramX509Name.toASN1Primitive());
    this.certSerialNumber = new ASN1Integer(paramBigInteger);
  }
  
  public IssuerAndSerialNumber(X509Name paramX509Name, ASN1Integer paramASN1Integer) {
    this.name = X500Name.getInstance(paramX509Name.toASN1Primitive());
    this.certSerialNumber = paramASN1Integer;
  }
  
  public IssuerAndSerialNumber(X500Name paramX500Name, BigInteger paramBigInteger) {
    this.name = paramX500Name;
    this.certSerialNumber = new ASN1Integer(paramBigInteger);
  }
  
  public X500Name getName() {
    return this.name;
  }
  
  public ASN1Integer getCertificateSerialNumber() {
    return this.certSerialNumber;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.name);
    aSN1EncodableVector.add((ASN1Encodable)this.certSerialNumber);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
