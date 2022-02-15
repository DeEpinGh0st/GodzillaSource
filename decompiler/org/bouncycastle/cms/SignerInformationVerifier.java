package org.bouncycastle.cms;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.SignatureAlgorithmIdentifierFinder;

public class SignerInformationVerifier {
  private ContentVerifierProvider verifierProvider;
  
  private DigestCalculatorProvider digestProvider;
  
  private SignatureAlgorithmIdentifierFinder sigAlgorithmFinder;
  
  private CMSSignatureAlgorithmNameGenerator sigNameGenerator;
  
  public SignerInformationVerifier(CMSSignatureAlgorithmNameGenerator paramCMSSignatureAlgorithmNameGenerator, SignatureAlgorithmIdentifierFinder paramSignatureAlgorithmIdentifierFinder, ContentVerifierProvider paramContentVerifierProvider, DigestCalculatorProvider paramDigestCalculatorProvider) {
    this.sigNameGenerator = paramCMSSignatureAlgorithmNameGenerator;
    this.sigAlgorithmFinder = paramSignatureAlgorithmIdentifierFinder;
    this.verifierProvider = paramContentVerifierProvider;
    this.digestProvider = paramDigestCalculatorProvider;
  }
  
  public boolean hasAssociatedCertificate() {
    return this.verifierProvider.hasAssociatedCertificate();
  }
  
  public X509CertificateHolder getAssociatedCertificate() {
    return this.verifierProvider.getAssociatedCertificate();
  }
  
  public ContentVerifier getContentVerifier(AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2) throws OperatorCreationException {
    String str = this.sigNameGenerator.getSignatureName(paramAlgorithmIdentifier2, paramAlgorithmIdentifier1);
    AlgorithmIdentifier algorithmIdentifier = this.sigAlgorithmFinder.find(str);
    return this.verifierProvider.get(new AlgorithmIdentifier(algorithmIdentifier.getAlgorithm(), paramAlgorithmIdentifier1.getParameters()));
  }
  
  public DigestCalculator getDigestCalculator(AlgorithmIdentifier paramAlgorithmIdentifier) throws OperatorCreationException {
    return this.digestProvider.get(paramAlgorithmIdentifier);
  }
}
