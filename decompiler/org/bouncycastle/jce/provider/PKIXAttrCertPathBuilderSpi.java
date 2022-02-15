package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.Principal;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathBuilderResult;
import java.security.cert.CertPathBuilderSpi;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathValidator;
import java.security.cert.CertSelector;
import java.security.cert.CertificateFactory;
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
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.jcajce.PKIXCertStore;
import org.bouncycastle.jcajce.PKIXCertStoreSelector;
import org.bouncycastle.jcajce.PKIXExtendedBuilderParameters;
import org.bouncycastle.jce.exception.ExtCertPathBuilderException;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;
import org.bouncycastle.x509.ExtendedPKIXBuilderParameters;
import org.bouncycastle.x509.X509AttributeCertStoreSelector;
import org.bouncycastle.x509.X509AttributeCertificate;
import org.bouncycastle.x509.X509CertStoreSelector;

public class PKIXAttrCertPathBuilderSpi extends CertPathBuilderSpi {
  private Exception certPathException;
  
  public CertPathBuilderResult engineBuild(CertPathParameters paramCertPathParameters) throws CertPathBuilderException, InvalidAlgorithmParameterException {
    PKIXExtendedBuilderParameters pKIXExtendedBuilderParameters;
    Collection collection;
    if (!(paramCertPathParameters instanceof PKIXBuilderParameters) && !(paramCertPathParameters instanceof ExtendedPKIXBuilderParameters) && !(paramCertPathParameters instanceof PKIXExtendedBuilderParameters))
      throw new InvalidAlgorithmParameterException("Parameters must be an instance of " + PKIXBuilderParameters.class.getName() + " or " + PKIXExtendedBuilderParameters.class.getName() + "."); 
    List list = new ArrayList();
    if (paramCertPathParameters instanceof PKIXBuilderParameters) {
      PKIXExtendedBuilderParameters.Builder builder = new PKIXExtendedBuilderParameters.Builder((PKIXBuilderParameters)paramCertPathParameters);
      if (paramCertPathParameters instanceof org.bouncycastle.x509.ExtendedPKIXParameters) {
        ExtendedPKIXBuilderParameters extendedPKIXBuilderParameters = (ExtendedPKIXBuilderParameters)paramCertPathParameters;
        builder.addExcludedCerts(extendedPKIXBuilderParameters.getExcludedCerts());
        builder.setMaxPathLength(extendedPKIXBuilderParameters.getMaxPathLength());
        list = extendedPKIXBuilderParameters.getStores();
      } 
      pKIXExtendedBuilderParameters = builder.build();
    } else {
      pKIXExtendedBuilderParameters = (PKIXExtendedBuilderParameters)paramCertPathParameters;
    } 
    ArrayList arrayList = new ArrayList();
    PKIXCertStoreSelector pKIXCertStoreSelector = pKIXExtendedBuilderParameters.getBaseParameters().getTargetConstraints();
    if (!(pKIXCertStoreSelector instanceof X509AttributeCertStoreSelector))
      throw new CertPathBuilderException("TargetConstraints must be an instance of " + X509AttributeCertStoreSelector.class.getName() + " for " + getClass().getName() + " class."); 
    try {
      collection = findCertificates((X509AttributeCertStoreSelector)pKIXCertStoreSelector, list);
    } catch (AnnotatedException annotatedException) {
      throw new ExtCertPathBuilderException("Error finding target attribute certificate.", annotatedException);
    } 
    if (collection.isEmpty())
      throw new CertPathBuilderException("No attribute certificate found matching targetContraints."); 
    CertPathBuilderResult certPathBuilderResult = null;
    Iterator<X509AttributeCertificate> iterator = collection.iterator();
    while (iterator.hasNext() && certPathBuilderResult == null) {
      X509AttributeCertificate x509AttributeCertificate = iterator.next();
      X509CertStoreSelector x509CertStoreSelector = new X509CertStoreSelector();
      Principal[] arrayOfPrincipal = x509AttributeCertificate.getIssuer().getPrincipals();
      HashSet hashSet = new HashSet();
      for (byte b = 0; b < arrayOfPrincipal.length; b++) {
        try {
          if (arrayOfPrincipal[b] instanceof X500Principal)
            x509CertStoreSelector.setSubject(((X500Principal)arrayOfPrincipal[b]).getEncoded()); 
          PKIXCertStoreSelector pKIXCertStoreSelector1 = (new PKIXCertStoreSelector.Builder((CertSelector)x509CertStoreSelector)).build();
          hashSet.addAll(CertPathValidatorUtilities.findCertificates(pKIXCertStoreSelector1, pKIXExtendedBuilderParameters.getBaseParameters().getCertStores()));
          hashSet.addAll(CertPathValidatorUtilities.findCertificates(pKIXCertStoreSelector1, pKIXExtendedBuilderParameters.getBaseParameters().getCertificateStores()));
        } catch (AnnotatedException annotatedException) {
          throw new ExtCertPathBuilderException("Public key certificate for attribute certificate cannot be searched.", annotatedException);
        } catch (IOException iOException) {
          throw new ExtCertPathBuilderException("cannot encode X500Principal.", iOException);
        } 
      } 
      if (hashSet.isEmpty())
        throw new CertPathBuilderException("Public key certificate for attribute certificate cannot be found."); 
      Iterator<X509Certificate> iterator1 = hashSet.iterator();
      while (iterator1.hasNext() && certPathBuilderResult == null)
        certPathBuilderResult = build(x509AttributeCertificate, iterator1.next(), pKIXExtendedBuilderParameters, arrayList); 
    } 
    if (certPathBuilderResult == null && this.certPathException != null)
      throw new ExtCertPathBuilderException("Possible certificate chain could not be validated.", this.certPathException); 
    if (certPathBuilderResult == null && this.certPathException == null)
      throw new CertPathBuilderException("Unable to find certificate chain."); 
    return certPathBuilderResult;
  }
  
