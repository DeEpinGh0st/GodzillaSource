package org.bouncycastle.asn1.cmc;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class PublishTrustAnchors extends ASN1Object {
  private final ASN1Integer seqNumber;
  
  private final AlgorithmIdentifier hashAlgorithm;
  
  private final ASN1Sequence anchorHashes;
  
  public PublishTrustAnchors(BigInteger paramBigInteger, AlgorithmIdentifier paramAlgorithmIdentifier, byte[][] paramArrayOfbyte) {
    this.seqNumber = new ASN1Integer(paramBigInteger);
    this.hashAlgorithm = paramAlgorithmIdentifier;
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    for (byte b = 0; b != paramArrayOfbyte.length; b++)
      aSN1EncodableVector.add((ASN1Encodable)new DEROctetString(Arrays.clone(paramArrayOfbyte[b]))); 
    this.anchorHashes = (ASN1Sequence)new DERSequence(aSN1EncodableVector);
  }
  
  private PublishTrustAnchors(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 3)
      throw new IllegalArgumentException("incorrect sequence size"); 
    this.seqNumber = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(0));
    this.hashAlgorithm = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(1));
    this.anchorHashes = ASN1Sequence.getInstance(paramASN1Sequence.getObjectAt(2));
  }
  
  public static PublishTrustAnchors getInstance(Object paramObject) {
    return (paramObject instanceof PublishTrustAnchors) ? (PublishTrustAnchors)paramObject : ((paramObject != null) ? new PublishTrustAnchors(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public BigInteger getSeqNumber() {
    return this.seqNumber.getValue();
  }
  
  public AlgorithmIdentifier getHashAlgorithm() {
    return this.hashAlgorithm;
  }
  
  public byte[][] getAnchorHashes() {
    byte[][] arrayOfByte = new byte[this.anchorHashes.size()][];
    for (byte b = 0; b != arrayOfByte.length; b++)
      arrayOfByte[b] = Arrays.clone(ASN1OctetString.getInstance(this.anchorHashes.getObjectAt(b)).getOctets()); 
    return arrayOfByte;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.seqNumber);
    aSN1EncodableVector.add((ASN1Encodable)this.hashAlgorithm);
    aSN1EncodableVector.add((ASN1Encodable)this.anchorHashes);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
