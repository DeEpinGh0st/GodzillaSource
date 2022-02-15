package org.bouncycastle.asn1.esf;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.Attribute;
import org.bouncycastle.asn1.x509.AttributeCertificate;

public class SignerAttribute extends ASN1Object {
  private Object[] values;
  
  public static SignerAttribute getInstance(Object paramObject) {
    return (paramObject instanceof SignerAttribute) ? (SignerAttribute)paramObject : ((paramObject != null) ? new SignerAttribute(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private SignerAttribute(ASN1Sequence paramASN1Sequence) {
    byte b = 0;
    this.values = new Object[paramASN1Sequence.size()];
    Enumeration enumeration = paramASN1Sequence.getObjects();
    while (enumeration.hasMoreElements()) {
      ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(enumeration.nextElement());
      if (aSN1TaggedObject.getTagNo() == 0) {
        ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(aSN1TaggedObject, true);
        Attribute[] arrayOfAttribute = new Attribute[aSN1Sequence.size()];
        for (byte b1 = 0; b1 != arrayOfAttribute.length; b1++)
          arrayOfAttribute[b1] = Attribute.getInstance(aSN1Sequence.getObjectAt(b1)); 
        this.values[b] = arrayOfAttribute;
      } else if (aSN1TaggedObject.getTagNo() == 1) {
        this.values[b] = AttributeCertificate.getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, true));
      } else {
        throw new IllegalArgumentException("illegal tag: " + aSN1TaggedObject.getTagNo());
      } 
      b++;
    } 
  }
  
  public SignerAttribute(Attribute[] paramArrayOfAttribute) {
    this.values = new Object[1];
    this.values[0] = paramArrayOfAttribute;
  }
  
  public SignerAttribute(AttributeCertificate paramAttributeCertificate) {
    this.values = new Object[1];
    this.values[0] = paramAttributeCertificate;
  }
  
  public Object[] getValues() {
    Object[] arrayOfObject = new Object[this.values.length];
    System.arraycopy(this.values, 0, arrayOfObject, 0, arrayOfObject.length);
    return arrayOfObject;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    for (byte b = 0; b != this.values.length; b++) {
      if (this.values[b] instanceof Attribute[]) {
        aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(0, (ASN1Encodable)new DERSequence((ASN1Encodable[])this.values[b])));
      } else {
        aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(1, (ASN1Encodable)this.values[b]));
      } 
    } 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
