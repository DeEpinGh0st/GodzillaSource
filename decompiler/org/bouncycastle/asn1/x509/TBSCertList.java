package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;

public class TBSCertList extends ASN1Object {
  ASN1Integer version;
  
  AlgorithmIdentifier signature;
  
  X500Name issuer;
  
  Time thisUpdate;
  
  Time nextUpdate;
  
  ASN1Sequence revokedCertificates;
  
  Extensions crlExtensions;
  
  public static TBSCertList getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static TBSCertList getInstance(Object paramObject) {
    return (paramObject instanceof TBSCertList) ? (TBSCertList)paramObject : ((paramObject != null) ? new TBSCertList(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public TBSCertList(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() < 3 || paramASN1Sequence.size() > 7)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    byte b = 0;
    if (paramASN1Sequence.getObjectAt(b) instanceof ASN1Integer) {
      this.version = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(b++));
    } else {
      this.version = null;
    } 
    this.signature = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(b++));
    this.issuer = X500Name.getInstance(paramASN1Sequence.getObjectAt(b++));
    this.thisUpdate = Time.getInstance(paramASN1Sequence.getObjectAt(b++));
    if (b < paramASN1Sequence.size() && (paramASN1Sequence.getObjectAt(b) instanceof org.bouncycastle.asn1.ASN1UTCTime || paramASN1Sequence.getObjectAt(b) instanceof org.bouncycastle.asn1.ASN1GeneralizedTime || paramASN1Sequence.getObjectAt(b) instanceof Time))
      this.nextUpdate = Time.getInstance(paramASN1Sequence.getObjectAt(b++)); 
    if (b < paramASN1Sequence.size() && !(paramASN1Sequence.getObjectAt(b) instanceof ASN1TaggedObject))
      this.revokedCertificates = ASN1Sequence.getInstance(paramASN1Sequence.getObjectAt(b++)); 
    if (b < paramASN1Sequence.size() && paramASN1Sequence.getObjectAt(b) instanceof ASN1TaggedObject)
      this.crlExtensions = Extensions.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)paramASN1Sequence.getObjectAt(b), true)); 
  }
  
  public int getVersionNumber() {
    return (this.version == null) ? 1 : (this.version.getValue().intValue() + 1);
  }
  
  public ASN1Integer getVersion() {
    return this.version;
  }
  
  public AlgorithmIdentifier getSignature() {
    return this.signature;
  }
  
  public X500Name getIssuer() {
    return this.issuer;
  }
  
  public Time getThisUpdate() {
    return this.thisUpdate;
  }
  
  public Time getNextUpdate() {
    return this.nextUpdate;
  }
  
  public CRLEntry[] getRevokedCertificates() {
    if (this.revokedCertificates == null)
      return new CRLEntry[0]; 
    CRLEntry[] arrayOfCRLEntry = new CRLEntry[this.revokedCertificates.size()];
    for (byte b = 0; b < arrayOfCRLEntry.length; b++)
      arrayOfCRLEntry[b] = CRLEntry.getInstance(this.revokedCertificates.getObjectAt(b)); 
    return arrayOfCRLEntry;
  }
  
  public Enumeration getRevokedCertificateEnumeration() {
    return (Enumeration)((this.revokedCertificates == null) ? new EmptyEnumeration() : new RevokedCertificatesEnumeration(this.revokedCertificates.getObjects()));
  }
  
  public Extensions getExtensions() {
    return this.crlExtensions;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (this.version != null)
      aSN1EncodableVector.add((ASN1Encodable)this.version); 
    aSN1EncodableVector.add((ASN1Encodable)this.signature);
    aSN1EncodableVector.add((ASN1Encodable)this.issuer);
    aSN1EncodableVector.add((ASN1Encodable)this.thisUpdate);
    if (this.nextUpdate != null)
      aSN1EncodableVector.add((ASN1Encodable)this.nextUpdate); 
    if (this.revokedCertificates != null)
      aSN1EncodableVector.add((ASN1Encodable)this.revokedCertificates); 
    if (this.crlExtensions != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(0, (ASN1Encodable)this.crlExtensions)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  public static class CRLEntry extends ASN1Object {
    ASN1Sequence seq;
    
    Extensions crlEntryExtensions;
    
    private CRLEntry(ASN1Sequence param1ASN1Sequence) {
      if (param1ASN1Sequence.size() < 2 || param1ASN1Sequence.size() > 3)
        throw new IllegalArgumentException("Bad sequence size: " + param1ASN1Sequence.size()); 
      this.seq = param1ASN1Sequence;
    }
    
    public static CRLEntry getInstance(Object param1Object) {
      return (param1Object instanceof CRLEntry) ? (CRLEntry)param1Object : ((param1Object != null) ? new CRLEntry(ASN1Sequence.getInstance(param1Object)) : null);
    }
    
    public ASN1Integer getUserCertificate() {
      return ASN1Integer.getInstance(this.seq.getObjectAt(0));
    }
    
    public Time getRevocationDate() {
      return Time.getInstance(this.seq.getObjectAt(1));
    }
    
    public Extensions getExtensions() {
      if (this.crlEntryExtensions == null && this.seq.size() == 3)
        this.crlEntryExtensions = Extensions.getInstance(this.seq.getObjectAt(2)); 
      return this.crlEntryExtensions;
    }
    
    public ASN1Primitive toASN1Primitive() {
      return (ASN1Primitive)this.seq;
    }
    
    public boolean hasExtensions() {
      return (this.seq.size() == 3);
    }
  }
  
  private class EmptyEnumeration implements Enumeration {
    private EmptyEnumeration() {}
    
    public boolean hasMoreElements() {
      return false;
    }
    
    public Object nextElement() {
      throw new NoSuchElementException("Empty Enumeration");
    }
  }
  
  private class RevokedCertificatesEnumeration implements Enumeration {
    private final Enumeration en;
    
    RevokedCertificatesEnumeration(Enumeration param1Enumeration) {
      this.en = param1Enumeration;
    }
    
    public boolean hasMoreElements() {
      return this.en.hasMoreElements();
    }
    
    public Object nextElement() {
      return TBSCertList.CRLEntry.getInstance(this.en.nextElement());
    }
  }
}
