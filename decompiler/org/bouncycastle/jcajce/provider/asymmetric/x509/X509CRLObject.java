package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.CRLNumber;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.asn1.x509.TBSCertList;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

class X509CRLObject extends X509CRL {
  private JcaJceHelper bcHelper;
  
  private CertificateList c;
  
  private String sigAlgName;
  
  private byte[] sigAlgParams;
  
  private boolean isIndirect;
  
  private boolean isHashCodeSet = false;
  
  private int hashCodeValue;
  
  static boolean isIndirectCRL(X509CRL paramX509CRL) throws CRLException {
    try {
      byte[] arrayOfByte = paramX509CRL.getExtensionValue(Extension.issuingDistributionPoint.getId());
      return (arrayOfByte != null && IssuingDistributionPoint.getInstance(ASN1OctetString.getInstance(arrayOfByte).getOctets()).isIndirectCRL());
    } catch (Exception exception) {
      throw new ExtCRLException("Exception reading IssuingDistributionPoint", exception);
    } 
  }
  
  protected X509CRLObject(JcaJceHelper paramJcaJceHelper, CertificateList paramCertificateList) throws CRLException {
    this.bcHelper = paramJcaJceHelper;
    this.c = paramCertificateList;
    try {
      this.sigAlgName = X509SignatureUtil.getSignatureName(paramCertificateList.getSignatureAlgorithm());
      if (paramCertificateList.getSignatureAlgorithm().getParameters() != null) {
        this.sigAlgParams = paramCertificateList.getSignatureAlgorithm().getParameters().toASN1Primitive().getEncoded("DER");
      } else {
        this.sigAlgParams = null;
      } 
      this.isIndirect = isIndirectCRL(this);
    } catch (Exception exception) {
      throw new CRLException("CRL contents invalid: " + exception);
    } 
  }
  
  public boolean hasUnsupportedCriticalExtension() {
    Set set = getCriticalExtensionOIDs();
    if (set == null)
      return false; 
    set.remove(Extension.issuingDistributionPoint.getId());
    set.remove(Extension.deltaCRLIndicator.getId());
    return !set.isEmpty();
  }
  
  private Set getExtensionOIDs(boolean paramBoolean) {
    if (getVersion() == 2) {
      Extensions extensions = this.c.getTBSCertList().getExtensions();
      if (extensions != null) {
        HashSet<String> hashSet = new HashSet();
        Enumeration<ASN1ObjectIdentifier> enumeration = extensions.oids();
        while (enumeration.hasMoreElements()) {
          ASN1ObjectIdentifier aSN1ObjectIdentifier = enumeration.nextElement();
          Extension extension = extensions.getExtension(aSN1ObjectIdentifier);
          if (paramBoolean == extension.isCritical())
            hashSet.add(aSN1ObjectIdentifier.getId()); 
        } 
        return hashSet;
      } 
    } 
    return null;
  }
  
  public Set getCriticalExtensionOIDs() {
    return getExtensionOIDs(true);
  }
  
  public Set getNonCriticalExtensionOIDs() {
    return getExtensionOIDs(false);
  }
  
  public byte[] getExtensionValue(String paramString) {
    Extensions extensions = this.c.getTBSCertList().getExtensions();
    if (extensions != null) {
      Extension extension = extensions.getExtension(new ASN1ObjectIdentifier(paramString));
      if (extension != null)
        try {
          return extension.getExtnValue().getEncoded();
        } catch (Exception exception) {
          throw new IllegalStateException("error parsing " + exception.toString());
        }  
    } 
    return null;
  }
  
  public byte[] getEncoded() throws CRLException {
    try {
      return this.c.getEncoded("DER");
    } catch (IOException iOException) {
      throw new CRLException(iOException.toString());
    } 
  }
  
