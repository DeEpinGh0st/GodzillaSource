package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.CRLException;
import java.security.cert.X509CRLEntry;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.TBSCertList;
import org.bouncycastle.util.Strings;

class X509CRLEntryObject extends X509CRLEntry {
  private TBSCertList.CRLEntry c;
  
  private X500Name certificateIssuer;
  
  private int hashValue;
  
  private boolean isHashValueSet;
  
  protected X509CRLEntryObject(TBSCertList.CRLEntry paramCRLEntry) {
    this.c = paramCRLEntry;
    this.certificateIssuer = null;
  }
  
  protected X509CRLEntryObject(TBSCertList.CRLEntry paramCRLEntry, boolean paramBoolean, X500Name paramX500Name) {
    this.c = paramCRLEntry;
    this.certificateIssuer = loadCertificateIssuer(paramBoolean, paramX500Name);
  }
  
  public boolean hasUnsupportedCriticalExtension() {
    Set set = getCriticalExtensionOIDs();
    return (set != null && !set.isEmpty());
  }
  
  private X500Name loadCertificateIssuer(boolean paramBoolean, X500Name paramX500Name) {
    if (!paramBoolean)
      return null; 
    Extension extension = getExtension(Extension.certificateIssuer);
    if (extension == null)
      return paramX500Name; 
    try {
      GeneralName[] arrayOfGeneralName = GeneralNames.getInstance(extension.getParsedValue()).getNames();
      for (byte b = 0; b < arrayOfGeneralName.length; b++) {
        if (arrayOfGeneralName[b].getTagNo() == 4)
          return X500Name.getInstance(arrayOfGeneralName[b].getName()); 
      } 
      return null;
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public X500Principal getCertificateIssuer() {
    if (this.certificateIssuer == null)
      return null; 
    try {
      return new X500Principal(this.certificateIssuer.getEncoded());
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  private Set getExtensionOIDs(boolean paramBoolean) {
    Extensions extensions = this.c.getExtensions();
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
    return null;
  }
  
  public Set getCriticalExtensionOIDs() {
    return getExtensionOIDs(true);
  }
  
  public Set getNonCriticalExtensionOIDs() {
    return getExtensionOIDs(false);
  }
  
  private Extension getExtension(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    Extensions extensions = this.c.getExtensions();
    return (extensions != null) ? extensions.getExtension(paramASN1ObjectIdentifier) : null;
  }
  
  public byte[] getExtensionValue(String paramString) {
    Extension extension = getExtension(new ASN1ObjectIdentifier(paramString));
    if (extension != null)
      try {
        return extension.getExtnValue().getEncoded();
      } catch (Exception exception) {
        throw new IllegalStateException("Exception encoding: " + exception.toString());
      }  
    return null;
  }
  
  public int hashCode() {
    if (!this.isHashValueSet) {
      this.hashValue = super.hashCode();
      this.isHashValueSet = true;
    } 
    return this.hashValue;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (paramObject instanceof X509CRLEntryObject) {
      X509CRLEntryObject x509CRLEntryObject = (X509CRLEntryObject)paramObject;
      return this.c.equals(x509CRLEntryObject.c);
    } 
    return super.equals(this);
  }
  
  public byte[] getEncoded() throws CRLException {
    try {
      return this.c.getEncoded("DER");
    } catch (IOException iOException) {
      throw new CRLException(iOException.toString());
    } 
  }
  
  public BigInteger getSerialNumber() {
    return this.c.getUserCertificate().getValue();
  }
  
  public Date getRevocationDate() {
    return this.c.getRevocationDate().getDate();
  }
  
  public boolean hasExtensions() {
    return (this.c.getExtensions() != null);
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    String str = Strings.lineSeparator();
    stringBuffer.append("      userCertificate: ").append(getSerialNumber()).append(str);
    stringBuffer.append("       revocationDate: ").append(getRevocationDate()).append(str);
    stringBuffer.append("       certificateIssuer: ").append(getCertificateIssuer()).append(str);
    Extensions extensions = this.c.getExtensions();
    if (extensions != null) {
      Enumeration<ASN1ObjectIdentifier> enumeration = extensions.oids();
      if (enumeration.hasMoreElements()) {
        stringBuffer.append("   crlEntryExtensions:").append(str);
        while (enumeration.hasMoreElements()) {
          ASN1ObjectIdentifier aSN1ObjectIdentifier = enumeration.nextElement();
          Extension extension = extensions.getExtension(aSN1ObjectIdentifier);
          if (extension.getExtnValue() != null) {
            byte[] arrayOfByte = extension.getExtnValue().getOctets();
            ASN1InputStream aSN1InputStream = new ASN1InputStream(arrayOfByte);
            stringBuffer.append("                       critical(").append(extension.isCritical()).append(") ");
            try {
              if (aSN1ObjectIdentifier.equals(Extension.reasonCode)) {
                stringBuffer.append(CRLReason.getInstance(ASN1Enumerated.getInstance(aSN1InputStream.readObject()))).append(str);
                continue;
              } 
              if (aSN1ObjectIdentifier.equals(Extension.certificateIssuer)) {
                stringBuffer.append("Certificate issuer: ").append(GeneralNames.getInstance(aSN1InputStream.readObject())).append(str);
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
    } 
    return stringBuffer.toString();
  }
}
