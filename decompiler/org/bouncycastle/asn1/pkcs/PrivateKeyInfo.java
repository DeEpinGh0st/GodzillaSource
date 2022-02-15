package org.bouncycastle.asn1.pkcs;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class PrivateKeyInfo extends ASN1Object {
  private ASN1OctetString privKey;
  
  private AlgorithmIdentifier algId;
  
  private ASN1Set attributes;
  
  public static PrivateKeyInfo getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static PrivateKeyInfo getInstance(Object paramObject) {
    return (paramObject instanceof PrivateKeyInfo) ? (PrivateKeyInfo)paramObject : ((paramObject != null) ? new PrivateKeyInfo(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public PrivateKeyInfo(AlgorithmIdentifier paramAlgorithmIdentifier, ASN1Encodable paramASN1Encodable) throws IOException {
    this(paramAlgorithmIdentifier, paramASN1Encodable, null);
  }
  
  public PrivateKeyInfo(AlgorithmIdentifier paramAlgorithmIdentifier, ASN1Encodable paramASN1Encodable, ASN1Set paramASN1Set) throws IOException {
    this.privKey = (ASN1OctetString)new DEROctetString(paramASN1Encodable.toASN1Primitive().getEncoded("DER"));
    this.algId = paramAlgorithmIdentifier;
    this.attributes = paramASN1Set;
  }
  
  public PrivateKeyInfo(ASN1Sequence paramASN1Sequence) {
    Enumeration<ASN1Integer> enumeration = paramASN1Sequence.getObjects();
    BigInteger bigInteger = ((ASN1Integer)enumeration.nextElement()).getValue();
    if (bigInteger.intValue() != 0)
      throw new IllegalArgumentException("wrong version for private key info"); 
    this.algId = AlgorithmIdentifier.getInstance(enumeration.nextElement());
    this.privKey = ASN1OctetString.getInstance(enumeration.nextElement());
    if (enumeration.hasMoreElements())
      this.attributes = ASN1Set.getInstance((ASN1TaggedObject)enumeration.nextElement(), false); 
  }
  
  public AlgorithmIdentifier getPrivateKeyAlgorithm() {
    return this.algId;
  }
  
  public AlgorithmIdentifier getAlgorithmId() {
    return this.algId;
  }
  
  public ASN1Encodable parsePrivateKey() throws IOException {
    return (ASN1Encodable)ASN1Primitive.fromByteArray(this.privKey.getOctets());
  }
  
  public ASN1Primitive getPrivateKey() {
    try {
      return parsePrivateKey().toASN1Primitive();
    } catch (IOException iOException) {
      throw new IllegalStateException("unable to parse private key");
    } 
  }
  
  public ASN1Set getAttributes() {
    return this.attributes;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(0L));
    aSN1EncodableVector.add((ASN1Encodable)this.algId);
    aSN1EncodableVector.add((ASN1Encodable)this.privKey);
    if (this.attributes != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.attributes)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
