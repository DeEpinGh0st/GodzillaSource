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

public class DecryptedPOP extends ASN1Object {
  private final BodyPartID bodyPartID;
  
  private final AlgorithmIdentifier thePOPAlgID;
  
  private final byte[] thePOP;
  
  public DecryptedPOP(BodyPartID paramBodyPartID, AlgorithmIdentifier paramAlgorithmIdentifier, byte[] paramArrayOfbyte) {
    this.bodyPartID = paramBodyPartID;
    this.thePOPAlgID = paramAlgorithmIdentifier;
    this.thePOP = Arrays.clone(paramArrayOfbyte);
  }
  
  private DecryptedPOP(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 3)
      throw new IllegalArgumentException("incorrect sequence size"); 
    this.bodyPartID = BodyPartID.getInstance(paramASN1Sequence.getObjectAt(0));
    this.thePOPAlgID = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(1));
    this.thePOP = Arrays.clone(ASN1OctetString.getInstance(paramASN1Sequence.getObjectAt(2)).getOctets());
  }
  
  public static DecryptedPOP getInstance(Object paramObject) {
    return (paramObject instanceof DecryptedPOP) ? (DecryptedPOP)paramObject : ((paramObject != null) ? new DecryptedPOP(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public BodyPartID getBodyPartID() {
    return this.bodyPartID;
  }
  
  public AlgorithmIdentifier getThePOPAlgID() {
    return this.thePOPAlgID;
  }
  
  public byte[] getThePOP() {
    return Arrays.clone(this.thePOP);
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.bodyPartID);
    aSN1EncodableVector.add((ASN1Encodable)this.thePOPAlgID);
    aSN1EncodableVector.add((ASN1Encodable)new DEROctetString(this.thePOP));
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
