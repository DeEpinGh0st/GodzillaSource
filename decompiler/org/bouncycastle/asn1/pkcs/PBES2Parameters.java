package org.bouncycastle.asn1.pkcs;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class PBES2Parameters extends ASN1Object implements PKCSObjectIdentifiers {
  private KeyDerivationFunc func;
  
  private EncryptionScheme scheme;
  
  public static PBES2Parameters getInstance(Object paramObject) {
    return (paramObject instanceof PBES2Parameters) ? (PBES2Parameters)paramObject : ((paramObject != null) ? new PBES2Parameters(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public PBES2Parameters(KeyDerivationFunc paramKeyDerivationFunc, EncryptionScheme paramEncryptionScheme) {
    this.func = paramKeyDerivationFunc;
    this.scheme = paramEncryptionScheme;
  }
  
  private PBES2Parameters(ASN1Sequence paramASN1Sequence) {
    Enumeration<ASN1Encodable> enumeration = paramASN1Sequence.getObjects();
    ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(((ASN1Encodable)enumeration.nextElement()).toASN1Primitive());
    if (aSN1Sequence.getObjectAt(0).equals(id_PBKDF2)) {
      this.func = new KeyDerivationFunc(id_PBKDF2, (ASN1Encodable)PBKDF2Params.getInstance(aSN1Sequence.getObjectAt(1)));
    } else {
      this.func = KeyDerivationFunc.getInstance(aSN1Sequence);
    } 
    this.scheme = EncryptionScheme.getInstance(enumeration.nextElement());
  }
  
  public KeyDerivationFunc getKeyDerivationFunc() {
    return this.func;
  }
  
  public EncryptionScheme getEncryptionScheme() {
    return this.scheme;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.func);
    aSN1EncodableVector.add((ASN1Encodable)this.scheme);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
