package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.security.NoSuchProviderException;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.SignedData;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemObjectGenerator;
import org.bouncycastle.util.io.pem.PemWriter;

public class PKIXCertPath extends CertPath {
  private final JcaJceHelper helper = (JcaJceHelper)new BCJcaJceHelper();
  
  static final List certPathEncodings;
  
  private List certificates;
  
  private List sortCerts(List<X509Certificate> paramList) {
    if (paramList.size() < 2)
      return paramList; 
    X500Principal x500Principal = ((X509Certificate)paramList.get(0)).getIssuerX500Principal();
    boolean bool = true;
    for (byte b1 = 1; b1 != paramList.size(); b1++) {
      X509Certificate x509Certificate = paramList.get(b1);
      if (x500Principal.equals(x509Certificate.getSubjectX500Principal())) {
        x500Principal = ((X509Certificate)paramList.get(b1)).getIssuerX500Principal();
      } else {
        bool = false;
        break;
      } 
    } 
    if (bool)
      return paramList; 
    ArrayList<X509Certificate> arrayList1 = new ArrayList(paramList.size());
    ArrayList<X509Certificate> arrayList2 = new ArrayList<X509Certificate>(paramList);
    byte b2;
    for (b2 = 0; b2 < paramList.size(); b2++) {
      X509Certificate x509Certificate = paramList.get(b2);
      boolean bool1 = false;
      X500Principal x500Principal1 = x509Certificate.getSubjectX500Principal();
      for (byte b = 0; b != paramList.size(); b++) {
        X509Certificate x509Certificate1 = paramList.get(b);
        if (x509Certificate1.getIssuerX500Principal().equals(x500Principal1)) {
          bool1 = true;
          break;
        } 
      } 
      if (!bool1) {
        arrayList1.add(x509Certificate);
        paramList.remove(b2);
      } 
    } 
    if (arrayList1.size() > 1)
      return arrayList2; 
    for (b2 = 0; b2 != arrayList1.size(); b2++) {
      x500Principal = ((X509Certificate)arrayList1.get(b2)).getIssuerX500Principal();
      for (byte b = 0; b < paramList.size(); b++) {
        X509Certificate x509Certificate = paramList.get(b);
        if (x500Principal.equals(x509Certificate.getSubjectX500Principal())) {
          arrayList1.add(x509Certificate);
          paramList.remove(b);
          break;
        } 
      } 
    } 
    return (paramList.size() > 0) ? arrayList2 : arrayList1;
  }
  
  PKIXCertPath(List<?> paramList) {
    super("X.509");
    this.certificates = sortCerts(new ArrayList(paramList));
  }
  
  PKIXCertPath(InputStream paramInputStream, String paramString) throws CertificateException {
    super("X.509");
    try {
      if (paramString.equalsIgnoreCase("PkiPath")) {
        ASN1InputStream aSN1InputStream = new ASN1InputStream(paramInputStream);
        ASN1Primitive aSN1Primitive = aSN1InputStream.readObject();
        if (!(aSN1Primitive instanceof ASN1Sequence))
          throw new CertificateException("input stream does not contain a ASN1 SEQUENCE while reading PkiPath encoded data to load CertPath"); 
        Enumeration<ASN1Encodable> enumeration = ((ASN1Sequence)aSN1Primitive).getObjects();
        this.certificates = new ArrayList();
        CertificateFactory certificateFactory = this.helper.createCertificateFactory("X.509");
        while (enumeration.hasMoreElements()) {
          ASN1Encodable aSN1Encodable = enumeration.nextElement();
          byte[] arrayOfByte = aSN1Encodable.toASN1Primitive().getEncoded("DER");
          this.certificates.add(0, certificateFactory.generateCertificate(new ByteArrayInputStream(arrayOfByte)));
        } 
      } else if (paramString.equalsIgnoreCase("PKCS7") || paramString.equalsIgnoreCase("PEM")) {
        paramInputStream = new BufferedInputStream(paramInputStream);
        this.certificates = new ArrayList();
        CertificateFactory certificateFactory = this.helper.createCertificateFactory("X.509");
        Certificate certificate;
        while ((certificate = certificateFactory.generateCertificate(paramInputStream)) != null)
          this.certificates.add(certificate); 
      } else {
        throw new CertificateException("unsupported encoding: " + paramString);
      } 
    } catch (IOException iOException) {
      throw new CertificateException("IOException throw while decoding CertPath:\n" + iOException.toString());
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new CertificateException("BouncyCastle provider not found while trying to get a CertificateFactory:\n" + noSuchProviderException.toString());
    } 
    this.certificates = sortCerts(this.certificates);
  }
  
