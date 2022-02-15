package org.bouncycastle.asn1.x509;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class GeneralSubtree extends ASN1Object {
  private static final BigInteger ZERO = BigInteger.valueOf(0L);
  
  private GeneralName base;
  
  private ASN1Integer minimum;
  
  private ASN1Integer maximum;
  
  private GeneralSubtree(ASN1Sequence paramASN1Sequence) {
    ASN1TaggedObject aSN1TaggedObject1;
    ASN1TaggedObject aSN1TaggedObject2;
    this.base = GeneralName.getInstance(paramASN1Sequence.getObjectAt(0));
    switch (paramASN1Sequence.size()) {
      case 1:
        return;
      case 2:
        aSN1TaggedObject1 = ASN1TaggedObject.getInstance(paramASN1Sequence.getObjectAt(1));
        switch (aSN1TaggedObject1.getTagNo()) {
          case 0:
            this.minimum = ASN1Integer.getInstance(aSN1TaggedObject1, false);
          case 1:
            this.maximum = ASN1Integer.getInstance(aSN1TaggedObject1, false);
        } 
        throw new IllegalArgumentException("Bad tag number: " + aSN1TaggedObject1.getTagNo());
      case 3:
        aSN1TaggedObject2 = ASN1TaggedObject.getInstance(paramASN1Sequence.getObjectAt(1));
        if (aSN1TaggedObject2.getTagNo() != 0)
          throw new IllegalArgumentException("Bad tag number for 'minimum': " + aSN1TaggedObject2.getTagNo()); 
        this.minimum = ASN1Integer.getInstance(aSN1TaggedObject2, false);
        aSN1TaggedObject2 = ASN1TaggedObject.getInstance(paramASN1Sequence.getObjectAt(2));
        if (aSN1TaggedObject2.getTagNo() != 1)
          throw new IllegalArgumentException("Bad tag number for 'maximum': " + aSN1TaggedObject2.getTagNo()); 
        this.maximum = ASN1Integer.getInstance(aSN1TaggedObject2, false);
    } 
    throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size());
  }
  
  public GeneralSubtree(GeneralName paramGeneralName, BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    this.base = paramGeneralName;
    if (paramBigInteger2 != null)
      this.maximum = new ASN1Integer(paramBigInteger2); 
    if (paramBigInteger1 == null) {
      this.minimum = null;
    } else {
      this.minimum = new ASN1Integer(paramBigInteger1);
    } 
  }
  
  public GeneralSubtree(GeneralName paramGeneralName) {
    this(paramGeneralName, null, null);
  }
  
  public static GeneralSubtree getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return new GeneralSubtree(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static GeneralSubtree getInstance(Object paramObject) {
    return (paramObject == null) ? null : ((paramObject instanceof GeneralSubtree) ? (GeneralSubtree)paramObject : new GeneralSubtree(ASN1Sequence.getInstance(paramObject)));
  }
  
  public GeneralName getBase() {
    return this.base;
  }
  
  public BigInteger getMinimum() {
    return (this.minimum == null) ? ZERO : this.minimum.getValue();
  }
  
  public BigInteger getMaximum() {
    return (this.maximum == null) ? null : this.maximum.getValue();
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.base);
    if (this.minimum != null && !this.minimum.getValue().equals(ZERO))
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.minimum)); 
    if (this.maximum != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)this.maximum)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
