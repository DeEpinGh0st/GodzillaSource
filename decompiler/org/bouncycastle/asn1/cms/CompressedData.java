package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class CompressedData extends ASN1Object {
  private ASN1Integer version = new ASN1Integer(0L);
  
  private AlgorithmIdentifier compressionAlgorithm;
  
  private ContentInfo encapContentInfo;
  
  public CompressedData(AlgorithmIdentifier paramAlgorithmIdentifier, ContentInfo paramContentInfo) {
    this.compressionAlgorithm = paramAlgorithmIdentifier;
    this.encapContentInfo = paramContentInfo;
  }
  
  private CompressedData(ASN1Sequence paramASN1Sequence) {
    this.compressionAlgorithm = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(1));
    this.encapContentInfo = ContentInfo.getInstance(paramASN1Sequence.getObjectAt(2));
  }
  
  public static CompressedData getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static CompressedData getInstance(Object paramObject) {
    return (paramObject instanceof CompressedData) ? (CompressedData)paramObject : ((paramObject != null) ? new CompressedData(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1Integer getVersion() {
    return this.version;
  }
  
  public AlgorithmIdentifier getCompressionAlgorithmIdentifier() {
    return this.compressionAlgorithm;
  }
  
  public ContentInfo getEncapContentInfo() {
    return this.encapContentInfo;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.version);
    aSN1EncodableVector.add((ASN1Encodable)this.compressionAlgorithm);
    aSN1EncodableVector.add((ASN1Encodable)this.encapContentInfo);
    return (ASN1Primitive)new BERSequence(aSN1EncodableVector);
  }
}
