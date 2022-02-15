package org.bouncycastle.asn1.cmp;

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

public class PBMParameter extends ASN1Object {
  private ASN1OctetString salt;
  
  private AlgorithmIdentifier owf;
  
  private ASN1Integer iterationCount;
  
  private AlgorithmIdentifier mac;
  
  private PBMParameter(ASN1Sequence paramASN1Sequence) {
    this.salt = ASN1OctetString.getInstance(paramASN1Sequence.getObjectAt(0));
    this.owf = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(1));
    this.iterationCount = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(2));
    this.mac = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(3));
  }
  
  public static PBMParameter getInstance(Object paramObject) {
    return (paramObject instanceof PBMParameter) ? (PBMParameter)paramObject : ((paramObject != null) ? new PBMParameter(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public PBMParameter(byte[] paramArrayOfbyte, AlgorithmIdentifier paramAlgorithmIdentifier1, int paramInt, AlgorithmIdentifier paramAlgorithmIdentifier2) {
    this((ASN1OctetString)new DEROctetString(paramArrayOfbyte), paramAlgorithmIdentifier1, new ASN1Integer(paramInt), paramAlgorithmIdentifier2);
  }
  
  public PBMParameter(ASN1OctetString paramASN1OctetString, AlgorithmIdentifier paramAlgorithmIdentifier1, ASN1Integer paramASN1Integer, AlgorithmIdentifier paramAlgorithmIdentifier2) {
    this.salt = paramASN1OctetString;
    this.owf = paramAlgorithmIdentifier1;
    this.iterationCount = paramASN1Integer;
    this.mac = paramAlgorithmIdentifier2;
  }
  
  public ASN1OctetString getSalt() {
    return this.salt;
  }
  
  public AlgorithmIdentifier getOwf() {
    return this.owf;
  }
  
  public ASN1Integer getIterationCount() {
    return this.iterationCount;
  }
  
  public AlgorithmIdentifier getMac() {
    return this.mac;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.salt);
    aSN1EncodableVector.add((ASN1Encodable)this.owf);
    aSN1EncodableVector.add((ASN1Encodable)this.iterationCount);
    aSN1EncodableVector.add((ASN1Encodable)this.mac);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
