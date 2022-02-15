package org.bouncycastle.asn1.tsp;

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

public class MessageImprint extends ASN1Object {
  AlgorithmIdentifier hashAlgorithm;
  
  byte[] hashedMessage;
  
  public static MessageImprint getInstance(Object paramObject) {
    return (paramObject instanceof MessageImprint) ? (MessageImprint)paramObject : ((paramObject != null) ? new MessageImprint(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private MessageImprint(ASN1Sequence paramASN1Sequence) {
    this.hashAlgorithm = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(0));
    this.hashedMessage = ASN1OctetString.getInstance(paramASN1Sequence.getObjectAt(1)).getOctets();
  }
  
  public MessageImprint(AlgorithmIdentifier paramAlgorithmIdentifier, byte[] paramArrayOfbyte) {
    this.hashAlgorithm = paramAlgorithmIdentifier;
    this.hashedMessage = Arrays.clone(paramArrayOfbyte);
  }
  
  public AlgorithmIdentifier getHashAlgorithm() {
    return this.hashAlgorithm;
  }
  
  public byte[] getHashedMessage() {
    return Arrays.clone(this.hashedMessage);
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.hashAlgorithm);
    aSN1EncodableVector.add((ASN1Encodable)new DEROctetString(this.hashedMessage));
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
