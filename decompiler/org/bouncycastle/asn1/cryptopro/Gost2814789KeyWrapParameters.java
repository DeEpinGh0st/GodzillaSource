package org.bouncycastle.asn1.cryptopro;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class Gost2814789KeyWrapParameters extends ASN1Object {
  private final ASN1ObjectIdentifier encryptionParamSet;
  
  private final byte[] ukm;
  
  private Gost2814789KeyWrapParameters(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() == 2) {
      this.encryptionParamSet = ASN1ObjectIdentifier.getInstance(paramASN1Sequence.getObjectAt(0));
      this.ukm = ASN1OctetString.getInstance(paramASN1Sequence.getObjectAt(1)).getOctets();
    } else if (paramASN1Sequence.size() == 1) {
      this.encryptionParamSet = ASN1ObjectIdentifier.getInstance(paramASN1Sequence.getObjectAt(0));
      this.ukm = null;
    } else {
      throw new IllegalArgumentException("unknown sequence length: " + paramASN1Sequence.size());
    } 
  }
  
  public static Gost2814789KeyWrapParameters getInstance(Object paramObject) {
    return (paramObject instanceof Gost2814789KeyWrapParameters) ? (Gost2814789KeyWrapParameters)paramObject : ((paramObject != null) ? new Gost2814789KeyWrapParameters(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public Gost2814789KeyWrapParameters(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    this(paramASN1ObjectIdentifier, null);
  }
  
  public Gost2814789KeyWrapParameters(ASN1ObjectIdentifier paramASN1ObjectIdentifier, byte[] paramArrayOfbyte) {
    this.encryptionParamSet = paramASN1ObjectIdentifier;
    this.ukm = Arrays.clone(paramArrayOfbyte);
  }
  
  public ASN1ObjectIdentifier getEncryptionParamSet() {
    return this.encryptionParamSet;
  }
  
  public byte[] getUkm() {
    return this.ukm;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.encryptionParamSet);
    if (this.ukm != null)
      aSN1EncodableVector.add((ASN1Encodable)new DEROctetString(this.ukm)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
