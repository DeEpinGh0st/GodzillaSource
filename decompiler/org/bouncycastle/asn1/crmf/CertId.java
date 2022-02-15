package org.bouncycastle.asn1.crmf;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.GeneralName;

public class CertId extends ASN1Object {
  private GeneralName issuer;
  
  private ASN1Integer serialNumber;
  
  private CertId(ASN1Sequence paramASN1Sequence) {
    this.issuer = GeneralName.getInstance(paramASN1Sequence.getObjectAt(0));
    this.serialNumber = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(1));
  }
  
  public static CertId getInstance(Object paramObject) {
    return (paramObject instanceof CertId) ? (CertId)paramObject : ((paramObject != null) ? new CertId(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public static CertId getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public CertId(GeneralName paramGeneralName, BigInteger paramBigInteger) {
    this(paramGeneralName, new ASN1Integer(paramBigInteger));
  }
  
  public CertId(GeneralName paramGeneralName, ASN1Integer paramASN1Integer) {
    this.issuer = paramGeneralName;
    this.serialNumber = paramASN1Integer;
  }
  
  public GeneralName getIssuer() {
    return this.issuer;
  }
  
  public ASN1Integer getSerialNumber() {
    return this.serialNumber;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.issuer);
    aSN1EncodableVector.add((ASN1Encodable)this.serialNumber);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
