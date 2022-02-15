package org.bouncycastle.asn1.x9;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class KeySpecificInfo extends ASN1Object {
  private ASN1ObjectIdentifier algorithm;
  
  private ASN1OctetString counter;
  
  public KeySpecificInfo(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1OctetString paramASN1OctetString) {
    this.algorithm = paramASN1ObjectIdentifier;
    this.counter = paramASN1OctetString;
  }
  
  public static KeySpecificInfo getInstance(Object paramObject) {
    return (paramObject instanceof KeySpecificInfo) ? (KeySpecificInfo)paramObject : ((paramObject != null) ? new KeySpecificInfo(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private KeySpecificInfo(ASN1Sequence paramASN1Sequence) {
    Enumeration<ASN1ObjectIdentifier> enumeration = paramASN1Sequence.getObjects();
    this.algorithm = enumeration.nextElement();
    this.counter = (ASN1OctetString)enumeration.nextElement();
  }
  
  public ASN1ObjectIdentifier getAlgorithm() {
    return this.algorithm;
  }
  
  public ASN1OctetString getCounter() {
    return this.counter;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.algorithm);
    aSN1EncodableVector.add((ASN1Encodable)this.counter);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
