package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class EncryptedValue extends ASN1Object {
  private AlgorithmIdentifier intendedAlg;
  
  private AlgorithmIdentifier symmAlg;
  
  private DERBitString encSymmKey;
  
  private AlgorithmIdentifier keyAlg;
  
  private ASN1OctetString valueHint;
  
  private DERBitString encValue;
  
  private EncryptedValue(ASN1Sequence paramASN1Sequence) {
    byte b;
    for (b = 0; paramASN1Sequence.getObjectAt(b) instanceof ASN1TaggedObject; b++) {
      ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)paramASN1Sequence.getObjectAt(b);
      switch (aSN1TaggedObject.getTagNo()) {
        case 0:
          this.intendedAlg = AlgorithmIdentifier.getInstance(aSN1TaggedObject, false);
          break;
        case 1:
          this.symmAlg = AlgorithmIdentifier.getInstance(aSN1TaggedObject, false);
          break;
        case 2:
          this.encSymmKey = DERBitString.getInstance(aSN1TaggedObject, false);
          break;
        case 3:
          this.keyAlg = AlgorithmIdentifier.getInstance(aSN1TaggedObject, false);
          break;
        case 4:
          this.valueHint = ASN1OctetString.getInstance(aSN1TaggedObject, false);
          break;
        default:
          throw new IllegalArgumentException("Unknown tag encountered: " + aSN1TaggedObject.getTagNo());
      } 
    } 
    this.encValue = DERBitString.getInstance(paramASN1Sequence.getObjectAt(b));
  }
  
  public static EncryptedValue getInstance(Object paramObject) {
    return (paramObject instanceof EncryptedValue) ? (EncryptedValue)paramObject : ((paramObject != null) ? new EncryptedValue(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public EncryptedValue(AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2, DERBitString paramDERBitString1, AlgorithmIdentifier paramAlgorithmIdentifier3, ASN1OctetString paramASN1OctetString, DERBitString paramDERBitString2) {
    if (paramDERBitString2 == null)
      throw new IllegalArgumentException("'encValue' cannot be null"); 
    this.intendedAlg = paramAlgorithmIdentifier1;
    this.symmAlg = paramAlgorithmIdentifier2;
    this.encSymmKey = paramDERBitString1;
    this.keyAlg = paramAlgorithmIdentifier3;
    this.valueHint = paramASN1OctetString;
    this.encValue = paramDERBitString2;
  }
  
  public AlgorithmIdentifier getIntendedAlg() {
    return this.intendedAlg;
  }
  
  public AlgorithmIdentifier getSymmAlg() {
    return this.symmAlg;
  }
  
  public DERBitString getEncSymmKey() {
    return this.encSymmKey;
  }
  
  public AlgorithmIdentifier getKeyAlg() {
    return this.keyAlg;
  }
  
  public ASN1OctetString getValueHint() {
    return this.valueHint;
  }
  
  public DERBitString getEncValue() {
    return this.encValue;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    addOptional(aSN1EncodableVector, 0, (ASN1Encodable)this.intendedAlg);
    addOptional(aSN1EncodableVector, 1, (ASN1Encodable)this.symmAlg);
    addOptional(aSN1EncodableVector, 2, (ASN1Encodable)this.encSymmKey);
    addOptional(aSN1EncodableVector, 3, (ASN1Encodable)this.keyAlg);
    addOptional(aSN1EncodableVector, 4, (ASN1Encodable)this.valueHint);
    aSN1EncodableVector.add((ASN1Encodable)this.encValue);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  private void addOptional(ASN1EncodableVector paramASN1EncodableVector, int paramInt, ASN1Encodable paramASN1Encodable) {
    if (paramASN1Encodable != null)
      paramASN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, paramInt, paramASN1Encodable)); 
  }
}
