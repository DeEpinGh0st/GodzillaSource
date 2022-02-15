package org.bouncycastle.x509;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.util.Arrays;

public class X509V2AttributeCertificate implements X509AttributeCertificate {
  private AttributeCertificate cert;
  
  private Date notBefore;
  
  private Date notAfter;
  
  private static AttributeCertificate getObject(InputStream paramInputStream) throws IOException {
    try {
      return AttributeCertificate.getInstance((new ASN1InputStream(paramInputStream)).readObject());
    } catch (IOException iOException) {
      throw iOException;
    } catch (Exception exception) {
      throw new IOException("exception decoding certificate structure: " + exception.toString());
    } 
  }
  
  public X509V2AttributeCertificate(InputStream paramInputStream) throws IOException {
    this(getObject(paramInputStream));
  }
  
  public X509V2AttributeCertificate(byte[] paramArrayOfbyte) throws IOException {
    this(new ByteArrayInputStream(paramArrayOfbyte));
  }
  
  X509V2AttributeCertificate(AttributeCertificate paramAttributeCertificate) throws IOException {
    this.cert = paramAttributeCertificate;
    try {
      this.notAfter = paramAttributeCertificate.getAcinfo().getAttrCertValidityPeriod().getNotAfterTime().getDate();
      this.notBefore = paramAttributeCertificate.getAcinfo().getAttrCertValidityPeriod().getNotBeforeTime().getDate();
    } catch (ParseException parseException) {
      throw new IOException("invalid data structure in certificate!");
    } 
  }
  
  public int getVersion() {
    return this.cert.getAcinfo().getVersion().getValue().intValue() + 1;
  }
  
  public BigInteger getSerialNumber() {
    return this.cert.getAcinfo().getSerialNumber().getValue();
  }
  
  public AttributeCertificateHolder getHolder() {
    return new AttributeCertificateHolder((ASN1Sequence)this.cert.getAcinfo().getHolder().toASN1Primitive());
  }
  
  public AttributeCertificateIssuer getIssuer() {
    return new AttributeCertificateIssuer(this.cert.getAcinfo().getIssuer());
  }
  
  public Date getNotBefore() {
    return this.notBefore;
  }
  
  public Date getNotAfter() {
    return this.notAfter;
  }
  
  public boolean[] getIssuerUniqueID() {
    DERBitString dERBitString = this.cert.getAcinfo().getIssuerUniqueID();
    if (dERBitString != null) {
      byte[] arrayOfByte = dERBitString.getBytes();
      boolean[] arrayOfBoolean = new boolean[arrayOfByte.length * 8 - dERBitString.getPadBits()];
      for (byte b = 0; b != arrayOfBoolean.length; b++)
        arrayOfBoolean[b] = ((arrayOfByte[b / 8] & 128 >>> b % 8) != 0); 
      return arrayOfBoolean;
    } 
    return null;
  }
  
  public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {
    checkValidity(new Date());
  }
  
  public void checkValidity(Date paramDate) throws CertificateExpiredException, CertificateNotYetValidException {
    if (paramDate.after(getNotAfter()))
      throw new CertificateExpiredException("certificate expired on " + getNotAfter()); 
    if (paramDate.before(getNotBefore()))
      throw new CertificateNotYetValidException("certificate not valid till " + getNotBefore()); 
  }
  
  public byte[] getSignature() {
    return this.cert.getSignatureValue().getOctets();
  }
  
  public final void verify(PublicKey paramPublicKey, String paramString) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
    Signature signature = null;
    if (!this.cert.getSignatureAlgorithm().equals(this.cert.getAcinfo().getSignature()))
      throw new CertificateException("Signature algorithm in certificate info not same as outer certificate"); 
    signature = Signature.getInstance(this.cert.getSignatureAlgorithm().getAlgorithm().getId(), paramString);
    signature.initVerify(paramPublicKey);
    try {
      signature.update(this.cert.getAcinfo().getEncoded());
    } catch (IOException iOException) {
      throw new SignatureException("Exception encoding certificate info object");
    } 
    if (!signature.verify(getSignature()))
      throw new InvalidKeyException("Public key presented not for certificate signature"); 
  }
  
  public byte[] getEncoded() throws IOException {
    return this.cert.getEncoded();
  }
  
  public byte[] getExtensionValue(String paramString) {
    Extensions extensions = this.cert.getAcinfo().getExtensions();
    if (extensions != null) {
      Extension extension = extensions.getExtension(new ASN1ObjectIdentifier(paramString));
      if (extension != null)
        try {
          return extension.getExtnValue().getEncoded("DER");
        } catch (Exception exception) {
          throw new RuntimeException("error encoding " + exception.toString());
        }  
    } 
    return null;
  }
  
  private Set getExtensionOIDs(boolean paramBoolean) {
    Extensions extensions = this.cert.getAcinfo().getExtensions();
    if (extensions != null) {
      HashSet<String> hashSet = new HashSet();
      Enumeration<ASN1ObjectIdentifier> enumeration = extensions.oids();
      while (enumeration.hasMoreElements()) {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = enumeration.nextElement();
        Extension extension = extensions.getExtension(aSN1ObjectIdentifier);
        if (extension.isCritical() == paramBoolean)
          hashSet.add(aSN1ObjectIdentifier.getId()); 
      } 
      return hashSet;
    } 
    return null;
  }
  
  public Set getNonCriticalExtensionOIDs() {
    return getExtensionOIDs(false);
  }
  
  public Set getCriticalExtensionOIDs() {
    return getExtensionOIDs(true);
  }
  
  public boolean hasUnsupportedCriticalExtension() {
    Set set = getCriticalExtensionOIDs();
    return (set != null && !set.isEmpty());
  }
  
  public X509Attribute[] getAttributes() {
    ASN1Sequence aSN1Sequence = this.cert.getAcinfo().getAttributes();
    X509Attribute[] arrayOfX509Attribute = new X509Attribute[aSN1Sequence.size()];
    for (byte b = 0; b != aSN1Sequence.size(); b++)
      arrayOfX509Attribute[b] = new X509Attribute(aSN1Sequence.getObjectAt(b)); 
    return arrayOfX509Attribute;
  }
  
  public X509Attribute[] getAttributes(String paramString) {
    ASN1Sequence aSN1Sequence = this.cert.getAcinfo().getAttributes();
    ArrayList<X509Attribute> arrayList = new ArrayList();
    for (byte b = 0; b != aSN1Sequence.size(); b++) {
      X509Attribute x509Attribute = new X509Attribute(aSN1Sequence.getObjectAt(b));
      if (x509Attribute.getOID().equals(paramString))
        arrayList.add(x509Attribute); 
    } 
    return (arrayList.size() == 0) ? null : arrayList.<X509Attribute>toArray(new X509Attribute[arrayList.size()]);
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof X509AttributeCertificate))
      return false; 
    X509AttributeCertificate x509AttributeCertificate = (X509AttributeCertificate)paramObject;
    try {
      byte[] arrayOfByte1 = getEncoded();
      byte[] arrayOfByte2 = x509AttributeCertificate.getEncoded();
      return Arrays.areEqual(arrayOfByte1, arrayOfByte2);
    } catch (IOException iOException) {
      return false;
    } 
  }
  
  public int hashCode() {
    try {
      return Arrays.hashCode(getEncoded());
    } catch (IOException iOException) {
      return 0;
    } 
  }
}
