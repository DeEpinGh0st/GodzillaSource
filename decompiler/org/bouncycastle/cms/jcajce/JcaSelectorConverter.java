package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.security.cert.X509CertSelector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cms.KeyTransRecipientId;
import org.bouncycastle.cms.SignerId;

public class JcaSelectorConverter {
  public SignerId getSignerId(X509CertSelector paramX509CertSelector) {
    try {
      return (paramX509CertSelector.getSubjectKeyIdentifier() != null) ? new SignerId(X500Name.getInstance(paramX509CertSelector.getIssuerAsBytes()), paramX509CertSelector.getSerialNumber(), ASN1OctetString.getInstance(paramX509CertSelector.getSubjectKeyIdentifier()).getOctets()) : new SignerId(X500Name.getInstance(paramX509CertSelector.getIssuerAsBytes()), paramX509CertSelector.getSerialNumber());
    } catch (IOException iOException) {
      throw new IllegalArgumentException("unable to convert issuer: " + iOException.getMessage());
    } 
  }
  
  public KeyTransRecipientId getKeyTransRecipientId(X509CertSelector paramX509CertSelector) {
    try {
      return (paramX509CertSelector.getSubjectKeyIdentifier() != null) ? new KeyTransRecipientId(X500Name.getInstance(paramX509CertSelector.getIssuerAsBytes()), paramX509CertSelector.getSerialNumber(), ASN1OctetString.getInstance(paramX509CertSelector.getSubjectKeyIdentifier()).getOctets()) : new KeyTransRecipientId(X500Name.getInstance(paramX509CertSelector.getIssuerAsBytes()), paramX509CertSelector.getSerialNumber());
    } catch (IOException iOException) {
      throw new IllegalArgumentException("unable to convert issuer: " + iOException.getMessage());
    } 
  }
}
