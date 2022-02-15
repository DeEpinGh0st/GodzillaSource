package org.bouncycastle.asn1.x509;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class BasicConstraints extends ASN1Object {
  ASN1Boolean cA = ASN1Boolean.getInstance(false);
  
  ASN1Integer pathLenConstraint = null;
  
  public static BasicConstraints getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static BasicConstraints getInstance(Object paramObject) {
    return (paramObject instanceof BasicConstraints) ? (BasicConstraints)paramObject : ((paramObject instanceof X509Extension) ? getInstance(X509Extension.convertValueToObject((X509Extension)paramObject)) : ((paramObject != null) ? new BasicConstraints(ASN1Sequence.getInstance(paramObject)) : null));
  }
  
  public static BasicConstraints fromExtensions(Extensions paramExtensions) {
    return getInstance(paramExtensions.getExtensionParsedValue(Extension.basicConstraints));
  }
  
  private BasicConstraints(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() == 0) {
      this.cA = null;
      this.pathLenConstraint = null;
    } else {
      if (paramASN1Sequence.getObjectAt(0) instanceof ASN1Boolean) {
        this.cA = ASN1Boolean.getInstance(paramASN1Sequence.getObjectAt(0));
      } else {
        this.cA = null;
        this.pathLenConstraint = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(0));
      } 
      if (paramASN1Sequence.size() > 1)
        if (this.cA != null) {
          this.pathLenConstraint = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(1));
        } else {
          throw new IllegalArgumentException("wrong sequence in constructor");
        }  
    } 
  }
  
  public BasicConstraints(boolean paramBoolean) {
    if (paramBoolean) {
      this.cA = ASN1Boolean.getInstance(true);
    } else {
      this.cA = null;
    } 
    this.pathLenConstraint = null;
  }
  
  public BasicConstraints(int paramInt) {
    this.cA = ASN1Boolean.getInstance(true);
    this.pathLenConstraint = new ASN1Integer(paramInt);
  }
  
  public boolean isCA() {
    return (this.cA != null && this.cA.isTrue());
  }
  
  public BigInteger getPathLenConstraint() {
    return (this.pathLenConstraint != null) ? this.pathLenConstraint.getValue() : null;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (this.cA != null)
      aSN1EncodableVector.add((ASN1Encodable)this.cA); 
    if (this.pathLenConstraint != null)
      aSN1EncodableVector.add((ASN1Encodable)this.pathLenConstraint); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  public String toString() {
    return (this.pathLenConstraint == null) ? ((this.cA == null) ? "BasicConstraints: isCa(false)" : ("BasicConstraints: isCa(" + isCA() + ")")) : ("BasicConstraints: isCa(" + isCA() + "), pathLenConstraint = " + this.pathLenConstraint.getValue());
  }
}
