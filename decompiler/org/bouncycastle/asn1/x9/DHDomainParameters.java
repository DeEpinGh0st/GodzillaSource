package org.bouncycastle.asn1.x9;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class DHDomainParameters extends ASN1Object {
  private ASN1Integer p;
  
  private ASN1Integer g;
  
  private ASN1Integer q;
  
  private ASN1Integer j;
  
  private DHValidationParms validationParms;
  
  public static DHDomainParameters getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static DHDomainParameters getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof DHDomainParameters)
      return (DHDomainParameters)paramObject; 
    if (paramObject instanceof ASN1Sequence)
      return new DHDomainParameters((ASN1Sequence)paramObject); 
    throw new IllegalArgumentException("Invalid DHDomainParameters: " + paramObject.getClass().getName());
  }
  
  public DHDomainParameters(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4, DHValidationParms paramDHValidationParms) {
    if (paramBigInteger1 == null)
      throw new IllegalArgumentException("'p' cannot be null"); 
    if (paramBigInteger2 == null)
      throw new IllegalArgumentException("'g' cannot be null"); 
    if (paramBigInteger3 == null)
      throw new IllegalArgumentException("'q' cannot be null"); 
    this.p = new ASN1Integer(paramBigInteger1);
    this.g = new ASN1Integer(paramBigInteger2);
    this.q = new ASN1Integer(paramBigInteger3);
    this.j = new ASN1Integer(paramBigInteger4);
    this.validationParms = paramDHValidationParms;
  }
  
  public DHDomainParameters(ASN1Integer paramASN1Integer1, ASN1Integer paramASN1Integer2, ASN1Integer paramASN1Integer3, ASN1Integer paramASN1Integer4, DHValidationParms paramDHValidationParms) {
    if (paramASN1Integer1 == null)
      throw new IllegalArgumentException("'p' cannot be null"); 
    if (paramASN1Integer2 == null)
      throw new IllegalArgumentException("'g' cannot be null"); 
    if (paramASN1Integer3 == null)
      throw new IllegalArgumentException("'q' cannot be null"); 
    this.p = paramASN1Integer1;
    this.g = paramASN1Integer2;
    this.q = paramASN1Integer3;
    this.j = paramASN1Integer4;
    this.validationParms = paramDHValidationParms;
  }
  
  private DHDomainParameters(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() < 3 || paramASN1Sequence.size() > 5)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    Enumeration enumeration = paramASN1Sequence.getObjects();
    this.p = ASN1Integer.getInstance(enumeration.nextElement());
    this.g = ASN1Integer.getInstance(enumeration.nextElement());
    this.q = ASN1Integer.getInstance(enumeration.nextElement());
    ASN1Encodable aSN1Encodable = getNext(enumeration);
    if (aSN1Encodable != null && aSN1Encodable instanceof ASN1Integer) {
      this.j = ASN1Integer.getInstance(aSN1Encodable);
      aSN1Encodable = getNext(enumeration);
    } 
    if (aSN1Encodable != null)
      this.validationParms = DHValidationParms.getInstance(aSN1Encodable.toASN1Primitive()); 
  }
  
  private static ASN1Encodable getNext(Enumeration<ASN1Encodable> paramEnumeration) {
    return paramEnumeration.hasMoreElements() ? paramEnumeration.nextElement() : null;
  }
  
  public ASN1Integer getP() {
    return this.p;
  }
  
  public ASN1Integer getG() {
    return this.g;
  }
  
  public ASN1Integer getQ() {
    return this.q;
  }
  
  public ASN1Integer getJ() {
    return this.j;
  }
  
  public DHValidationParms getValidationParms() {
    return this.validationParms;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.p);
    aSN1EncodableVector.add((ASN1Encodable)this.g);
    aSN1EncodableVector.add((ASN1Encodable)this.q);
    if (this.j != null)
      aSN1EncodableVector.add((ASN1Encodable)this.j); 
    if (this.validationParms != null)
      aSN1EncodableVector.add((ASN1Encodable)this.validationParms); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
