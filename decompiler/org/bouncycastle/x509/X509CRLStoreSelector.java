package org.bouncycastle.x509;

import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.CRL;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLSelector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Selector;
import org.bouncycastle.x509.extension.X509ExtensionUtil;

public class X509CRLStoreSelector extends X509CRLSelector implements Selector {
  private boolean deltaCRLIndicator = false;
  
  private boolean completeCRLEnabled = false;
  
  private BigInteger maxBaseCRLNumber = null;
  
  private byte[] issuingDistributionPoint = null;
  
  private boolean issuingDistributionPointEnabled = false;
  
  private X509AttributeCertificate attrCertChecking;
  
  public boolean isIssuingDistributionPointEnabled() {
    return this.issuingDistributionPointEnabled;
  }
  
  public void setIssuingDistributionPointEnabled(boolean paramBoolean) {
    this.issuingDistributionPointEnabled = paramBoolean;
  }
  
  public void setAttrCertificateChecking(X509AttributeCertificate paramX509AttributeCertificate) {
    this.attrCertChecking = paramX509AttributeCertificate;
  }
  
  public X509AttributeCertificate getAttrCertificateChecking() {
    return this.attrCertChecking;
  }
  
  public boolean match(Object paramObject) {
    if (!(paramObject instanceof X509CRL))
      return false; 
    X509CRL x509CRL = (X509CRL)paramObject;
    ASN1Integer aSN1Integer = null;
    try {
      byte[] arrayOfByte = x509CRL.getExtensionValue(X509Extensions.DeltaCRLIndicator.getId());
      if (arrayOfByte != null)
        aSN1Integer = ASN1Integer.getInstance(X509ExtensionUtil.fromExtensionValue(arrayOfByte)); 
    } catch (Exception exception) {
      return false;
    } 
    if (isDeltaCRLIndicatorEnabled() && aSN1Integer == null)
      return false; 
    if (isCompleteCRLEnabled() && aSN1Integer != null)
      return false; 
    if (aSN1Integer != null && this.maxBaseCRLNumber != null && aSN1Integer.getPositiveValue().compareTo(this.maxBaseCRLNumber) == 1)
      return false; 
    if (this.issuingDistributionPointEnabled) {
      byte[] arrayOfByte = x509CRL.getExtensionValue(X509Extensions.IssuingDistributionPoint.getId());
      if (this.issuingDistributionPoint == null) {
        if (arrayOfByte != null)
          return false; 
      } else if (!Arrays.areEqual(arrayOfByte, this.issuingDistributionPoint)) {
        return false;
      } 
    } 
    return super.match((X509CRL)paramObject);
  }
  
  public boolean match(CRL paramCRL) {
    return match(paramCRL);
  }
  
  public boolean isDeltaCRLIndicatorEnabled() {
    return this.deltaCRLIndicator;
  }
  
  public void setDeltaCRLIndicatorEnabled(boolean paramBoolean) {
    this.deltaCRLIndicator = paramBoolean;
  }
  
  public static X509CRLStoreSelector getInstance(X509CRLSelector paramX509CRLSelector) {
    if (paramX509CRLSelector == null)
      throw new IllegalArgumentException("cannot create from null selector"); 
    X509CRLStoreSelector x509CRLStoreSelector = new X509CRLStoreSelector();
    x509CRLStoreSelector.setCertificateChecking(paramX509CRLSelector.getCertificateChecking());
    x509CRLStoreSelector.setDateAndTime(paramX509CRLSelector.getDateAndTime());
    try {
      x509CRLStoreSelector.setIssuerNames(paramX509CRLSelector.getIssuerNames());
    } catch (IOException iOException) {
      throw new IllegalArgumentException(iOException.getMessage());
    } 
    x509CRLStoreSelector.setIssuers(paramX509CRLSelector.getIssuers());
    x509CRLStoreSelector.setMaxCRLNumber(paramX509CRLSelector.getMaxCRL());
    x509CRLStoreSelector.setMinCRLNumber(paramX509CRLSelector.getMinCRL());
    return x509CRLStoreSelector;
  }
  
  public Object clone() {
    X509CRLStoreSelector x509CRLStoreSelector = getInstance(this);
    x509CRLStoreSelector.deltaCRLIndicator = this.deltaCRLIndicator;
    x509CRLStoreSelector.completeCRLEnabled = this.completeCRLEnabled;
    x509CRLStoreSelector.maxBaseCRLNumber = this.maxBaseCRLNumber;
    x509CRLStoreSelector.attrCertChecking = this.attrCertChecking;
    x509CRLStoreSelector.issuingDistributionPointEnabled = this.issuingDistributionPointEnabled;
    x509CRLStoreSelector.issuingDistributionPoint = Arrays.clone(this.issuingDistributionPoint);
    return x509CRLStoreSelector;
  }
  
  public boolean isCompleteCRLEnabled() {
    return this.completeCRLEnabled;
  }
  
  public void setCompleteCRLEnabled(boolean paramBoolean) {
    this.completeCRLEnabled = paramBoolean;
  }
  
  public BigInteger getMaxBaseCRLNumber() {
    return this.maxBaseCRLNumber;
  }
  
  public void setMaxBaseCRLNumber(BigInteger paramBigInteger) {
    this.maxBaseCRLNumber = paramBigInteger;
  }
  
  public byte[] getIssuingDistributionPoint() {
    return Arrays.clone(this.issuingDistributionPoint);
  }
  
  public void setIssuingDistributionPoint(byte[] paramArrayOfbyte) {
    this.issuingDistributionPoint = Arrays.clone(paramArrayOfbyte);
  }
}
