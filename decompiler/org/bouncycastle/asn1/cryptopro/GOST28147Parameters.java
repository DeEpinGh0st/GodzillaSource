package org.bouncycastle.asn1.cryptopro;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class GOST28147Parameters extends ASN1Object {
  private ASN1OctetString iv;
  
  private ASN1ObjectIdentifier paramSet;
  
  public static GOST28147Parameters getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static GOST28147Parameters getInstance(Object paramObject) {
    return (paramObject instanceof GOST28147Parameters) ? (GOST28147Parameters)paramObject : ((paramObject != null) ? new GOST28147Parameters(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public GOST28147Parameters(byte[] paramArrayOfbyte, ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    this.iv = (ASN1OctetString)new DEROctetString(paramArrayOfbyte);
    this.paramSet = paramASN1ObjectIdentifier;
  }
  
  public GOST28147Parameters(ASN1Sequence paramASN1Sequence) {
    Enumeration<ASN1OctetString> enumeration = paramASN1Sequence.getObjects();
    this.iv = enumeration.nextElement();
    this.paramSet = (ASN1ObjectIdentifier)enumeration.nextElement();
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.iv);
    aSN1EncodableVector.add((ASN1Encodable)this.paramSet);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  public ASN1ObjectIdentifier getEncryptionParamSet() {
    return this.paramSet;
  }
  
  public byte[] getIV() {
    return Arrays.clone(this.iv.getOctets());
  }
}
