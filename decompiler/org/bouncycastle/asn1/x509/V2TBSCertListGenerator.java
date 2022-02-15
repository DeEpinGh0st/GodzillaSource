package org.bouncycastle.asn1.x509;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;

public class V2TBSCertListGenerator {
  private ASN1Integer version = new ASN1Integer(1L);
  
  private AlgorithmIdentifier signature;
  
  private X500Name issuer;
  
  private Time thisUpdate;
  
  private Time nextUpdate = null;
  
  private Extensions extensions = null;
  
  private ASN1EncodableVector crlentries = new ASN1EncodableVector();
  
  private static final ASN1Sequence[] reasons = new ASN1Sequence[11];
  
  public void setSignature(AlgorithmIdentifier paramAlgorithmIdentifier) {
    this.signature = paramAlgorithmIdentifier;
  }
  
  public void setIssuer(X509Name paramX509Name) {
    this.issuer = X500Name.getInstance(paramX509Name.toASN1Primitive());
  }
  
  public void setIssuer(X500Name paramX500Name) {
    this.issuer = paramX500Name;
  }
  
  public void setThisUpdate(ASN1UTCTime paramASN1UTCTime) {
    this.thisUpdate = new Time((ASN1Primitive)paramASN1UTCTime);
  }
  
  public void setNextUpdate(ASN1UTCTime paramASN1UTCTime) {
    this.nextUpdate = new Time((ASN1Primitive)paramASN1UTCTime);
  }
  
  public void setThisUpdate(Time paramTime) {
    this.thisUpdate = paramTime;
  }
  
  public void setNextUpdate(Time paramTime) {
    this.nextUpdate = paramTime;
  }
  
  public void addCRLEntry(ASN1Sequence paramASN1Sequence) {
    this.crlentries.add((ASN1Encodable)paramASN1Sequence);
  }
  
  public void addCRLEntry(ASN1Integer paramASN1Integer, ASN1UTCTime paramASN1UTCTime, int paramInt) {
    addCRLEntry(paramASN1Integer, new Time((ASN1Primitive)paramASN1UTCTime), paramInt);
  }
  
  public void addCRLEntry(ASN1Integer paramASN1Integer, Time paramTime, int paramInt) {
    addCRLEntry(paramASN1Integer, paramTime, paramInt, null);
  }
  
  public void addCRLEntry(ASN1Integer paramASN1Integer, Time paramTime, int paramInt, ASN1GeneralizedTime paramASN1GeneralizedTime) {
    if (paramInt != 0) {
      ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
      if (paramInt < reasons.length) {
        if (paramInt < 0)
          throw new IllegalArgumentException("invalid reason value: " + paramInt); 
        aSN1EncodableVector.add((ASN1Encodable)reasons[paramInt]);
      } else {
        aSN1EncodableVector.add((ASN1Encodable)createReasonExtension(paramInt));
      } 
      if (paramASN1GeneralizedTime != null)
        aSN1EncodableVector.add((ASN1Encodable)createInvalidityDateExtension(paramASN1GeneralizedTime)); 
      internalAddCRLEntry(paramASN1Integer, paramTime, (ASN1Sequence)new DERSequence(aSN1EncodableVector));
    } else if (paramASN1GeneralizedTime != null) {
      ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
      aSN1EncodableVector.add((ASN1Encodable)createInvalidityDateExtension(paramASN1GeneralizedTime));
      internalAddCRLEntry(paramASN1Integer, paramTime, (ASN1Sequence)new DERSequence(aSN1EncodableVector));
    } else {
      addCRLEntry(paramASN1Integer, paramTime, (Extensions)null);
    } 
  }
  
  private void internalAddCRLEntry(ASN1Integer paramASN1Integer, Time paramTime, ASN1Sequence paramASN1Sequence) {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)paramASN1Integer);
    aSN1EncodableVector.add((ASN1Encodable)paramTime);
    if (paramASN1Sequence != null)
      aSN1EncodableVector.add((ASN1Encodable)paramASN1Sequence); 
    addCRLEntry((ASN1Sequence)new DERSequence(aSN1EncodableVector));
  }
  
  public void addCRLEntry(ASN1Integer paramASN1Integer, Time paramTime, Extensions paramExtensions) {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)paramASN1Integer);
    aSN1EncodableVector.add((ASN1Encodable)paramTime);
    if (paramExtensions != null)
      aSN1EncodableVector.add((ASN1Encodable)paramExtensions); 
    addCRLEntry((ASN1Sequence)new DERSequence(aSN1EncodableVector));
  }
  
  public void setExtensions(X509Extensions paramX509Extensions) {
    setExtensions(Extensions.getInstance(paramX509Extensions));
  }
  
  public void setExtensions(Extensions paramExtensions) {
    this.extensions = paramExtensions;
  }
  
  public TBSCertList generateTBSCertList() {
    if (this.signature == null || this.issuer == null || this.thisUpdate == null)
      throw new IllegalStateException("Not all mandatory fields set in V2 TBSCertList generator."); 
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.version);
    aSN1EncodableVector.add((ASN1Encodable)this.signature);
    aSN1EncodableVector.add((ASN1Encodable)this.issuer);
    aSN1EncodableVector.add((ASN1Encodable)this.thisUpdate);
    if (this.nextUpdate != null)
      aSN1EncodableVector.add((ASN1Encodable)this.nextUpdate); 
    if (this.crlentries.size() != 0)
      aSN1EncodableVector.add((ASN1Encodable)new DERSequence(this.crlentries)); 
    if (this.extensions != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(0, (ASN1Encodable)this.extensions)); 
    return new TBSCertList((ASN1Sequence)new DERSequence(aSN1EncodableVector));
  }
  
  private static ASN1Sequence createReasonExtension(int paramInt) {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    CRLReason cRLReason = CRLReason.lookup(paramInt);
    try {
      aSN1EncodableVector.add((ASN1Encodable)Extension.reasonCode);
      aSN1EncodableVector.add((ASN1Encodable)new DEROctetString(cRLReason.getEncoded()));
    } catch (IOException iOException) {
      throw new IllegalArgumentException("error encoding reason: " + iOException);
    } 
    return (ASN1Sequence)new DERSequence(aSN1EncodableVector);
  }
  
  private static ASN1Sequence createInvalidityDateExtension(ASN1GeneralizedTime paramASN1GeneralizedTime) {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    try {
      aSN1EncodableVector.add((ASN1Encodable)Extension.invalidityDate);
      aSN1EncodableVector.add((ASN1Encodable)new DEROctetString(paramASN1GeneralizedTime.getEncoded()));
    } catch (IOException iOException) {
      throw new IllegalArgumentException("error encoding reason: " + iOException);
    } 
    return (ASN1Sequence)new DERSequence(aSN1EncodableVector);
  }
  
  static {
    reasons[0] = createReasonExtension(0);
    reasons[1] = createReasonExtension(1);
    reasons[2] = createReasonExtension(2);
    reasons[3] = createReasonExtension(3);
    reasons[4] = createReasonExtension(4);
    reasons[5] = createReasonExtension(5);
    reasons[6] = createReasonExtension(6);
    reasons[7] = createReasonExtension(7);
    reasons[8] = createReasonExtension(8);
    reasons[9] = createReasonExtension(9);
    reasons[10] = createReasonExtension(10);
  }
}
