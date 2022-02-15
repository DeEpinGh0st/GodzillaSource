package org.bouncycastle.asn1.pkcs;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class DHParameter extends ASN1Object {
  ASN1Integer p;
  
  ASN1Integer g;
  
  ASN1Integer l;
  
  public DHParameter(BigInteger paramBigInteger1, BigInteger paramBigInteger2, int paramInt) {
    this.p = new ASN1Integer(paramBigInteger1);
    this.g = new ASN1Integer(paramBigInteger2);
    if (paramInt != 0) {
      this.l = new ASN1Integer(paramInt);
    } else {
      this.l = null;
    } 
  }
  
  public static DHParameter getInstance(Object paramObject) {
    return (paramObject instanceof DHParameter) ? (DHParameter)paramObject : ((paramObject != null) ? new DHParameter(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private DHParameter(ASN1Sequence paramASN1Sequence) {
    Enumeration<ASN1Integer> enumeration = paramASN1Sequence.getObjects();
    this.p = ASN1Integer.getInstance(enumeration.nextElement());
    this.g = ASN1Integer.getInstance(enumeration.nextElement());
    if (enumeration.hasMoreElements()) {
      this.l = enumeration.nextElement();
    } else {
      this.l = null;
    } 
  }
  
  public BigInteger getP() {
    return this.p.getPositiveValue();
  }
  
  public BigInteger getG() {
    return this.g.getPositiveValue();
  }
  
  public BigInteger getL() {
    return (this.l == null) ? null : this.l.getPositiveValue();
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.p);
    aSN1EncodableVector.add((ASN1Encodable)this.g);
    if (getL() != null)
      aSN1EncodableVector.add((ASN1Encodable)this.l); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
