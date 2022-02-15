package org.bouncycastle.jcajce;

import java.math.BigInteger;
import java.security.cert.CRL;
import java.security.cert.CRLSelector;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509Certificate;
import java.util.Collection;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Selector;

public class PKIXCRLStoreSelector<T extends CRL> implements Selector<T> {
  private final CRLSelector baseSelector;
  
  private final boolean deltaCRLIndicator;
  
  private final boolean completeCRLEnabled;
  
  private final BigInteger maxBaseCRLNumber;
  
  private final byte[] issuingDistributionPoint;
  
  private final boolean issuingDistributionPointEnabled;
  
  private PKIXCRLStoreSelector(Builder paramBuilder) {
    this.baseSelector = paramBuilder.baseSelector;
    this.deltaCRLIndicator = paramBuilder.deltaCRLIndicator;
    this.completeCRLEnabled = paramBuilder.completeCRLEnabled;
    this.maxBaseCRLNumber = paramBuilder.maxBaseCRLNumber;
    this.issuingDistributionPoint = paramBuilder.issuingDistributionPoint;
    this.issuingDistributionPointEnabled = paramBuilder.issuingDistributionPointEnabled;
  }
  
  public boolean isIssuingDistributionPointEnabled() {
    return this.issuingDistributionPointEnabled;
  }
  
  public boolean match(CRL paramCRL) {
    if (!(paramCRL instanceof X509CRL))
      return this.baseSelector.match(paramCRL); 
    X509CRL x509CRL = (X509CRL)paramCRL;
    ASN1Integer aSN1Integer = null;
    try {
      byte[] arrayOfByte = x509CRL.getExtensionValue(Extension.deltaCRLIndicator.getId());
      if (arrayOfByte != null)
        aSN1Integer = ASN1Integer.getInstance(ASN1OctetString.getInstance(arrayOfByte).getOctets()); 
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
      byte[] arrayOfByte = x509CRL.getExtensionValue(Extension.issuingDistributionPoint.getId());
      if (this.issuingDistributionPoint == null) {
        if (arrayOfByte != null)
          return false; 
      } else if (!Arrays.areEqual(arrayOfByte, this.issuingDistributionPoint)) {
        return false;
      } 
    } 
    return this.baseSelector.match(paramCRL);
  }
  
  public boolean isDeltaCRLIndicatorEnabled() {
    return this.deltaCRLIndicator;
  }
  
  public Object clone() {
    return this;
  }
  
  public boolean isCompleteCRLEnabled() {
    return this.completeCRLEnabled;
  }
  
  public BigInteger getMaxBaseCRLNumber() {
    return this.maxBaseCRLNumber;
  }
  
  public byte[] getIssuingDistributionPoint() {
    return Arrays.clone(this.issuingDistributionPoint);
  }
  
  public X509Certificate getCertificateChecking() {
    return (this.baseSelector instanceof X509CRLSelector) ? ((X509CRLSelector)this.baseSelector).getCertificateChecking() : null;
  }
  
  public static Collection<? extends CRL> getCRLs(PKIXCRLStoreSelector paramPKIXCRLStoreSelector, CertStore paramCertStore) throws CertStoreException {
    return paramCertStore.getCRLs(new SelectorClone(paramPKIXCRLStoreSelector));
  }
  
  public static class Builder {
    private final CRLSelector baseSelector;
    
    private boolean deltaCRLIndicator = false;
    
    private boolean completeCRLEnabled = false;
    
    private BigInteger maxBaseCRLNumber = null;
    
    private byte[] issuingDistributionPoint = null;
    
    private boolean issuingDistributionPointEnabled = false;
    
    public Builder(CRLSelector param1CRLSelector) {
      this.baseSelector = (CRLSelector)param1CRLSelector.clone();
    }
    
    public Builder setCompleteCRLEnabled(boolean param1Boolean) {
      this.completeCRLEnabled = param1Boolean;
      return this;
    }
    
    public Builder setDeltaCRLIndicatorEnabled(boolean param1Boolean) {
      this.deltaCRLIndicator = param1Boolean;
      return this;
    }
    
    public void setMaxBaseCRLNumber(BigInteger param1BigInteger) {
      this.maxBaseCRLNumber = param1BigInteger;
    }
    
    public void setIssuingDistributionPointEnabled(boolean param1Boolean) {
      this.issuingDistributionPointEnabled = param1Boolean;
    }
    
    public void setIssuingDistributionPoint(byte[] param1ArrayOfbyte) {
      this.issuingDistributionPoint = Arrays.clone(param1ArrayOfbyte);
    }
    
    public PKIXCRLStoreSelector<? extends CRL> build() {
      return new PKIXCRLStoreSelector<CRL>(this);
    }
  }
  
  private static class SelectorClone extends X509CRLSelector {
    private final PKIXCRLStoreSelector selector;
    
    SelectorClone(PKIXCRLStoreSelector param1PKIXCRLStoreSelector) {
      this.selector = param1PKIXCRLStoreSelector;
      if (param1PKIXCRLStoreSelector.baseSelector instanceof X509CRLSelector) {
        X509CRLSelector x509CRLSelector = (X509CRLSelector)param1PKIXCRLStoreSelector.baseSelector;
        setCertificateChecking(x509CRLSelector.getCertificateChecking());
        setDateAndTime(x509CRLSelector.getDateAndTime());
        setIssuers(x509CRLSelector.getIssuers());
        setMinCRLNumber(x509CRLSelector.getMinCRL());
        setMaxCRLNumber(x509CRLSelector.getMaxCRL());
      } 
    }
    
    public boolean match(CRL param1CRL) {
      return (this.selector == null) ? ((param1CRL != null)) : this.selector.match(param1CRL);
    }
  }
}
