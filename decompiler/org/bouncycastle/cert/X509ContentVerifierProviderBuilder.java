package org.bouncycastle.cert;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;

public interface X509ContentVerifierProviderBuilder {
  ContentVerifierProvider build(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) throws OperatorCreationException;
  
  ContentVerifierProvider build(X509CertificateHolder paramX509CertificateHolder) throws OperatorCreationException;
}
