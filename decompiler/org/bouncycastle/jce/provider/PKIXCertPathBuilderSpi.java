package org.bouncycastle.jce.provider;

import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathBuilderResult;
import java.security.cert.CertPathBuilderSpi;
import java.security.cert.CertPathParameters;
import java.security.cert.CertificateParsingException;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.jcajce.PKIXCertStore;
import org.bouncycastle.jcajce.PKIXCertStoreSelector;
import org.bouncycastle.jcajce.PKIXExtendedBuilderParameters;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import org.bouncycastle.jcajce.provider.asymmetric.x509.CertificateFactory;
import org.bouncycastle.jce.exception.ExtCertPathBuilderException;
import org.bouncycastle.x509.ExtendedPKIXBuilderParameters;

public class PKIXCertPathBuilderSpi extends CertPathBuilderSpi {
  private Exception certPathException;
  
  public CertPathBuilderResult engineBuild(CertPathParameters paramCertPathParameters) throws CertPathBuilderException, InvalidAlgorithmParameterException {
    PKIXExtendedBuilderParameters pKIXExtendedBuilderParameters;
    Collection collection;
    if (paramCertPathParameters instanceof PKIXBuilderParameters) {
      PKIXExtendedBuilderParameters.Builder builder1;
      PKIXExtendedParameters.Builder builder = new PKIXExtendedParameters.Builder((PKIXBuilderParameters)paramCertPathParameters);
      if (paramCertPathParameters instanceof org.bouncycastle.x509.ExtendedPKIXParameters) {
        ExtendedPKIXBuilderParameters extendedPKIXBuilderParameters = (ExtendedPKIXBuilderParameters)paramCertPathParameters;
        Iterator<PKIXCertStore> iterator1 = extendedPKIXBuilderParameters.getAdditionalStores().iterator();
        while (iterator1.hasNext())
          builder.addCertificateStore(iterator1.next()); 
        builder1 = new PKIXExtendedBuilderParameters.Builder(builder.build());
        builder1.addExcludedCerts(extendedPKIXBuilderParameters.getExcludedCerts());
        builder1.setMaxPathLength(extendedPKIXBuilderParameters.getMaxPathLength());
      } else {
        builder1 = new PKIXExtendedBuilderParameters.Builder((PKIXBuilderParameters)paramCertPathParameters);
      } 
      pKIXExtendedBuilderParameters = builder1.build();
    } else if (paramCertPathParameters instanceof PKIXExtendedBuilderParameters) {
      pKIXExtendedBuilderParameters = (PKIXExtendedBuilderParameters)paramCertPathParameters;
    } else {
      throw new InvalidAlgorithmParameterException("Parameters must be an instance of " + PKIXBuilderParameters.class.getName() + " or " + PKIXExtendedBuilderParameters.class.getName() + ".");
    } 
    ArrayList arrayList = new ArrayList();
    PKIXCertStoreSelector pKIXCertStoreSelector = pKIXExtendedBuilderParameters.getBaseParameters().getTargetConstraints();
    try {
      collection = CertPathValidatorUtilities.findCertificates(pKIXCertStoreSelector, pKIXExtendedBuilderParameters.getBaseParameters().getCertificateStores());
      collection.addAll(CertPathValidatorUtilities.findCertificates(pKIXCertStoreSelector, pKIXExtendedBuilderParameters.getBaseParameters().getCertStores()));
    } catch (AnnotatedException annotatedException) {
      throw new ExtCertPathBuilderException("Error finding target certificate.", annotatedException);
    } 
    if (collection.isEmpty())
      throw new CertPathBuilderException("No certificate found matching targetContraints."); 
    CertPathBuilderResult certPathBuilderResult = null;
    Iterator<X509Certificate> iterator = collection.iterator();
    while (iterator.hasNext() && certPathBuilderResult == null) {
      X509Certificate x509Certificate = iterator.next();
      certPathBuilderResult = build(x509Certificate, pKIXExtendedBuilderParameters, arrayList);
    } 
    if (certPathBuilderResult == null && this.certPathException != null) {
      if (this.certPathException instanceof AnnotatedException)
        throw new CertPathBuilderException(this.certPathException.getMessage(), this.certPathException.getCause()); 
      throw new CertPathBuilderException("Possible certificate chain could not be validated.", this.certPathException);
    } 
    if (certPathBuilderResult == null && this.certPathException == null)
      throw new CertPathBuilderException("Unable to find certificate chain."); 
    return certPathBuilderResult;
  }
  
