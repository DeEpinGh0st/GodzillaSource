package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class DigestedData extends ASN1Object {
  private ASN1Integer version = new ASN1Integer(0L);
  
  private AlgorithmIdentifier digestAlgorithm;
  
  private ContentInfo encapContentInfo;
  
  private ASN1OctetString digest;
  
  public DigestedData(AlgorithmIdentifier paramAlgorithmIdentifier, ContentInfo paramContentInfo, byte[] paramArrayOfbyte) {
    this.digestAlgorithm = paramAlgorithmIdentifier;
    this.encapContentInfo = paramContentInfo;
    this.digest = (ASN1OctetString)new DEROctetString(paramArrayOfbyte);
  }
  
  private DigestedData(ASN1Sequence paramASN1Sequence) {
    this.digestAlgorithm = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(1));
    this.encapContentInfo = ContentInfo.getInstance(paramASN1Sequence.getObjectAt(2));
    this.digest = ASN1OctetString.getInstance(paramASN1Sequence.getObjectAt(3));
  }
  
  public static DigestedData getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static DigestedData getInstance(Object paramObject) {
    return (paramObject instanceof DigestedData) ? (DigestedData)paramObject : ((paramObject != null) ? new DigestedData(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1Integer getVersion() {
    return this.version;
  }
  
  public AlgorithmIdentifier getDigestAlgorithm() {
    return this.digestAlgorithm;
  }
  
  public ContentInfo getEncapContentInfo() {
    return this.encapContentInfo;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.version);
    aSN1EncodableVector.add((ASN1Encodable)this.digestAlgorithm);
    aSN1EncodableVector.add((ASN1Encodable)this.encapContentInfo);
    aSN1EncodableVector.add((ASN1Encodable)this.digest);
    return (ASN1Primitive)new BERSequence(aSN1EncodableVector);
  }
  
  public byte[] getDigest() {
    return this.digest.getOctets();
  }
}
