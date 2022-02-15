package org.bouncycastle.asn1.pkcs;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class RSASSAPSSparams extends ASN1Object {
  private AlgorithmIdentifier hashAlgorithm = DEFAULT_HASH_ALGORITHM;
  
  private AlgorithmIdentifier maskGenAlgorithm = DEFAULT_MASK_GEN_FUNCTION;
  
  private ASN1Integer saltLength = DEFAULT_SALT_LENGTH;
  
  private ASN1Integer trailerField = DEFAULT_TRAILER_FIELD;
  
  public static final AlgorithmIdentifier DEFAULT_HASH_ALGORITHM = new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, (ASN1Encodable)DERNull.INSTANCE);
  
  public static final AlgorithmIdentifier DEFAULT_MASK_GEN_FUNCTION = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, (ASN1Encodable)DEFAULT_HASH_ALGORITHM);
  
  public static final ASN1Integer DEFAULT_SALT_LENGTH = new ASN1Integer(20L);
  
  public static final ASN1Integer DEFAULT_TRAILER_FIELD = new ASN1Integer(1L);
  
  public static RSASSAPSSparams getInstance(Object paramObject) {
    return (paramObject instanceof RSASSAPSSparams) ? (RSASSAPSSparams)paramObject : ((paramObject != null) ? new RSASSAPSSparams(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public RSASSAPSSparams() {}
  
  public RSASSAPSSparams(AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2, ASN1Integer paramASN1Integer1, ASN1Integer paramASN1Integer2) {}
  
  private RSASSAPSSparams(ASN1Sequence paramASN1Sequence) {
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
          this.saltLength = ASN1Integer.getInstance(aSN1TaggedObject, true);
          break;
        case 3:
          this.trailerField = ASN1Integer.getInstance(aSN1TaggedObject, true);
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
  
  public BigInteger getSaltLength() {
    return this.saltLength.getValue();
  }
  
  public BigInteger getTrailerField() {
    return this.trailerField.getValue();
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (!this.hashAlgorithm.equals(DEFAULT_HASH_ALGORITHM))
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.hashAlgorithm)); 
    if (!this.maskGenAlgorithm.equals(DEFAULT_MASK_GEN_FUNCTION))
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 1, (ASN1Encodable)this.maskGenAlgorithm)); 
    if (!this.saltLength.equals(DEFAULT_SALT_LENGTH))
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 2, (ASN1Encodable)this.saltLength)); 
    if (!this.trailerField.equals(DEFAULT_TRAILER_FIELD))
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 3, (ASN1Encodable)this.trailerField)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
