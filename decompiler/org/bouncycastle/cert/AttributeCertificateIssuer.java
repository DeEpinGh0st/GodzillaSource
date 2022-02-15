package org.bouncycastle.cert;

import java.util.ArrayList;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AttCertIssuer;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.V2Form;
import org.bouncycastle.util.Selector;

public class AttributeCertificateIssuer implements Selector {
  final ASN1Encodable form;
  
  public AttributeCertificateIssuer(AttCertIssuer paramAttCertIssuer) {
    this.form = paramAttCertIssuer.getIssuer();
  }
  
  public AttributeCertificateIssuer(X500Name paramX500Name) {
    this.form = (ASN1Encodable)new V2Form(new GeneralNames(new GeneralName(paramX500Name)));
  }
  
  public X500Name[] getNames() {
    GeneralNames generalNames;
    if (this.form instanceof V2Form) {
      generalNames = ((V2Form)this.form).getIssuerName();
    } else {
      generalNames = (GeneralNames)this.form;
    } 
    GeneralName[] arrayOfGeneralName = generalNames.getNames();
    ArrayList<X500Name> arrayList = new ArrayList(arrayOfGeneralName.length);
    for (byte b = 0; b != arrayOfGeneralName.length; b++) {
      if (arrayOfGeneralName[b].getTagNo() == 4)
        arrayList.add(X500Name.getInstance(arrayOfGeneralName[b].getName())); 
    } 
    return arrayList.<X500Name>toArray(new X500Name[arrayList.size()]);
  }
  
  private boolean matchesDN(X500Name paramX500Name, GeneralNames paramGeneralNames) {
    GeneralName[] arrayOfGeneralName = paramGeneralNames.getNames();
    for (byte b = 0; b != arrayOfGeneralName.length; b++) {
      GeneralName generalName = arrayOfGeneralName[b];
      if (generalName.getTagNo() == 4 && X500Name.getInstance(generalName.getName()).equals(paramX500Name))
        return true; 
    } 
    return false;
  }
  
  public Object clone() {
    return new AttributeCertificateIssuer(AttCertIssuer.getInstance(this.form));
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof AttributeCertificateIssuer))
      return false; 
    AttributeCertificateIssuer attributeCertificateIssuer = (AttributeCertificateIssuer)paramObject;
    return this.form.equals(attributeCertificateIssuer.form);
  }
  
  public int hashCode() {
    return this.form.hashCode();
  }
  
  public boolean match(Object paramObject) {
    if (!(paramObject instanceof X509CertificateHolder))
      return false; 
    X509CertificateHolder x509CertificateHolder = (X509CertificateHolder)paramObject;
    if (this.form instanceof V2Form) {
      V2Form v2Form = (V2Form)this.form;
      if (v2Form.getBaseCertificateID() != null)
        return (v2Form.getBaseCertificateID().getSerial().getValue().equals(x509CertificateHolder.getSerialNumber()) && matchesDN(x509CertificateHolder.getIssuer(), v2Form.getBaseCertificateID().getIssuer())); 
      GeneralNames generalNames = v2Form.getIssuerName();
      if (matchesDN(x509CertificateHolder.getSubject(), generalNames))
        return true; 
    } else {
      GeneralNames generalNames = (GeneralNames)this.form;
      if (matchesDN(x509CertificateHolder.getSubject(), generalNames))
        return true; 
    } 
    return false;
  }
}
