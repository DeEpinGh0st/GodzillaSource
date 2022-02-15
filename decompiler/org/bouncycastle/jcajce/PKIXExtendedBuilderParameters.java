package org.bouncycastle.jcajce;

import java.security.InvalidParameterException;
import java.security.cert.CertPathParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PKIXExtendedBuilderParameters implements CertPathParameters {
  private final PKIXExtendedParameters baseParameters;
  
  private final Set<X509Certificate> excludedCerts;
  
  private final int maxPathLength;
  
  private PKIXExtendedBuilderParameters(Builder paramBuilder) {
    this.baseParameters = paramBuilder.baseParameters;
    this.excludedCerts = Collections.unmodifiableSet(paramBuilder.excludedCerts);
    this.maxPathLength = paramBuilder.maxPathLength;
  }
  
  public PKIXExtendedParameters getBaseParameters() {
    return this.baseParameters;
  }
  
  public Set getExcludedCerts() {
    return this.excludedCerts;
  }
  
  public int getMaxPathLength() {
    return this.maxPathLength;
  }
  
  public Object clone() {
    return this;
  }
  
  public static class Builder {
    private final PKIXExtendedParameters baseParameters;
    
    private int maxPathLength = 5;
    
    private Set<X509Certificate> excludedCerts = new HashSet<X509Certificate>();
    
    public Builder(PKIXBuilderParameters param1PKIXBuilderParameters) {
      this.baseParameters = (new PKIXExtendedParameters.Builder(param1PKIXBuilderParameters)).build();
      this.maxPathLength = param1PKIXBuilderParameters.getMaxPathLength();
    }
    
    public Builder(PKIXExtendedParameters param1PKIXExtendedParameters) {
      this.baseParameters = param1PKIXExtendedParameters;
    }
    
    public Builder addExcludedCerts(Set<X509Certificate> param1Set) {
      this.excludedCerts.addAll(param1Set);
      return this;
    }
    
    public Builder setMaxPathLength(int param1Int) {
      if (param1Int < -1)
        throw new InvalidParameterException("The maximum path length parameter can not be less than -1."); 
      this.maxPathLength = param1Int;
      return this;
    }
    
    public PKIXExtendedBuilderParameters build() {
      return new PKIXExtendedBuilderParameters(this);
    }
  }
}
