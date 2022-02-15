package org.bouncycastle.cert.selector;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.Target;
import org.bouncycastle.asn1.x509.TargetInformation;
import org.bouncycastle.asn1.x509.Targets;
import org.bouncycastle.cert.AttributeCertificateHolder;
import org.bouncycastle.cert.AttributeCertificateIssuer;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.util.Selector;

public class X509AttributeCertificateHolderSelector implements Selector {
  private final AttributeCertificateHolder holder;
  
  private final AttributeCertificateIssuer issuer;
  
  private final BigInteger serialNumber;
  
  private final Date attributeCertificateValid;
  
  private final X509AttributeCertificateHolder attributeCert;
  
  private final Collection targetNames;
  
  private final Collection targetGroups;
  
  X509AttributeCertificateHolderSelector(AttributeCertificateHolder paramAttributeCertificateHolder, AttributeCertificateIssuer paramAttributeCertificateIssuer, BigInteger paramBigInteger, Date paramDate, X509AttributeCertificateHolder paramX509AttributeCertificateHolder, Collection paramCollection1, Collection paramCollection2) {
    this.holder = paramAttributeCertificateHolder;
    this.issuer = paramAttributeCertificateIssuer;
    this.serialNumber = paramBigInteger;
    this.attributeCertificateValid = paramDate;
    this.attributeCert = paramX509AttributeCertificateHolder;
    this.targetNames = paramCollection1;
    this.targetGroups = paramCollection2;
  }
  
  public boolean match(Object paramObject) {
    if (!(paramObject instanceof X509AttributeCertificateHolder))
      return false; 
    X509AttributeCertificateHolder x509AttributeCertificateHolder = (X509AttributeCertificateHolder)paramObject;
    if (this.attributeCert != null && !this.attributeCert.equals(x509AttributeCertificateHolder))
      return false; 
    if (this.serialNumber != null && !x509AttributeCertificateHolder.getSerialNumber().equals(this.serialNumber))
      return false; 
    if (this.holder != null && !x509AttributeCertificateHolder.getHolder().equals(this.holder))
      return false; 
    if (this.issuer != null && !x509AttributeCertificateHolder.getIssuer().equals(this.issuer))
      return false; 
    if (this.attributeCertificateValid != null && !x509AttributeCertificateHolder.isValidOn(this.attributeCertificateValid))
      return false; 
    if (!this.targetNames.isEmpty() || !this.targetGroups.isEmpty()) {
      Extension extension = x509AttributeCertificateHolder.getExtension(Extension.targetInformation);
      if (extension != null) {
        TargetInformation targetInformation;
        try {
          targetInformation = TargetInformation.getInstance(extension.getParsedValue());
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
    return new X509AttributeCertificateHolderSelector(this.holder, this.issuer, this.serialNumber, this.attributeCertificateValid, this.attributeCert, this.targetNames, this.targetGroups);
  }
  
  public X509AttributeCertificateHolder getAttributeCert() {
    return this.attributeCert;
  }
  
  public Date getAttributeCertificateValid() {
    return (this.attributeCertificateValid != null) ? new Date(this.attributeCertificateValid.getTime()) : null;
  }
  
  public AttributeCertificateHolder getHolder() {
    return this.holder;
  }
  
  public AttributeCertificateIssuer getIssuer() {
    return this.issuer;
  }
  
  public BigInteger getSerialNumber() {
    return this.serialNumber;
  }
  
  public Collection getTargetNames() {
    return this.targetNames;
  }
  
  public Collection getTargetGroups() {
    return this.targetGroups;
  }
}