  private CertPathBuilderResult build(X509AttributeCertificate paramX509AttributeCertificate, X509Certificate paramX509Certificate, PKIXExtendedBuilderParameters paramPKIXExtendedBuilderParameters, List<X509Certificate> paramList) {
    CertificateFactory certificateFactory;
    CertPathValidator certPathValidator;
    if (paramList.contains(paramX509Certificate))
      return null; 
    if (paramPKIXExtendedBuilderParameters.getExcludedCerts().contains(paramX509Certificate))
      return null; 
    if (paramPKIXExtendedBuilderParameters.getMaxPathLength() != -1 && paramList.size() - 1 > paramPKIXExtendedBuilderParameters.getMaxPathLength())
      return null; 
    paramList.add(paramX509Certificate);
    CertPathBuilderResult certPathBuilderResult = null;
    try {
      certificateFactory = CertificateFactory.getInstance("X.509", "BC");
      certPathValidator = CertPathValidator.getInstance("RFC3281", "BC");
    } catch (Exception exception) {
      throw new RuntimeException("Exception creating support classes.");
    } 
    try {
      if (CertPathValidatorUtilities.isIssuerTrustAnchor(paramX509Certificate, paramPKIXExtendedBuilderParameters.getBaseParameters().getTrustAnchors(), paramPKIXExtendedBuilderParameters.getBaseParameters().getSigProvider())) {
        CertPath certPath;
        PKIXCertPathValidatorResult pKIXCertPathValidatorResult;
        try {
          certPath = certificateFactory.generateCertPath((List)paramList);
        } catch (Exception exception) {
          throw new AnnotatedException("Certification path could not be constructed from certificate list.", exception);
        } 
        try {
          pKIXCertPathValidatorResult = (PKIXCertPathValidatorResult)certPathValidator.validate(certPath, (CertPathParameters)paramPKIXExtendedBuilderParameters);
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
        if (x509Certificate.getIssuerX500Principal().equals(x509Certificate.getSubjectX500Principal()))
          continue; 
        certPathBuilderResult = build(paramX509AttributeCertificate, x509Certificate, paramPKIXExtendedBuilderParameters, paramList);
      } 
    } catch (AnnotatedException annotatedException) {
      this.certPathException = new AnnotatedException("No valid certification path could be build.", annotatedException);
    } 
    if (certPathBuilderResult == null)
      paramList.remove(paramX509Certificate); 
    return certPathBuilderResult;
  }
  
  protected static Collection findCertificates(X509AttributeCertStoreSelector paramX509AttributeCertStoreSelector, List paramList) throws AnnotatedException {
    HashSet hashSet = new HashSet();
    for (Store store : paramList) {
      if (store instanceof Store) {
        Store store1 = store;
        try {
          hashSet.addAll(store1.getMatches((Selector)paramX509AttributeCertStoreSelector));
        } catch (StoreException storeException) {
          throw new AnnotatedException("Problem while picking certificates from X.509 store.", storeException);
        } 
      } 
    } 
    return hashSet;
  }
}
