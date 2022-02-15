package org.bouncycastle.jce.provider;

import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CertPath;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.CertPathValidatorSpi;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.jcajce.PKIXCertStoreSelector;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.exception.ExtCertPathValidatorException;
import org.bouncycastle.x509.ExtendedPKIXParameters;
import org.bouncycastle.x509.X509AttributeCertStoreSelector;
import org.bouncycastle.x509.X509AttributeCertificate;

public class PKIXAttrCertPathValidatorSpi extends CertPathValidatorSpi {
  private final JcaJceHelper helper = (JcaJceHelper)new BCJcaJceHelper();
  
  public CertPathValidatorResult engineValidate(CertPath paramCertPath, CertPathParameters paramCertPathParameters) throws CertPathValidatorException, InvalidAlgorithmParameterException {
    PKIXExtendedParameters pKIXExtendedParameters;
    if (!(paramCertPathParameters instanceof ExtendedPKIXParameters) && !(paramCertPathParameters instanceof PKIXExtendedParameters))
      throw new InvalidAlgorithmParameterException("Parameters must be a " + ExtendedPKIXParameters.class.getName() + " instance."); 
    Set set1 = new HashSet();
    Set set2 = new HashSet();
    Set set3 = new HashSet();
    HashSet hashSet = new HashSet();
    if (paramCertPathParameters instanceof PKIXParameters) {
      PKIXExtendedParameters.Builder builder = new PKIXExtendedParameters.Builder((PKIXParameters)paramCertPathParameters);
      if (paramCertPathParameters instanceof ExtendedPKIXParameters) {
        ExtendedPKIXParameters extendedPKIXParameters = (ExtendedPKIXParameters)paramCertPathParameters;
        builder.setUseDeltasEnabled(extendedPKIXParameters.isUseDeltasEnabled());
        builder.setValidityModel(extendedPKIXParameters.getValidityModel());
        set1 = extendedPKIXParameters.getAttrCertCheckers();
        set2 = extendedPKIXParameters.getProhibitedACAttributes();
        set3 = extendedPKIXParameters.getNecessaryACAttributes();
      } 
      pKIXExtendedParameters = builder.build();
    } else {
      pKIXExtendedParameters = (PKIXExtendedParameters)paramCertPathParameters;
    } 
    PKIXCertStoreSelector pKIXCertStoreSelector = pKIXExtendedParameters.getTargetConstraints();
    if (!(pKIXCertStoreSelector instanceof X509AttributeCertStoreSelector))
      throw new InvalidAlgorithmParameterException("TargetConstraints must be an instance of " + X509AttributeCertStoreSelector.class.getName() + " for " + getClass().getName() + " class."); 
    X509AttributeCertificate x509AttributeCertificate = ((X509AttributeCertStoreSelector)pKIXCertStoreSelector).getAttributeCert();
    CertPath certPath = RFC3281CertPathUtilities.processAttrCert1(x509AttributeCertificate, pKIXExtendedParameters);
    CertPathValidatorResult certPathValidatorResult = RFC3281CertPathUtilities.processAttrCert2(paramCertPath, pKIXExtendedParameters);
    X509Certificate x509Certificate = (X509Certificate)paramCertPath.getCertificates().get(0);
    RFC3281CertPathUtilities.processAttrCert3(x509Certificate, pKIXExtendedParameters);
    RFC3281CertPathUtilities.processAttrCert4(x509Certificate, hashSet);
    RFC3281CertPathUtilities.processAttrCert5(x509AttributeCertificate, pKIXExtendedParameters);
    RFC3281CertPathUtilities.processAttrCert7(x509AttributeCertificate, paramCertPath, certPath, pKIXExtendedParameters, set1);
    RFC3281CertPathUtilities.additionalChecks(x509AttributeCertificate, set2, set3);
    Date date = null;
    try {
      date = CertPathValidatorUtilities.getValidCertDateFromValidityModel(pKIXExtendedParameters, null, -1);
    } catch (AnnotatedException annotatedException) {
      throw new ExtCertPathValidatorException("Could not get validity date from attribute certificate.", annotatedException);
    } 
    RFC3281CertPathUtilities.checkCRLs(x509AttributeCertificate, pKIXExtendedParameters, x509Certificate, date, paramCertPath.getCertificates(), this.helper);
    return certPathValidatorResult;
  }
}
