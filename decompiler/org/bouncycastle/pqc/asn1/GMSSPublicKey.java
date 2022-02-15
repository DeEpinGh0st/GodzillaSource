package org.bouncycastle.pqc.asn1;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class GMSSPublicKey extends ASN1Object {
  private ASN1Integer version;
  
  private byte[] publicKey;
  
  private GMSSPublicKey(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 2)
      throw new IllegalArgumentException("size of seq = " + paramASN1Sequence.size()); 
    this.version = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(0));
    this.publicKey = ASN1OctetString.getInstance(paramASN1Sequence.getObjectAt(1)).getOctets();
  }
  
  public GMSSPublicKey(byte[] paramArrayOfbyte) {
    this.version = new ASN1Integer(0L);
    this.publicKey = paramArrayOfbyte;
  }
  
  public static GMSSPublicKey getInstance(Object paramObject) {
    return (paramObject instanceof GMSSPublicKey) ? (GMSSPublicKey)paramObject : ((paramObject != null) ? new GMSSPublicKey(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public byte[] getPublicKey() {
    return Arrays.clone(this.publicKey);
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.version);
    aSN1EncodableVector.add((ASN1Encodable)new DEROctetString(this.publicKey));
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
