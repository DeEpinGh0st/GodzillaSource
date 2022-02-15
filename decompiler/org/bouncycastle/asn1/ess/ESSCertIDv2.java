package org.bouncycastle.asn1.ess;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.IssuerSerial;
import org.bouncycastle.util.Arrays;

public class ESSCertIDv2 extends ASN1Object {
  private AlgorithmIdentifier hashAlgorithm;
  
  private byte[] certHash;
  
  private IssuerSerial issuerSerial;
  
  private static final AlgorithmIdentifier DEFAULT_ALG_ID = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
  
  public static ESSCertIDv2 getInstance(Object paramObject) {
    return (paramObject instanceof ESSCertIDv2) ? (ESSCertIDv2)paramObject : ((paramObject != null) ? new ESSCertIDv2(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private ESSCertIDv2(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() > 3)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    byte b = 0;
    if (paramASN1Sequence.getObjectAt(0) instanceof ASN1OctetString) {
      this.hashAlgorithm = DEFAULT_ALG_ID;
    } else {
      this.hashAlgorithm = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(b++).toASN1Primitive());
    } 
    this.certHash = ASN1OctetString.getInstance(paramASN1Sequence.getObjectAt(b++).toASN1Primitive()).getOctets();
    if (paramASN1Sequence.size() > b)
      this.issuerSerial = IssuerSerial.getInstance(paramASN1Sequence.getObjectAt(b)); 
  }
  
  public ESSCertIDv2(byte[] paramArrayOfbyte) {
    this(null, paramArrayOfbyte, null);
  }
  
  public ESSCertIDv2(AlgorithmIdentifier paramAlgorithmIdentifier, byte[] paramArrayOfbyte) {
    this(paramAlgorithmIdentifier, paramArrayOfbyte, null);
  }
  
  public ESSCertIDv2(byte[] paramArrayOfbyte, IssuerSerial paramIssuerSerial) {
    this(null, paramArrayOfbyte, paramIssuerSerial);
  }
  
  public ESSCertIDv2(AlgorithmIdentifier paramAlgorithmIdentifier, byte[] paramArrayOfbyte, IssuerSerial paramIssuerSerial) {
    if (paramAlgorithmIdentifier == null) {
      this.hashAlgorithm = DEFAULT_ALG_ID;
    } else {
      this.hashAlgorithm = paramAlgorithmIdentifier;
    } 
    this.certHash = Arrays.clone(paramArrayOfbyte);
    this.issuerSerial = paramIssuerSerial;
  }
  
  public AlgorithmIdentifier getHashAlgorithm() {
    return this.hashAlgorithm;
  }
  
  public byte[] getCertHash() {
    return Arrays.clone(this.certHash);
  }
  
  public IssuerSerial getIssuerSerial() {
    return this.issuerSerial;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (!this.hashAlgorithm.equals(DEFAULT_ALG_ID))
      aSN1EncodableVector.add((ASN1Encodable)this.hashAlgorithm); 
    aSN1EncodableVector.add((ASN1Encodable)(new DEROctetString(this.certHash)).toASN1Primitive());
    if (this.issuerSerial != null)
      aSN1EncodableVector.add((ASN1Encodable)this.issuerSerial); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
