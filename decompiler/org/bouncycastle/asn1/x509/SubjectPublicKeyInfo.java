package org.bouncycastle.asn1.x509;

import java.io.IOException;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;

public class SubjectPublicKeyInfo extends ASN1Object {
  private AlgorithmIdentifier algId;
  
  private DERBitString keyData;
  
  public static SubjectPublicKeyInfo getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static SubjectPublicKeyInfo getInstance(Object paramObject) {
    return (paramObject instanceof SubjectPublicKeyInfo) ? (SubjectPublicKeyInfo)paramObject : ((paramObject != null) ? new SubjectPublicKeyInfo(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public SubjectPublicKeyInfo(AlgorithmIdentifier paramAlgorithmIdentifier, ASN1Encodable paramASN1Encodable) throws IOException {
    this.keyData = new DERBitString(paramASN1Encodable);
    this.algId = paramAlgorithmIdentifier;
  }
  
  public SubjectPublicKeyInfo(AlgorithmIdentifier paramAlgorithmIdentifier, byte[] paramArrayOfbyte) {
    this.keyData = new DERBitString(paramArrayOfbyte);
    this.algId = paramAlgorithmIdentifier;
  }
  
  public SubjectPublicKeyInfo(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 2)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    Enumeration enumeration = paramASN1Sequence.getObjects();
    this.algId = AlgorithmIdentifier.getInstance(enumeration.nextElement());
    this.keyData = DERBitString.getInstance(enumeration.nextElement());
  }
  
  public AlgorithmIdentifier getAlgorithm() {
    return this.algId;
  }
  
  public AlgorithmIdentifier getAlgorithmId() {
    return this.algId;
  }
  
  public ASN1Primitive parsePublicKey() throws IOException {
    ASN1InputStream aSN1InputStream = new ASN1InputStream(this.keyData.getOctets());
    return aSN1InputStream.readObject();
  }
  
  public ASN1Primitive getPublicKey() throws IOException {
    ASN1InputStream aSN1InputStream = new ASN1InputStream(this.keyData.getOctets());
    return aSN1InputStream.readObject();
  }
  
  public DERBitString getPublicKeyData() {
    return this.keyData;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.algId);
    aSN1EncodableVector.add((ASN1Encodable)this.keyData);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
