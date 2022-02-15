package org.bouncycastle.cert.ocsp;

import java.util.Date;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ocsp.RevokedInfo;
import org.bouncycastle.asn1.x509.CRLReason;

public class RevokedStatus implements CertificateStatus {
  RevokedInfo info;
  
  public RevokedStatus(RevokedInfo paramRevokedInfo) {
    this.info = paramRevokedInfo;
  }
  
  public RevokedStatus(Date paramDate, int paramInt) {
    this.info = new RevokedInfo(new ASN1GeneralizedTime(paramDate), CRLReason.lookup(paramInt));
  }
  
  public Date getRevocationTime() {
    return OCSPUtils.extractDate(this.info.getRevocationTime());
  }
  
  public boolean hasRevocationReason() {
    return (this.info.getRevocationReason() != null);
  }
  
  public int getRevocationReason() {
    if (this.info.getRevocationReason() == null)
      throw new IllegalStateException("attempt to get a reason where none is available"); 
    return this.info.getRevocationReason().getValue().intValue();
  }
}
