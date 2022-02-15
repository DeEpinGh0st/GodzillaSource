package org.bouncycastle.asn1.x509;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;

public class IssuerSerial extends ASN1Object {
  GeneralNames issuer;
  
  ASN1Integer serial;
  
  DERBitString issuerUID;
  
  public static IssuerSerial getInstance(Object paramObject) {
    return (paramObject instanceof IssuerSerial) ? (IssuerSerial)paramObject : ((paramObject != null) ? new IssuerSerial(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public static IssuerSerial getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  private IssuerSerial(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 2 && paramASN1Sequence.size() != 3)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    this.issuer = GeneralNames.getInstance(paramASN1Sequence.getObjectAt(0));
    this.serial = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(1));
    if (paramASN1Sequence.size() == 3)
      this.issuerUID = DERBitString.getInstance(paramASN1Sequence.getObjectAt(2)); 
  }
  
  public IssuerSerial(X500Name paramX500Name, BigInteger paramBigInteger) {
    this(new GeneralNames(new GeneralName(paramX500Name)), new ASN1Integer(paramBigInteger));
  }
  
  public IssuerSerial(GeneralNames paramGeneralNames, BigInteger paramBigInteger) {
    this(paramGeneralNames, new ASN1Integer(paramBigInteger));
  }
  
  public IssuerSerial(GeneralNames paramGeneralNames, ASN1Integer paramASN1Integer) {
    this.issuer = paramGeneralNames;
    this.serial = paramASN1Integer;
  }
  
  public GeneralNames getIssuer() {
    return this.issuer;
  }
  
  public ASN1Integer getSerial() {
    return this.serial;
  }
  
  public DERBitString getIssuerUID() {
    return this.issuerUID;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.issuer);
    aSN1EncodableVector.add((ASN1Encodable)this.serial);
    if (this.issuerUID != null)
      aSN1EncodableVector.add((ASN1Encodable)this.issuerUID); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
