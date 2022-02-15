package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.security.cert.X509Extension;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.GeneralSubtree;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.asn1.x509.NameConstraints;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.jcajce.PKIXCRLStore;
import org.bouncycastle.jcajce.PKIXCRLStoreSelector;
import org.bouncycastle.jcajce.PKIXCertStoreSelector;
import org.bouncycastle.jcajce.PKIXExtendedBuilderParameters;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.exception.ExtCertPathValidatorException;
import org.bouncycastle.util.Arrays;

class RFC3280CertPathUtilities {
  private static final PKIXCRLUtil CRL_UTIL = new PKIXCRLUtil();
  
  public static final String CERTIFICATE_POLICIES = Extension.certificatePolicies.getId();
  
  public static final String POLICY_MAPPINGS = Extension.policyMappings.getId();
  
  public static final String INHIBIT_ANY_POLICY = Extension.inhibitAnyPolicy.getId();
  
  public static final String ISSUING_DISTRIBUTION_POINT = Extension.issuingDistributionPoint.getId();
  
  public static final String FRESHEST_CRL = Extension.freshestCRL.getId();
  
  public static final String DELTA_CRL_INDICATOR = Extension.deltaCRLIndicator.getId();
  
  public static final String POLICY_CONSTRAINTS = Extension.policyConstraints.getId();
  
  public static final String BASIC_CONSTRAINTS = Extension.basicConstraints.getId();
  
  public static final String CRL_DISTRIBUTION_POINTS = Extension.cRLDistributionPoints.getId();
  
  public static final String SUBJECT_ALTERNATIVE_NAME = Extension.subjectAlternativeName.getId();
  
  public static final String NAME_CONSTRAINTS = Extension.nameConstraints.getId();
  
  public static final String AUTHORITY_KEY_IDENTIFIER = Extension.authorityKeyIdentifier.getId();
  
  public static final String KEY_USAGE = Extension.keyUsage.getId();
  
  public static final String CRL_NUMBER = Extension.cRLNumber.getId();
  
  public static final String ANY_POLICY = "2.5.29.32.0";
  
  protected static final int KEY_CERT_SIGN = 5;
  
  protected static final int CRL_SIGN = 6;
  
  protected static final String[] crlReasons = new String[] { 
      "unspecified", "keyCompromise", "cACompromise", "affiliationChanged", "superseded", "cessationOfOperation", "certificateHold", "unknown", "removeFromCRL", "privilegeWithdrawn", 
      "aACompromise" };
  
