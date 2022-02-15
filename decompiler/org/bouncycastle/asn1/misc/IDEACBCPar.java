package org.bouncycastle.asn1.misc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class IDEACBCPar extends ASN1Object {
  ASN1OctetString iv;
  
  public static IDEACBCPar getInstance(Object paramObject) {
    return (paramObject instanceof IDEACBCPar) ? (IDEACBCPar)paramObject : ((paramObject != null) ? new IDEACBCPar(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public IDEACBCPar(byte[] paramArrayOfbyte) {
    this.iv = (ASN1OctetString)new DEROctetString(paramArrayOfbyte);
  }
  
  public IDEACBCPar(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() == 1) {
      this.iv = (ASN1OctetString)paramASN1Sequence.getObjectAt(0);
    } else {
      this.iv = null;
    } 
  }
  
  public byte[] getIV() {
    return (this.iv != null) ? Arrays.clone(this.iv.getOctets()) : null;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (this.iv != null)
      aSN1EncodableVector.add((ASN1Encodable)this.iv); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
