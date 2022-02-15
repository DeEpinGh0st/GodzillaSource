package org.bouncycastle.asn1.sec;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.util.BigIntegers;

public class ECPrivateKeyStructure extends ASN1Object {
  private ASN1Sequence seq;
  
  public ECPrivateKeyStructure(ASN1Sequence paramASN1Sequence) {
    this.seq = paramASN1Sequence;
  }
  
  public ECPrivateKeyStructure(BigInteger paramBigInteger) {
    byte[] arrayOfByte = BigIntegers.asUnsignedByteArray(paramBigInteger);
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(1L));
    aSN1EncodableVector.add((ASN1Encodable)new DEROctetString(arrayOfByte));
    this.seq = (ASN1Sequence)new DERSequence(aSN1EncodableVector);
  }
  
  public ECPrivateKeyStructure(BigInteger paramBigInteger, ASN1Encodable paramASN1Encodable) {
    this(paramBigInteger, null, paramASN1Encodable);
  }
  
  public ECPrivateKeyStructure(BigInteger paramBigInteger, DERBitString paramDERBitString, ASN1Encodable paramASN1Encodable) {
    byte[] arrayOfByte = BigIntegers.asUnsignedByteArray(paramBigInteger);
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(1L));
    aSN1EncodableVector.add((ASN1Encodable)new DEROctetString(arrayOfByte));
    if (paramASN1Encodable != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 0, paramASN1Encodable)); 
    if (paramDERBitString != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 1, (ASN1Encodable)paramDERBitString)); 
    this.seq = (ASN1Sequence)new DERSequence(aSN1EncodableVector);
  }
  
  public BigInteger getKey() {
    ASN1OctetString aSN1OctetString = (ASN1OctetString)this.seq.getObjectAt(1);
    return new BigInteger(1, aSN1OctetString.getOctets());
  }
  
  public DERBitString getPublicKey() {
    return (DERBitString)getObjectInTag(1);
  }
  
  public ASN1Primitive getParameters() {
    return getObjectInTag(0);
  }
  
  private ASN1Primitive getObjectInTag(int paramInt) {
    Enumeration<ASN1Encodable> enumeration = this.seq.getObjects();
    while (enumeration.hasMoreElements()) {
      ASN1Encodable aSN1Encodable = enumeration.nextElement();
      if (aSN1Encodable instanceof ASN1TaggedObject) {
        ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)aSN1Encodable;
        if (aSN1TaggedObject.getTagNo() == paramInt)
          return aSN1TaggedObject.getObject().toASN1Primitive(); 
      } 
    } 
    return null;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.seq;
  }
}