  protected static void processCRLB2(DistributionPoint paramDistributionPoint, Object paramObject, X509CRL paramX509CRL) throws AnnotatedException {
    IssuingDistributionPoint issuingDistributionPoint = null;
    try {
      issuingDistributionPoint = IssuingDistributionPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(paramX509CRL, ISSUING_DISTRIBUTION_POINT));
    } catch (Exception exception) {
      throw new AnnotatedException("Issuing distribution point extension could not be decoded.", exception);
    } 
    if (issuingDistributionPoint != null) {
      if (issuingDistributionPoint.getDistributionPoint() != null) {
        DistributionPointName distributionPointName = IssuingDistributionPoint.getInstance(issuingDistributionPoint).getDistributionPoint();
        ArrayList<GeneralName> arrayList = new ArrayList();
        if (distributionPointName.getType() == 0) {
          GeneralName[] arrayOfGeneralName = GeneralNames.getInstance(distributionPointName.getName()).getNames();
          for (byte b = 0; b < arrayOfGeneralName.length; b++)
            arrayList.add(arrayOfGeneralName[b]); 
        } 
        if (distributionPointName.getType() == 1) {
          ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
          try {
            Enumeration<ASN1Encodable> enumeration = ASN1Sequence.getInstance(PrincipalUtils.getIssuerPrincipal(paramX509CRL)).getObjects();
            while (enumeration.hasMoreElements())
              aSN1EncodableVector.add(enumeration.nextElement()); 
          } catch (Exception exception) {
            throw new AnnotatedException("Could not read CRL issuer.", exception);
          } 
          aSN1EncodableVector.add(distributionPointName.getName());
          arrayList.add(new GeneralName(X500Name.getInstance(new DERSequence(aSN1EncodableVector))));
        } 
        boolean bool = false;
        if (paramDistributionPoint.getDistributionPoint() != null) {
          distributionPointName = paramDistributionPoint.getDistributionPoint();
          GeneralName[] arrayOfGeneralName = null;
          if (distributionPointName.getType() == 0)
            arrayOfGeneralName = GeneralNames.getInstance(distributionPointName.getName()).getNames(); 
          if (distributionPointName.getType() == 1) {
            if (paramDistributionPoint.getCRLIssuer() != null) {
              arrayOfGeneralName = paramDistributionPoint.getCRLIssuer().getNames();
            } else {
              arrayOfGeneralName = new GeneralName[1];
              try {
                arrayOfGeneralName[0] = new GeneralName(X500Name.getInstance(PrincipalUtils.getEncodedIssuerPrincipal(paramObject).getEncoded()));
              } catch (Exception exception) {
                throw new AnnotatedException("Could not read certificate issuer.", exception);
              } 
            } 
            for (byte b = 0; b < arrayOfGeneralName.length; b++) {
              Enumeration<ASN1Encodable> enumeration = ASN1Sequence.getInstance(arrayOfGeneralName[b].getName().toASN1Primitive()).getObjects();
              ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
              while (enumeration.hasMoreElements())
                aSN1EncodableVector.add(enumeration.nextElement()); 
              aSN1EncodableVector.add(distributionPointName.getName());
              arrayOfGeneralName[b] = new GeneralName(X500Name.getInstance(new DERSequence(aSN1EncodableVector)));
            } 
          } 
          if (arrayOfGeneralName != null)
            for (byte b = 0; b < arrayOfGeneralName.length; b++) {
              if (arrayList.contains(arrayOfGeneralName[b])) {
                bool = true;
                break;
              } 
            }  
          if (!bool)
            throw new AnnotatedException("No match for certificate CRL issuing distribution point name to cRLIssuer CRL distribution point."); 
        } else {
          if (paramDistributionPoint.getCRLIssuer() == null)
            throw new AnnotatedException("Either the cRLIssuer or the distributionPoint field must be contained in DistributionPoint."); 
          GeneralName[] arrayOfGeneralName = paramDistributionPoint.getCRLIssuer().getNames();
          for (byte b = 0; b < arrayOfGeneralName.length; b++) {
            if (arrayList.contains(arrayOfGeneralName[b])) {
              bool = true;
              break;
            } 
          } 
          if (!bool)
            throw new AnnotatedException("No match for certificate CRL issuing distribution point name to cRLIssuer CRL distribution point."); 
        } 
      } 
      BasicConstraints basicConstraints = null;
      try {
        basicConstraints = BasicConstraints.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Extension)paramObject, BASIC_CONSTRAINTS));
      } catch (Exception exception) {
        throw new AnnotatedException("Basic constraints extension could not be decoded.", exception);
      } 
      if (paramObject instanceof X509Certificate) {
        if (issuingDistributionPoint.onlyContainsUserCerts() && basicConstraints != null && basicConstraints.isCA())
          throw new AnnotatedException("CA Cert CRL only contains user certificates."); 
        if (issuingDistributionPoint.onlyContainsCACerts() && (basicConstraints == null || !basicConstraints.isCA()))
          throw new AnnotatedException("End CRL only contains CA certificates."); 
      } 
      if (issuingDistributionPoint.onlyContainsAttributeCerts())
        throw new AnnotatedException("onlyContainsAttributeCerts boolean is asserted."); 
    } 
  }
  
  protected static void processCRLB1(DistributionPoint paramDistributionPoint, Object paramObject, X509CRL paramX509CRL) throws AnnotatedException {
    byte[] arrayOfByte;
    ASN1Primitive aSN1Primitive = CertPathValidatorUtilities.getExtensionValue(paramX509CRL, ISSUING_DISTRIBUTION_POINT);
    boolean bool1 = false;
    if (aSN1Primitive != null && IssuingDistributionPoint.getInstance(aSN1Primitive).isIndirectCRL())
      bool1 = true; 
    try {
      arrayOfByte = PrincipalUtils.getIssuerPrincipal(paramX509CRL).getEncoded();
    } catch (IOException iOException) {
      throw new AnnotatedException("Exception encoding CRL issuer: " + iOException.getMessage(), iOException);
    } 
    boolean bool2 = false;
    if (paramDistributionPoint.getCRLIssuer() != null) {
      GeneralName[] arrayOfGeneralName = paramDistributionPoint.getCRLIssuer().getNames();
      for (byte b = 0; b < arrayOfGeneralName.length; b++) {
        if (arrayOfGeneralName[b].getTagNo() == 4)
          try {
            if (Arrays.areEqual(arrayOfGeneralName[b].getName().toASN1Primitive().getEncoded(), arrayOfByte))
              bool2 = true; 
          } catch (IOException iOException) {
            throw new AnnotatedException("CRL issuer information from distribution point cannot be decoded.", iOException);
          }  
      } 
      if (bool2 && !bool1)
        throw new AnnotatedException("Distribution point contains cRLIssuer field but CRL is not indirect."); 
      if (!bool2)
        throw new AnnotatedException("CRL issuer of CRL does not match CRL issuer of distribution point."); 
    } else if (PrincipalUtils.getIssuerPrincipal(paramX509CRL).equals(PrincipalUtils.getEncodedIssuerPrincipal(paramObject))) {
      bool2 = true;
    } 
    if (!bool2)
      throw new AnnotatedException("Cannot find matching CRL issuer for certificate."); 
  }
  
  protected static ReasonsMask processCRLD(X509CRL paramX509CRL, DistributionPoint paramDistributionPoint) throws AnnotatedException {
    IssuingDistributionPoint issuingDistributionPoint = null;
    try {
      issuingDistributionPoint = IssuingDistributionPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(paramX509CRL, ISSUING_DISTRIBUTION_POINT));
    } catch (Exception exception) {
      throw new AnnotatedException("Issuing distribution point extension could not be decoded.", exception);
    } 
    return (issuingDistributionPoint != null && issuingDistributionPoint.getOnlySomeReasons() != null && paramDistributionPoint.getReasons() != null) ? (new ReasonsMask(paramDistributionPoint.getReasons())).intersect(new ReasonsMask(issuingDistributionPoint.getOnlySomeReasons())) : (((issuingDistributionPoint == null || issuingDistributionPoint.getOnlySomeReasons() == null) && paramDistributionPoint.getReasons() == null) ? ReasonsMask.allReasons : ((paramDistributionPoint.getReasons() == null) ? ReasonsMask.allReasons : new ReasonsMask(paramDistributionPoint.getReasons())).intersect((issuingDistributionPoint == null) ? ReasonsMask.allReasons : new ReasonsMask(issuingDistributionPoint.getOnlySomeReasons())));
  }
  
  protected static Set processCRLF(X509CRL paramX509CRL, Object paramObject, X509Certificate paramX509Certificate, PublicKey paramPublicKey, PKIXExtendedParameters paramPKIXExtendedParameters, List paramList, JcaJceHelper paramJcaJceHelper) throws AnnotatedException {
    Collection<X509Certificate> collection;
    X509CertSelector x509CertSelector = new X509CertSelector();
    try {
      byte[] arrayOfByte = PrincipalUtils.getIssuerPrincipal(paramX509CRL).getEncoded();
      x509CertSelector.setSubject(arrayOfByte);
    } catch (IOException iOException) {
      throw new AnnotatedException("Subject criteria for certificate selector to find issuer certificate for CRL could not be set.", iOException);
    } 
    PKIXCertStoreSelector pKIXCertStoreSelector = (new PKIXCertStoreSelector.Builder(x509CertSelector)).build();
    try {
      collection = CertPathValidatorUtilities.findCertificates(pKIXCertStoreSelector, paramPKIXExtendedParameters.getCertificateStores());
      collection.addAll(CertPathValidatorUtilities.findCertificates(pKIXCertStoreSelector, paramPKIXExtendedParameters.getCertStores()));
    } catch (AnnotatedException annotatedException1) {
      throw new AnnotatedException("Issuer certificate for CRL cannot be searched.", annotatedException1);
    } 
    collection.add(paramX509Certificate);
    Iterator<X509Certificate> iterator = collection.iterator();
    ArrayList<X509Certificate> arrayList = new ArrayList();
    ArrayList<PublicKey> arrayList1 = new ArrayList();
    while (iterator.hasNext()) {
      X509Certificate x509Certificate = iterator.next();
      if (x509Certificate.equals(paramX509Certificate)) {
        arrayList.add(x509Certificate);
        arrayList1.add(paramPublicKey);
        continue;
      } 
      try {
        PKIXCertPathBuilderSpi pKIXCertPathBuilderSpi = new PKIXCertPathBuilderSpi();
        X509CertSelector x509CertSelector1 = new X509CertSelector();
        x509CertSelector1.setCertificate(x509Certificate);
        PKIXExtendedParameters.Builder builder = (new PKIXExtendedParameters.Builder(paramPKIXExtendedParameters)).setTargetConstraints((new PKIXCertStoreSelector.Builder(x509CertSelector1)).build());
        if (paramList.contains(x509Certificate)) {
          builder.setRevocationEnabled(false);
        } else {
          builder.setRevocationEnabled(true);
        } 
        PKIXExtendedBuilderParameters pKIXExtendedBuilderParameters = (new PKIXExtendedBuilderParameters.Builder(builder.build())).build();
        List<? extends Certificate> list = pKIXCertPathBuilderSpi.engineBuild((CertPathParameters)pKIXExtendedBuilderParameters).getCertPath().getCertificates();
        arrayList.add(x509Certificate);
        arrayList1.add(CertPathValidatorUtilities.getNextWorkingKey(list, 0, paramJcaJceHelper));
      } catch (CertPathBuilderException certPathBuilderException) {
        throw new AnnotatedException("CertPath for CRL signer failed to validate.", certPathBuilderException);
      } catch (CertPathValidatorException certPathValidatorException) {
        throw new AnnotatedException("Public key of issuer certificate of CRL could not be retrieved.", certPathValidatorException);
      } catch (Exception exception) {
        throw new AnnotatedException(exception.getMessage());
      } 
    } 
    HashSet hashSet = new HashSet();
    AnnotatedException annotatedException = null;
    for (byte b = 0; b < arrayList.size(); b++) {
      X509Certificate x509Certificate = arrayList.get(b);
      boolean[] arrayOfBoolean = x509Certificate.getKeyUsage();
      if (arrayOfBoolean != null && (arrayOfBoolean.length < 7 || !arrayOfBoolean[6])) {
        annotatedException = new AnnotatedException("Issuer certificate key usage extension does not permit CRL signing.");
      } else {
        hashSet.add(arrayList1.get(b));
      } 
    } 
    if (hashSet.isEmpty() && annotatedException == null)
      throw new AnnotatedException("Cannot find a valid issuer certificate."); 
    if (hashSet.isEmpty() && annotatedException != null)
      throw annotatedException; 
    return hashSet;
  }
  
  protected static PublicKey processCRLG(X509CRL paramX509CRL, Set paramSet) throws AnnotatedException {
    Exception exception = null;
    for (PublicKey publicKey : paramSet) {
      try {
        paramX509CRL.verify(publicKey);
        return publicKey;
      } catch (Exception exception1) {
        exception = exception1;
      } 
    } 
    throw new AnnotatedException("Cannot verify CRL.", exception);
  }
  
  protected static X509CRL processCRLH(Set paramSet, PublicKey paramPublicKey) throws AnnotatedException {
    Exception exception = null;
    for (X509CRL x509CRL : paramSet) {
      try {
        x509CRL.verify(paramPublicKey);
        return x509CRL;
      } catch (Exception exception1) {
        exception = exception1;
      } 
    } 
    if (exception != null)
      throw new AnnotatedException("Cannot verify delta CRL.", exception); 
    return null;
  }
  
  protected static Set processCRLA1i(Date paramDate, PKIXExtendedParameters paramPKIXExtendedParameters, X509Certificate paramX509Certificate, X509CRL paramX509CRL) throws AnnotatedException {
    HashSet hashSet = new HashSet();
    if (paramPKIXExtendedParameters.isUseDeltasEnabled()) {
      CRLDistPoint cRLDistPoint = null;
      try {
        cRLDistPoint = CRLDistPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(paramX509Certificate, FRESHEST_CRL));
      } catch (AnnotatedException annotatedException) {
        throw new AnnotatedException("Freshest CRL extension could not be decoded from certificate.", annotatedException);
      } 
      if (cRLDistPoint == null)
        try {
          cRLDistPoint = CRLDistPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(paramX509CRL, FRESHEST_CRL));
        } catch (AnnotatedException annotatedException) {
          throw new AnnotatedException("Freshest CRL extension could not be decoded from CRL.", annotatedException);
        }  
      if (cRLDistPoint != null) {
        ArrayList<PKIXCRLStore> arrayList = new ArrayList();
        arrayList.addAll(paramPKIXExtendedParameters.getCRLStores());
        try {
          arrayList.addAll(CertPathValidatorUtilities.getAdditionalStoresFromCRLDistributionPoint(cRLDistPoint, paramPKIXExtendedParameters.getNamedCRLStoreMap()));
        } catch (AnnotatedException annotatedException) {
          throw new AnnotatedException("No new delta CRL locations could be added from Freshest CRL extension.", annotatedException);
        } 
        try {
          hashSet.addAll(CertPathValidatorUtilities.getDeltaCRLs(paramDate, paramX509CRL, paramPKIXExtendedParameters.getCertStores(), arrayList));
        } catch (AnnotatedException annotatedException) {
          throw new AnnotatedException("Exception obtaining delta CRLs.", annotatedException);
        } 
      } 
    } 
    return hashSet;
  }
  
  protected static Set[] processCRLA1ii(Date paramDate, PKIXExtendedParameters paramPKIXExtendedParameters, X509Certificate paramX509Certificate, X509CRL paramX509CRL) throws AnnotatedException {
    HashSet hashSet = new HashSet();
    X509CRLSelector x509CRLSelector = new X509CRLSelector();
    x509CRLSelector.setCertificateChecking(paramX509Certificate);
    try {
      x509CRLSelector.addIssuerName(PrincipalUtils.getIssuerPrincipal(paramX509CRL).getEncoded());
    } catch (IOException iOException) {
      throw new AnnotatedException("Cannot extract issuer from CRL." + iOException, iOException);
    } 
    PKIXCRLStoreSelector pKIXCRLStoreSelector = (new PKIXCRLStoreSelector.Builder(x509CRLSelector)).setCompleteCRLEnabled(true).build();
    Date date = paramDate;
    if (paramPKIXExtendedParameters.getDate() != null)
      date = paramPKIXExtendedParameters.getDate(); 
    Set set = CRL_UTIL.findCRLs(pKIXCRLStoreSelector, date, paramPKIXExtendedParameters.getCertStores(), paramPKIXExtendedParameters.getCRLStores());
    if (paramPKIXExtendedParameters.isUseDeltasEnabled())
      try {
        hashSet.addAll(CertPathValidatorUtilities.getDeltaCRLs(date, paramX509CRL, paramPKIXExtendedParameters.getCertStores(), paramPKIXExtendedParameters.getCRLStores()));
      } catch (AnnotatedException annotatedException) {
        throw new AnnotatedException("Exception obtaining delta CRLs.", annotatedException);
      }  
    return new Set[] { set, hashSet };
  }
  
  protected static void processCRLC(X509CRL paramX509CRL1, X509CRL paramX509CRL2, PKIXExtendedParameters paramPKIXExtendedParameters) throws AnnotatedException {
    if (paramX509CRL1 == null)
      return; 
    IssuingDistributionPoint issuingDistributionPoint = null;
    try {
      issuingDistributionPoint = IssuingDistributionPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(paramX509CRL2, ISSUING_DISTRIBUTION_POINT));
    } catch (Exception exception) {
      throw new AnnotatedException("Issuing distribution point extension could not be decoded.", exception);
    } 
    if (paramPKIXExtendedParameters.isUseDeltasEnabled()) {
      if (!PrincipalUtils.getIssuerPrincipal(paramX509CRL1).equals(PrincipalUtils.getIssuerPrincipal(paramX509CRL2)))
        throw new AnnotatedException("Complete CRL issuer does not match delta CRL issuer."); 
      IssuingDistributionPoint issuingDistributionPoint1 = null;
      try {
        issuingDistributionPoint1 = IssuingDistributionPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(paramX509CRL1, ISSUING_DISTRIBUTION_POINT));
      } catch (Exception exception) {
        throw new AnnotatedException("Issuing distribution point extension from delta CRL could not be decoded.", exception);
      } 
      boolean bool = false;
      if (issuingDistributionPoint == null) {
        if (issuingDistributionPoint1 == null)
          bool = true; 
      } else if (issuingDistributionPoint.equals(issuingDistributionPoint1)) {
        bool = true;
      } 
      if (!bool)
        throw new AnnotatedException("Issuing distribution point extension from delta CRL and complete CRL does not match."); 
      ASN1Primitive aSN1Primitive1 = null;
      try {
        aSN1Primitive1 = CertPathValidatorUtilities.getExtensionValue(paramX509CRL2, AUTHORITY_KEY_IDENTIFIER);
      } catch (AnnotatedException annotatedException) {
        throw new AnnotatedException("Authority key identifier extension could not be extracted from complete CRL.", annotatedException);
      } 
      ASN1Primitive aSN1Primitive2 = null;
      try {
        aSN1Primitive2 = CertPathValidatorUtilities.getExtensionValue(paramX509CRL1, AUTHORITY_KEY_IDENTIFIER);
      } catch (AnnotatedException annotatedException) {
        throw new AnnotatedException("Authority key identifier extension could not be extracted from delta CRL.", annotatedException);
      } 
      if (aSN1Primitive1 == null)
        throw new AnnotatedException("CRL authority key identifier is null."); 
      if (aSN1Primitive2 == null)
        throw new AnnotatedException("Delta CRL authority key identifier is null."); 
      if (!aSN1Primitive1.equals(aSN1Primitive2))
        throw new AnnotatedException("Delta CRL authority key identifier does not match complete CRL authority key identifier."); 
    } 
  }
  
  protected static void processCRLI(Date paramDate, X509CRL paramX509CRL, Object paramObject, CertStatus paramCertStatus, PKIXExtendedParameters paramPKIXExtendedParameters) throws AnnotatedException {
    if (paramPKIXExtendedParameters.isUseDeltasEnabled() && paramX509CRL != null)
      CertPathValidatorUtilities.getCertStatus(paramDate, paramX509CRL, paramObject, paramCertStatus); 
  }
  
  protected static void processCRLJ(Date paramDate, X509CRL paramX509CRL, Object paramObject, CertStatus paramCertStatus) throws AnnotatedException {
    if (paramCertStatus.getCertStatus() == 11)
      CertPathValidatorUtilities.getCertStatus(paramDate, paramX509CRL, paramObject, paramCertStatus); 
  }
  
  protected static PKIXPolicyNode prepareCertB(CertPath paramCertPath, int paramInt1, List[] paramArrayOfList, PKIXPolicyNode paramPKIXPolicyNode, int paramInt2) throws CertPathValidatorException {
    List<? extends Certificate> list = paramCertPath.getCertificates();
    X509Certificate x509Certificate = (X509Certificate)list.get(paramInt1);
    int i = list.size();
    int j = i - paramInt1;
    ASN1Sequence aSN1Sequence = null;
    try {
      aSN1Sequence = DERSequence.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, POLICY_MAPPINGS));
    } catch (AnnotatedException annotatedException) {
      throw new ExtCertPathValidatorException("Policy mappings extension could not be decoded.", annotatedException, paramCertPath, paramInt1);
    } 
    PKIXPolicyNode pKIXPolicyNode = paramPKIXPolicyNode;
    if (aSN1Sequence != null) {
      ASN1Sequence aSN1Sequence1 = aSN1Sequence;
      HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
      HashSet<String> hashSet = new HashSet();
      for (byte b = 0; b < aSN1Sequence1.size(); b++) {
        ASN1Sequence aSN1Sequence2 = (ASN1Sequence)aSN1Sequence1.getObjectAt(b);
        String str1 = ((ASN1ObjectIdentifier)aSN1Sequence2.getObjectAt(0)).getId();
        String str2 = ((ASN1ObjectIdentifier)aSN1Sequence2.getObjectAt(1)).getId();
        if (!hashMap.containsKey(str1)) {
          HashSet<String> hashSet1 = new HashSet();
          hashSet1.add(str2);
          hashMap.put(str1, hashSet1);
          hashSet.add(str1);
        } else {
          Set<String> set = (Set)hashMap.get(str1);
          set.add(str2);
        } 
      } 
      for (String str : hashSet) {
        if (paramInt2 > 0) {
          boolean bool = false;
          for (PKIXPolicyNode pKIXPolicyNode1 : paramArrayOfList[j]) {
            if (pKIXPolicyNode1.getValidPolicy().equals(str)) {
              bool = true;
              pKIXPolicyNode1.expectedPolicies = (Set)hashMap.get(str);
              break;
            } 
          } 
          if (!bool)
            for (PKIXPolicyNode pKIXPolicyNode1 : paramArrayOfList[j]) {
              if ("2.5.29.32.0".equals(pKIXPolicyNode1.getValidPolicy())) {
                Set set = null;
                ASN1Sequence aSN1Sequence2 = null;
                try {
                  aSN1Sequence2 = (ASN1Sequence)CertPathValidatorUtilities.getExtensionValue(x509Certificate, CERTIFICATE_POLICIES);
                } catch (AnnotatedException annotatedException) {
                  throw new ExtCertPathValidatorException("Certificate policies extension could not be decoded.", annotatedException, paramCertPath, paramInt1);
                } 
                Enumeration enumeration = aSN1Sequence2.getObjects();
                while (enumeration.hasMoreElements()) {
                  PolicyInformation policyInformation = null;
                  try {
                    policyInformation = PolicyInformation.getInstance(enumeration.nextElement());
                  } catch (Exception exception) {
                    throw new CertPathValidatorException("Policy information could not be decoded.", exception, paramCertPath, paramInt1);
                  } 
                  if ("2.5.29.32.0".equals(policyInformation.getPolicyIdentifier().getId())) {
                    try {
                      set = CertPathValidatorUtilities.getQualifierSet(policyInformation.getPolicyQualifiers());
                    } catch (CertPathValidatorException certPathValidatorException) {
                      throw new ExtCertPathValidatorException("Policy qualifier info set could not be decoded.", certPathValidatorException, paramCertPath, paramInt1);
                    } 
                    break;
                  } 
                } 
                boolean bool1 = false;
                if (x509Certificate.getCriticalExtensionOIDs() != null)
                  bool1 = x509Certificate.getCriticalExtensionOIDs().contains(CERTIFICATE_POLICIES); 
                PKIXPolicyNode pKIXPolicyNode2 = (PKIXPolicyNode)pKIXPolicyNode1.getParent();
                if ("2.5.29.32.0".equals(pKIXPolicyNode2.getValidPolicy())) {
                  PKIXPolicyNode pKIXPolicyNode3 = new PKIXPolicyNode(new ArrayList(), j, (Set)hashMap.get(str), pKIXPolicyNode2, set, str, bool1);
                  pKIXPolicyNode2.addChild(pKIXPolicyNode3);
                  paramArrayOfList[j].add(pKIXPolicyNode3);
                } 
              } 
            }  
          continue;
        } 
        if (paramInt2 <= 0) {
          Iterator<?> iterator = paramArrayOfList[j].iterator();
          while (iterator.hasNext()) {
            PKIXPolicyNode pKIXPolicyNode1 = (PKIXPolicyNode)iterator.next();
            if (pKIXPolicyNode1.getValidPolicy().equals(str)) {
              PKIXPolicyNode pKIXPolicyNode2 = (PKIXPolicyNode)pKIXPolicyNode1.getParent();
              pKIXPolicyNode2.removeChild(pKIXPolicyNode1);
              iterator.remove();
              for (int k = j - 1; k >= 0; k--) {
                List<PKIXPolicyNode> list1 = paramArrayOfList[k];
                for (byte b1 = 0; b1 < list1.size(); b1++) {
                  PKIXPolicyNode pKIXPolicyNode3 = list1.get(b1);
                  if (!pKIXPolicyNode3.hasChildren()) {
                    pKIXPolicyNode = CertPathValidatorUtilities.removePolicyNode(pKIXPolicyNode, paramArrayOfList, pKIXPolicyNode3);
                    if (pKIXPolicyNode == null)
                      break; 
                  } 
                } 
              } 
            } 
          } 
        } 
      } 
    } 
    return pKIXPolicyNode;
  }
  
  protected static void prepareNextCertA(CertPath paramCertPath, int paramInt) throws CertPathValidatorException {
    List<? extends Certificate> list = paramCertPath.getCertificates();
    X509Certificate x509Certificate = (X509Certificate)list.get(paramInt);
    ASN1Sequence aSN1Sequence = null;
    try {
      aSN1Sequence = DERSequence.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, POLICY_MAPPINGS));
    } catch (AnnotatedException annotatedException) {
      throw new ExtCertPathValidatorException("Policy mappings extension could not be decoded.", annotatedException, paramCertPath, paramInt);
    } 
    if (aSN1Sequence != null) {
      ASN1Sequence aSN1Sequence1 = aSN1Sequence;
      for (byte b = 0; b < aSN1Sequence1.size(); b++) {
        ASN1ObjectIdentifier aSN1ObjectIdentifier1 = null;
        ASN1ObjectIdentifier aSN1ObjectIdentifier2 = null;
        try {
          ASN1Sequence aSN1Sequence2 = DERSequence.getInstance(aSN1Sequence1.getObjectAt(b));
          aSN1ObjectIdentifier1 = ASN1ObjectIdentifier.getInstance(aSN1Sequence2.getObjectAt(0));
          aSN1ObjectIdentifier2 = ASN1ObjectIdentifier.getInstance(aSN1Sequence2.getObjectAt(1));
        } catch (Exception exception) {
          throw new ExtCertPathValidatorException("Policy mappings extension contents could not be decoded.", exception, paramCertPath, paramInt);
        } 
        if ("2.5.29.32.0".equals(aSN1ObjectIdentifier1.getId()))
          throw new CertPathValidatorException("IssuerDomainPolicy is anyPolicy", null, paramCertPath, paramInt); 
        if ("2.5.29.32.0".equals(aSN1ObjectIdentifier2.getId()))
          throw new CertPathValidatorException("SubjectDomainPolicy is anyPolicy,", null, paramCertPath, paramInt); 
      } 
    } 
  }
  
  protected static void processCertF(CertPath paramCertPath, int paramInt1, PKIXPolicyNode paramPKIXPolicyNode, int paramInt2) throws CertPathValidatorException {
    if (paramInt2 <= 0 && paramPKIXPolicyNode == null)
      throw new ExtCertPathValidatorException("No valid policy tree found when one expected.", null, paramCertPath, paramInt1); 
  }
  
  protected static PKIXPolicyNode processCertE(CertPath paramCertPath, int paramInt, PKIXPolicyNode paramPKIXPolicyNode) throws CertPathValidatorException {
    List<? extends Certificate> list = paramCertPath.getCertificates();
    X509Certificate x509Certificate = (X509Certificate)list.get(paramInt);
    ASN1Sequence aSN1Sequence = null;
    try {
      aSN1Sequence = DERSequence.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, CERTIFICATE_POLICIES));
    } catch (AnnotatedException annotatedException) {
      throw new ExtCertPathValidatorException("Could not read certificate policies extension from certificate.", annotatedException, paramCertPath, paramInt);
    } 
    if (aSN1Sequence == null)
      paramPKIXPolicyNode = null; 
    return paramPKIXPolicyNode;
  }
  
  protected static void processCertBC(CertPath paramCertPath, int paramInt, PKIXNameConstraintValidator paramPKIXNameConstraintValidator) throws CertPathValidatorException {
    List<? extends Certificate> list = paramCertPath.getCertificates();
    X509Certificate x509Certificate = (X509Certificate)list.get(paramInt);
    int i = list.size();
    int j = i - paramInt;
    if (!CertPathValidatorUtilities.isSelfIssued(x509Certificate) || j >= i) {
      ASN1Sequence aSN1Sequence;
      X500Name x500Name = PrincipalUtils.getSubjectPrincipal(x509Certificate);
      try {
        aSN1Sequence = DERSequence.getInstance(x500Name.getEncoded());
      } catch (Exception exception) {
        throw new CertPathValidatorException("Exception extracting subject name when checking subtrees.", exception, paramCertPath, paramInt);
      } 
      try {
        paramPKIXNameConstraintValidator.checkPermittedDN(aSN1Sequence);
        paramPKIXNameConstraintValidator.checkExcludedDN(aSN1Sequence);
      } catch (PKIXNameConstraintValidatorException pKIXNameConstraintValidatorException) {
        throw new CertPathValidatorException("Subtree check for certificate subject failed.", pKIXNameConstraintValidatorException, paramCertPath, paramInt);
      } 
      GeneralNames generalNames = null;
      try {
        generalNames = GeneralNames.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, SUBJECT_ALTERNATIVE_NAME));
      } catch (Exception exception) {
        throw new CertPathValidatorException("Subject alternative name extension could not be decoded.", exception, paramCertPath, paramInt);
      } 
      RDN[] arrayOfRDN = X500Name.getInstance(aSN1Sequence).getRDNs(BCStyle.EmailAddress);
      for (byte b = 0; b != arrayOfRDN.length; b++) {
        String str = ((ASN1String)arrayOfRDN[b].getFirst().getValue()).getString();
        GeneralName generalName = new GeneralName(1, str);
        try {
          paramPKIXNameConstraintValidator.checkPermitted(generalName);
          paramPKIXNameConstraintValidator.checkExcluded(generalName);
        } catch (PKIXNameConstraintValidatorException pKIXNameConstraintValidatorException) {
          throw new CertPathValidatorException("Subtree check for certificate subject alternative email failed.", pKIXNameConstraintValidatorException, paramCertPath, paramInt);
        } 
      } 
      if (generalNames != null) {
        GeneralName[] arrayOfGeneralName = null;
        try {
          arrayOfGeneralName = generalNames.getNames();
        } catch (Exception exception) {
          throw new CertPathValidatorException("Subject alternative name contents could not be decoded.", exception, paramCertPath, paramInt);
        } 
        for (byte b1 = 0; b1 < arrayOfGeneralName.length; b1++) {
          try {
            paramPKIXNameConstraintValidator.checkPermitted(arrayOfGeneralName[b1]);
            paramPKIXNameConstraintValidator.checkExcluded(arrayOfGeneralName[b1]);
          } catch (PKIXNameConstraintValidatorException pKIXNameConstraintValidatorException) {
            throw new CertPathValidatorException("Subtree check for certificate subject alternative name failed.", pKIXNameConstraintValidatorException, paramCertPath, paramInt);
          } 
        } 
      } 
    } 
  }
  
  protected static PKIXPolicyNode processCertD(CertPath paramCertPath, int paramInt1, Set<String> paramSet, PKIXPolicyNode paramPKIXPolicyNode, List[] paramArrayOfList, int paramInt2) throws CertPathValidatorException {
    List<? extends Certificate> list = paramCertPath.getCertificates();
    X509Certificate x509Certificate = (X509Certificate)list.get(paramInt1);
    int i = list.size();
    int j = i - paramInt1;
    ASN1Sequence aSN1Sequence = null;
    try {
      aSN1Sequence = DERSequence.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, CERTIFICATE_POLICIES));
    } catch (AnnotatedException annotatedException) {
      throw new ExtCertPathValidatorException("Could not read certificate policies extension from certificate.", annotatedException, paramCertPath, paramInt1);
    } 
    if (aSN1Sequence != null && paramPKIXPolicyNode != null) {
      Enumeration enumeration = aSN1Sequence.getObjects();
      HashSet<String> hashSet = new HashSet();
      while (enumeration.hasMoreElements()) {
        PolicyInformation policyInformation = PolicyInformation.getInstance(enumeration.nextElement());
        ASN1ObjectIdentifier aSN1ObjectIdentifier = policyInformation.getPolicyIdentifier();
        hashSet.add(aSN1ObjectIdentifier.getId());
        if (!"2.5.29.32.0".equals(aSN1ObjectIdentifier.getId())) {
          Set set1 = null;
          try {
            set1 = CertPathValidatorUtilities.getQualifierSet(policyInformation.getPolicyQualifiers());
          } catch (CertPathValidatorException certPathValidatorException) {
            throw new ExtCertPathValidatorException("Policy qualifier info set could not be build.", certPathValidatorException, paramCertPath, paramInt1);
          } 
          boolean bool = CertPathValidatorUtilities.processCertD1i(j, paramArrayOfList, aSN1ObjectIdentifier, set1);
          if (!bool)
            CertPathValidatorUtilities.processCertD1ii(j, paramArrayOfList, aSN1ObjectIdentifier, set1); 
        } 
      } 
      if (paramSet.isEmpty() || paramSet.contains("2.5.29.32.0")) {
        paramSet.clear();
        paramSet.addAll(hashSet);
      } else {
        Iterator<String> iterator = paramSet.iterator();
        HashSet<? extends String> hashSet1 = new HashSet();
        while (iterator.hasNext()) {
          Object object = iterator.next();
          if (hashSet.contains(object))
            hashSet1.add(object); 
        } 
        paramSet.clear();
        paramSet.addAll(hashSet1);
      } 
      if (paramInt2 > 0 || (j < i && CertPathValidatorUtilities.isSelfIssued(x509Certificate))) {
        enumeration = aSN1Sequence.getObjects();
        while (enumeration.hasMoreElements()) {
          PolicyInformation policyInformation = PolicyInformation.getInstance(enumeration.nextElement());
          if ("2.5.29.32.0".equals(policyInformation.getPolicyIdentifier().getId())) {
            Set set1 = CertPathValidatorUtilities.getQualifierSet(policyInformation.getPolicyQualifiers());
            List<PKIXPolicyNode> list1 = paramArrayOfList[j - 1];
            for (byte b = 0; b < list1.size(); b++) {
              PKIXPolicyNode pKIXPolicyNode1 = list1.get(b);
              for (String str1 : pKIXPolicyNode1.getExpectedPolicies()) {
                String str2;
                if (str1 instanceof String) {
                  str2 = str1;
                } else if (str1 instanceof ASN1ObjectIdentifier) {
                  str2 = ((ASN1ObjectIdentifier)str1).getId();
                } else {
                  continue;
                } 
                boolean bool = false;
                Iterator<PKIXPolicyNode> iterator = pKIXPolicyNode1.getChildren();
                while (iterator.hasNext()) {
                  PKIXPolicyNode pKIXPolicyNode2 = iterator.next();
                  if (str2.equals(pKIXPolicyNode2.getValidPolicy()))
                    bool = true; 
                } 
                if (!bool) {
                  HashSet<String> hashSet1 = new HashSet();
                  hashSet1.add(str2);
                  PKIXPolicyNode pKIXPolicyNode2 = new PKIXPolicyNode(new ArrayList(), j, hashSet1, pKIXPolicyNode1, set1, str2, false);
                  pKIXPolicyNode1.addChild(pKIXPolicyNode2);
                  paramArrayOfList[j].add(pKIXPolicyNode2);
                } 
              } 
            } 
            break;
          } 
        } 
      } 
      PKIXPolicyNode pKIXPolicyNode = paramPKIXPolicyNode;
      for (int k = j - 1; k >= 0; k--) {
        List<PKIXPolicyNode> list1 = paramArrayOfList[k];
        for (byte b = 0; b < list1.size(); b++) {
          PKIXPolicyNode pKIXPolicyNode1 = list1.get(b);
          if (!pKIXPolicyNode1.hasChildren()) {
            pKIXPolicyNode = CertPathValidatorUtilities.removePolicyNode(pKIXPolicyNode, paramArrayOfList, pKIXPolicyNode1);
            if (pKIXPolicyNode == null)
              break; 
          } 
        } 
      } 
      Set<String> set = x509Certificate.getCriticalExtensionOIDs();
      if (set != null) {
        boolean bool = set.contains(CERTIFICATE_POLICIES);
        List<PKIXPolicyNode> list1 = paramArrayOfList[j];
        for (byte b = 0; b < list1.size(); b++) {
          PKIXPolicyNode pKIXPolicyNode1 = list1.get(b);
          pKIXPolicyNode1.setCritical(bool);
        } 
      } 
      return pKIXPolicyNode;
    } 
    return null;
  }
  
  protected static void processCertA(CertPath paramCertPath, PKIXExtendedParameters paramPKIXExtendedParameters, int paramInt, PublicKey paramPublicKey, boolean paramBoolean, X500Name paramX500Name, X509Certificate paramX509Certificate, JcaJceHelper paramJcaJceHelper) throws ExtCertPathValidatorException {
    List<? extends Certificate> list = paramCertPath.getCertificates();
    X509Certificate x509Certificate = (X509Certificate)list.get(paramInt);
    if (!paramBoolean)
      try {
        CertPathValidatorUtilities.verifyX509Certificate(x509Certificate, paramPublicKey, paramPKIXExtendedParameters.getSigProvider());
      } catch (GeneralSecurityException generalSecurityException) {
        throw new ExtCertPathValidatorException("Could not validate certificate signature.", generalSecurityException, paramCertPath, paramInt);
      }  
    try {
      x509Certificate.checkValidity(CertPathValidatorUtilities.getValidCertDateFromValidityModel(paramPKIXExtendedParameters, paramCertPath, paramInt));
    } catch (CertificateExpiredException certificateExpiredException) {
      throw new ExtCertPathValidatorException("Could not validate certificate: " + certificateExpiredException.getMessage(), certificateExpiredException, paramCertPath, paramInt);
    } catch (CertificateNotYetValidException certificateNotYetValidException) {
      throw new ExtCertPathValidatorException("Could not validate certificate: " + certificateNotYetValidException.getMessage(), certificateNotYetValidException, paramCertPath, paramInt);
    } catch (AnnotatedException annotatedException) {
      throw new ExtCertPathValidatorException("Could not validate time of certificate.", annotatedException, paramCertPath, paramInt);
    } 
    if (paramPKIXExtendedParameters.isRevocationEnabled())
      try {
        checkCRLs(paramPKIXExtendedParameters, x509Certificate, CertPathValidatorUtilities.getValidCertDateFromValidityModel(paramPKIXExtendedParameters, paramCertPath, paramInt), paramX509Certificate, paramPublicKey, list, paramJcaJceHelper);
      } catch (AnnotatedException annotatedException) {
        Throwable throwable = annotatedException;
        if (null != annotatedException.getCause())
          throwable = annotatedException.getCause(); 
        throw new ExtCertPathValidatorException(annotatedException.getMessage(), throwable, paramCertPath, paramInt);
      }  
    if (!PrincipalUtils.getEncodedIssuerPrincipal(x509Certificate).equals(paramX500Name))
      throw new ExtCertPathValidatorException("IssuerName(" + PrincipalUtils.getEncodedIssuerPrincipal(x509Certificate) + ") does not match SubjectName(" + paramX500Name + ") of signing certificate.", null, paramCertPath, paramInt); 
  }
  
  protected static int prepareNextCertI1(CertPath paramCertPath, int paramInt1, int paramInt2) throws CertPathValidatorException {
    List<? extends Certificate> list = paramCertPath.getCertificates();
    X509Certificate x509Certificate = (X509Certificate)list.get(paramInt1);
    ASN1Sequence aSN1Sequence = null;
    try {
      aSN1Sequence = DERSequence.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, POLICY_CONSTRAINTS));
    } catch (Exception exception) {
      throw new ExtCertPathValidatorException("Policy constraints extension cannot be decoded.", exception, paramCertPath, paramInt1);
    } 
    if (aSN1Sequence != null) {
      Enumeration enumeration = aSN1Sequence.getObjects();
      while (enumeration.hasMoreElements()) {
        try {
          ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(enumeration.nextElement());
          if (aSN1TaggedObject.getTagNo() == 0) {
            int i = ASN1Integer.getInstance(aSN1TaggedObject, false).getValue().intValue();
            if (i < paramInt2)
              return i; 
            break;
          } 
        } catch (IllegalArgumentException illegalArgumentException) {
          throw new ExtCertPathValidatorException("Policy constraints extension contents cannot be decoded.", illegalArgumentException, paramCertPath, paramInt1);
        } 
      } 
    } 
    return paramInt2;
  }
  
  protected static int prepareNextCertI2(CertPath paramCertPath, int paramInt1, int paramInt2) throws CertPathValidatorException {
    List<? extends Certificate> list = paramCertPath.getCertificates();
    X509Certificate x509Certificate = (X509Certificate)list.get(paramInt1);
    ASN1Sequence aSN1Sequence = null;
    try {
      aSN1Sequence = DERSequence.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, POLICY_CONSTRAINTS));
    } catch (Exception exception) {
      throw new ExtCertPathValidatorException("Policy constraints extension cannot be decoded.", exception, paramCertPath, paramInt1);
    } 
    if (aSN1Sequence != null) {
      Enumeration enumeration = aSN1Sequence.getObjects();
      while (enumeration.hasMoreElements()) {
        try {
          ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(enumeration.nextElement());
          if (aSN1TaggedObject.getTagNo() == 1) {
            int i = ASN1Integer.getInstance(aSN1TaggedObject, false).getValue().intValue();
            if (i < paramInt2)
              return i; 
            break;
          } 
        } catch (IllegalArgumentException illegalArgumentException) {
          throw new ExtCertPathValidatorException("Policy constraints extension contents cannot be decoded.", illegalArgumentException, paramCertPath, paramInt1);
        } 
      } 
    } 
    return paramInt2;
  }
  
  protected static void prepareNextCertG(CertPath paramCertPath, int paramInt, PKIXNameConstraintValidator paramPKIXNameConstraintValidator) throws CertPathValidatorException {
    List<? extends Certificate> list = paramCertPath.getCertificates();
    X509Certificate x509Certificate = (X509Certificate)list.get(paramInt);
    NameConstraints nameConstraints = null;
    try {
      ASN1Sequence aSN1Sequence = DERSequence.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, NAME_CONSTRAINTS));
      if (aSN1Sequence != null)
        nameConstraints = NameConstraints.getInstance(aSN1Sequence); 
    } catch (Exception exception) {
      throw new ExtCertPathValidatorException("Name constraints extension could not be decoded.", exception, paramCertPath, paramInt);
    } 
    if (nameConstraints != null) {
      GeneralSubtree[] arrayOfGeneralSubtree1 = nameConstraints.getPermittedSubtrees();
      if (arrayOfGeneralSubtree1 != null)
        try {
          paramPKIXNameConstraintValidator.intersectPermittedSubtree(arrayOfGeneralSubtree1);
        } catch (Exception exception) {
          throw new ExtCertPathValidatorException("Permitted subtrees cannot be build from name constraints extension.", exception, paramCertPath, paramInt);
        }  
      GeneralSubtree[] arrayOfGeneralSubtree2 = nameConstraints.getExcludedSubtrees();
      if (arrayOfGeneralSubtree2 != null)
        for (byte b = 0; b != arrayOfGeneralSubtree2.length; b++) {
          try {
            paramPKIXNameConstraintValidator.addExcludedSubtree(arrayOfGeneralSubtree2[b]);
          } catch (Exception exception) {
            throw new ExtCertPathValidatorException("Excluded subtrees cannot be build from name constraints extension.", exception, paramCertPath, paramInt);
          } 
        }  
    } 
  }
  
  private static void checkCRL(DistributionPoint paramDistributionPoint, PKIXExtendedParameters paramPKIXExtendedParameters, X509Certificate paramX509Certificate1, Date paramDate, X509Certificate paramX509Certificate2, PublicKey paramPublicKey, CertStatus paramCertStatus, ReasonsMask paramReasonsMask, List paramList, JcaJceHelper paramJcaJceHelper) throws AnnotatedException {
    Date date = new Date(System.currentTimeMillis());
    if (paramDate.getTime() > date.getTime())
      throw new AnnotatedException("Validation time is in future."); 
    Set set = CertPathValidatorUtilities.getCompleteCRLs(paramDistributionPoint, paramX509Certificate1, date, paramPKIXExtendedParameters);
    boolean bool = false;
    AnnotatedException annotatedException = null;
    Iterator<X509CRL> iterator = set.iterator();
    while (iterator.hasNext() && paramCertStatus.getCertStatus() == 11 && !paramReasonsMask.isAllReasons()) {
      try {
        X509CRL x509CRL1 = iterator.next();
        ReasonsMask reasonsMask = processCRLD(x509CRL1, paramDistributionPoint);
        if (!reasonsMask.hasNewReasons(paramReasonsMask))
          continue; 
        Set set1 = processCRLF(x509CRL1, paramX509Certificate1, paramX509Certificate2, paramPublicKey, paramPKIXExtendedParameters, paramList, paramJcaJceHelper);
        PublicKey publicKey = processCRLG(x509CRL1, set1);
        X509CRL x509CRL2 = null;
        Date date1 = date;
        if (paramPKIXExtendedParameters.getDate() != null)
          date1 = paramPKIXExtendedParameters.getDate(); 
        if (paramPKIXExtendedParameters.isUseDeltasEnabled()) {
          Set set3 = CertPathValidatorUtilities.getDeltaCRLs(date1, x509CRL1, paramPKIXExtendedParameters.getCertStores(), paramPKIXExtendedParameters.getCRLStores());
          x509CRL2 = processCRLH(set3, publicKey);
        } 
        if (paramPKIXExtendedParameters.getValidityModel() != 1 && paramX509Certificate1.getNotAfter().getTime() < x509CRL1.getThisUpdate().getTime())
          throw new AnnotatedException("No valid CRL for current time found."); 
        processCRLB1(paramDistributionPoint, paramX509Certificate1, x509CRL1);
        processCRLB2(paramDistributionPoint, paramX509Certificate1, x509CRL1);
        processCRLC(x509CRL2, x509CRL1, paramPKIXExtendedParameters);
        processCRLI(paramDate, x509CRL2, paramX509Certificate1, paramCertStatus, paramPKIXExtendedParameters);
        processCRLJ(paramDate, x509CRL1, paramX509Certificate1, paramCertStatus);
        if (paramCertStatus.getCertStatus() == 8)
          paramCertStatus.setCertStatus(11); 
        paramReasonsMask.addReasons(reasonsMask);
        Set<String> set2 = x509CRL1.getCriticalExtensionOIDs();
        if (set2 != null) {
          set2 = new HashSet<String>(set2);
          set2.remove(Extension.issuingDistributionPoint.getId());
          set2.remove(Extension.deltaCRLIndicator.getId());
          if (!set2.isEmpty())
            throw new AnnotatedException("CRL contains unsupported critical extensions."); 
        } 
        if (x509CRL2 != null) {
          set2 = x509CRL2.getCriticalExtensionOIDs();
          if (set2 != null) {
            set2 = new HashSet<String>(set2);
            set2.remove(Extension.issuingDistributionPoint.getId());
            set2.remove(Extension.deltaCRLIndicator.getId());
            if (!set2.isEmpty())
              throw new AnnotatedException("Delta CRL contains unsupported critical extension."); 
          } 
        } 
        bool = true;
      } catch (AnnotatedException annotatedException1) {
        annotatedException = annotatedException1;
      } 
    } 
    if (!bool)
      throw annotatedException; 
  }
  
  protected static void checkCRLs(PKIXExtendedParameters paramPKIXExtendedParameters, X509Certificate paramX509Certificate1, Date paramDate, X509Certificate paramX509Certificate2, PublicKey paramPublicKey, List paramList, JcaJceHelper paramJcaJceHelper) throws AnnotatedException {
    AnnotatedException annotatedException = null;
    CRLDistPoint cRLDistPoint = null;
    try {
      cRLDistPoint = CRLDistPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(paramX509Certificate1, CRL_DISTRIBUTION_POINTS));
    } catch (Exception exception) {
      throw new AnnotatedException("CRL distribution point extension could not be read.", exception);
    } 
    PKIXExtendedParameters.Builder builder = new PKIXExtendedParameters.Builder(paramPKIXExtendedParameters);
    try {
      List<PKIXCRLStore> list = CertPathValidatorUtilities.getAdditionalStoresFromCRLDistributionPoint(cRLDistPoint, paramPKIXExtendedParameters.getNamedCRLStoreMap());
      Iterator<PKIXCRLStore> iterator = list.iterator();
      while (iterator.hasNext())
        builder.addCRLStore(iterator.next()); 
    } catch (AnnotatedException annotatedException1) {
      throw new AnnotatedException("No additional CRL locations could be decoded from CRL distribution point extension.", annotatedException1);
    } 
    CertStatus certStatus = new CertStatus();
    ReasonsMask reasonsMask = new ReasonsMask();
    PKIXExtendedParameters pKIXExtendedParameters = builder.build();
    boolean bool = false;
    if (cRLDistPoint != null) {
      DistributionPoint[] arrayOfDistributionPoint = null;
      try {
        arrayOfDistributionPoint = cRLDistPoint.getDistributionPoints();
      } catch (Exception exception) {
        throw new AnnotatedException("Distribution points could not be read.", exception);
      } 
      if (arrayOfDistributionPoint != null)
        for (byte b = 0; b < arrayOfDistributionPoint.length && certStatus.getCertStatus() == 11 && !reasonsMask.isAllReasons(); b++) {
          try {
            checkCRL(arrayOfDistributionPoint[b], pKIXExtendedParameters, paramX509Certificate1, paramDate, paramX509Certificate2, paramPublicKey, certStatus, reasonsMask, paramList, paramJcaJceHelper);
            bool = true;
          } catch (AnnotatedException annotatedException1) {
            annotatedException = annotatedException1;
          } 
        }  
    } 
    if (certStatus.getCertStatus() == 11 && !reasonsMask.isAllReasons())
      try {
        ASN1Primitive aSN1Primitive = null;
        try {
          aSN1Primitive = (new ASN1InputStream(PrincipalUtils.getEncodedIssuerPrincipal(paramX509Certificate1).getEncoded())).readObject();
        } catch (Exception exception) {
          throw new AnnotatedException("Issuer from certificate for CRL could not be reencoded.", exception);
        } 
        DistributionPoint distributionPoint = new DistributionPoint(new DistributionPointName(0, (ASN1Encodable)new GeneralNames(new GeneralName(4, (ASN1Encodable)aSN1Primitive))), null, null);
        PKIXExtendedParameters pKIXExtendedParameters1 = (PKIXExtendedParameters)paramPKIXExtendedParameters.clone();
        checkCRL(distributionPoint, pKIXExtendedParameters1, paramX509Certificate1, paramDate, paramX509Certificate2, paramPublicKey, certStatus, reasonsMask, paramList, paramJcaJceHelper);
        bool = true;
      } catch (AnnotatedException annotatedException1) {
        annotatedException = annotatedException1;
      }  
    if (!bool) {
      if (annotatedException instanceof AnnotatedException)
        throw annotatedException; 
      throw new AnnotatedException("No valid CRL found.", annotatedException);
    } 
    if (certStatus.getCertStatus() != 11) {
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
      simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
      String str = "Certificate revocation after " + simpleDateFormat.format(certStatus.getRevocationDate());
      str = str + ", reason: " + crlReasons[certStatus.getCertStatus()];
      throw new AnnotatedException(str);
    } 
    if (!reasonsMask.isAllReasons() && certStatus.getCertStatus() == 11)
      certStatus.setCertStatus(12); 
    if (certStatus.getCertStatus() == 12)
      throw new AnnotatedException("Certificate status could not be determined."); 
  }
  
  protected static int prepareNextCertJ(CertPath paramCertPath, int paramInt1, int paramInt2) throws CertPathValidatorException {
    List<? extends Certificate> list = paramCertPath.getCertificates();
    X509Certificate x509Certificate = (X509Certificate)list.get(paramInt1);
    ASN1Integer aSN1Integer = null;
    try {
      aSN1Integer = ASN1Integer.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, INHIBIT_ANY_POLICY));
    } catch (Exception exception) {
      throw new ExtCertPathValidatorException("Inhibit any-policy extension cannot be decoded.", exception, paramCertPath, paramInt1);
    } 
    if (aSN1Integer != null) {
      int i = aSN1Integer.getValue().intValue();
      if (i < paramInt2)
        return i; 
    } 
    return paramInt2;
  }
  
  protected static void prepareNextCertK(CertPath paramCertPath, int paramInt) throws CertPathValidatorException {
    List<? extends Certificate> list = paramCertPath.getCertificates();
    X509Certificate x509Certificate = (X509Certificate)list.get(paramInt);
    BasicConstraints basicConstraints = null;
    try {
      basicConstraints = BasicConstraints.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, BASIC_CONSTRAINTS));
    } catch (Exception exception) {
      throw new ExtCertPathValidatorException("Basic constraints extension cannot be decoded.", exception, paramCertPath, paramInt);
    } 
    if (basicConstraints != null) {
      if (!basicConstraints.isCA())
        throw new CertPathValidatorException("Not a CA certificate"); 
    } else {
      throw new CertPathValidatorException("Intermediate certificate lacks BasicConstraints");
    } 
  }
  
  protected static int prepareNextCertL(CertPath paramCertPath, int paramInt1, int paramInt2) throws CertPathValidatorException {
    List<? extends Certificate> list = paramCertPath.getCertificates();
    X509Certificate x509Certificate = (X509Certificate)list.get(paramInt1);
    if (!CertPathValidatorUtilities.isSelfIssued(x509Certificate)) {
      if (paramInt2 <= 0)
        throw new ExtCertPathValidatorException("Max path length not greater than zero", null, paramCertPath, paramInt1); 
      return paramInt2 - 1;
    } 
    return paramInt2;
  }
  
  protected static int prepareNextCertM(CertPath paramCertPath, int paramInt1, int paramInt2) throws CertPathValidatorException {
    List<? extends Certificate> list = paramCertPath.getCertificates();
    X509Certificate x509Certificate = (X509Certificate)list.get(paramInt1);
    BasicConstraints basicConstraints = null;
    try {
      basicConstraints = BasicConstraints.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, BASIC_CONSTRAINTS));
    } catch (Exception exception) {
      throw new ExtCertPathValidatorException("Basic constraints extension cannot be decoded.", exception, paramCertPath, paramInt1);
    } 
    if (basicConstraints != null) {
      BigInteger bigInteger = basicConstraints.getPathLenConstraint();
      if (bigInteger != null) {
        int i = bigInteger.intValue();
        if (i < paramInt2)
          return i; 
      } 
    } 
    return paramInt2;
  }
  
  protected static void prepareNextCertN(CertPath paramCertPath, int paramInt) throws CertPathValidatorException {
    List<? extends Certificate> list = paramCertPath.getCertificates();
    X509Certificate x509Certificate = (X509Certificate)list.get(paramInt);
    boolean[] arrayOfBoolean = x509Certificate.getKeyUsage();
    if (arrayOfBoolean != null && !arrayOfBoolean[5])
      throw new ExtCertPathValidatorException("Issuer certificate keyusage extension is critical and does not permit key signing.", null, paramCertPath, paramInt); 
  }
  
  protected static void prepareNextCertO(CertPath paramCertPath, int paramInt, Set<String> paramSet, List paramList) throws CertPathValidatorException {
    List<? extends Certificate> list = paramCertPath.getCertificates();
    X509Certificate x509Certificate = (X509Certificate)list.get(paramInt);
    Iterator<PKIXCertPathChecker> iterator = paramList.iterator();
    while (iterator.hasNext()) {
      try {
        ((PKIXCertPathChecker)iterator.next()).check(x509Certificate, paramSet);
      } catch (CertPathValidatorException certPathValidatorException) {
        throw new CertPathValidatorException(certPathValidatorException.getMessage(), certPathValidatorException.getCause(), paramCertPath, paramInt);
      } 
    } 
    if (!paramSet.isEmpty())
      throw new ExtCertPathValidatorException("Certificate has unsupported critical extension: " + paramSet, null, paramCertPath, paramInt); 
  }
  
  protected static int prepareNextCertH1(CertPath paramCertPath, int paramInt1, int paramInt2) {
    List<? extends Certificate> list = paramCertPath.getCertificates();
    X509Certificate x509Certificate = (X509Certificate)list.get(paramInt1);
    return (!CertPathValidatorUtilities.isSelfIssued(x509Certificate) && paramInt2 != 0) ? (paramInt2 - 1) : paramInt2;
  }
  
  protected static int prepareNextCertH2(CertPath paramCertPath, int paramInt1, int paramInt2) {
    List<? extends Certificate> list = paramCertPath.getCertificates();
    X509Certificate x509Certificate = (X509Certificate)list.get(paramInt1);
    return (!CertPathValidatorUtilities.isSelfIssued(x509Certificate) && paramInt2 != 0) ? (paramInt2 - 1) : paramInt2;
  }
  
  protected static int prepareNextCertH3(CertPath paramCertPath, int paramInt1, int paramInt2) {
    List<? extends Certificate> list = paramCertPath.getCertificates();
    X509Certificate x509Certificate = (X509Certificate)list.get(paramInt1);
    return (!CertPathValidatorUtilities.isSelfIssued(x509Certificate) && paramInt2 != 0) ? (paramInt2 - 1) : paramInt2;
  }
  
  protected static int wrapupCertA(int paramInt, X509Certificate paramX509Certificate) {
    if (!CertPathValidatorUtilities.isSelfIssued(paramX509Certificate) && paramInt != 0)
      paramInt--; 
    return paramInt;
  }
  
  protected static int wrapupCertB(CertPath paramCertPath, int paramInt1, int paramInt2) throws CertPathValidatorException {
    List<? extends Certificate> list = paramCertPath.getCertificates();
    X509Certificate x509Certificate = (X509Certificate)list.get(paramInt1);
    ASN1Sequence aSN1Sequence = null;
    try {
      aSN1Sequence = DERSequence.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, POLICY_CONSTRAINTS));
    } catch (AnnotatedException annotatedException) {
      throw new ExtCertPathValidatorException("Policy constraints could not be decoded.", annotatedException, paramCertPath, paramInt1);
    } 
    if (aSN1Sequence != null) {
      Enumeration<ASN1TaggedObject> enumeration = aSN1Sequence.getObjects();
      while (enumeration.hasMoreElements()) {
        int i;
        ASN1TaggedObject aSN1TaggedObject = enumeration.nextElement();
        switch (aSN1TaggedObject.getTagNo()) {
          case 0:
            try {
              i = ASN1Integer.getInstance(aSN1TaggedObject, false).getValue().intValue();
            } catch (Exception exception) {
              throw new ExtCertPathValidatorException("Policy constraints requireExplicitPolicy field could not be decoded.", exception, paramCertPath, paramInt1);
            } 
            if (i == 0)
              return 0; 
        } 
      } 
    } 
    return paramInt2;
  }
  
  protected static void wrapupCertF(CertPath paramCertPath, int paramInt, List paramList, Set<String> paramSet) throws CertPathValidatorException {
    List<? extends Certificate> list = paramCertPath.getCertificates();
    X509Certificate x509Certificate = (X509Certificate)list.get(paramInt);
    Iterator<PKIXCertPathChecker> iterator = paramList.iterator();
    while (iterator.hasNext()) {
      try {
        ((PKIXCertPathChecker)iterator.next()).check(x509Certificate, paramSet);
      } catch (CertPathValidatorException certPathValidatorException) {
        throw new ExtCertPathValidatorException("Additional certificate path checker failed.", certPathValidatorException, paramCertPath, paramInt);
      } 
    } 
    if (!paramSet.isEmpty())
      throw new ExtCertPathValidatorException("Certificate has unsupported critical extension: " + paramSet, null, paramCertPath, paramInt); 
  }
  
  protected static PKIXPolicyNode wrapupCertG(CertPath paramCertPath, PKIXExtendedParameters paramPKIXExtendedParameters, Set paramSet1, int paramInt, List[] paramArrayOfList, PKIXPolicyNode paramPKIXPolicyNode, Set paramSet2) throws CertPathValidatorException {
    PKIXPolicyNode pKIXPolicyNode;
    int i = paramCertPath.getCertificates().size();
    if (paramPKIXPolicyNode == null) {
      if (paramPKIXExtendedParameters.isExplicitPolicyRequired())
        throw new ExtCertPathValidatorException("Explicit policy requested but none available.", null, paramCertPath, paramInt); 
      pKIXPolicyNode = null;
    } else if (CertPathValidatorUtilities.isAnyPolicy(paramSet1)) {
      if (paramPKIXExtendedParameters.isExplicitPolicyRequired()) {
        if (paramSet2.isEmpty())
          throw new ExtCertPathValidatorException("Explicit policy requested but none available.", null, paramCertPath, paramInt); 
        HashSet hashSet = new HashSet();
        for (byte b = 0; b < paramArrayOfList.length; b++) {
          List<PKIXPolicyNode> list = paramArrayOfList[b];
          for (byte b1 = 0; b1 < list.size(); b1++) {
            PKIXPolicyNode pKIXPolicyNode1 = list.get(b1);
            if ("2.5.29.32.0".equals(pKIXPolicyNode1.getValidPolicy())) {
              Iterator iterator = pKIXPolicyNode1.getChildren();
              while (iterator.hasNext())
                hashSet.add(iterator.next()); 
            } 
          } 
        } 
        for (PKIXPolicyNode pKIXPolicyNode1 : hashSet) {
          String str = pKIXPolicyNode1.getValidPolicy();
          if (!paramSet2.contains(str));
        } 
        if (paramPKIXPolicyNode != null)
          for (int j = i - 1; j >= 0; j--) {
            List<PKIXPolicyNode> list = paramArrayOfList[j];
            for (byte b1 = 0; b1 < list.size(); b1++) {
              PKIXPolicyNode pKIXPolicyNode1 = list.get(b1);
              if (!pKIXPolicyNode1.hasChildren())
                paramPKIXPolicyNode = CertPathValidatorUtilities.removePolicyNode(paramPKIXPolicyNode, paramArrayOfList, pKIXPolicyNode1); 
            } 
          }  
      } 
      pKIXPolicyNode = paramPKIXPolicyNode;
    } else {
      HashSet<PKIXPolicyNode> hashSet = new HashSet();
      for (byte b = 0; b < paramArrayOfList.length; b++) {
        List<PKIXPolicyNode> list = paramArrayOfList[b];
        for (byte b1 = 0; b1 < list.size(); b1++) {
          PKIXPolicyNode pKIXPolicyNode1 = list.get(b1);
          if ("2.5.29.32.0".equals(pKIXPolicyNode1.getValidPolicy())) {
            Iterator<PKIXPolicyNode> iterator = pKIXPolicyNode1.getChildren();
            while (iterator.hasNext()) {
              PKIXPolicyNode pKIXPolicyNode2 = iterator.next();
              if (!"2.5.29.32.0".equals(pKIXPolicyNode2.getValidPolicy()))
                hashSet.add(pKIXPolicyNode2); 
            } 
          } 
        } 
      } 
      for (PKIXPolicyNode pKIXPolicyNode1 : hashSet) {
        String str = pKIXPolicyNode1.getValidPolicy();
        if (!paramSet1.contains(str))
          paramPKIXPolicyNode = CertPathValidatorUtilities.removePolicyNode(paramPKIXPolicyNode, paramArrayOfList, pKIXPolicyNode1); 
      } 
      if (paramPKIXPolicyNode != null)
        for (int j = i - 1; j >= 0; j--) {
          List<PKIXPolicyNode> list = paramArrayOfList[j];
          for (byte b1 = 0; b1 < list.size(); b1++) {
            PKIXPolicyNode pKIXPolicyNode1 = list.get(b1);
            if (!pKIXPolicyNode1.hasChildren())
              paramPKIXPolicyNode = CertPathValidatorUtilities.removePolicyNode(paramPKIXPolicyNode, paramArrayOfList, pKIXPolicyNode1); 
          } 
        }  
      pKIXPolicyNode = paramPKIXPolicyNode;
    } 
    return pKIXPolicyNode;
  }
}
