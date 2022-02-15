package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class KeyDerivationFunc extends ASN1Object {
  private AlgorithmIdentifier algId;
  
  public KeyDerivationFunc(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Encodable paramASN1Encodable) {
    this.algId = new AlgorithmIdentifier(paramASN1ObjectIdentifier, paramASN1Encodable);
  }
  
  private KeyDerivationFunc(ASN1Sequence paramASN1Sequence) {
    this.algId = AlgorithmIdentifier.getInstance(paramASN1Sequence);
  }
  
  public static KeyDerivationFunc getInstance(Object paramObject) {
    return (paramObject instanceof KeyDerivationFunc) ? (KeyDerivationFunc)paramObject : ((paramObject != null) ? new KeyDerivationFunc(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1ObjectIdentifier getAlgorithm() {
    return this.algId.getAlgorithm();
  }
  
  public ASN1Encodable getParameters() {
    return this.algId.getParameters();
  }
  
  public ASN1Primitive toASN1Primitive() {
    return this.algId.toASN1Primitive();
  }
}
