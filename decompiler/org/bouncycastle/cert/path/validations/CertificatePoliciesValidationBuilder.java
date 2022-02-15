package org.bouncycastle.cert.path.validations;

import org.bouncycastle.cert.path.CertPath;

public class CertificatePoliciesValidationBuilder {
  private boolean isExplicitPolicyRequired;
  
  private boolean isAnyPolicyInhibited;
  
  private boolean isPolicyMappingInhibited;
  
  public void setAnyPolicyInhibited(boolean paramBoolean) {
    this.isAnyPolicyInhibited = paramBoolean;
  }
  
  public void setExplicitPolicyRequired(boolean paramBoolean) {
    this.isExplicitPolicyRequired = paramBoolean;
  }
  
  public void setPolicyMappingInhibited(boolean paramBoolean) {
    this.isPolicyMappingInhibited = paramBoolean;
  }
  
  public CertificatePoliciesValidation build(int paramInt) {
    return new CertificatePoliciesValidation(paramInt, this.isExplicitPolicyRequired, this.isAnyPolicyInhibited, this.isPolicyMappingInhibited);
  }
  
  public CertificatePoliciesValidation build(CertPath paramCertPath) {
    return build(paramCertPath.length());
  }
}
