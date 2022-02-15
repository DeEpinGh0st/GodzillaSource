package org.bouncycastle.cert.path.validations;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.CertException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509ContentVerifierProviderBuilder;
import org.bouncycastle.cert.path.CertPathValidation;
import org.bouncycastle.cert.path.CertPathValidationContext;
import org.bouncycastle.cert.path.CertPathValidationException;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Memoable;

public class ParentCertIssuedValidation implements CertPathValidation {
  private X509ContentVerifierProviderBuilder contentVerifierProvider;
  
  private X500Name workingIssuerName;
  
  private SubjectPublicKeyInfo workingPublicKey;
  
  private AlgorithmIdentifier workingAlgId;
  
  public ParentCertIssuedValidation(X509ContentVerifierProviderBuilder paramX509ContentVerifierProviderBuilder) {
    this.contentVerifierProvider = paramX509ContentVerifierProviderBuilder;
  }
  
  public void validate(CertPathValidationContext paramCertPathValidationContext, X509CertificateHolder paramX509CertificateHolder) throws CertPathValidationException {
    if (this.workingIssuerName != null && !this.workingIssuerName.equals(paramX509CertificateHolder.getIssuer()))
      throw new CertPathValidationException("Certificate issue does not match parent"); 
    if (this.workingPublicKey != null)
      try {
        SubjectPublicKeyInfo subjectPublicKeyInfo;
        if (this.workingPublicKey.getAlgorithm().equals(this.workingAlgId)) {
          subjectPublicKeyInfo = this.workingPublicKey;
        } else {
          subjectPublicKeyInfo = new SubjectPublicKeyInfo(this.workingAlgId, (ASN1Encodable)this.workingPublicKey.parsePublicKey());
        } 
        if (!paramX509CertificateHolder.isSignatureValid(this.contentVerifierProvider.build(subjectPublicKeyInfo)))
          throw new CertPathValidationException("Certificate signature not for public key in parent"); 
      } catch (OperatorCreationException operatorCreationException) {
        throw new CertPathValidationException("Unable to create verifier: " + operatorCreationException.getMessage(), operatorCreationException);
      } catch (CertException certException) {
        throw new CertPathValidationException("Unable to validate signature: " + certException.getMessage(), certException);
      } catch (IOException iOException) {
        throw new CertPathValidationException("Unable to build public key: " + iOException.getMessage(), iOException);
      }  
    this.workingIssuerName = paramX509CertificateHolder.getSubject();
    this.workingPublicKey = paramX509CertificateHolder.getSubjectPublicKeyInfo();
    if (this.workingAlgId != null) {
      if (this.workingPublicKey.getAlgorithm().getAlgorithm().equals(this.workingAlgId.getAlgorithm())) {
        if (!isNull(this.workingPublicKey.getAlgorithm().getParameters()))
          this.workingAlgId = this.workingPublicKey.getAlgorithm(); 
      } else {
        this.workingAlgId = this.workingPublicKey.getAlgorithm();
      } 
    } else {
      this.workingAlgId = this.workingPublicKey.getAlgorithm();
    } 
  }
  
  private boolean isNull(ASN1Encodable paramASN1Encodable) {
    return (paramASN1Encodable == null || paramASN1Encodable instanceof org.bouncycastle.asn1.ASN1Null);
  }
  
  public Memoable copy() {
    ParentCertIssuedValidation parentCertIssuedValidation = new ParentCertIssuedValidation(this.contentVerifierProvider);
    parentCertIssuedValidation.workingAlgId = this.workingAlgId;
    parentCertIssuedValidation.workingIssuerName = this.workingIssuerName;
    parentCertIssuedValidation.workingPublicKey = this.workingPublicKey;
    return (Memoable)parentCertIssuedValidation;
  }
  
  public void reset(Memoable paramMemoable) {
    ParentCertIssuedValidation parentCertIssuedValidation = (ParentCertIssuedValidation)paramMemoable;
    this.contentVerifierProvider = parentCertIssuedValidation.contentVerifierProvider;
    this.workingAlgId = parentCertIssuedValidation.workingAlgId;
    this.workingIssuerName = parentCertIssuedValidation.workingIssuerName;
    this.workingPublicKey = parentCertIssuedValidation.workingPublicKey;
  }
}
