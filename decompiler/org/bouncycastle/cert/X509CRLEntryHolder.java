package org.bouncycastle.cert;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.TBSCertList;

public class X509CRLEntryHolder {
  private TBSCertList.CRLEntry entry;
  
  private GeneralNames ca;
  
  X509CRLEntryHolder(TBSCertList.CRLEntry paramCRLEntry, boolean paramBoolean, GeneralNames paramGeneralNames) {
    this.entry = paramCRLEntry;
    this.ca = paramGeneralNames;
    if (paramBoolean && paramCRLEntry.hasExtensions()) {
      Extension extension = paramCRLEntry.getExtensions().getExtension(Extension.certificateIssuer);
      if (extension != null)
        this.ca = GeneralNames.getInstance(extension.getParsedValue()); 
    } 
  }
  
  public BigInteger getSerialNumber() {
    return this.entry.getUserCertificate().getValue();
  }
  
  public Date getRevocationDate() {
    return this.entry.getRevocationDate().getDate();
  }
  
  public boolean hasExtensions() {
    return this.entry.hasExtensions();
  }
  
  public GeneralNames getCertificateIssuer() {
    return this.ca;
  }
  
  public Extension getExtension(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    Extensions extensions = this.entry.getExtensions();
    return (extensions != null) ? extensions.getExtension(paramASN1ObjectIdentifier) : null;
  }
  
  public Extensions getExtensions() {
    return this.entry.getExtensions();
  }
  
  public List getExtensionOIDs() {
    return CertUtils.getExtensionOIDs(this.entry.getExtensions());
  }
  
  public Set getCriticalExtensionOIDs() {
    return CertUtils.getCriticalExtensionOIDs(this.entry.getExtensions());
  }
  
  public Set getNonCriticalExtensionOIDs() {
    return CertUtils.getNonCriticalExtensionOIDs(this.entry.getExtensions());
  }
}
