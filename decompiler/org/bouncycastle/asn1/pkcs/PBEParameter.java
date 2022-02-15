package org.bouncycastle.asn1.pkcs;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;

public class PBEParameter extends ASN1Object {
  ASN1Integer iterations;
  
  ASN1OctetString salt;
  
  public PBEParameter(byte[] paramArrayOfbyte, int paramInt) {
    if (paramArrayOfbyte.length != 8)
      throw new IllegalArgumentException("salt length must be 8"); 
    this.salt = (ASN1OctetString)new DEROctetString(paramArrayOfbyte);
    this.iterations = new ASN1Integer(paramInt);
  }
  
  private PBEParameter(ASN1Sequence paramASN1Sequence) {
    this.salt = (ASN1OctetString)paramASN1Sequence.getObjectAt(0);
    this.iterations = (ASN1Integer)paramASN1Sequence.getObjectAt(1);
  }
  
  public static PBEParameter getInstance(Object paramObject) {
    return (paramObject instanceof PBEParameter) ? (PBEParameter)paramObject : ((paramObject != null) ? new PBEParameter(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public BigInteger getIterationCount() {
    return this.iterations.getValue();
  }
  
  public byte[] getSalt() {
    return this.salt.getOctets();
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.salt);
    aSN1EncodableVector.add((ASN1Encodable)this.iterations);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