  public Iterator getEncodings() {
    return certPathEncodings.iterator();
  }
  
  public byte[] getEncoded() throws CertificateEncodingException {
    Iterator<Object> iterator = getEncodings();
    if (iterator.hasNext()) {
      String str = (String)iterator.next();
      if (str instanceof String)
        return getEncoded(str); 
    } 
    return null;
  }
  
  public byte[] getEncoded(String paramString) throws CertificateEncodingException {
    if (paramString.equalsIgnoreCase("PkiPath")) {
      ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
      ListIterator<X509Certificate> listIterator = this.certificates.listIterator(this.certificates.size());
      while (listIterator.hasPrevious())
        aSN1EncodableVector.add((ASN1Encodable)toASN1Object(listIterator.previous())); 
      return toDEREncoded((ASN1Encodable)new DERSequence(aSN1EncodableVector));
    } 
    if (paramString.equalsIgnoreCase("PKCS7")) {
      ContentInfo contentInfo = new ContentInfo(PKCSObjectIdentifiers.data, null);
      ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
      for (byte b = 0; b != this.certificates.size(); b++)
        aSN1EncodableVector.add((ASN1Encodable)toASN1Object(this.certificates.get(b))); 
      SignedData signedData = new SignedData(new ASN1Integer(1L), (ASN1Set)new DERSet(), contentInfo, (ASN1Set)new DERSet(aSN1EncodableVector), null, (ASN1Set)new DERSet());
      return toDEREncoded((ASN1Encodable)new ContentInfo(PKCSObjectIdentifiers.signedData, (ASN1Encodable)signedData));
    } 
    if (paramString.equalsIgnoreCase("PEM")) {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      PemWriter pemWriter = new PemWriter(new OutputStreamWriter(byteArrayOutputStream));
      try {
        for (byte b = 0; b != this.certificates.size(); b++)
          pemWriter.writeObject((PemObjectGenerator)new PemObject("CERTIFICATE", ((X509Certificate)this.certificates.get(b)).getEncoded())); 
        pemWriter.close();
      } catch (Exception exception) {
        throw new CertificateEncodingException("can't encode certificate for PEM encoded path");
      } 
      return byteArrayOutputStream.toByteArray();
    } 
    throw new CertificateEncodingException("unsupported encoding: " + paramString);
  }
  
  public List getCertificates() {
    return Collections.unmodifiableList(new ArrayList(this.certificates));
  }
  
  private ASN1Primitive toASN1Object(X509Certificate paramX509Certificate) throws CertificateEncodingException {
    try {
      return (new ASN1InputStream(paramX509Certificate.getEncoded())).readObject();
    } catch (Exception exception) {
      throw new CertificateEncodingException("Exception while encoding certificate: " + exception.toString());
    } 
  }
  
  private byte[] toDEREncoded(ASN1Encodable paramASN1Encodable) throws CertificateEncodingException {
    try {
      return paramASN1Encodable.toASN1Primitive().getEncoded("DER");
    } catch (IOException iOException) {
      throw new CertificateEncodingException("Exception thrown: " + iOException);
    } 
  }
  
  static {
    ArrayList<String> arrayList = new ArrayList();
    arrayList.add("PkiPath");
    arrayList.add("PEM");
    arrayList.add("PKCS7");
    certPathEncodings = Collections.unmodifiableList(arrayList);
  }
}
