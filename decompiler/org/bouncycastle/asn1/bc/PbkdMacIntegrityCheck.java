package org.bouncycastle.asn1.bc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.KeyDerivationFunc;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class PbkdMacIntegrityCheck extends ASN1Object {
  private final AlgorithmIdentifier macAlgorithm;
  
  private final KeyDerivationFunc pbkdAlgorithm;
  
  private final ASN1OctetString mac;
  
  public PbkdMacIntegrityCheck(AlgorithmIdentifier paramAlgorithmIdentifier, KeyDerivationFunc paramKeyDerivationFunc, byte[] paramArrayOfbyte) {
    this.macAlgorithm = paramAlgorithmIdentifier;
    this.pbkdAlgorithm = paramKeyDerivationFunc;
    this.mac = (ASN1OctetString)new DEROctetString(Arrays.clone(paramArrayOfbyte));
  }
  
  private PbkdMacIntegrityCheck(ASN1Sequence paramASN1Sequence) {
    this.macAlgorithm = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(0));
    this.pbkdAlgorithm = KeyDerivationFunc.getInstance(paramASN1Sequence.getObjectAt(1));
    this.mac = ASN1OctetString.getInstance(paramASN1Sequence.getObjectAt(2));
  }
  
  public static PbkdMacIntegrityCheck getInstance(Object paramObject) {
    return (paramObject instanceof PbkdMacIntegrityCheck) ? (PbkdMacIntegrityCheck)paramObject : ((paramObject != null) ? new PbkdMacIntegrityCheck(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public AlgorithmIdentifier getMacAlgorithm() {
    return this.macAlgorithm;
  }
  
  public KeyDerivationFunc getPbkdAlgorithm() {
    return this.pbkdAlgorithm;
  }
  
  public byte[] getMac() {
    return Arrays.clone(this.mac.getOctets());
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.macAlgorithm);
    aSN1EncodableVector.add((ASN1Encodable)this.pbkdAlgorithm);
    aSN1EncodableVector.add((ASN1Encodable)this.mac);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
