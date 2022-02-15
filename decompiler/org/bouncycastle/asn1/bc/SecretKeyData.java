package org.bouncycastle.asn1.bc;

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

public class SecretKeyData extends ASN1Object {
  private final ASN1ObjectIdentifier keyAlgorithm;
  
  private final ASN1OctetString keyBytes;
  
  public SecretKeyData(ASN1ObjectIdentifier paramASN1ObjectIdentifier, byte[] paramArrayOfbyte) {
    this.keyAlgorithm = paramASN1ObjectIdentifier;
    this.keyBytes = (ASN1OctetString)new DEROctetString(Arrays.clone(paramArrayOfbyte));
  }
  
  private SecretKeyData(ASN1Sequence paramASN1Sequence) {
    this.keyAlgorithm = ASN1ObjectIdentifier.getInstance(paramASN1Sequence.getObjectAt(0));
    this.keyBytes = ASN1OctetString.getInstance(paramASN1Sequence.getObjectAt(1));
  }
  
  public static SecretKeyData getInstance(Object paramObject) {
    return (paramObject instanceof SecretKeyData) ? (SecretKeyData)paramObject : ((paramObject != null) ? new SecretKeyData(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public byte[] getKeyBytes() {
    return Arrays.clone(this.keyBytes.getOctets());
  }
  
  public ASN1ObjectIdentifier getKeyAlgorithm() {
    return this.keyAlgorithm;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.keyAlgorithm);
    aSN1EncodableVector.add((ASN1Encodable)this.keyBytes);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
