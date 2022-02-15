package org.bouncycastle.asn1.cmc;

import java.io.IOException;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class CertificationRequest extends ASN1Object {
  private static final ASN1Integer ZERO = new ASN1Integer(0L);
  
  private final CertificationRequestInfo certificationRequestInfo;
  
  private final AlgorithmIdentifier signatureAlgorithm;
  
  private final DERBitString signature;
  
  public CertificationRequest(X500Name paramX500Name, AlgorithmIdentifier paramAlgorithmIdentifier1, DERBitString paramDERBitString1, ASN1Set paramASN1Set, AlgorithmIdentifier paramAlgorithmIdentifier2, DERBitString paramDERBitString2) {
    this.certificationRequestInfo = new CertificationRequestInfo(paramX500Name, paramAlgorithmIdentifier1, paramDERBitString1, paramASN1Set);
    this.signatureAlgorithm = paramAlgorithmIdentifier2;
    this.signature = paramDERBitString2;
  }
  
  private CertificationRequest(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 3)
      throw new IllegalArgumentException("incorrect sequence size"); 
    this.certificationRequestInfo = new CertificationRequestInfo(ASN1Sequence.getInstance(paramASN1Sequence.getObjectAt(0)));
    this.signatureAlgorithm = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(1));
    this.signature = DERBitString.getInstance(paramASN1Sequence.getObjectAt(2));
  }
  
  public static CertificationRequest getInstance(Object paramObject) {
    return (paramObject instanceof CertificationRequest) ? (CertificationRequest)paramObject : ((paramObject != null) ? new CertificationRequest(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public BigInteger getVersion() {
    return this.certificationRequestInfo.getVersion().getValue();
  }
  
  public X500Name getSubject() {
    return this.certificationRequestInfo.getSubject();
  }
  
  public ASN1Set getAttributes() {
    return this.certificationRequestInfo.getAttributes();
  }
  
  public AlgorithmIdentifier getSubjectPublicKeyAlgorithm() {
    return AlgorithmIdentifier.getInstance(this.certificationRequestInfo.getSubjectPublicKeyInfo().getObjectAt(0));
  }
  
  public DERBitString getSubjectPublicKey() {
    return DERBitString.getInstance(this.certificationRequestInfo.getSubjectPublicKeyInfo().getObjectAt(1));
  }
  
  public ASN1Primitive parsePublicKey() throws IOException {
    return ASN1Primitive.fromByteArray(getSubjectPublicKey().getOctets());
  }
  
  public AlgorithmIdentifier getSignatureAlgorithm() {
    return this.signatureAlgorithm;
  }
  
  public DERBitString getSignature() {
    return this.signature;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.certificationRequestInfo);
    aSN1EncodableVector.add((ASN1Encodable)this.signatureAlgorithm);
    aSN1EncodableVector.add((ASN1Encodable)this.signature);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  private class CertificationRequestInfo extends ASN1Object {
    private final ASN1Integer version;
    
    private final X500Name subject;
    
    private final ASN1Sequence subjectPublicKeyInfo;
    
    private final ASN1Set attributes;
    
    private CertificationRequestInfo(ASN1Sequence param1ASN1Sequence) {
      if (param1ASN1Sequence.size() != 4)
        throw new IllegalArgumentException("incorrect sequence size for CertificationRequestInfo"); 
      this.version = ASN1Integer.getInstance(param1ASN1Sequence.getObjectAt(0));
      this.subject = X500Name.getInstance(param1ASN1Sequence.getObjectAt(1));
      this.subjectPublicKeyInfo = ASN1Sequence.getInstance(param1ASN1Sequence.getObjectAt(2));
      if (this.subjectPublicKeyInfo.size() != 2)
        throw new IllegalArgumentException("incorrect subjectPublicKeyInfo size for CertificationRequestInfo"); 
      ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)param1ASN1Sequence.getObjectAt(3);
      if (aSN1TaggedObject.getTagNo() != 0)
        throw new IllegalArgumentException("incorrect tag number on attributes for CertificationRequestInfo"); 
      this.attributes = ASN1Set.getInstance(aSN1TaggedObject, false);
    }
    
    private CertificationRequestInfo(X500Name param1X500Name, AlgorithmIdentifier param1AlgorithmIdentifier, DERBitString param1DERBitString, ASN1Set param1ASN1Set) {
      this.version = CertificationRequest.ZERO;
      this.subject = param1X500Name;
      this.subjectPublicKeyInfo = (ASN1Sequence)new DERSequence(new ASN1Encodable[] { (ASN1Encodable)param1AlgorithmIdentifier, (ASN1Encodable)param1DERBitString });
      this.attributes = param1ASN1Set;
    }
    
    private ASN1Integer getVersion() {
      return this.version;
    }
    
    private X500Name getSubject() {
      return this.subject;
    }
    
    private ASN1Sequence getSubjectPublicKeyInfo() {
      return this.subjectPublicKeyInfo;
    }
    
    private ASN1Set getAttributes() {
      return this.attributes;
    }
    
    public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
      aSN1EncodableVector.add((ASN1Encodable)this.version);
      aSN1EncodableVector.add((ASN1Encodable)this.subject);
      aSN1EncodableVector.add((ASN1Encodable)this.subjectPublicKeyInfo);
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.attributes));
      return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
    }
  }
}