  public void verify(PublicKey paramPublicKey) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
    Signature signature;
    try {
      signature = this.bcHelper.createSignature(getSigAlgName());
    } catch (Exception exception) {
      signature = Signature.getInstance(getSigAlgName());
    } 
    doVerify(paramPublicKey, signature);
  }
  
  public void verify(PublicKey paramPublicKey, String paramString) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
    Signature signature;
    if (paramString != null) {
      signature = Signature.getInstance(getSigAlgName(), paramString);
    } else {
      signature = Signature.getInstance(getSigAlgName());
    } 
    doVerify(paramPublicKey, signature);
  }
  
  public void verify(PublicKey paramPublicKey, Provider paramProvider) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
    Signature signature;
    if (paramProvider != null) {
      signature = Signature.getInstance(getSigAlgName(), paramProvider);
    } else {
      signature = Signature.getInstance(getSigAlgName());
    } 
    doVerify(paramPublicKey, signature);
  }
  
  private void doVerify(PublicKey paramPublicKey, Signature paramSignature) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
    if (!this.c.getSignatureAlgorithm().equals(this.c.getTBSCertList().getSignature()))
      throw new CRLException("Signature algorithm on CertificateList does not match TBSCertList."); 
    paramSignature.initVerify(paramPublicKey);
    paramSignature.update(getTBSCertList());
    if (!paramSignature.verify(getSignature()))
      throw new SignatureException("CRL does not verify with supplied public key."); 
  }
  
  public int getVersion() {
    return this.c.getVersionNumber();
  }
  
  public Principal getIssuerDN() {
    return (Principal)new X509Principal(X500Name.getInstance(this.c.getIssuer().toASN1Primitive()));
  }
  
  public X500Principal getIssuerX500Principal() {
    try {
      return new X500Principal(this.c.getIssuer().getEncoded());
    } catch (IOException iOException) {
      throw new IllegalStateException("can't encode issuer DN");
    } 
  }
  
  public Date getThisUpdate() {
    return this.c.getThisUpdate().getDate();
  }
  
  public Date getNextUpdate() {
    return (this.c.getNextUpdate() != null) ? this.c.getNextUpdate().getDate() : null;
  }
  
  private Set loadCRLEntries() {
    HashSet<X509CRLEntryObject> hashSet = new HashSet();
    Enumeration<TBSCertList.CRLEntry> enumeration = this.c.getRevokedCertificateEnumeration();
    X500Name x500Name = null;
    while (enumeration.hasMoreElements()) {
      TBSCertList.CRLEntry cRLEntry = enumeration.nextElement();
      X509CRLEntryObject x509CRLEntryObject = new X509CRLEntryObject(cRLEntry, this.isIndirect, x500Name);
      hashSet.add(x509CRLEntryObject);
      if (this.isIndirect && cRLEntry.hasExtensions()) {
        Extension extension = cRLEntry.getExtensions().getExtension(Extension.certificateIssuer);
        if (extension != null)
          x500Name = X500Name.getInstance(GeneralNames.getInstance(extension.getParsedValue()).getNames()[0].getName()); 
      } 
    } 
    return hashSet;
  }
  
  public X509CRLEntry getRevokedCertificate(BigInteger paramBigInteger) {
    Enumeration<TBSCertList.CRLEntry> enumeration = this.c.getRevokedCertificateEnumeration();
    X500Name x500Name = null;
    while (enumeration.hasMoreElements()) {
      TBSCertList.CRLEntry cRLEntry = enumeration.nextElement();
      if (paramBigInteger.equals(cRLEntry.getUserCertificate().getValue()))
        return new X509CRLEntryObject(cRLEntry, this.isIndirect, x500Name); 
      if (this.isIndirect && cRLEntry.hasExtensions()) {
        Extension extension = cRLEntry.getExtensions().getExtension(Extension.certificateIssuer);
        if (extension != null)
          x500Name = X500Name.getInstance(GeneralNames.getInstance(extension.getParsedValue()).getNames()[0].getName()); 
      } 
    } 
    return null;
  }
  
  public Set getRevokedCertificates() {
    Set<?> set = loadCRLEntries();
    return !set.isEmpty() ? Collections.unmodifiableSet(set) : null;
  }
  
  public byte[] getTBSCertList() throws CRLException {
    try {
      return this.c.getTBSCertList().getEncoded("DER");
    } catch (IOException iOException) {
      throw new CRLException(iOException.toString());
    } 
  }
  
  public byte[] getSignature() {
    return this.c.getSignature().getOctets();
  }
  
  public String getSigAlgName() {
    return this.sigAlgName;
  }
  
  public String getSigAlgOID() {
    return this.c.getSignatureAlgorithm().getAlgorithm().getId();
  }
  
  public byte[] getSigAlgParams() {
    if (this.sigAlgParams != null) {
      byte[] arrayOfByte = new byte[this.sigAlgParams.length];
      System.arraycopy(this.sigAlgParams, 0, arrayOfByte, 0, arrayOfByte.length);
      return arrayOfByte;
    } 
    return null;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    String str = Strings.lineSeparator();
    stringBuffer.append("              Version: ").append(getVersion()).append(str);
    stringBuffer.append("             IssuerDN: ").append(getIssuerDN()).append(str);
    stringBuffer.append("          This update: ").append(getThisUpdate()).append(str);
    stringBuffer.append("          Next update: ").append(getNextUpdate()).append(str);
    stringBuffer.append("  Signature Algorithm: ").append(getSigAlgName()).append(str);
    byte[] arrayOfByte = getSignature();
    stringBuffer.append("            Signature: ").append(new String(Hex.encode(arrayOfByte, 0, 20))).append(str);
    for (byte b = 20; b < arrayOfByte.length; b += 20) {
      if (b < arrayOfByte.length - 20) {
        stringBuffer.append("                       ").append(new String(Hex.encode(arrayOfByte, b, 20))).append(str);
      } else {
        stringBuffer.append("                       ").append(new String(Hex.encode(arrayOfByte, b, arrayOfByte.length - b))).append(str);
      } 
    } 
    Extensions extensions = this.c.getTBSCertList().getExtensions();
    if (extensions != null) {
      Enumeration<ASN1ObjectIdentifier> enumeration = extensions.oids();
      if (enumeration.hasMoreElements())
        stringBuffer.append("           Extensions: ").append(str); 
      while (enumeration.hasMoreElements()) {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = enumeration.nextElement();
        Extension extension = extensions.getExtension(aSN1ObjectIdentifier);
        if (extension.getExtnValue() != null) {
          byte[] arrayOfByte1 = extension.getExtnValue().getOctets();
          ASN1InputStream aSN1InputStream = new ASN1InputStream(arrayOfByte1);
          stringBuffer.append("                       critical(").append(extension.isCritical()).append(") ");
          try {
            if (aSN1ObjectIdentifier.equals(Extension.cRLNumber)) {
              stringBuffer.append(new CRLNumber(ASN1Integer.getInstance(aSN1InputStream.readObject()).getPositiveValue())).append(str);
              continue;
            } 
            if (aSN1ObjectIdentifier.equals(Extension.deltaCRLIndicator)) {
              stringBuffer.append("Base CRL: " + new CRLNumber(ASN1Integer.getInstance(aSN1InputStream.readObject()).getPositiveValue())).append(str);
              continue;
            } 
            if (aSN1ObjectIdentifier.equals(Extension.issuingDistributionPoint)) {
              stringBuffer.append(IssuingDistributionPoint.getInstance(aSN1InputStream.readObject())).append(str);
              continue;
            } 
            if (aSN1ObjectIdentifier.equals(Extension.cRLDistributionPoints)) {
              stringBuffer.append(CRLDistPoint.getInstance(aSN1InputStream.readObject())).append(str);
              continue;
            } 
            if (aSN1ObjectIdentifier.equals(Extension.freshestCRL)) {
              stringBuffer.append(CRLDistPoint.getInstance(aSN1InputStream.readObject())).append(str);
              continue;
            } 
            stringBuffer.append(aSN1ObjectIdentifier.getId());
            stringBuffer.append(" value = ").append(ASN1Dump.dumpAsString(aSN1InputStream.readObject())).append(str);
          } catch (Exception exception) {
            stringBuffer.append(aSN1ObjectIdentifier.getId());
            stringBuffer.append(" value = ").append("*****").append(str);
          } 
          continue;
        } 
        stringBuffer.append(str);
      } 
    } 
    Set set = getRevokedCertificates();
    if (set != null) {
      Iterator iterator = set.iterator();
      while (iterator.hasNext()) {
        stringBuffer.append(iterator.next());
        stringBuffer.append(str);
      } 
    } 
    return stringBuffer.toString();
  }
  
  public boolean isRevoked(Certificate paramCertificate) {
    if (!paramCertificate.getType().equals("X.509"))
      throw new IllegalArgumentException("X.509 CRL used with non X.509 Cert"); 
    Enumeration enumeration = this.c.getRevokedCertificateEnumeration();
    X500Name x500Name = this.c.getIssuer();
    if (enumeration.hasMoreElements()) {
      BigInteger bigInteger = ((X509Certificate)paramCertificate).getSerialNumber();
      while (enumeration.hasMoreElements()) {
        TBSCertList.CRLEntry cRLEntry = TBSCertList.CRLEntry.getInstance(enumeration.nextElement());
        if (this.isIndirect && cRLEntry.hasExtensions()) {
          Extension extension = cRLEntry.getExtensions().getExtension(Extension.certificateIssuer);
          if (extension != null)
            x500Name = X500Name.getInstance(GeneralNames.getInstance(extension.getParsedValue()).getNames()[0].getName()); 
        } 
        if (cRLEntry.getUserCertificate().getValue().equals(bigInteger)) {
          X500Name x500Name1;
          if (paramCertificate instanceof X509Certificate) {
            x500Name1 = X500Name.getInstance(((X509Certificate)paramCertificate).getIssuerX500Principal().getEncoded());
          } else {
            try {
              x500Name1 = Certificate.getInstance(paramCertificate.getEncoded()).getIssuer();
            } catch (CertificateEncodingException certificateEncodingException) {
              throw new IllegalArgumentException("Cannot process certificate: " + certificateEncodingException.getMessage());
            } 
          } 
          return !!x500Name.equals(x500Name1);
        } 
      } 
    } 
    return false;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof X509CRL))
      return false; 
    if (paramObject instanceof X509CRLObject) {
      X509CRLObject x509CRLObject = (X509CRLObject)paramObject;
      if (this.isHashCodeSet) {
        boolean bool = x509CRLObject.isHashCodeSet;
        if (bool && x509CRLObject.hashCodeValue != this.hashCodeValue)
          return false; 
      } 
      return this.c.equals(x509CRLObject.c);
    } 
    return super.equals(paramObject);
  }
  
  public int hashCode() {
    if (!this.isHashCodeSet) {
      this.isHashCodeSet = true;
      this.hashCodeValue = super.hashCode();
    } 
    return this.hashCodeValue;
  }
}
