package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class IdentityProofV2 extends ASN1Object {
  private final AlgorithmIdentifier proofAlgID;
  
  private final AlgorithmIdentifier macAlgId;
  
  private final byte[] witness;
  
  public IdentityProofV2(AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2, byte[] paramArrayOfbyte) {
    this.proofAlgID = paramAlgorithmIdentifier1;
    this.macAlgId = paramAlgorithmIdentifier2;
    this.witness = Arrays.clone(paramArrayOfbyte);
  }
  
  private IdentityProofV2(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 3)
      throw new IllegalArgumentException("incorrect sequence size"); 
    this.proofAlgID = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(0));
    this.macAlgId = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(1));
    this.witness = Arrays.clone(ASN1OctetString.getInstance(paramASN1Sequence.getObjectAt(2)).getOctets());
  }
  
  public static IdentityProofV2 getInstance(Object paramObject) {
    return (paramObject instanceof IdentityProofV2) ? (IdentityProofV2)paramObject : ((paramObject != null) ? new IdentityProofV2(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public AlgorithmIdentifier getProofAlgID() {
    return this.proofAlgID;
  }
  
  public AlgorithmIdentifier getMacAlgId() {
    return this.macAlgId;
  }
  
  public byte[] getWitness() {
    return Arrays.clone(this.witness);
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.proofAlgID);
    aSN1EncodableVector.add((ASN1Encodable)this.macAlgId);
    aSN1EncodableVector.add((ASN1Encodable)new DEROctetString(getWitness()));
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
