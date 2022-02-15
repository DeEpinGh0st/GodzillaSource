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

public class RC2CBCParameter extends ASN1Object {
  ASN1Integer version;
  
  ASN1OctetString iv;
  
  public static RC2CBCParameter getInstance(Object paramObject) {
    return (paramObject instanceof RC2CBCParameter) ? (RC2CBCParameter)paramObject : ((paramObject != null) ? new RC2CBCParameter(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public RC2CBCParameter(byte[] paramArrayOfbyte) {
    this.version = null;
    this.iv = (ASN1OctetString)new DEROctetString(paramArrayOfbyte);
  }
  
  public RC2CBCParameter(int paramInt, byte[] paramArrayOfbyte) {
    this.version = new ASN1Integer(paramInt);
    this.iv = (ASN1OctetString)new DEROctetString(paramArrayOfbyte);
  }
  
  private RC2CBCParameter(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() == 1) {
      this.version = null;
      this.iv = (ASN1OctetString)paramASN1Sequence.getObjectAt(0);
    } else {
      this.version = (ASN1Integer)paramASN1Sequence.getObjectAt(0);
      this.iv = (ASN1OctetString)paramASN1Sequence.getObjectAt(1);
    } 
  }
  
  public BigInteger getRC2ParameterVersion() {
    return (this.version == null) ? null : this.version.getValue();
  }
  
  public byte[] getIV() {
    return this.iv.getOctets();
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (this.version != null)
      aSN1EncodableVector.add((ASN1Encodable)this.version); 
    aSN1EncodableVector.add((ASN1Encodable)this.iv);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
