package org.bouncycastle.jce.provider;

import java.security.InvalidAlgorithmParameterException;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.CertPathValidatorSpi;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.jcajce.PKIXExtendedBuilderParameters;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.exception.ExtCertPathValidatorException;
import org.bouncycastle.x509.ExtendedPKIXParameters;

public class PKIXCertPathValidatorSpi extends CertPathValidatorSpi {
  private final JcaJceHelper helper = (JcaJceHelper)new BCJcaJceHelper();
  
  public CertPathValidatorResult engineValidate(CertPath paramCertPath, CertPathParameters paramCertPathParameters) throws CertPathValidatorException, InvalidAlgorithmParameterException {
    TrustAnchor trustAnchor;
    int m;
    int n;
    PublicKey publicKey;
    X500Name x500Name;
    if (paramCertPathParameters instanceof PKIXParameters) {
      PKIXExtendedParameters.Builder builder = new PKIXExtendedParameters.Builder((PKIXParameters)paramCertPathParameters);
      if (paramCertPathParameters instanceof ExtendedPKIXParameters) {
        ExtendedPKIXParameters extendedPKIXParameters = (ExtendedPKIXParameters)paramCertPathParameters;
        builder.setUseDeltasEnabled(extendedPKIXParameters.isUseDeltasEnabled());
        builder.setValidityModel(extendedPKIXParameters.getValidityModel());
      } 
      pKIXExtendedParameters = builder.build();
    } else if (paramCertPathParameters instanceof PKIXExtendedBuilderParameters) {
      pKIXExtendedParameters = ((PKIXExtendedBuilderParameters)paramCertPathParameters).getBaseParameters();
    } else if (paramCertPathParameters instanceof PKIXExtendedParameters) {
      pKIXExtendedParameters = (PKIXExtendedParameters)paramCertPathParameters;
    } else {
      throw new InvalidAlgorithmParameterException("Parameters must be a " + PKIXParameters.class.getName() + " instance.");
    } 
    if (pKIXExtendedParameters.getTrustAnchors() == null)
      throw new InvalidAlgorithmParameterException("trustAnchors is null, this is not allowed for certification path validation."); 
    List<? extends Certificate> list = paramCertPath.getCertificates();
    int i = list.size();
    if (list.isEmpty())
      throw new CertPathValidatorException("Certification path is empty.", null, paramCertPath, -1); 
    Set set = pKIXExtendedParameters.getInitialPolicies();
    try {
      trustAnchor = CertPathValidatorUtilities.findTrustAnchor((X509Certificate)list.get(list.size() - 1), pKIXExtendedParameters.getTrustAnchors(), pKIXExtendedParameters.getSigProvider());
      if (trustAnchor == null)
        throw new CertPathValidatorException("Trust anchor for certification path not found.", null, paramCertPath, -1); 
      checkCertificate(trustAnchor.getTrustedCert());
    } catch (AnnotatedException annotatedException) {
      throw new CertPathValidatorException(annotatedException.getMessage(), annotatedException.getUnderlyingException(), paramCertPath, list.size() - 1);
    } 
    PKIXExtendedParameters pKIXExtendedParameters = (new PKIXExtendedParameters.Builder(pKIXExtendedParameters)).setTrustAnchor(trustAnchor).build();
    int j = 0;
    ArrayList[] arrayOfArrayList = new ArrayList[i + 1];
    for (byte b = 0; b < arrayOfArrayList.length; b++)
      arrayOfArrayList[b] = new ArrayList(); 
    HashSet<String> hashSet = new HashSet();
    hashSet.add("2.5.29.32.0");
    PKIXPolicyNode pKIXPolicyNode1 = new PKIXPolicyNode(new ArrayList(), 0, hashSet, null, new HashSet(), "2.5.29.32.0", false);
    arrayOfArrayList[0].add(pKIXPolicyNode1);
    PKIXNameConstraintValidator pKIXNameConstraintValidator = new PKIXNameConstraintValidator();
    HashSet hashSet1 = new HashSet();
    if (pKIXExtendedParameters.isExplicitPolicyRequired()) {
      k = 0;
    } else {
      k = i + 1;
    } 
    if (pKIXExtendedParameters.isAnyPolicyInhibited()) {
      m = 0;
    } else {
      m = i + 1;
    } 
    if (pKIXExtendedParameters.isPolicyMappingInhibited()) {
      n = 0;
    } else {
      n = i + 1;
    } 
    X509Certificate x509Certificate1 = trustAnchor.getTrustedCert();
    try {
      if (x509Certificate1 != null) {
        x500Name = PrincipalUtils.getSubjectPrincipal(x509Certificate1);
        publicKey = x509Certificate1.getPublicKey();
      } else {
        x500Name = PrincipalUtils.getCA(trustAnchor);
        publicKey = trustAnchor.getCAPublicKey();
      } 
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new ExtCertPathValidatorException("Subject of trust anchor could not be (re)encoded.", illegalArgumentException, paramCertPath, -1);
    } 
    AlgorithmIdentifier algorithmIdentifier = null;
    try {
      algorithmIdentifier = CertPathValidatorUtilities.getAlgorithmIdentifier(publicKey);
    } catch (CertPathValidatorException certPathValidatorException) {
      throw new ExtCertPathValidatorException("Algorithm identifier of public key of trust anchor could not be read.", certPathValidatorException, paramCertPath, -1);
    } 
    ASN1ObjectIdentifier aSN1ObjectIdentifier = algorithmIdentifier.getAlgorithm();
    ASN1Encodable aSN1Encodable = algorithmIdentifier.getParameters();
    int i1 = i;
    if (pKIXExtendedParameters.getTargetConstraints() != null && !pKIXExtendedParameters.getTargetConstraints().match(list.get(0)))
      throw new ExtCertPathValidatorException("Target certificate in certification path does not match targetConstraints.", null, paramCertPath, 0); 
    List list1 = pKIXExtendedParameters.getCertPathCheckers();
    Iterator<PKIXCertPathChecker> iterator = list1.iterator();
    while (iterator.hasNext())
      ((PKIXCertPathChecker)iterator.next()).init(false); 
    X509Certificate x509Certificate2 = null;
    for (j = list.size() - 1; j >= 0; j--) {
      int i2 = i - j;
      x509Certificate2 = (X509Certificate)list.get(j);
      boolean bool = (j == list.size() - 1) ? true : false;
      try {
        checkCertificate(x509Certificate2);
      } catch (AnnotatedException annotatedException) {
        throw new CertPathValidatorException(annotatedException.getMessage(), annotatedException.getUnderlyingException(), paramCertPath, j);
      } 
      RFC3280CertPathUtilities.processCertA(paramCertPath, pKIXExtendedParameters, j, publicKey, bool, x500Name, x509Certificate1, this.helper);
      RFC3280CertPathUtilities.processCertBC(paramCertPath, j, pKIXNameConstraintValidator);
      pKIXPolicyNode1 = RFC3280CertPathUtilities.processCertD(paramCertPath, j, hashSet1, pKIXPolicyNode1, (List[])arrayOfArrayList, m);
      pKIXPolicyNode1 = RFC3280CertPathUtilities.processCertE(paramCertPath, j, pKIXPolicyNode1);
      RFC3280CertPathUtilities.processCertF(paramCertPath, j, pKIXPolicyNode1, k);
      if (i2 != i)
        if (x509Certificate2 != null && x509Certificate2.getVersion() == 1) {
          if (i2 != 1 || !x509Certificate2.equals(trustAnchor.getTrustedCert()))
            throw new CertPathValidatorException("Version 1 certificates can't be used as CA ones.", null, paramCertPath, j); 
        } else {
          RFC3280CertPathUtilities.prepareNextCertA(paramCertPath, j);
          pKIXPolicyNode1 = RFC3280CertPathUtilities.prepareCertB(paramCertPath, j, (List[])arrayOfArrayList, pKIXPolicyNode1, n);
          RFC3280CertPathUtilities.prepareNextCertG(paramCertPath, j, pKIXNameConstraintValidator);
          k = RFC3280CertPathUtilities.prepareNextCertH1(paramCertPath, j, k);
          n = RFC3280CertPathUtilities.prepareNextCertH2(paramCertPath, j, n);
          m = RFC3280CertPathUtilities.prepareNextCertH3(paramCertPath, j, m);
          k = RFC3280CertPathUtilities.prepareNextCertI1(paramCertPath, j, k);
          n = RFC3280CertPathUtilities.prepareNextCertI2(paramCertPath, j, n);
          m = RFC3280CertPathUtilities.prepareNextCertJ(paramCertPath, j, m);
          RFC3280CertPathUtilities.prepareNextCertK(paramCertPath, j);
          i1 = RFC3280CertPathUtilities.prepareNextCertL(paramCertPath, j, i1);
          i1 = RFC3280CertPathUtilities.prepareNextCertM(paramCertPath, j, i1);
          RFC3280CertPathUtilities.prepareNextCertN(paramCertPath, j);
          Set<String> set2 = x509Certificate2.getCriticalExtensionOIDs();
          if (set2 != null) {
            set2 = new HashSet<String>(set2);
            set2.remove(RFC3280CertPathUtilities.KEY_USAGE);
            set2.remove(RFC3280CertPathUtilities.CERTIFICATE_POLICIES);
            set2.remove(RFC3280CertPathUtilities.POLICY_MAPPINGS);
            set2.remove(RFC3280CertPathUtilities.INHIBIT_ANY_POLICY);
            set2.remove(RFC3280CertPathUtilities.ISSUING_DISTRIBUTION_POINT);
            set2.remove(RFC3280CertPathUtilities.DELTA_CRL_INDICATOR);
            set2.remove(RFC3280CertPathUtilities.POLICY_CONSTRAINTS);
            set2.remove(RFC3280CertPathUtilities.BASIC_CONSTRAINTS);
            set2.remove(RFC3280CertPathUtilities.SUBJECT_ALTERNATIVE_NAME);
            set2.remove(RFC3280CertPathUtilities.NAME_CONSTRAINTS);
          } else {
            set2 = new HashSet<String>();
          } 
          RFC3280CertPathUtilities.prepareNextCertO(paramCertPath, j, set2, list1);
          x509Certificate1 = x509Certificate2;
          x500Name = PrincipalUtils.getSubjectPrincipal(x509Certificate1);
          try {
            publicKey = CertPathValidatorUtilities.getNextWorkingKey(paramCertPath.getCertificates(), j, this.helper);
          } catch (CertPathValidatorException certPathValidatorException) {
            throw new CertPathValidatorException("Next working key could not be retrieved.", certPathValidatorException, paramCertPath, j);
          } 
          algorithmIdentifier = CertPathValidatorUtilities.getAlgorithmIdentifier(publicKey);
          aSN1ObjectIdentifier = algorithmIdentifier.getAlgorithm();
          aSN1Encodable = algorithmIdentifier.getParameters();
        }  
    } 
    int k = RFC3280CertPathUtilities.wrapupCertA(k, x509Certificate2);
    k = RFC3280CertPathUtilities.wrapupCertB(paramCertPath, j + 1, k);
    Set<String> set1 = x509Certificate2.getCriticalExtensionOIDs();
    if (set1 != null) {
      set1 = new HashSet<String>(set1);
      set1.remove(RFC3280CertPathUtilities.KEY_USAGE);
      set1.remove(RFC3280CertPathUtilities.CERTIFICATE_POLICIES);
      set1.remove(RFC3280CertPathUtilities.POLICY_MAPPINGS);
      set1.remove(RFC3280CertPathUtilities.INHIBIT_ANY_POLICY);
      set1.remove(RFC3280CertPathUtilities.ISSUING_DISTRIBUTION_POINT);
      set1.remove(RFC3280CertPathUtilities.DELTA_CRL_INDICATOR);
      set1.remove(RFC3280CertPathUtilities.POLICY_CONSTRAINTS);
      set1.remove(RFC3280CertPathUtilities.BASIC_CONSTRAINTS);
      set1.remove(RFC3280CertPathUtilities.SUBJECT_ALTERNATIVE_NAME);
      set1.remove(RFC3280CertPathUtilities.NAME_CONSTRAINTS);
      set1.remove(RFC3280CertPathUtilities.CRL_DISTRIBUTION_POINTS);
      set1.remove(Extension.extendedKeyUsage.getId());
    } else {
      set1 = new HashSet<String>();
    } 
    RFC3280CertPathUtilities.wrapupCertF(paramCertPath, j + 1, list1, set1);
    PKIXPolicyNode pKIXPolicyNode2 = RFC3280CertPathUtilities.wrapupCertG(paramCertPath, pKIXExtendedParameters, set, j + 1, (List[])arrayOfArrayList, pKIXPolicyNode1, hashSet1);
    if (k > 0 || pKIXPolicyNode2 != null)
      return new PKIXCertPathValidatorResult(trustAnchor, pKIXPolicyNode2, x509Certificate2.getPublicKey()); 
    throw new CertPathValidatorException("Path processing failed on policy.", null, paramCertPath, j);
  }
  
  static void checkCertificate(X509Certificate paramX509Certificate) throws AnnotatedException {
    try {
      TBSCertificate.getInstance(paramX509Certificate.getTBSCertificate());
    } catch (CertificateEncodingException certificateEncodingException) {
      throw new AnnotatedException("unable to process TBSCertificate");
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new AnnotatedException(illegalArgumentException.getMessage());
    } 
  }
}
