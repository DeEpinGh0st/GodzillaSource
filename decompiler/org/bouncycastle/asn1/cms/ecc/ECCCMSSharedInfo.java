package org.bouncycastle.asn1.cms.ecc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class ECCCMSSharedInfo extends ASN1Object {
  private final AlgorithmIdentifier keyInfo;
  
  private final byte[] entityUInfo;
  
  private final byte[] suppPubInfo;
  
  public ECCCMSSharedInfo(AlgorithmIdentifier paramAlgorithmIdentifier, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    this.keyInfo = paramAlgorithmIdentifier;
    this.entityUInfo = Arrays.clone(paramArrayOfbyte1);
    this.suppPubInfo = Arrays.clone(paramArrayOfbyte2);
  }
  
  public ECCCMSSharedInfo(AlgorithmIdentifier paramAlgorithmIdentifier, byte[] paramArrayOfbyte) {
    this.keyInfo = paramAlgorithmIdentifier;
    this.entityUInfo = null;
    this.suppPubInfo = Arrays.clone(paramArrayOfbyte);
  }
  
  private ECCCMSSharedInfo(ASN1Sequence paramASN1Sequence) {
    this.keyInfo = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(0));
    if (paramASN1Sequence.size() == 2) {
      this.entityUInfo = null;
      this.suppPubInfo = ASN1OctetString.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(1), true).getOctets();
    } else {
      this.entityUInfo = ASN1OctetString.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(1), true).getOctets();
      this.suppPubInfo = ASN1OctetString.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(2), true).getOctets();
    } 
  }
  
  public static ECCCMSSharedInfo getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static ECCCMSSharedInfo getInstance(Object paramObject) {
    return (paramObject instanceof ECCCMSSharedInfo) ? (ECCCMSSharedInfo)paramObject : ((paramObject != null) ? new ECCCMSSharedInfo(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.keyInfo);
    if (this.entityUInfo != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)new DEROctetString(this.entityUInfo))); 
    aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 2, (ASN1Encodable)new DEROctetString(this.suppPubInfo)));
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
