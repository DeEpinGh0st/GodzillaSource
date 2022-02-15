package org.bouncycastle.asn1.bc;

import java.math.BigInteger;
import java.util.Date;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERGeneralizedTime;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class ObjectStoreData extends ASN1Object {
  private final BigInteger version = BigInteger.valueOf(1L);
  
  private final AlgorithmIdentifier integrityAlgorithm;
  
  private final ASN1GeneralizedTime creationDate;
  
  private final ASN1GeneralizedTime lastModifiedDate;
  
  private final ObjectDataSequence objectDataSequence;
  
  private final String comment;
  
  public ObjectStoreData(AlgorithmIdentifier paramAlgorithmIdentifier, Date paramDate1, Date paramDate2, ObjectDataSequence paramObjectDataSequence, String paramString) {
    this.integrityAlgorithm = paramAlgorithmIdentifier;
    this.creationDate = (ASN1GeneralizedTime)new DERGeneralizedTime(paramDate1);
    this.lastModifiedDate = (ASN1GeneralizedTime)new DERGeneralizedTime(paramDate2);
    this.objectDataSequence = paramObjectDataSequence;
    this.comment = paramString;
  }
  
  private ObjectStoreData(ASN1Sequence paramASN1Sequence) {
    this.integrityAlgorithm = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(1));
    this.creationDate = ASN1GeneralizedTime.getInstance(paramASN1Sequence.getObjectAt(2));
    this.lastModifiedDate = ASN1GeneralizedTime.getInstance(paramASN1Sequence.getObjectAt(3));
    this.objectDataSequence = ObjectDataSequence.getInstance(paramASN1Sequence.getObjectAt(4));
    this.comment = (paramASN1Sequence.size() == 6) ? DERUTF8String.getInstance(paramASN1Sequence.getObjectAt(5)).getString() : null;
  }
  
  public static ObjectStoreData getInstance(Object paramObject) {
    return (paramObject instanceof ObjectStoreData) ? (ObjectStoreData)paramObject : ((paramObject != null) ? new ObjectStoreData(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public String getComment() {
    return this.comment;
  }
  
  public ASN1GeneralizedTime getCreationDate() {
    return this.creationDate;
  }
  
  public AlgorithmIdentifier getIntegrityAlgorithm() {
    return this.integrityAlgorithm;
  }
  
  public ASN1GeneralizedTime getLastModifiedDate() {
    return this.lastModifiedDate;
  }
  
  public ObjectDataSequence getObjectDataSequence() {
    return this.objectDataSequence;
  }
  
  public BigInteger getVersion() {
    return this.version;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(this.version));
    aSN1EncodableVector.add((ASN1Encodable)this.integrityAlgorithm);
    aSN1EncodableVector.add((ASN1Encodable)this.creationDate);
    aSN1EncodableVector.add((ASN1Encodable)this.lastModifiedDate);
    aSN1EncodableVector.add((ASN1Encodable)this.objectDataSequence);
    if (this.comment != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERUTF8String(this.comment)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
