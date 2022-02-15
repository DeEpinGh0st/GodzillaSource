package org.bouncycastle.cert.selector;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cert.AttributeCertificateHolder;
import org.bouncycastle.cert.AttributeCertificateIssuer;
import org.bouncycastle.cert.X509AttributeCertificateHolder;

public class X509AttributeCertificateHolderSelectorBuilder {
  private AttributeCertificateHolder holder;
  
  private AttributeCertificateIssuer issuer;
  
  private BigInteger serialNumber;
  
  private Date attributeCertificateValid;
  
  private X509AttributeCertificateHolder attributeCert;
  
  private Collection targetNames = new HashSet();
  
  private Collection targetGroups = new HashSet();
  
  public void setAttributeCert(X509AttributeCertificateHolder paramX509AttributeCertificateHolder) {
    this.attributeCert = paramX509AttributeCertificateHolder;
  }
  
  public void setAttributeCertificateValid(Date paramDate) {
    if (paramDate != null) {
      this.attributeCertificateValid = new Date(paramDate.getTime());
    } else {
      this.attributeCertificateValid = null;
    } 
  }
  
  public void setHolder(AttributeCertificateHolder paramAttributeCertificateHolder) {
    this.holder = paramAttributeCertificateHolder;
  }
  
  public void setIssuer(AttributeCertificateIssuer paramAttributeCertificateIssuer) {
    this.issuer = paramAttributeCertificateIssuer;
  }
  
  public void setSerialNumber(BigInteger paramBigInteger) {
    this.serialNumber = paramBigInteger;
  }
  
  public void addTargetName(GeneralName paramGeneralName) {
    this.targetNames.add(paramGeneralName);
  }
  
  public void setTargetNames(Collection paramCollection) throws IOException {
    this.targetNames = extractGeneralNames(paramCollection);
  }
  
  public void addTargetGroup(GeneralName paramGeneralName) {
    this.targetGroups.add(paramGeneralName);
  }
  
  public void setTargetGroups(Collection paramCollection) throws IOException {
    this.targetGroups = extractGeneralNames(paramCollection);
  }
  
  private Set extractGeneralNames(Collection paramCollection) throws IOException {
    if (paramCollection == null || paramCollection.isEmpty())
      return new HashSet(); 
    HashSet<GeneralName> hashSet = new HashSet();
    Iterator iterator = paramCollection.iterator();
    while (iterator.hasNext())
      hashSet.add(GeneralName.getInstance(iterator.next())); 
    return hashSet;
  }
  
  public X509AttributeCertificateHolderSelector build() {
    return new X509AttributeCertificateHolderSelector(this.holder, this.issuer, this.serialNumber, this.attributeCertificateValid, this.attributeCert, Collections.unmodifiableCollection(new HashSet(this.targetNames)), Collections.unmodifiableCollection(new HashSet(this.targetGroups)));
  }
}
