package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class EncryptedPOP extends ASN1Object {
  private final TaggedRequest request;
  
  private final ContentInfo cms;
  
  private final AlgorithmIdentifier thePOPAlgID;
  
  private final AlgorithmIdentifier witnessAlgID;
  
  private final byte[] witness;
  
  private EncryptedPOP(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 5)
      throw new IllegalArgumentException("incorrect sequence size"); 
    this.request = TaggedRequest.getInstance(paramASN1Sequence.getObjectAt(0));
    this.cms = ContentInfo.getInstance(paramASN1Sequence.getObjectAt(1));
    this.thePOPAlgID = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(2));
    this.witnessAlgID = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(3));
    this.witness = Arrays.clone(ASN1OctetString.getInstance(paramASN1Sequence.getObjectAt(4)).getOctets());
  }
  
  public EncryptedPOP(TaggedRequest paramTaggedRequest, ContentInfo paramContentInfo, AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2, byte[] paramArrayOfbyte) {
    this.request = paramTaggedRequest;
    this.cms = paramContentInfo;
    this.thePOPAlgID = paramAlgorithmIdentifier1;
    this.witnessAlgID = paramAlgorithmIdentifier2;
    this.witness = Arrays.clone(paramArrayOfbyte);
  }
  
  public static EncryptedPOP getInstance(Object paramObject) {
    return (paramObject instanceof EncryptedPOP) ? (EncryptedPOP)paramObject : ((paramObject != null) ? new EncryptedPOP(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public TaggedRequest getRequest() {
    return this.request;
  }
  
  public ContentInfo getCms() {
    return this.cms;
  }
  
  public AlgorithmIdentifier getThePOPAlgID() {
    return this.thePOPAlgID;
  }
  
  public AlgorithmIdentifier getWitnessAlgID() {
    return this.witnessAlgID;
  }
  
  public byte[] getWitness() {
    return Arrays.clone(this.witness);
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.request);
    aSN1EncodableVector.add((ASN1Encodable)this.cms);
    aSN1EncodableVector.add((ASN1Encodable)this.thePOPAlgID);
    aSN1EncodableVector.add((ASN1Encodable)this.witnessAlgID);
    aSN1EncodableVector.add((ASN1Encodable)new DEROctetString(this.witness));
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
