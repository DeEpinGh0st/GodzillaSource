package org.bouncycastle.cert.selector;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Selector;

public class X509CertificateHolderSelector implements Selector {
  private byte[] subjectKeyId;
  
  private X500Name issuer;
  
  private BigInteger serialNumber;
  
  public X509CertificateHolderSelector(byte[] paramArrayOfbyte) {
    this(null, null, paramArrayOfbyte);
  }
  
  public X509CertificateHolderSelector(X500Name paramX500Name, BigInteger paramBigInteger) {
    this(paramX500Name, paramBigInteger, null);
  }
  
  public X509CertificateHolderSelector(X500Name paramX500Name, BigInteger paramBigInteger, byte[] paramArrayOfbyte) {
    this.issuer = paramX500Name;
    this.serialNumber = paramBigInteger;
    this.subjectKeyId = paramArrayOfbyte;
  }
  
  public X500Name getIssuer() {
    return this.issuer;
  }
  
  public BigInteger getSerialNumber() {
    return this.serialNumber;
  }
  
  public byte[] getSubjectKeyIdentifier() {
    return Arrays.clone(this.subjectKeyId);
  }
  
  public int hashCode() {
    int i = Arrays.hashCode(this.subjectKeyId);
    if (this.serialNumber != null)
      i ^= this.serialNumber.hashCode(); 
    if (this.issuer != null)
      i ^= this.issuer.hashCode(); 
    return i;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof X509CertificateHolderSelector))
      return false; 
    X509CertificateHolderSelector x509CertificateHolderSelector = (X509CertificateHolderSelector)paramObject;
    return (Arrays.areEqual(this.subjectKeyId, x509CertificateHolderSelector.subjectKeyId) && equalsObj(this.serialNumber, x509CertificateHolderSelector.serialNumber) && equalsObj(this.issuer, x509CertificateHolderSelector.issuer));
  }
  
  private boolean equalsObj(Object paramObject1, Object paramObject2) {
    return (paramObject1 != null) ? paramObject1.equals(paramObject2) : ((paramObject2 == null));
  }
  
  public boolean match(Object paramObject) {
    if (paramObject instanceof X509CertificateHolder) {
      X509CertificateHolder x509CertificateHolder = (X509CertificateHolder)paramObject;
      if (getSerialNumber() != null) {
        IssuerAndSerialNumber issuerAndSerialNumber = new IssuerAndSerialNumber(x509CertificateHolder.toASN1Structure());
        return (issuerAndSerialNumber.getName().equals(this.issuer) && issuerAndSerialNumber.getSerialNumber().getValue().equals(this.serialNumber));
      } 
      if (this.subjectKeyId != null) {
        Extension extension = x509CertificateHolder.getExtension(Extension.subjectKeyIdentifier);
        if (extension == null)
          return Arrays.areEqual(this.subjectKeyId, MSOutlookKeyIdCalculator.calculateKeyId(x509CertificateHolder.getSubjectPublicKeyInfo())); 
        byte[] arrayOfByte = ASN1OctetString.getInstance(extension.getParsedValue()).getOctets();
        return Arrays.areEqual(this.subjectKeyId, arrayOfByte);
      } 
    } else if (paramObject instanceof byte[]) {
      return Arrays.areEqual(this.subjectKeyId, (byte[])paramObject);
    } 
    return false;
  }
  
  public Object clone() {
    return new X509CertificateHolderSelector(this.issuer, this.serialNumber, this.subjectKeyId);
  }
}
