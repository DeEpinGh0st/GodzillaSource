package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class Challenge extends ASN1Object {
  private AlgorithmIdentifier owf;
  
  private ASN1OctetString witness;
  
  private ASN1OctetString challenge;
  
  private Challenge(ASN1Sequence paramASN1Sequence) {
    byte b = 0;
    if (paramASN1Sequence.size() == 3)
      this.owf = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(b++)); 
    this.witness = ASN1OctetString.getInstance(paramASN1Sequence.getObjectAt(b++));
    this.challenge = ASN1OctetString.getInstance(paramASN1Sequence.getObjectAt(b));
  }
  
  public static Challenge getInstance(Object paramObject) {
    return (paramObject instanceof Challenge) ? (Challenge)paramObject : ((paramObject != null) ? new Challenge(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public Challenge(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    this(null, paramArrayOfbyte1, paramArrayOfbyte2);
  }
  
  public Challenge(AlgorithmIdentifier paramAlgorithmIdentifier, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    this.owf = paramAlgorithmIdentifier;
    this.witness = (ASN1OctetString)new DEROctetString(paramArrayOfbyte1);
    this.challenge = (ASN1OctetString)new DEROctetString(paramArrayOfbyte2);
  }
  
  public AlgorithmIdentifier getOwf() {
    return this.owf;
  }
  
  public byte[] getWitness() {
    return this.witness.getOctets();
  }
  
  public byte[] getChallenge() {
    return this.challenge.getOctets();
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    addOptional(aSN1EncodableVector, (ASN1Encodable)this.owf);
    aSN1EncodableVector.add((ASN1Encodable)this.witness);
    aSN1EncodableVector.add((ASN1Encodable)this.challenge);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  private void addOptional(ASN1EncodableVector paramASN1EncodableVector, ASN1Encodable paramASN1Encodable) {
    if (paramASN1Encodable != null)
      paramASN1EncodableVector.add(paramASN1Encodable); 
  }
}
