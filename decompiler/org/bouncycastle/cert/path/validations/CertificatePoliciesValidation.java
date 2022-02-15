package org.bouncycastle.cert.path.validations;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.PolicyConstraints;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.path.CertPathValidation;
import org.bouncycastle.cert.path.CertPathValidationContext;
import org.bouncycastle.cert.path.CertPathValidationException;
import org.bouncycastle.util.Memoable;

public class CertificatePoliciesValidation implements CertPathValidation {
  private int explicitPolicy;
  
  private int policyMapping;
  
  private int inhibitAnyPolicy;
  
  CertificatePoliciesValidation(int paramInt) {
    this(paramInt, false, false, false);
  }
  
  CertificatePoliciesValidation(int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
    if (paramBoolean1) {
      this.explicitPolicy = 0;
    } else {
      this.explicitPolicy = paramInt + 1;
    } 
    if (paramBoolean2) {
      this.inhibitAnyPolicy = 0;
    } else {
      this.inhibitAnyPolicy = paramInt + 1;
    } 
    if (paramBoolean3) {
      this.policyMapping = 0;
    } else {
      this.policyMapping = paramInt + 1;
    } 
  }
  
  public void validate(CertPathValidationContext paramCertPathValidationContext, X509CertificateHolder paramX509CertificateHolder) throws CertPathValidationException {
    paramCertPathValidationContext.addHandledExtension(Extension.policyConstraints);
    paramCertPathValidationContext.addHandledExtension(Extension.inhibitAnyPolicy);
    if (!paramCertPathValidationContext.isEndEntity() && !ValidationUtils.isSelfIssued(paramX509CertificateHolder)) {
      this.explicitPolicy = countDown(this.explicitPolicy);
      this.policyMapping = countDown(this.policyMapping);
      this.inhibitAnyPolicy = countDown(this.inhibitAnyPolicy);
      PolicyConstraints policyConstraints = PolicyConstraints.fromExtensions(paramX509CertificateHolder.getExtensions());
      if (policyConstraints != null) {
        BigInteger bigInteger1 = policyConstraints.getRequireExplicitPolicyMapping();
        if (bigInteger1 != null && bigInteger1.intValue() < this.explicitPolicy)
          this.explicitPolicy = bigInteger1.intValue(); 
        BigInteger bigInteger2 = policyConstraints.getInhibitPolicyMapping();
        if (bigInteger2 != null && bigInteger2.intValue() < this.policyMapping)
          this.policyMapping = bigInteger2.intValue(); 
      } 
      Extension extension = paramX509CertificateHolder.getExtension(Extension.inhibitAnyPolicy);
      if (extension != null) {
        int i = ASN1Integer.getInstance(extension.getParsedValue()).getValue().intValue();
        if (i < this.inhibitAnyPolicy)
          this.inhibitAnyPolicy = i; 
      } 
    } 
  }
  
  private int countDown(int paramInt) {
    return (paramInt != 0) ? (paramInt - 1) : 0;
  }
  
  public Memoable copy() {
    return (Memoable)new CertificatePoliciesValidation(0);
  }
  
  public void reset(Memoable paramMemoable) {
    CertificatePoliciesValidation certificatePoliciesValidation = (CertificatePoliciesValidation)paramMemoable;
  }
}
