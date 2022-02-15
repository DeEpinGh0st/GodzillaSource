package org.bouncycastle.cert.path;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Memoable;

public interface CertPathValidation extends Memoable {
  void validate(CertPathValidationContext paramCertPathValidationContext, X509CertificateHolder paramX509CertificateHolder) throws CertPathValidationException;
}
