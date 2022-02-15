package org.bouncycastle.pqc.asn1;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class SPHINCS256KeyParams extends ASN1Object {
  private final ASN1Integer version = new ASN1Integer(0L);
  
  private final AlgorithmIdentifier treeDigest;
  
  public SPHINCS256KeyParams(AlgorithmIdentifier paramAlgorithmIdentifier) {
    this.treeDigest = paramAlgorithmIdentifier;
  }
  
  private SPHINCS256KeyParams(ASN1Sequence paramASN1Sequence) {
    this.treeDigest = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(1));
  }
  
  public static final SPHINCS256KeyParams getInstance(Object paramObject) {
    return (paramObject instanceof SPHINCS256KeyParams) ? (SPHINCS256KeyParams)paramObject : ((paramObject != null) ? new SPHINCS256KeyParams(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public AlgorithmIdentifier getTreeDigest() {
    return this.treeDigest;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.version);
    aSN1EncodableVector.add((ASN1Encodable)this.treeDigest);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
