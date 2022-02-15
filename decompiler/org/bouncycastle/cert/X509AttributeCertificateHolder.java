package org.bouncycastle.cert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AttCertValidityPeriod;
import org.bouncycastle.asn1.x509.Attribute;
import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.bouncycastle.asn1.x509.AttributeCertificateInfo;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.util.Encodable;

public class X509AttributeCertificateHolder implements Encodable, Serializable {
  private static final long serialVersionUID = 20170722001L;
  
  private static Attribute[] EMPTY_ARRAY = new Attribute[0];
  
  private transient AttributeCertificate attrCert;
  
  private transient Extensions extensions;
  
  private static AttributeCertificate parseBytes(byte[] paramArrayOfbyte) throws IOException {
    try {
      return AttributeCertificate.getInstance(CertUtils.parseNonEmptyASN1(paramArrayOfbyte));
    } catch (ClassCastException classCastException) {
      throw new CertIOException("malformed data: " + classCastException.getMessage(), classCastException);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new CertIOException("malformed data: " + illegalArgumentException.getMessage(), illegalArgumentException);
    } 
  }
  
  public X509AttributeCertificateHolder(byte[] paramArrayOfbyte) throws IOException {
    this(parseBytes(paramArrayOfbyte));
  }
  
  public X509AttributeCertificateHolder(AttributeCertificate paramAttributeCertificate) {
    init(paramAttributeCertificate);
  }
  
  private void init(AttributeCertificate paramAttributeCertificate) {
    this.attrCert = paramAttributeCertificate;
    this.extensions = paramAttributeCertificate.getAcinfo().getExtensions();
  }
  
  public byte[] getEncoded() throws IOException {
    return this.attrCert.getEncoded();
  }
  
  public int getVersion() {
    return this.attrCert.getAcinfo().getVersion().getValue().intValue() + 1;
  }
  
  public BigInteger getSerialNumber() {
    return this.attrCert.getAcinfo().getSerialNumber().getValue();
  }
  
  public AttributeCertificateHolder getHolder() {
    return new AttributeCertificateHolder((ASN1Sequence)this.attrCert.getAcinfo().getHolder().toASN1Primitive());
  }
  
  public AttributeCertificateIssuer getIssuer() {
    return new AttributeCertificateIssuer(this.attrCert.getAcinfo().getIssuer());
  }
  
  public Date getNotBefore() {
    return CertUtils.recoverDate(this.attrCert.getAcinfo().getAttrCertValidityPeriod().getNotBeforeTime());
  }
  
  public Date getNotAfter() {
    return CertUtils.recoverDate(this.attrCert.getAcinfo().getAttrCertValidityPeriod().getNotAfterTime());
  }
  
  public Attribute[] getAttributes() {
    ASN1Sequence aSN1Sequence = this.attrCert.getAcinfo().getAttributes();
    Attribute[] arrayOfAttribute = new Attribute[aSN1Sequence.size()];
    for (byte b = 0; b != aSN1Sequence.size(); b++)
      arrayOfAttribute[b] = Attribute.getInstance(aSN1Sequence.getObjectAt(b)); 
    return arrayOfAttribute;
  }
  
  public Attribute[] getAttributes(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    ASN1Sequence aSN1Sequence = this.attrCert.getAcinfo().getAttributes();
    ArrayList<Attribute> arrayList = new ArrayList();
    for (byte b = 0; b != aSN1Sequence.size(); b++) {
      Attribute attribute = Attribute.getInstance(aSN1Sequence.getObjectAt(b));
      if (attribute.getAttrType().equals(paramASN1ObjectIdentifier))
        arrayList.add(attribute); 
    } 
    return (arrayList.size() == 0) ? EMPTY_ARRAY : arrayList.<Attribute>toArray(new Attribute[arrayList.size()]);
  }
  
  public boolean hasExtensions() {
    return (this.extensions != null);
  }
  
  public Extension getExtension(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return (this.extensions != null) ? this.extensions.getExtension(paramASN1ObjectIdentifier) : null;
  }
  
  public Extensions getExtensions() {
    return this.extensions;
  }
  
  public List getExtensionOIDs() {
    return CertUtils.getExtensionOIDs(this.extensions);
  }
  
  public Set getCriticalExtensionOIDs() {
    return CertUtils.getCriticalExtensionOIDs(this.extensions);
  }
  
  public Set getNonCriticalExtensionOIDs() {
    return CertUtils.getNonCriticalExtensionOIDs(this.extensions);
  }
  
  public boolean[] getIssuerUniqueID() {
    return CertUtils.bitStringToBoolean(this.attrCert.getAcinfo().getIssuerUniqueID());
  }
  
  public AlgorithmIdentifier getSignatureAlgorithm() {
    return this.attrCert.getSignatureAlgorithm();
  }
  
  public byte[] getSignature() {
    return this.attrCert.getSignatureValue().getOctets();
  }
  
  public AttributeCertificate toASN1Structure() {
    return this.attrCert;
  }
  
  public boolean isValidOn(Date paramDate) {
    AttCertValidityPeriod attCertValidityPeriod = this.attrCert.getAcinfo().getAttrCertValidityPeriod();
    return (!paramDate.before(CertUtils.recoverDate(attCertValidityPeriod.getNotBeforeTime())) && !paramDate.after(CertUtils.recoverDate(attCertValidityPeriod.getNotAfterTime())));
  }
  
  public boolean isSignatureValid(ContentVerifierProvider paramContentVerifierProvider) throws CertException {
    ContentVerifier contentVerifier;
    AttributeCertificateInfo attributeCertificateInfo = this.attrCert.getAcinfo();
    if (!CertUtils.isAlgIdEqual(attributeCertificateInfo.getSignature(), this.attrCert.getSignatureAlgorithm()))
      throw new CertException("signature invalid - algorithm identifier mismatch"); 
    try {
      contentVerifier = paramContentVerifierProvider.get(attributeCertificateInfo.getSignature());
      OutputStream outputStream = contentVerifier.getOutputStream();
      DEROutputStream dEROutputStream = new DEROutputStream(outputStream);
      dEROutputStream.writeObject((ASN1Encodable)attributeCertificateInfo);
      outputStream.close();
    } catch (Exception exception) {
      throw new CertException("unable to process signature: " + exception.getMessage(), exception);
    } 
    return contentVerifier.verify(getSignature());
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof X509AttributeCertificateHolder))
      return false; 
    X509AttributeCertificateHolder x509AttributeCertificateHolder = (X509AttributeCertificateHolder)paramObject;
    return this.attrCert.equals(x509AttributeCertificateHolder.attrCert);
  }
  
  public int hashCode() {
    return this.attrCert.hashCode();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    init(AttributeCertificate.getInstance(paramObjectInputStream.readObject()));
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(getEncoded());
  }
}
