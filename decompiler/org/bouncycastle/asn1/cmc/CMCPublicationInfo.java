package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.crmf.PKIPublicationInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class CMCPublicationInfo extends ASN1Object {
  private final AlgorithmIdentifier hashAlg;
  
  private final ASN1Sequence certHashes;
  
  private final PKIPublicationInfo pubInfo;
  
  public CMCPublicationInfo(AlgorithmIdentifier paramAlgorithmIdentifier, byte[][] paramArrayOfbyte, PKIPublicationInfo paramPKIPublicationInfo) {
    this.hashAlg = paramAlgorithmIdentifier;
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    for (byte b = 0; b != paramArrayOfbyte.length; b++)
      aSN1EncodableVector.add((ASN1Encodable)new DEROctetString(Arrays.clone(paramArrayOfbyte[b]))); 
    this.certHashes = (ASN1Sequence)new DERSequence(aSN1EncodableVector);
    this.pubInfo = paramPKIPublicationInfo;
  }
  
  private CMCPublicationInfo(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 3)
      throw new IllegalArgumentException("incorrect sequence size"); 
    this.hashAlg = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(0));
    this.certHashes = ASN1Sequence.getInstance(paramASN1Sequence.getObjectAt(1));
    this.pubInfo = PKIPublicationInfo.getInstance(paramASN1Sequence.getObjectAt(2));
  }
  
  public static CMCPublicationInfo getInstance(Object paramObject) {
    return (paramObject instanceof CMCPublicationInfo) ? (CMCPublicationInfo)paramObject : ((paramObject != null) ? new CMCPublicationInfo(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public AlgorithmIdentifier getHashAlg() {
    return this.hashAlg;
  }
  
  public byte[][] getCertHashes() {
    byte[][] arrayOfByte = new byte[this.certHashes.size()][];
    for (byte b = 0; b != arrayOfByte.length; b++)
      arrayOfByte[b] = Arrays.clone(ASN1OctetString.getInstance(this.certHashes.getObjectAt(b)).getOctets()); 
    return arrayOfByte;
  }
  
  public PKIPublicationInfo getPubInfo() {
    return this.pubInfo;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.hashAlg);
    aSN1EncodableVector.add((ASN1Encodable)this.certHashes);
    aSN1EncodableVector.add((ASN1Encodable)this.pubInfo);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
