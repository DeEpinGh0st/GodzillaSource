package org.bouncycastle.cert.cmp;

import org.bouncycastle.asn1.cmp.CertConfirmContent;
import org.bouncycastle.asn1.cmp.CertStatus;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;

public class CertificateConfirmationContent {
  private DigestAlgorithmIdentifierFinder digestAlgFinder;
  
  private CertConfirmContent content;
  
  public CertificateConfirmationContent(CertConfirmContent paramCertConfirmContent) {
    this(paramCertConfirmContent, (DigestAlgorithmIdentifierFinder)new DefaultDigestAlgorithmIdentifierFinder());
  }
  
  public CertificateConfirmationContent(CertConfirmContent paramCertConfirmContent, DigestAlgorithmIdentifierFinder paramDigestAlgorithmIdentifierFinder) {
    this.digestAlgFinder = paramDigestAlgorithmIdentifierFinder;
    this.content = paramCertConfirmContent;
  }
  
  public CertConfirmContent toASN1Structure() {
    return this.content;
  }
  
  public CertificateStatus[] getStatusMessages() {
    CertStatus[] arrayOfCertStatus = this.content.toCertStatusArray();
    CertificateStatus[] arrayOfCertificateStatus = new CertificateStatus[arrayOfCertStatus.length];
    for (byte b = 0; b != arrayOfCertificateStatus.length; b++)
      arrayOfCertificateStatus[b] = new CertificateStatus(this.digestAlgFinder, arrayOfCertStatus[b]); 
    return arrayOfCertificateStatus;
  }
}
