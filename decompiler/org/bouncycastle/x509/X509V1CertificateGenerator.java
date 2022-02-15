package org.bouncycastle.x509;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Iterator;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.V1TBSCertificateGenerator;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jcajce.provider.asymmetric.x509.CertificateFactory;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.X509Principal;

public class X509V1CertificateGenerator {
  private final JcaJceHelper bcHelper = (JcaJceHelper)new BCJcaJceHelper();
  
  private final CertificateFactory certificateFactory = new CertificateFactory();
  
  private V1TBSCertificateGenerator tbsGen = new V1TBSCertificateGenerator();
  
  private ASN1ObjectIdentifier sigOID;
  
  private AlgorithmIdentifier sigAlgId;
  
  private String signatureAlgorithm;
  
  public void reset() {
    this.tbsGen = new V1TBSCertificateGenerator();
  }
  
  public void setSerialNumber(BigInteger paramBigInteger) {
    if (paramBigInteger.compareTo(BigInteger.ZERO) <= 0)
      throw new IllegalArgumentException("serial number must be a positive integer"); 
    this.tbsGen.setSerialNumber(new ASN1Integer(paramBigInteger));
  }
  
  public void setIssuerDN(X500Principal paramX500Principal) {
    try {
      this.tbsGen.setIssuer((X509Name)new X509Principal(paramX500Principal.getEncoded()));
    } catch (IOException iOException) {
      throw new IllegalArgumentException("can't process principal: " + iOException);
    } 
  }
  
  public void setIssuerDN(X509Name paramX509Name) {
    this.tbsGen.setIssuer(paramX509Name);
  }
  
  public void setNotBefore(Date paramDate) {
    this.tbsGen.setStartDate(new Time(paramDate));
  }
  
  public void setNotAfter(Date paramDate) {
    this.tbsGen.setEndDate(new Time(paramDate));
  }
  
  public void setSubjectDN(X500Principal paramX500Principal) {
    try {
      this.tbsGen.setSubject((X509Name)new X509Principal(paramX500Principal.getEncoded()));
    } catch (IOException iOException) {
      throw new IllegalArgumentException("can't process principal: " + iOException);
    } 
  }
  
  public void setSubjectDN(X509Name paramX509Name) {
    this.tbsGen.setSubject(paramX509Name);
  }
  
  public void setPublicKey(PublicKey paramPublicKey) {
    try {
      this.tbsGen.setSubjectPublicKeyInfo(SubjectPublicKeyInfo.getInstance(paramPublicKey.getEncoded()));
    } catch (Exception exception) {
      throw new IllegalArgumentException("unable to process key - " + exception.toString());
    } 
  }
  
  public void setSignatureAlgorithm(String paramString) {
    this.signatureAlgorithm = paramString;
    try {
      this.sigOID = X509Util.getAlgorithmOID(paramString);
    } catch (Exception exception) {
      throw new IllegalArgumentException("Unknown signature type requested");
    } 
    this.sigAlgId = X509Util.getSigAlgID(this.sigOID, paramString);
    this.tbsGen.setSignature(this.sigAlgId);
  }
  
  public X509Certificate generateX509Certificate(PrivateKey paramPrivateKey) throws SecurityException, SignatureException, InvalidKeyException {
    try {
      return generateX509Certificate(paramPrivateKey, "BC", null);
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new SecurityException("BC provider not installed!");
    } 
  }
  
  public X509Certificate generateX509Certificate(PrivateKey paramPrivateKey, SecureRandom paramSecureRandom) throws SecurityException, SignatureException, InvalidKeyException {
    try {
      return generateX509Certificate(paramPrivateKey, "BC", paramSecureRandom);
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new SecurityException("BC provider not installed!");
    } 
  }
  
  public X509Certificate generateX509Certificate(PrivateKey paramPrivateKey, String paramString) throws NoSuchProviderException, SecurityException, SignatureException, InvalidKeyException {
    return generateX509Certificate(paramPrivateKey, paramString, null);
  }
  
  public X509Certificate generateX509Certificate(PrivateKey paramPrivateKey, String paramString, SecureRandom paramSecureRandom) throws NoSuchProviderException, SecurityException, SignatureException, InvalidKeyException {
    try {
      return generate(paramPrivateKey, paramString, paramSecureRandom);
    } catch (NoSuchProviderException noSuchProviderException) {
      throw noSuchProviderException;
    } catch (SignatureException signatureException) {
      throw signatureException;
    } catch (InvalidKeyException invalidKeyException) {
      throw invalidKeyException;
    } catch (GeneralSecurityException generalSecurityException) {
      throw new SecurityException("exception: " + generalSecurityException);
    } 
  }
  
  public X509Certificate generate(PrivateKey paramPrivateKey) throws CertificateEncodingException, IllegalStateException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
    return generate(paramPrivateKey, (SecureRandom)null);
  }
  
  public X509Certificate generate(PrivateKey paramPrivateKey, SecureRandom paramSecureRandom) throws CertificateEncodingException, IllegalStateException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
    byte[] arrayOfByte;
    TBSCertificate tBSCertificate = this.tbsGen.generateTBSCertificate();
    try {
      arrayOfByte = X509Util.calculateSignature(this.sigOID, this.signatureAlgorithm, paramPrivateKey, paramSecureRandom, (ASN1Encodable)tBSCertificate);
    } catch (IOException iOException) {
      throw new ExtCertificateEncodingException("exception encoding TBS cert", iOException);
    } 
    return generateJcaObject(tBSCertificate, arrayOfByte);
  }
  
  public X509Certificate generate(PrivateKey paramPrivateKey, String paramString) throws CertificateEncodingException, IllegalStateException, NoSuchProviderException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
    return generate(paramPrivateKey, paramString, null);
  }
  
  public X509Certificate generate(PrivateKey paramPrivateKey, String paramString, SecureRandom paramSecureRandom) throws CertificateEncodingException, IllegalStateException, NoSuchProviderException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
    byte[] arrayOfByte;
    TBSCertificate tBSCertificate = this.tbsGen.generateTBSCertificate();
    try {
      arrayOfByte = X509Util.calculateSignature(this.sigOID, this.signatureAlgorithm, paramString, paramPrivateKey, paramSecureRandom, (ASN1Encodable)tBSCertificate);
    } catch (IOException iOException) {
      throw new ExtCertificateEncodingException("exception encoding TBS cert", iOException);
    } 
    return generateJcaObject(tBSCertificate, arrayOfByte);
  }
  
  private X509Certificate generateJcaObject(TBSCertificate paramTBSCertificate, byte[] paramArrayOfbyte) throws CertificateEncodingException {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)paramTBSCertificate);
    aSN1EncodableVector.add((ASN1Encodable)this.sigAlgId);
    aSN1EncodableVector.add((ASN1Encodable)new DERBitString(paramArrayOfbyte));
    try {
      return (X509Certificate)this.certificateFactory.engineGenerateCertificate(new ByteArrayInputStream((new DERSequence(aSN1EncodableVector)).getEncoded("DER")));
    } catch (Exception exception) {
      throw new ExtCertificateEncodingException("exception producing certificate object", exception);
    } 
  }
  
  public Iterator getSignatureAlgNames() {
    return X509Util.getAlgNames();
  }
}
