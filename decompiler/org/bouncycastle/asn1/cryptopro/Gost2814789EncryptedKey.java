package org.bouncycastle.asn1.cryptopro;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.util.Arrays;

public class Gost2814789EncryptedKey extends ASN1Object {
  private final byte[] encryptedKey;
  
  private final byte[] maskKey;
  
  private final byte[] macKey;
  
  private Gost2814789EncryptedKey(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() == 2) {
      this.encryptedKey = Arrays.clone(ASN1OctetString.getInstance(paramASN1Sequence.getObjectAt(0)).getOctets());
      this.macKey = Arrays.clone(ASN1OctetString.getInstance(paramASN1Sequence.getObjectAt(1)).getOctets());
      this.maskKey = null;
    } else if (paramASN1Sequence.size() == 3) {
      this.encryptedKey = Arrays.clone(ASN1OctetString.getInstance(paramASN1Sequence.getObjectAt(0)).getOctets());
      this.maskKey = Arrays.clone(ASN1OctetString.getInstance(ASN1TaggedObject.getInstance(paramASN1Sequence.getObjectAt(1)), false).getOctets());
      this.macKey = Arrays.clone(ASN1OctetString.getInstance(paramASN1Sequence.getObjectAt(2)).getOctets());
    } else {
      throw new IllegalArgumentException("unknown sequence length: " + paramASN1Sequence.size());
    } 
  }
  
  public static Gost2814789EncryptedKey getInstance(Object paramObject) {
    return (paramObject instanceof Gost2814789EncryptedKey) ? (Gost2814789EncryptedKey)paramObject : ((paramObject != null) ? new Gost2814789EncryptedKey(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public Gost2814789EncryptedKey(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    this(paramArrayOfbyte1, null, paramArrayOfbyte2);
  }
  
  public Gost2814789EncryptedKey(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3) {
    this.encryptedKey = Arrays.clone(paramArrayOfbyte1);
    this.maskKey = Arrays.clone(paramArrayOfbyte2);
    this.macKey = Arrays.clone(paramArrayOfbyte3);
  }
  
  public byte[] getEncryptedKey() {
    return this.encryptedKey;
  }
  
  public byte[] getMaskKey() {
    return this.maskKey;
  }
  
  public byte[] getMacKey() {
    return this.macKey;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)new DEROctetString(this.encryptedKey));
    if (this.maskKey != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)new DEROctetString(this.encryptedKey))); 
    aSN1EncodableVector.add((ASN1Encodable)new DEROctetString(this.macKey));
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
