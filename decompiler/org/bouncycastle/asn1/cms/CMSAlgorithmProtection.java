package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class CMSAlgorithmProtection extends ASN1Object {
  public static final int SIGNATURE = 1;
  
  public static final int MAC = 2;
  
  private final AlgorithmIdentifier digestAlgorithm;
  
  private final AlgorithmIdentifier signatureAlgorithm;
  
  private final AlgorithmIdentifier macAlgorithm;
  
  public CMSAlgorithmProtection(AlgorithmIdentifier paramAlgorithmIdentifier1, int paramInt, AlgorithmIdentifier paramAlgorithmIdentifier2) {
    if (paramAlgorithmIdentifier1 == null || paramAlgorithmIdentifier2 == null)
      throw new NullPointerException("AlgorithmIdentifiers cannot be null"); 
    this.digestAlgorithm = paramAlgorithmIdentifier1;
    if (paramInt == 1) {
      this.signatureAlgorithm = paramAlgorithmIdentifier2;
      this.macAlgorithm = null;
    } else if (paramInt == 2) {
      this.signatureAlgorithm = null;
      this.macAlgorithm = paramAlgorithmIdentifier2;
    } else {
      throw new IllegalArgumentException("Unknown type: " + paramInt);
    } 
  }
  
  private CMSAlgorithmProtection(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 2)
      throw new IllegalArgumentException("Sequence wrong size: One of signatureAlgorithm or macAlgorithm must be present"); 
    this.digestAlgorithm = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(0));
    ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(paramASN1Sequence.getObjectAt(1));
    if (aSN1TaggedObject.getTagNo() == 1) {
      this.signatureAlgorithm = AlgorithmIdentifier.getInstance(aSN1TaggedObject, false);
      this.macAlgorithm = null;
    } else if (aSN1TaggedObject.getTagNo() == 2) {
      this.signatureAlgorithm = null;
      this.macAlgorithm = AlgorithmIdentifier.getInstance(aSN1TaggedObject, false);
    } else {
      throw new IllegalArgumentException("Unknown tag found: " + aSN1TaggedObject.getTagNo());
    } 
  }
  
  public static CMSAlgorithmProtection getInstance(Object paramObject) {
    return (paramObject instanceof CMSAlgorithmProtection) ? (CMSAlgorithmProtection)paramObject : ((paramObject != null) ? new CMSAlgorithmProtection(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public AlgorithmIdentifier getDigestAlgorithm() {
    return this.digestAlgorithm;
  }
  
  public AlgorithmIdentifier getMacAlgorithm() {
    return this.macAlgorithm;
  }
  
  public AlgorithmIdentifier getSignatureAlgorithm() {
    return this.signatureAlgorithm;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.digestAlgorithm);
    if (this.signatureAlgorithm != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)this.signatureAlgorithm)); 
    if (this.macAlgorithm != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 2, (ASN1Encodable)this.macAlgorithm)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