  protected CertPathBuilderResult build(X509Certificate paramX509Certificate, PKIXExtendedBuilderParameters paramPKIXExtendedBuilderParameters, List<X509Certificate> paramList) {
    CertificateFactory certificateFactory;
    PKIXCertPathValidatorSpi pKIXCertPathValidatorSpi;
    if (paramList.contains(paramX509Certificate))
      return null; 
    if (paramPKIXExtendedBuilderParameters.getExcludedCerts().contains(paramX509Certificate))
      return null; 
    if (paramPKIXExtendedBuilderParameters.getMaxPathLength() != -1 && paramList.size() - 1 > paramPKIXExtendedBuilderParameters.getMaxPathLength())
      return null; 
    paramList.add(paramX509Certificate);
    CertPathBuilderResult certPathBuilderResult = null;
    try {
      certificateFactory = new CertificateFactory();
      pKIXCertPathValidatorSpi = new PKIXCertPathValidatorSpi();
    } catch (Exception exception) {
      throw new RuntimeException("Exception creating support classes.");
    } 
    try {
      if (CertPathValidatorUtilities.isIssuerTrustAnchor(paramX509Certificate, paramPKIXExtendedBuilderParameters.getBaseParameters().getTrustAnchors(), paramPKIXExtendedBuilderParameters.getBaseParameters().getSigProvider())) {
        CertPath certPath = null;
        PKIXCertPathValidatorResult pKIXCertPathValidatorResult = null;
        try {
          certPath = certificateFactory.engineGenerateCertPath(paramList);
        } catch (Exception exception) {
          throw new AnnotatedException("Certification path could not be constructed from certificate list.", exception);
        } 
        try {
          pKIXCertPathValidatorResult = (PKIXCertPathValidatorResult)pKIXCertPathValidatorSpi.engineValidate(certPath, (CertPathParameters)paramPKIXExtendedBuilderParameters);
        } catch (Exception exception) {
          throw new AnnotatedException("Certification path could not be validated.", exception);
        } 
        return new PKIXCertPathBuilderResult(certPath, pKIXCertPathValidatorResult.getTrustAnchor(), pKIXCertPathValidatorResult.getPolicyTree(), pKIXCertPathValidatorResult.getPublicKey());
      } 
      ArrayList<PKIXCertStore> arrayList = new ArrayList();
      arrayList.addAll(paramPKIXExtendedBuilderParameters.getBaseParameters().getCertificateStores());
      try {
        arrayList.addAll(CertPathValidatorUtilities.getAdditionalStoresFromAltNames(paramX509Certificate.getExtensionValue(Extension.issuerAlternativeName.getId()), paramPKIXExtendedBuilderParameters.getBaseParameters().getNamedCertificateStoreMap()));
      } catch (CertificateParsingException certificateParsingException) {
        throw new AnnotatedException("No additional X.509 stores can be added from certificate locations.", certificateParsingException);
      } 
      HashSet hashSet = new HashSet();
      try {
        hashSet.addAll(CertPathValidatorUtilities.findIssuerCerts(paramX509Certificate, paramPKIXExtendedBuilderParameters.getBaseParameters().getCertStores(), arrayList));
      } catch (AnnotatedException annotatedException) {
        throw new AnnotatedException("Cannot find issuer certificate for certificate in certification path.", annotatedException);
      } 
      if (hashSet.isEmpty())
        throw new AnnotatedException("No issuer certificate for certificate in certification path found."); 
      Iterator<X509Certificate> iterator = hashSet.iterator();
      while (iterator.hasNext() && certPathBuilderResult == null) {
        X509Certificate x509Certificate = iterator.next();
        certPathBuilderResult = build(x509Certificate, paramPKIXExtendedBuilderParameters, paramList);
      } 
    } catch (AnnotatedException annotatedException) {
      this.certPathException = annotatedException;
    } 
    if (certPathBuilderResult == null)
      paramList.remove(paramX509Certificate); 
    return certPathBuilderResult;
  }
}
