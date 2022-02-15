package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathBuilderResult;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.CertSelector;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.security.cert.X509Extension;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.TargetInformation;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.jcajce.PKIXCRLStore;
import org.bouncycastle.jcajce.PKIXCertStoreSelector;
import org.bouncycastle.jcajce.PKIXExtendedBuilderParameters;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.exception.ExtCertPathValidatorException;
import org.bouncycastle.x509.PKIXAttrCertChecker;
import org.bouncycastle.x509.X509AttributeCertificate;
import org.bouncycastle.x509.X509CertStoreSelector;

class RFC3281CertPathUtilities {
  private static final String TARGET_INFORMATION = Extension.targetInformation.getId();
  
  private static final String NO_REV_AVAIL = Extension.noRevAvail.getId();
  
  private static final String CRL_DISTRIBUTION_POINTS = Extension.cRLDistributionPoints.getId();
  
  private static final String AUTHORITY_INFO_ACCESS = Extension.authorityInfoAccess.getId();
  
  protected static void processAttrCert7(X509AttributeCertificate paramX509AttributeCertificate, CertPath paramCertPath1, CertPath paramCertPath2, PKIXExtendedParameters paramPKIXExtendedParameters, Set paramSet) throws CertPathValidatorException {
    Set set = paramX509AttributeCertificate.getCriticalExtensionOIDs();
    if (set.contains(TARGET_INFORMATION))
      try {
        TargetInformation.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Extension)paramX509AttributeCertificate, TARGET_INFORMATION));
      } catch (AnnotatedException annotatedException) {
        throw new ExtCertPathValidatorException("Target information extension could not be read.", annotatedException);
      } catch (IllegalArgumentException illegalArgumentException) {
        throw new ExtCertPathValidatorException("Target information extension could not be read.", illegalArgumentException);
      }  
    set.remove(TARGET_INFORMATION);
    Iterator<PKIXAttrCertChecker> iterator = paramSet.iterator();
    while (iterator.hasNext())
      ((PKIXAttrCertChecker)iterator.next()).check(paramX509AttributeCertificate, paramCertPath1, paramCertPath2, set); 
    if (!set.isEmpty())
      throw new CertPathValidatorException("Attribute certificate contains unsupported critical extensions: " + set); 
  }
  
  protected static void checkCRLs(X509AttributeCertificate paramX509AttributeCertificate, PKIXExtendedParameters paramPKIXExtendedParameters, X509Certificate paramX509Certificate, Date paramDate, List paramList, JcaJceHelper paramJcaJceHelper) throws CertPathValidatorException {
    if (paramPKIXExtendedParameters.isRevocationEnabled())
      if (paramX509AttributeCertificate.getExtensionValue(NO_REV_AVAIL) == null) {
        CRLDistPoint cRLDistPoint = null;
        try {
          cRLDistPoint = CRLDistPoint.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Extension)paramX509AttributeCertificate, CRL_DISTRIBUTION_POINTS));
        } catch (AnnotatedException annotatedException1) {
          throw new CertPathValidatorException("CRL distribution point extension could not be read.", annotatedException1);
        } 
        ArrayList<PKIXCRLStore> arrayList = new ArrayList();
        try {
          arrayList.addAll(CertPathValidatorUtilities.getAdditionalStoresFromCRLDistributionPoint(cRLDistPoint, paramPKIXExtendedParameters.getNamedCRLStoreMap()));
        } catch (AnnotatedException annotatedException1) {
          throw new CertPathValidatorException("No additional CRL locations could be decoded from CRL distribution point extension.", annotatedException1);
        } 
        PKIXExtendedParameters.Builder builder = new PKIXExtendedParameters.Builder(paramPKIXExtendedParameters);
        Iterator<PKIXCRLStore> iterator = arrayList.iterator();
        while (iterator.hasNext())
          builder.addCRLStore((PKIXCRLStore)arrayList); 
        paramPKIXExtendedParameters = builder.build();
        CertStatus certStatus = new CertStatus();
        ReasonsMask reasonsMask = new ReasonsMask();
        AnnotatedException annotatedException = null;
        boolean bool = false;
        if (cRLDistPoint != null) {
          DistributionPoint[] arrayOfDistributionPoint = null;
          try {
            arrayOfDistributionPoint = cRLDistPoint.getDistributionPoints();
          } catch (Exception exception) {
            throw new ExtCertPathValidatorException("Distribution points could not be read.", exception);
          } 
          try {
            for (byte b = 0; b < arrayOfDistributionPoint.length && certStatus.getCertStatus() == 11 && !reasonsMask.isAllReasons(); b++) {
              PKIXExtendedParameters pKIXExtendedParameters = (PKIXExtendedParameters)paramPKIXExtendedParameters.clone();
              checkCRL(arrayOfDistributionPoint[b], paramX509AttributeCertificate, pKIXExtendedParameters, paramDate, paramX509Certificate, certStatus, reasonsMask, paramList, paramJcaJceHelper);
              bool = true;
            } 
          } catch (AnnotatedException annotatedException1) {
            annotatedException = new AnnotatedException("No valid CRL for distribution point found.", annotatedException1);
          } 
        } 
        if (certStatus.getCertStatus() == 11 && !reasonsMask.isAllReasons())
          try {
            ASN1Primitive aSN1Primitive = null;
            try {
              aSN1Primitive = (new ASN1InputStream(((X500Principal)paramX509AttributeCertificate.getIssuer().getPrincipals()[0]).getEncoded())).readObject();
            } catch (Exception exception) {
              throw new AnnotatedException("Issuer from certificate for CRL could not be reencoded.", exception);
            } 
            DistributionPoint distributionPoint = new DistributionPoint(new DistributionPointName(0, (ASN1Encodable)new GeneralNames(new GeneralName(4, (ASN1Encodable)aSN1Primitive))), null, null);
            PKIXExtendedParameters pKIXExtendedParameters = (PKIXExtendedParameters)paramPKIXExtendedParameters.clone();
            checkCRL(distributionPoint, paramX509AttributeCertificate, pKIXExtendedParameters, paramDate, paramX509Certificate, certStatus, reasonsMask, paramList, paramJcaJceHelper);
            bool = true;
          } catch (AnnotatedException annotatedException1) {
            annotatedException = new AnnotatedException("No valid CRL for distribution point found.", annotatedException1);
          }  
        if (!bool)
          throw new ExtCertPathValidatorException("No valid CRL found.", annotatedException); 
        if (certStatus.getCertStatus() != 11) {
          String str = "Attribute certificate revocation after " + certStatus.getRevocationDate();
          str = str + ", reason: " + RFC3280CertPathUtilities.crlReasons[certStatus.getCertStatus()];
          throw new CertPathValidatorException(str);
        } 
        if (!reasonsMask.isAllReasons() && certStatus.getCertStatus() == 11)
          certStatus.setCertStatus(12); 
        if (certStatus.getCertStatus() == 12)
          throw new CertPathValidatorException("Attribute certificate status could not be determined."); 
      } else if (paramX509AttributeCertificate.getExtensionValue(CRL_DISTRIBUTION_POINTS) != null || paramX509AttributeCertificate.getExtensionValue(AUTHORITY_INFO_ACCESS) != null) {
        throw new CertPathValidatorException("No rev avail extension is set, but also an AC revocation pointer.");
      }  
  }
  
  protected static void additionalChecks(X509AttributeCertificate paramX509AttributeCertificate, Set paramSet1, Set paramSet2) throws CertPathValidatorException {
    for (String str : paramSet1) {
      if (paramX509AttributeCertificate.getAttributes(str) != null)
        throw new CertPathValidatorException("Attribute certificate contains prohibited attribute: " + str + "."); 
    } 
    for (String str : paramSet2) {
      if (paramX509AttributeCertificate.getAttributes(str) == null)
        throw new CertPathValidatorException("Attribute certificate does not contain necessary attribute: " + str + "."); 
    } 
  }
  
  protected static void processAttrCert5(X509AttributeCertificate paramX509AttributeCertificate, PKIXExtendedParameters paramPKIXExtendedParameters) throws CertPathValidatorException {
    try {
      paramX509AttributeCertificate.checkValidity(CertPathValidatorUtilities.getValidDate(paramPKIXExtendedParameters));
    } catch (CertificateExpiredException certificateExpiredException) {
      throw new ExtCertPathValidatorException("Attribute certificate is not valid.", certificateExpiredException);
    } catch (CertificateNotYetValidException certificateNotYetValidException) {
      throw new ExtCertPathValidatorException("Attribute certificate is not valid.", certificateNotYetValidException);
    } 
  }
  
  protected static void processAttrCert4(X509Certificate paramX509Certificate, Set paramSet) throws CertPathValidatorException {
    Set set = paramSet;
    boolean bool = false;
    for (TrustAnchor trustAnchor : set) {
      if (paramX509Certificate.getSubjectX500Principal().getName("RFC2253").equals(trustAnchor.getCAName()) || paramX509Certificate.equals(trustAnchor.getTrustedCert()))
        bool = true; 
    } 
    if (!bool)
      throw new CertPathValidatorException("Attribute certificate issuer is not directly trusted."); 
  }
  
  protected static void processAttrCert3(X509Certificate paramX509Certificate, PKIXExtendedParameters paramPKIXExtendedParameters) throws CertPathValidatorException {
    if (paramX509Certificate.getKeyUsage() != null && !paramX509Certificate.getKeyUsage()[0] && !paramX509Certificate.getKeyUsage()[1])
      throw new CertPathValidatorException("Attribute certificate issuer public key cannot be used to validate digital signatures."); 
    if (paramX509Certificate.getBasicConstraints() != -1)
      throw new CertPathValidatorException("Attribute certificate issuer is also a public key certificate issuer."); 
  }
  
  protected static CertPathValidatorResult processAttrCert2(CertPath paramCertPath, PKIXExtendedParameters paramPKIXExtendedParameters) throws CertPathValidatorException {
    CertPathValidator certPathValidator = null;
    try {
      certPathValidator = CertPathValidator.getInstance("PKIX", "BC");
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new ExtCertPathValidatorException("Support class could not be created.", noSuchProviderException);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new ExtCertPathValidatorException("Support class could not be created.", noSuchAlgorithmException);
    } 
    try {
      return certPathValidator.validate(paramCertPath, (CertPathParameters)paramPKIXExtendedParameters);
    } catch (CertPathValidatorException certPathValidatorException) {
      throw new ExtCertPathValidatorException("Certification path for issuer certificate of attribute certificate could not be validated.", certPathValidatorException);
    } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
      throw new RuntimeException(invalidAlgorithmParameterException.getMessage());
    } 
  }
  
  protected static CertPath processAttrCert1(X509AttributeCertificate paramX509AttributeCertificate, PKIXExtendedParameters paramPKIXExtendedParameters) throws CertPathValidatorException {
    CertPathBuilderResult certPathBuilderResult = null;
    HashSet hashSet = new HashSet();
    if (paramX509AttributeCertificate.getHolder().getIssuer() != null) {
      X509CertSelector x509CertSelector = new X509CertSelector();
      x509CertSelector.setSerialNumber(paramX509AttributeCertificate.getHolder().getSerialNumber());
      Principal[] arrayOfPrincipal = paramX509AttributeCertificate.getHolder().getIssuer();
      for (byte b = 0; b < arrayOfPrincipal.length; b++) {
        try {
          if (arrayOfPrincipal[b] instanceof X500Principal)
            x509CertSelector.setIssuer(((X500Principal)arrayOfPrincipal[b]).getEncoded()); 
          hashSet.addAll(CertPathValidatorUtilities.findCertificates((new PKIXCertStoreSelector.Builder(x509CertSelector)).build(), paramPKIXExtendedParameters.getCertStores()));
        } catch (AnnotatedException annotatedException) {
          throw new ExtCertPathValidatorException("Public key certificate for attribute certificate cannot be searched.", annotatedException);
        } catch (IOException iOException) {
          throw new ExtCertPathValidatorException("Unable to encode X500 principal.", iOException);
        } 
      } 
      if (hashSet.isEmpty())
        throw new CertPathValidatorException("Public key certificate specified in base certificate ID for attribute certificate cannot be found."); 
    } 
    if (paramX509AttributeCertificate.getHolder().getEntityNames() != null) {
      X509CertStoreSelector x509CertStoreSelector = new X509CertStoreSelector();
      Principal[] arrayOfPrincipal = paramX509AttributeCertificate.getHolder().getEntityNames();
      for (byte b = 0; b < arrayOfPrincipal.length; b++) {
        try {
          if (arrayOfPrincipal[b] instanceof X500Principal)
            x509CertStoreSelector.setIssuer(((X500Principal)arrayOfPrincipal[b]).getEncoded()); 
          hashSet.addAll(CertPathValidatorUtilities.findCertificates((new PKIXCertStoreSelector.Builder((CertSelector)x509CertStoreSelector)).build(), paramPKIXExtendedParameters.getCertStores()));
        } catch (AnnotatedException annotatedException) {
          throw new ExtCertPathValidatorException("Public key certificate for attribute certificate cannot be searched.", annotatedException);
        } catch (IOException iOException) {
          throw new ExtCertPathValidatorException("Unable to encode X500 principal.", iOException);
        } 
      } 
      if (hashSet.isEmpty())
        throw new CertPathValidatorException("Public key certificate specified in entity name for attribute certificate cannot be found."); 
    } 
    PKIXExtendedParameters.Builder builder = new PKIXExtendedParameters.Builder(paramPKIXExtendedParameters);
    ExtCertPathValidatorException extCertPathValidatorException = null;
    Iterator<X509Certificate> iterator = hashSet.iterator();
    while (iterator.hasNext()) {
      X509CertStoreSelector x509CertStoreSelector = new X509CertStoreSelector();
      x509CertStoreSelector.setCertificate(iterator.next());
      builder.setTargetConstraints((new PKIXCertStoreSelector.Builder((CertSelector)x509CertStoreSelector)).build());
      CertPathBuilder certPathBuilder = null;
      try {
        certPathBuilder = CertPathBuilder.getInstance("PKIX", "BC");
      } catch (NoSuchProviderException noSuchProviderException) {
        throw new ExtCertPathValidatorException("Support class could not be created.", noSuchProviderException);
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
        throw new ExtCertPathValidatorException("Support class could not be created.", noSuchAlgorithmException);
      } 
      try {
        certPathBuilderResult = certPathBuilder.build((CertPathParameters)(new PKIXExtendedBuilderParameters.Builder(builder.build())).build());
      } catch (CertPathBuilderException certPathBuilderException) {
        extCertPathValidatorException = new ExtCertPathValidatorException("Certification path for public key certificate of attribute certificate could not be build.", certPathBuilderException);
      } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
        throw new RuntimeException(invalidAlgorithmParameterException.getMessage());
      } 
    } 
    if (extCertPathValidatorException != null)
      throw extCertPathValidatorException; 
    return certPathBuilderResult.getCertPath();
  }
  
  private static void checkCRL(DistributionPoint paramDistributionPoint, X509AttributeCertificate paramX509AttributeCertificate, PKIXExtendedParameters paramPKIXExtendedParameters, Date paramDate, X509Certificate paramX509Certificate, CertStatus paramCertStatus, ReasonsMask paramReasonsMask, List paramList, JcaJceHelper paramJcaJceHelper) throws AnnotatedException {
    if (paramX509AttributeCertificate.getExtensionValue(X509Extensions.NoRevAvail.getId()) != null)
      return; 
    Date date = new Date(System.currentTimeMillis());
    if (paramDate.getTime() > date.getTime())
      throw new AnnotatedException("Validation time is in future."); 
    Set set = CertPathValidatorUtilities.getCompleteCRLs(paramDistributionPoint, paramX509AttributeCertificate, date, paramPKIXExtendedParameters);
    boolean bool = false;
    AnnotatedException annotatedException = null;
    Iterator<X509CRL> iterator = set.iterator();
    while (iterator.hasNext() && paramCertStatus.getCertStatus() == 11 && !paramReasonsMask.isAllReasons()) {
      try {
        X509CRL x509CRL1 = iterator.next();
        ReasonsMask reasonsMask = RFC3280CertPathUtilities.processCRLD(x509CRL1, paramDistributionPoint);
        if (!reasonsMask.hasNewReasons(paramReasonsMask))
          continue; 
        Set set1 = RFC3280CertPathUtilities.processCRLF(x509CRL1, paramX509AttributeCertificate, null, null, paramPKIXExtendedParameters, paramList, paramJcaJceHelper);
        PublicKey publicKey = RFC3280CertPathUtilities.processCRLG(x509CRL1, set1);
        X509CRL x509CRL2 = null;
        if (paramPKIXExtendedParameters.isUseDeltasEnabled()) {
          Set set2 = CertPathValidatorUtilities.getDeltaCRLs(date, x509CRL1, paramPKIXExtendedParameters.getCertStores(), paramPKIXExtendedParameters.getCRLStores());
          x509CRL2 = RFC3280CertPathUtilities.processCRLH(set2, publicKey);
        } 
        if (paramPKIXExtendedParameters.getValidityModel() != 1 && paramX509AttributeCertificate.getNotAfter().getTime() < x509CRL1.getThisUpdate().getTime())
          throw new AnnotatedException("No valid CRL for current time found."); 
        RFC3280CertPathUtilities.processCRLB1(paramDistributionPoint, paramX509AttributeCertificate, x509CRL1);
        RFC3280CertPathUtilities.processCRLB2(paramDistributionPoint, paramX509AttributeCertificate, x509CRL1);
        RFC3280CertPathUtilities.processCRLC(x509CRL2, x509CRL1, paramPKIXExtendedParameters);
        RFC3280CertPathUtilities.processCRLI(paramDate, x509CRL2, paramX509AttributeCertificate, paramCertStatus, paramPKIXExtendedParameters);
        RFC3280CertPathUtilities.processCRLJ(paramDate, x509CRL1, paramX509AttributeCertificate, paramCertStatus);
        if (paramCertStatus.getCertStatus() == 8)
          paramCertStatus.setCertStatus(11); 
        paramReasonsMask.addReasons(reasonsMask);
        bool = true;
      } catch (AnnotatedException annotatedException1) {
        annotatedException = annotatedException1;
      } 
    } 
    if (!bool)
      throw annotatedException; 
  }
}
