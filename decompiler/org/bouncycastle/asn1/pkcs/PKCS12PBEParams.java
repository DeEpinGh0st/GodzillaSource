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

public class PKCS12PBEParams extends ASN1Object {
  ASN1Integer iterations;
  
  ASN1OctetString iv;
  
  public PKCS12PBEParams(byte[] paramArrayOfbyte, int paramInt) {
    this.iv = (ASN1OctetString)new DEROctetString(paramArrayOfbyte);
    this.iterations = new ASN1Integer(paramInt);
  }
  
  private PKCS12PBEParams(ASN1Sequence paramASN1Sequence) {
    this.iv = (ASN1OctetString)paramASN1Sequence.getObjectAt(0);
    this.iterations = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(1));
  }
  
  public static PKCS12PBEParams getInstance(Object paramObject) {
    return (paramObject instanceof PKCS12PBEParams) ? (PKCS12PBEParams)paramObject : ((paramObject != null) ? new PKCS12PBEParams(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public BigInteger getIterations() {
    return this.iterations.getValue();
  }
  
  public byte[] getIV() {
    return this.iv.getOctets();
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.iv);
    aSN1EncodableVector.add((ASN1Encodable)this.iterations);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
