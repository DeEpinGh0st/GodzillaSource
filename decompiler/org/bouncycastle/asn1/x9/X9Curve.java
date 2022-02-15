package org.bouncycastle.asn1.x9;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;

public class X9Curve extends ASN1Object implements X9ObjectIdentifiers {
  private ECCurve curve;
  
  private byte[] seed;
  
  private ASN1ObjectIdentifier fieldIdentifier = null;
  
  public X9Curve(ECCurve paramECCurve) {
    this.curve = paramECCurve;
    this.seed = null;
    setFieldIdentifier();
  }
  
  public X9Curve(ECCurve paramECCurve, byte[] paramArrayOfbyte) {
    this.curve = paramECCurve;
    this.seed = paramArrayOfbyte;
    setFieldIdentifier();
  }
  
  public X9Curve(X9FieldID paramX9FieldID, ASN1Sequence paramASN1Sequence) {
    this.fieldIdentifier = paramX9FieldID.getIdentifier();
    if (this.fieldIdentifier.equals(prime_field)) {
      BigInteger bigInteger = ((ASN1Integer)paramX9FieldID.getParameters()).getValue();
      X9FieldElement x9FieldElement1 = new X9FieldElement(bigInteger, (ASN1OctetString)paramASN1Sequence.getObjectAt(0));
      X9FieldElement x9FieldElement2 = new X9FieldElement(bigInteger, (ASN1OctetString)paramASN1Sequence.getObjectAt(1));
      this.curve = (ECCurve)new ECCurve.Fp(bigInteger, x9FieldElement1.getValue().toBigInteger(), x9FieldElement2.getValue().toBigInteger());
    } else if (this.fieldIdentifier.equals(characteristic_two_field)) {
      ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(paramX9FieldID.getParameters());
      int i = ((ASN1Integer)aSN1Sequence.getObjectAt(0)).getValue().intValue();
      ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)aSN1Sequence.getObjectAt(1);
      int j = 0;
      int k = 0;
      int m = 0;
      if (aSN1ObjectIdentifier.equals(tpBasis)) {
        j = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(2)).getValue().intValue();
      } else if (aSN1ObjectIdentifier.equals(ppBasis)) {
        ASN1Sequence aSN1Sequence1 = ASN1Sequence.getInstance(aSN1Sequence.getObjectAt(2));
        j = ASN1Integer.getInstance(aSN1Sequence1.getObjectAt(0)).getValue().intValue();
        k = ASN1Integer.getInstance(aSN1Sequence1.getObjectAt(1)).getValue().intValue();
        m = ASN1Integer.getInstance(aSN1Sequence1.getObjectAt(2)).getValue().intValue();
      } else {
        throw new IllegalArgumentException("This type of EC basis is not implemented");
      } 
      X9FieldElement x9FieldElement1 = new X9FieldElement(i, j, k, m, (ASN1OctetString)paramASN1Sequence.getObjectAt(0));
      X9FieldElement x9FieldElement2 = new X9FieldElement(i, j, k, m, (ASN1OctetString)paramASN1Sequence.getObjectAt(1));
      this.curve = (ECCurve)new ECCurve.F2m(i, j, k, m, x9FieldElement1.getValue().toBigInteger(), x9FieldElement2.getValue().toBigInteger());
    } else {
      throw new IllegalArgumentException("This type of ECCurve is not implemented");
    } 
    if (paramASN1Sequence.size() == 3)
      this.seed = ((DERBitString)paramASN1Sequence.getObjectAt(2)).getBytes(); 
  }
  
  private void setFieldIdentifier() {
    if (ECAlgorithms.isFpCurve(this.curve)) {
      this.fieldIdentifier = prime_field;
    } else if (ECAlgorithms.isF2mCurve(this.curve)) {
      this.fieldIdentifier = characteristic_two_field;
    } else {
      throw new IllegalArgumentException("This type of ECCurve is not implemented");
    } 
  }
  
  public ECCurve getCurve() {
    return this.curve;
  }
  
  public byte[] getSeed() {
    return this.seed;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (this.fieldIdentifier.equals(prime_field)) {
      aSN1EncodableVector.add((ASN1Encodable)(new X9FieldElement(this.curve.getA())).toASN1Primitive());
      aSN1EncodableVector.add((ASN1Encodable)(new X9FieldElement(this.curve.getB())).toASN1Primitive());
    } else if (this.fieldIdentifier.equals(characteristic_two_field)) {
      aSN1EncodableVector.add((ASN1Encodable)(new X9FieldElement(this.curve.getA())).toASN1Primitive());
      aSN1EncodableVector.add((ASN1Encodable)(new X9FieldElement(this.curve.getB())).toASN1Primitive());
    } 
    if (this.seed != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERBitString(this.seed)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
