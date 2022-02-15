package org.bouncycastle.pqc.asn1;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class XMSSMTPublicKey extends ASN1Object {
  private final byte[] publicSeed;
  
  private final byte[] root;
  
  public XMSSMTPublicKey(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    this.publicSeed = Arrays.clone(paramArrayOfbyte1);
    this.root = Arrays.clone(paramArrayOfbyte2);
  }
  
  private XMSSMTPublicKey(ASN1Sequence paramASN1Sequence) {
    if (!ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(0)).getValue().equals(BigInteger.valueOf(0L)))
      throw new IllegalArgumentException("unknown version of sequence"); 
    this.publicSeed = Arrays.clone(DEROctetString.getInstance(paramASN1Sequence.getObjectAt(1)).getOctets());
    this.root = Arrays.clone(DEROctetString.getInstance(paramASN1Sequence.getObjectAt(2)).getOctets());
  }
  
  public static XMSSMTPublicKey getInstance(Object paramObject) {
    return (paramObject instanceof XMSSMTPublicKey) ? (XMSSMTPublicKey)paramObject : ((paramObject != null) ? new XMSSMTPublicKey(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public byte[] getPublicSeed() {
    return Arrays.clone(this.publicSeed);
  }
  
  public byte[] getRoot() {
    return Arrays.clone(this.root);
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(0L));
    aSN1EncodableVector.add((ASN1Encodable)new DEROctetString(this.publicSeed));
    aSN1EncodableVector.add((ASN1Encodable)new DEROctetString(this.root));
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
