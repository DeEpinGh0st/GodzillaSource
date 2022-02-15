package org.bouncycastle.pqc.asn1;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.util.Arrays;

public class XMSSPrivateKey extends ASN1Object {
  private final int index;
  
  private final byte[] secretKeySeed;
  
  private final byte[] secretKeyPRF;
  
  private final byte[] publicSeed;
  
  private final byte[] root;
  
  private final byte[] bdsState;
  
  public XMSSPrivateKey(int paramInt, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3, byte[] paramArrayOfbyte4, byte[] paramArrayOfbyte5) {
    this.index = paramInt;
    this.secretKeySeed = Arrays.clone(paramArrayOfbyte1);
    this.secretKeyPRF = Arrays.clone(paramArrayOfbyte2);
    this.publicSeed = Arrays.clone(paramArrayOfbyte3);
    this.root = Arrays.clone(paramArrayOfbyte4);
    this.bdsState = Arrays.clone(paramArrayOfbyte5);
  }
  
  private XMSSPrivateKey(ASN1Sequence paramASN1Sequence) {
    if (!ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(0)).getValue().equals(BigInteger.valueOf(0L)))
      throw new IllegalArgumentException("unknown version of sequence"); 
    if (paramASN1Sequence.size() != 2 && paramASN1Sequence.size() != 3)
      throw new IllegalArgumentException("key sequence wrong size"); 
    ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(paramASN1Sequence.getObjectAt(1));
    this.index = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0)).getValue().intValue();
    this.secretKeySeed = Arrays.clone(DEROctetString.getInstance(aSN1Sequence.getObjectAt(1)).getOctets());
    this.secretKeyPRF = Arrays.clone(DEROctetString.getInstance(aSN1Sequence.getObjectAt(2)).getOctets());
    this.publicSeed = Arrays.clone(DEROctetString.getInstance(aSN1Sequence.getObjectAt(3)).getOctets());
    this.root = Arrays.clone(DEROctetString.getInstance(aSN1Sequence.getObjectAt(4)).getOctets());
    if (paramASN1Sequence.size() == 3) {
      this.bdsState = Arrays.clone(DEROctetString.getInstance(ASN1TaggedObject.getInstance(paramASN1Sequence.getObjectAt(2)), true).getOctets());
    } else {
      this.bdsState = null;
    } 
  }
  
  public static XMSSPrivateKey getInstance(Object paramObject) {
    return (paramObject instanceof XMSSPrivateKey) ? (XMSSPrivateKey)paramObject : ((paramObject != null) ? new XMSSPrivateKey(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public int getIndex() {
    return this.index;
  }
  
  public byte[] getSecretKeySeed() {
    return Arrays.clone(this.secretKeySeed);
  }
  
  public byte[] getSecretKeyPRF() {
    return Arrays.clone(this.secretKeyPRF);
  }
  
  public byte[] getPublicSeed() {
    return Arrays.clone(this.publicSeed);
  }
  
  public byte[] getRoot() {
    return Arrays.clone(this.root);
  }
  
  public byte[] getBdsState() {
    return Arrays.clone(this.bdsState);
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector1 = new ASN1EncodableVector();
    aSN1EncodableVector1.add((ASN1Encodable)new ASN1Integer(0L));
    ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
    aSN1EncodableVector2.add((ASN1Encodable)new ASN1Integer(this.index));
    aSN1EncodableVector2.add((ASN1Encodable)new DEROctetString(this.secretKeySeed));
    aSN1EncodableVector2.add((ASN1Encodable)new DEROctetString(this.secretKeyPRF));
    aSN1EncodableVector2.add((ASN1Encodable)new DEROctetString(this.publicSeed));
    aSN1EncodableVector2.add((ASN1Encodable)new DEROctetString(this.root));
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector2));
    aSN1EncodableVector1.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)new DEROctetString(this.bdsState)));
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector1);
  }
}
