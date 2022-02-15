package org.bouncycastle.cms;

import java.util.ArrayList;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.util.Store;

public class OriginatorInformation {
  private OriginatorInfo originatorInfo;
  
  OriginatorInformation(OriginatorInfo paramOriginatorInfo) {
    this.originatorInfo = paramOriginatorInfo;
  }
  
  public Store getCertificates() {
    ASN1Set aSN1Set = this.originatorInfo.getCertificates();
    if (aSN1Set != null) {
      ArrayList<X509CertificateHolder> arrayList = new ArrayList(aSN1Set.size());
      Enumeration<ASN1Encodable> enumeration = aSN1Set.getObjects();
      while (enumeration.hasMoreElements()) {
        ASN1Primitive aSN1Primitive = ((ASN1Encodable)enumeration.nextElement()).toASN1Primitive();
        if (aSN1Primitive instanceof org.bouncycastle.asn1.ASN1Sequence)
          arrayList.add(new X509CertificateHolder(Certificate.getInstance(aSN1Primitive))); 
      } 
      return (Store)new CollectionStore(arrayList);
    } 
    return (Store)new CollectionStore(new ArrayList());
  }
  
  public Store getCRLs() {
    ASN1Set aSN1Set = this.originatorInfo.getCRLs();
    if (aSN1Set != null) {
      ArrayList<X509CRLHolder> arrayList = new ArrayList(aSN1Set.size());
      Enumeration<ASN1Encodable> enumeration = aSN1Set.getObjects();
      while (enumeration.hasMoreElements()) {
        ASN1Primitive aSN1Primitive = ((ASN1Encodable)enumeration.nextElement()).toASN1Primitive();
        if (aSN1Primitive instanceof org.bouncycastle.asn1.ASN1Sequence)
          arrayList.add(new X509CRLHolder(CertificateList.getInstance(aSN1Primitive))); 
      } 
      return (Store)new CollectionStore(arrayList);
    } 
    return (Store)new CollectionStore(new ArrayList());
  }
  
  public OriginatorInfo toASN1Structure() {
    return this.originatorInfo;
  }
}
