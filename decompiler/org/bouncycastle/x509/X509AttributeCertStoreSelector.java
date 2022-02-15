package org.bouncycastle.x509;

import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.Target;
import org.bouncycastle.asn1.x509.TargetInformation;
import org.bouncycastle.asn1.x509.Targets;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.util.Selector;

public class X509AttributeCertStoreSelector implements Selector {
  private AttributeCertificateHolder holder;
  
  private AttributeCertificateIssuer issuer;
  
  private BigInteger serialNumber;
  
  private Date attributeCertificateValid;
  
  private X509AttributeCertificate attributeCert;
  
  private Collection targetNames = new HashSet();
  
  private Collection targetGroups = new HashSet();
  
  public boolean match(Object paramObject) {
    if (!(paramObject instanceof X509AttributeCertificate))
      return false; 
    X509AttributeCertificate x509AttributeCertificate = (X509AttributeCertificate)paramObject;
    if (this.attributeCert != null && !this.attributeCert.equals(x509AttributeCertificate))
      return false; 
    if (this.serialNumber != null && !x509AttributeCertificate.getSerialNumber().equals(this.serialNumber))
      return false; 
    if (this.holder != null && !x509AttributeCertificate.getHolder().equals(this.holder))
      return false; 
    if (this.issuer != null && !x509AttributeCertificate.getIssuer().equals(this.issuer))
      return false; 
    if (this.attributeCertificateValid != null)
      try {
        x509AttributeCertificate.checkValidity(this.attributeCertificateValid);
      } catch (CertificateExpiredException certificateExpiredException) {
        return false;
      } catch (CertificateNotYetValidException certificateNotYetValidException) {
        return false;
      }  
    if (!this.targetNames.isEmpty() || !this.targetGroups.isEmpty()) {
      byte[] arrayOfByte = x509AttributeCertificate.getExtensionValue(X509Extensions.TargetInformation.getId());
      if (arrayOfByte != null) {
        TargetInformation targetInformation;
        try {
          targetInformation = TargetInformation.getInstance((new ASN1InputStream(((DEROctetString)DEROctetString.fromByteArray(arrayOfByte)).getOctets())).readObject());
        } catch (IOException iOException) {
          return false;
        } catch (IllegalArgumentException illegalArgumentException) {
          return false;
        } 
        Targets[] arrayOfTargets = targetInformation.getTargetsObjects();
        if (!this.targetNames.isEmpty()) {
          boolean bool = false;
          for (byte b = 0; b < arrayOfTargets.length; b++) {
            Targets targets = arrayOfTargets[b];
            Target[] arrayOfTarget = targets.getTargets();
            for (byte b1 = 0; b1 < arrayOfTarget.length; b1++) {
              if (this.targetNames.contains(GeneralName.getInstance(arrayOfTarget[b1].getTargetName()))) {
                bool = true;
                break;
              } 
            } 
          } 
          if (!bool)
            return false; 
        } 
        if (!this.targetGroups.isEmpty()) {
          boolean bool = false;
          for (byte b = 0; b < arrayOfTargets.length; b++) {
            Targets targets = arrayOfTargets[b];
            Target[] arrayOfTarget = targets.getTargets();
            for (byte b1 = 0; b1 < arrayOfTarget.length; b1++) {
              if (this.targetGroups.contains(GeneralName.getInstance(arrayOfTarget[b1].getTargetGroup()))) {
                bool = true;
                break;
              } 
            } 
          } 
          if (!bool)
            return false; 
        } 
      } 
    } 
    return true;
  }
  
  public Object clone() {
    X509AttributeCertStoreSelector x509AttributeCertStoreSelector = new X509AttributeCertStoreSelector();
    x509AttributeCertStoreSelector.attributeCert = this.attributeCert;
    x509AttributeCertStoreSelector.attributeCertificateValid = getAttributeCertificateValid();
    x509AttributeCertStoreSelector.holder = this.holder;
    x509AttributeCertStoreSelector.issuer = this.issuer;
    x509AttributeCertStoreSelector.serialNumber = this.serialNumber;
    x509AttributeCertStoreSelector.targetGroups = getTargetGroups();
    x509AttributeCertStoreSelector.targetNames = getTargetNames();
    return x509AttributeCertStoreSelector;
  }
  
  public X509AttributeCertificate getAttributeCert() {
    return this.attributeCert;
  }
  
  public void setAttributeCert(X509AttributeCertificate paramX509AttributeCertificate) {
    this.attributeCert = paramX509AttributeCertificate;
  }
  
  public Date getAttributeCertificateValid() {
    return (this.attributeCertificateValid != null) ? new Date(this.attributeCertificateValid.getTime()) : null;
  }
  
  public void setAttributeCertificateValid(Date paramDate) {
    if (paramDate != null) {
      this.attributeCertificateValid = new Date(paramDate.getTime());
    } else {
      this.attributeCertificateValid = null;
    } 
  }
  
  public AttributeCertificateHolder getHolder() {
    return this.holder;
  }
  
  public void setHolder(AttributeCertificateHolder paramAttributeCertificateHolder) {
    this.holder = paramAttributeCertificateHolder;
  }
  
  public AttributeCertificateIssuer getIssuer() {
    return this.issuer;
  }
  
  public void setIssuer(AttributeCertificateIssuer paramAttributeCertificateIssuer) {
    this.issuer = paramAttributeCertificateIssuer;
  }
  
  public BigInteger getSerialNumber() {
    return this.serialNumber;
  }
  
  public void setSerialNumber(BigInteger paramBigInteger) {
    this.serialNumber = paramBigInteger;
  }
  
  public void addTargetName(GeneralName paramGeneralName) {
    this.targetNames.add(paramGeneralName);
  }
  
  public void addTargetName(byte[] paramArrayOfbyte) throws IOException {
    addTargetName(GeneralName.getInstance(ASN1Primitive.fromByteArray(paramArrayOfbyte)));
  }
  
  public void setTargetNames(Collection paramCollection) throws IOException {
    this.targetNames = extractGeneralNames(paramCollection);
  }
  
  public Collection getTargetNames() {
    return Collections.unmodifiableCollection(this.targetNames);
  }
  
  public void addTargetGroup(GeneralName paramGeneralName) {
    this.targetGroups.add(paramGeneralName);
  }
  
  public void addTargetGroup(byte[] paramArrayOfbyte) throws IOException {
    addTargetGroup(GeneralName.getInstance(ASN1Primitive.fromByteArray(paramArrayOfbyte)));
  }
  
  public void setTargetGroups(Collection paramCollection) throws IOException {
    this.targetGroups = extractGeneralNames(paramCollection);
  }
  
  public Collection getTargetGroups() {
    return Collections.unmodifiableCollection(this.targetGroups);
  }
  
  private Set extractGeneralNames(Collection paramCollection) throws IOException {
    if (paramCollection == null || paramCollection.isEmpty())
      return new HashSet(); 
    HashSet<GeneralName> hashSet = new HashSet();
    for (byte[] arrayOfByte : paramCollection) {
      if (arrayOfByte instanceof GeneralName) {
        hashSet.add(arrayOfByte);
        continue;
      } 
      hashSet.add(GeneralName.getInstance(ASN1Primitive.fromByteArray(arrayOfByte)));
    } 
    return hashSet;
  }
}
