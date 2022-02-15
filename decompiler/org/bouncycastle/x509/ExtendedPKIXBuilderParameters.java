package org.bouncycastle.x509;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXParameters;
import java.security.cert.X509CertSelector;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.util.Selector;

public class ExtendedPKIXBuilderParameters extends ExtendedPKIXParameters {
  private int maxPathLength = 5;
  
  private Set excludedCerts = Collections.EMPTY_SET;
  
  public Set getExcludedCerts() {
    return Collections.unmodifiableSet(this.excludedCerts);
  }
  
  public void setExcludedCerts(Set<?> paramSet) {
    if (paramSet == null) {
      paramSet = Collections.EMPTY_SET;
    } else {
      this.excludedCerts = new HashSet(paramSet);
    } 
  }
  
  public ExtendedPKIXBuilderParameters(Set paramSet, Selector paramSelector) throws InvalidAlgorithmParameterException {
    super(paramSet);
    setTargetConstraints(paramSelector);
  }
  
  public void setMaxPathLength(int paramInt) {
    if (paramInt < -1)
      throw new InvalidParameterException("The maximum path length parameter can not be less than -1."); 
    this.maxPathLength = paramInt;
  }
  
  public int getMaxPathLength() {
    return this.maxPathLength;
  }
  
  protected void setParams(PKIXParameters paramPKIXParameters) {
    super.setParams(paramPKIXParameters);
    if (paramPKIXParameters instanceof ExtendedPKIXBuilderParameters) {
      ExtendedPKIXBuilderParameters extendedPKIXBuilderParameters = (ExtendedPKIXBuilderParameters)paramPKIXParameters;
      this.maxPathLength = extendedPKIXBuilderParameters.maxPathLength;
      this.excludedCerts = new HashSet(extendedPKIXBuilderParameters.excludedCerts);
    } 
    if (paramPKIXParameters instanceof PKIXBuilderParameters) {
      PKIXBuilderParameters pKIXBuilderParameters = (PKIXBuilderParameters)paramPKIXParameters;
      this.maxPathLength = pKIXBuilderParameters.getMaxPathLength();
    } 
  }
  
  public Object clone() {
    ExtendedPKIXBuilderParameters extendedPKIXBuilderParameters = null;
    try {
      extendedPKIXBuilderParameters = new ExtendedPKIXBuilderParameters(getTrustAnchors(), getTargetConstraints());
    } catch (Exception exception) {
      throw new RuntimeException(exception.getMessage());
    } 
    extendedPKIXBuilderParameters.setParams(this);
    return extendedPKIXBuilderParameters;
  }
  
  public static ExtendedPKIXParameters getInstance(PKIXParameters paramPKIXParameters) {
    ExtendedPKIXBuilderParameters extendedPKIXBuilderParameters;
    try {
      extendedPKIXBuilderParameters = new ExtendedPKIXBuilderParameters(paramPKIXParameters.getTrustAnchors(), X509CertStoreSelector.getInstance((X509CertSelector)paramPKIXParameters.getTargetCertConstraints()));
    } catch (Exception exception) {
      throw new RuntimeException(exception.getMessage());
    } 
    extendedPKIXBuilderParameters.setParams(paramPKIXParameters);
    return extendedPKIXBuilderParameters;
  }
}
