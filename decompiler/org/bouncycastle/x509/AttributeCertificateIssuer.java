package org.bouncycastle.x509;

import java.io.IOException;
import java.security.Principal;
import java.security.cert.CertSelector;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AttCertIssuer;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.V2Form;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.util.Selector;

public class AttributeCertificateIssuer implements CertSelector, Selector {
  final ASN1Encodable form;
  
  public AttributeCertificateIssuer(AttCertIssuer paramAttCertIssuer) {
    this.form = paramAttCertIssuer.getIssuer();
  }
  
  public AttributeCertificateIssuer(X500Principal paramX500Principal) throws IOException {
    this(new X509Principal(paramX500Principal.getEncoded()));
  }
  
  public AttributeCertificateIssuer(X509Principal paramX509Principal) {
    this.form = (ASN1Encodable)new V2Form(GeneralNames.getInstance(new DERSequence((ASN1Encodable)new GeneralName((X509Name)paramX509Principal))));
  }
  
  private Object[] getNames() {
    GeneralNames generalNames;
    if (this.form instanceof V2Form) {
      generalNames = ((V2Form)this.form).getIssuerName();
    } else {
      generalNames = (GeneralNames)this.form;
    } 
    GeneralName[] arrayOfGeneralName = generalNames.getNames();
    ArrayList<X500Principal> arrayList = new ArrayList(arrayOfGeneralName.length);
    for (byte b = 0; b != arrayOfGeneralName.length; b++) {
      if (arrayOfGeneralName[b].getTagNo() == 4)
        try {
          arrayList.add(new X500Principal(arrayOfGeneralName[b].getName().toASN1Primitive().getEncoded()));
        } catch (IOException iOException) {
          throw new RuntimeException("badly formed Name object");
        }  
    } 
    return arrayList.toArray(new Object[arrayList.size()]);
  }
  
  public Principal[] getPrincipals() {
    Object[] arrayOfObject = getNames();
    ArrayList<Object> arrayList = new ArrayList();
    for (byte b = 0; b != arrayOfObject.length; b++) {
      if (arrayOfObject[b] instanceof Principal)
        arrayList.add(arrayOfObject[b]); 
    } 
    return arrayList.<Principal>toArray(new Principal[arrayList.size()]);
  }
  
  private boolean matchesDN(X500Principal paramX500Principal, GeneralNames paramGeneralNames) {
    GeneralName[] arrayOfGeneralName = paramGeneralNames.getNames();
    for (byte b = 0; b != arrayOfGeneralName.length; b++) {
      GeneralName generalName = arrayOfGeneralName[b];
      if (generalName.getTagNo() == 4)
        try {
          if ((new X500Principal(generalName.getName().toASN1Primitive().getEncoded())).equals(paramX500Principal))
            return true; 
        } catch (IOException iOException) {} 
    } 
    return false;
  }
  
  public Object clone() {
    return new AttributeCertificateIssuer(AttCertIssuer.getInstance(this.form));
  }
  
  public boolean match(Certificate paramCertificate) {
    if (!(paramCertificate instanceof X509Certificate))
      return false; 
    X509Certificate x509Certificate = (X509Certificate)paramCertificate;
    if (this.form instanceof V2Form) {
      V2Form v2Form = (V2Form)this.form;
      if (v2Form.getBaseCertificateID() != null)
        return (v2Form.getBaseCertificateID().getSerial().getValue().equals(x509Certificate.getSerialNumber()) && matchesDN(x509Certificate.getIssuerX500Principal(), v2Form.getBaseCertificateID().getIssuer())); 
      GeneralNames generalNames = v2Form.getIssuerName();
      if (matchesDN(x509Certificate.getSubjectX500Principal(), generalNames))
        return true; 
    } else {
      GeneralNames generalNames = (GeneralNames)this.form;
      if (matchesDN(x509Certificate.getSubjectX500Principal(), generalNames))
        return true; 
    } 
    return false;
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
    return !(paramObject instanceof X509Certificate) ? false : match((Certificate)paramObject);
  }
}
