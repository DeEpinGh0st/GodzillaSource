package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;

public class ObjectDigestInfo extends ASN1Object {
  public static final int publicKey = 0;
  
  public static final int publicKeyCert = 1;
  
  public static final int otherObjectDigest = 2;
  
  ASN1Enumerated digestedObjectType;
  
  ASN1ObjectIdentifier otherObjectTypeID;
  
  AlgorithmIdentifier digestAlgorithm;
  
  DERBitString objectDigest;
  
  public static ObjectDigestInfo getInstance(Object paramObject) {
    return (paramObject instanceof ObjectDigestInfo) ? (ObjectDigestInfo)paramObject : ((paramObject != null) ? new ObjectDigestInfo(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public static ObjectDigestInfo getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public ObjectDigestInfo(int paramInt, ASN1ObjectIdentifier paramASN1ObjectIdentifier, AlgorithmIdentifier paramAlgorithmIdentifier, byte[] paramArrayOfbyte) {
    this.digestedObjectType = new ASN1Enumerated(paramInt);
    if (paramInt == 2)
      this.otherObjectTypeID = paramASN1ObjectIdentifier; 
    this.digestAlgorithm = paramAlgorithmIdentifier;
    this.objectDigest = new DERBitString(paramArrayOfbyte);
  }
  
  private ObjectDigestInfo(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() > 4 || paramASN1Sequence.size() < 3)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    this.digestedObjectType = ASN1Enumerated.getInstance(paramASN1Sequence.getObjectAt(0));
    byte b = 0;
    if (paramASN1Sequence.size() == 4) {
      this.otherObjectTypeID = ASN1ObjectIdentifier.getInstance(paramASN1Sequence.getObjectAt(1));
      b++;
    } 
    this.digestAlgorithm = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(1 + b));
    this.objectDigest = DERBitString.getInstance(paramASN1Sequence.getObjectAt(2 + b));
  }
  
  public ASN1Enumerated getDigestedObjectType() {
    return this.digestedObjectType;
  }
  
  public ASN1ObjectIdentifier getOtherObjectTypeID() {
    return this.otherObjectTypeID;
  }
  
  public AlgorithmIdentifier getDigestAlgorithm() {
    return this.digestAlgorithm;
  }
  
  public DERBitString getObjectDigest() {
    return this.objectDigest;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.digestedObjectType);
    if (this.otherObjectTypeID != null)
      aSN1EncodableVector.add((ASN1Encodable)this.otherObjectTypeID); 
    aSN1EncodableVector.add((ASN1Encodable)this.digestAlgorithm);
    aSN1EncodableVector.add((ASN1Encodable)this.objectDigest);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
