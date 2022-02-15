package org.bouncycastle.asn1.ua;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class DSTU4145BinaryField extends ASN1Object {
  private int m;
  
  private int k;
  
  private int j;
  
  private int l;
  
  private DSTU4145BinaryField(ASN1Sequence paramASN1Sequence) {
    this.m = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(0)).getPositiveValue().intValue();
    if (paramASN1Sequence.getObjectAt(1) instanceof ASN1Integer) {
      this.k = ((ASN1Integer)paramASN1Sequence.getObjectAt(1)).getPositiveValue().intValue();
    } else if (paramASN1Sequence.getObjectAt(1) instanceof ASN1Sequence) {
      ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(paramASN1Sequence.getObjectAt(1));
      this.k = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0)).getPositiveValue().intValue();
      this.j = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(1)).getPositiveValue().intValue();
      this.l = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(2)).getPositiveValue().intValue();
    } else {
      throw new IllegalArgumentException("object parse error");
    } 
  }
  
  public static DSTU4145BinaryField getInstance(Object paramObject) {
    return (paramObject instanceof DSTU4145BinaryField) ? (DSTU4145BinaryField)paramObject : ((paramObject != null) ? new DSTU4145BinaryField(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public DSTU4145BinaryField(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.m = paramInt1;
    this.k = paramInt2;
    this.j = paramInt3;
    this.l = paramInt4;
  }
  
  public int getM() {
    return this.m;
  }
  
  public int getK1() {
    return this.k;
  }
  
  public int getK2() {
    return this.j;
  }
  
  public int getK3() {
    return this.l;
  }
  
  public DSTU4145BinaryField(int paramInt1, int paramInt2) {
    this(paramInt1, paramInt2, 0, 0);
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(this.m));
    if (this.j == 0) {
      aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(this.k));
    } else {
      ASN1EncodableVector aSN1EncodableVector1 = new ASN1EncodableVector();
      aSN1EncodableVector1.add((ASN1Encodable)new ASN1Integer(this.k));
      aSN1EncodableVector1.add((ASN1Encodable)new ASN1Integer(this.j));
      aSN1EncodableVector1.add((ASN1Encodable)new ASN1Integer(this.l));
      aSN1EncodableVector.add((ASN1Encodable)new DERSequence(aSN1EncodableVector1));
    } 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
