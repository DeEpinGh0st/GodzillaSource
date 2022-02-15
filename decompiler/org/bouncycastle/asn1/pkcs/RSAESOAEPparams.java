package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class RSAESOAEPparams extends ASN1Object {
  private AlgorithmIdentifier hashAlgorithm = DEFAULT_HASH_ALGORITHM;
  
  private AlgorithmIdentifier maskGenAlgorithm = DEFAULT_MASK_GEN_FUNCTION;
  
  private AlgorithmIdentifier pSourceAlgorithm = DEFAULT_P_SOURCE_ALGORITHM;
  
  public static final AlgorithmIdentifier DEFAULT_HASH_ALGORITHM = new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, (ASN1Encodable)DERNull.INSTANCE);
  
  public static final AlgorithmIdentifier DEFAULT_MASK_GEN_FUNCTION = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, (ASN1Encodable)DEFAULT_HASH_ALGORITHM);
  
  public static final AlgorithmIdentifier DEFAULT_P_SOURCE_ALGORITHM = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_pSpecified, (ASN1Encodable)new DEROctetString(new byte[0]));
  
  public static RSAESOAEPparams getInstance(Object paramObject) {
    return (paramObject instanceof RSAESOAEPparams) ? (RSAESOAEPparams)paramObject : ((paramObject != null) ? new RSAESOAEPparams(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public RSAESOAEPparams() {}
  
  public RSAESOAEPparams(AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2, AlgorithmIdentifier paramAlgorithmIdentifier3) {}
  
  public RSAESOAEPparams(ASN1Sequence paramASN1Sequence) {
    for (byte b = 0; b != paramASN1Sequence.size(); b++) {
      ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)paramASN1Sequence.getObjectAt(b);
      switch (aSN1TaggedObject.getTagNo()) {
        case 0:
          this.hashAlgorithm = AlgorithmIdentifier.getInstance(aSN1TaggedObject, true);
          break;
        case 1:
          this.maskGenAlgorithm = AlgorithmIdentifier.getInstance(aSN1TaggedObject, true);
          break;
        case 2:
          this.pSourceAlgorithm = AlgorithmIdentifier.getInstance(aSN1TaggedObject, true);
          break;
        default:
          throw new IllegalArgumentException("unknown tag");
      } 
    } 
  }
  
  public AlgorithmIdentifier getHashAlgorithm() {
    return this.hashAlgorithm;
  }
  
  public AlgorithmIdentifier getMaskGenAlgorithm() {
    return this.maskGenAlgorithm;
  }
  
  public AlgorithmIdentifier getPSourceAlgorithm() {
    return this.pSourceAlgorithm;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (!this.hashAlgorithm.equals(DEFAULT_HASH_ALGORITHM))
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.hashAlgorithm)); 
    if (!this.maskGenAlgorithm.equals(DEFAULT_MASK_GEN_FUNCTION))
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 1, (ASN1Encodable)this.maskGenAlgorithm)); 
    if (!this.pSourceAlgorithm.equals(DEFAULT_P_SOURCE_ALGORITHM))
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 2, (ASN1Encodable)this.pSourceAlgorithm)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
