package org.bouncycastle.cert.path.validations;

import org.bouncycastle.cert.X509CertificateHolder;

class ValidationUtils {
  static boolean isSelfIssued(X509CertificateHolder paramX509CertificateHolder) {
    return paramX509CertificateHolder.getSubject().equals(paramX509CertificateHolder.getIssuer());
  }
}
