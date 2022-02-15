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

public class DomainParameters extends ASN1Object {
  private final ASN1Integer p;
  
  private final ASN1Integer g;
  
  private final ASN1Integer q;
  
  private final ASN1Integer j;
  
  private final ValidationParams validationParams;
  
  public static DomainParameters getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static DomainParameters getInstance(Object paramObject) {
    return (paramObject instanceof DomainParameters) ? (DomainParameters)paramObject : ((paramObject != null) ? new DomainParameters(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public DomainParameters(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4, ValidationParams paramValidationParams) {
    if (paramBigInteger1 == null)
      throw new IllegalArgumentException("'p' cannot be null"); 
    if (paramBigInteger2 == null)
      throw new IllegalArgumentException("'g' cannot be null"); 
    if (paramBigInteger3 == null)
      throw new IllegalArgumentException("'q' cannot be null"); 
    this.p = new ASN1Integer(paramBigInteger1);
    this.g = new ASN1Integer(paramBigInteger2);
    this.q = new ASN1Integer(paramBigInteger3);
    if (paramBigInteger4 != null) {
      this.j = new ASN1Integer(paramBigInteger4);
    } else {
      this.j = null;
    } 
    this.validationParams = paramValidationParams;
  }
  
  private DomainParameters(ASN1Sequence paramASN1Sequence) {
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
    } else {
      this.j = null;
    } 
    if (aSN1Encodable != null) {
      this.validationParams = ValidationParams.getInstance(aSN1Encodable.toASN1Primitive());
    } else {
      this.validationParams = null;
    } 
  }
  
  private static ASN1Encodable getNext(Enumeration<ASN1Encodable> paramEnumeration) {
    return paramEnumeration.hasMoreElements() ? paramEnumeration.nextElement() : null;
  }
  
  public BigInteger getP() {
    return this.p.getPositiveValue();
  }
  
  public BigInteger getG() {
    return this.g.getPositiveValue();
  }
  
  public BigInteger getQ() {
    return this.q.getPositiveValue();
  }
  
  public BigInteger getJ() {
    return (this.j == null) ? null : this.j.getPositiveValue();
  }
  
  public ValidationParams getValidationParams() {
    return this.validationParams;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.p);
    aSN1EncodableVector.add((ASN1Encodable)this.g);
    aSN1EncodableVector.add((ASN1Encodable)this.q);
    if (this.j != null)
      aSN1EncodableVector.add((ASN1Encodable)this.j); 
    if (this.validationParams != null)
      aSN1EncodableVector.add((ASN1Encodable)this.validationParams); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
