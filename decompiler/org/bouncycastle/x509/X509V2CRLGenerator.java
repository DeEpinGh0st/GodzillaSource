package org.bouncycastle.x509;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CRLException;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.TBSCertList;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.V2TBSCertListGenerator;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.asn1.x509.X509ExtensionsGenerator;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.X509CRLObject;

public class X509V2CRLGenerator {
  private final JcaJceHelper bcHelper = (JcaJceHelper)new BCJcaJceHelper();
  
  private V2TBSCertListGenerator tbsGen = new V2TBSCertListGenerator();
  
  private ASN1ObjectIdentifier sigOID;
  
  private AlgorithmIdentifier sigAlgId;
  
  private String signatureAlgorithm;
  
  private X509ExtensionsGenerator extGenerator = new X509ExtensionsGenerator();
  
  public void reset() {
    this.tbsGen = new V2TBSCertListGenerator();
    this.extGenerator.reset();
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
  
  public void setThisUpdate(Date paramDate) {
    this.tbsGen.setThisUpdate(new Time(paramDate));
  }
  
  public void setNextUpdate(Date paramDate) {
    this.tbsGen.setNextUpdate(new Time(paramDate));
  }
  
  public void addCRLEntry(BigInteger paramBigInteger, Date paramDate, int paramInt) {
    this.tbsGen.addCRLEntry(new ASN1Integer(paramBigInteger), new Time(paramDate), paramInt);
  }
  
  public void addCRLEntry(BigInteger paramBigInteger, Date paramDate1, int paramInt, Date paramDate2) {
    this.tbsGen.addCRLEntry(new ASN1Integer(paramBigInteger), new Time(paramDate1), paramInt, new ASN1GeneralizedTime(paramDate2));
  }
  
  public void addCRLEntry(BigInteger paramBigInteger, Date paramDate, X509Extensions paramX509Extensions) {
    this.tbsGen.addCRLEntry(new ASN1Integer(paramBigInteger), new Time(paramDate), Extensions.getInstance(paramX509Extensions));
  }
  
  public void addCRL(X509CRL paramX509CRL) throws CRLException {
    Set<? extends X509CRLEntry> set = paramX509CRL.getRevokedCertificates();
    if (set != null)
      for (X509CRLEntry x509CRLEntry : set) {
        ASN1InputStream aSN1InputStream = new ASN1InputStream(x509CRLEntry.getEncoded());
        try {
          this.tbsGen.addCRLEntry(ASN1Sequence.getInstance(aSN1InputStream.readObject()));
        } catch (IOException iOException) {
          throw new CRLException("exception processing encoding of CRL: " + iOException.toString());
        } 
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
  
  public void addExtension(String paramString, boolean paramBoolean, ASN1Encodable paramASN1Encodable) {
    addExtension(new ASN1ObjectIdentifier(paramString), paramBoolean, paramASN1Encodable);
  }
  
  public void addExtension(ASN1ObjectIdentifier paramASN1ObjectIdentifier, boolean paramBoolean, ASN1Encodable paramASN1Encodable) {
    this.extGenerator.addExtension(new ASN1ObjectIdentifier(paramASN1ObjectIdentifier.getId()), paramBoolean, paramASN1Encodable);
  }
  
  public void addExtension(String paramString, boolean paramBoolean, byte[] paramArrayOfbyte) {
    addExtension(new ASN1ObjectIdentifier(paramString), paramBoolean, paramArrayOfbyte);
  }
  
  public void addExtension(ASN1ObjectIdentifier paramASN1ObjectIdentifier, boolean paramBoolean, byte[] paramArrayOfbyte) {
    this.extGenerator.addExtension(new ASN1ObjectIdentifier(paramASN1ObjectIdentifier.getId()), paramBoolean, paramArrayOfbyte);
  }
  
  public X509CRL generateX509CRL(PrivateKey paramPrivateKey) throws SecurityException, SignatureException, InvalidKeyException {
    try {
      return generateX509CRL(paramPrivateKey, "BC", null);
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new SecurityException("BC provider not installed!");
    } 
  }
  
  public X509CRL generateX509CRL(PrivateKey paramPrivateKey, SecureRandom paramSecureRandom) throws SecurityException, SignatureException, InvalidKeyException {
    try {
      return generateX509CRL(paramPrivateKey, "BC", paramSecureRandom);
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new SecurityException("BC provider not installed!");
    } 
  }
  
  public X509CRL generateX509CRL(PrivateKey paramPrivateKey, String paramString) throws NoSuchProviderException, SecurityException, SignatureException, InvalidKeyException {
    return generateX509CRL(paramPrivateKey, paramString, null);
  }
  
  public X509CRL generateX509CRL(PrivateKey paramPrivateKey, String paramString, SecureRandom paramSecureRandom) throws NoSuchProviderException, SecurityException, SignatureException, InvalidKeyException {
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
  
  public X509CRL generate(PrivateKey paramPrivateKey) throws CRLException, IllegalStateException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
    return generate(paramPrivateKey, (SecureRandom)null);
  }
  
  public X509CRL generate(PrivateKey paramPrivateKey, SecureRandom paramSecureRandom) throws CRLException, IllegalStateException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
    byte[] arrayOfByte;
    TBSCertList tBSCertList = generateCertList();
    try {
      arrayOfByte = X509Util.calculateSignature(this.sigOID, this.signatureAlgorithm, paramPrivateKey, paramSecureRandom, (ASN1Encodable)tBSCertList);
    } catch (IOException iOException) {
      throw new ExtCRLException("cannot generate CRL encoding", iOException);
    } 
    return generateJcaObject(tBSCertList, arrayOfByte);
  }
  
  public X509CRL generate(PrivateKey paramPrivateKey, String paramString) throws CRLException, IllegalStateException, NoSuchProviderException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
    return generate(paramPrivateKey, paramString, null);
  }
  
  public X509CRL generate(PrivateKey paramPrivateKey, String paramString, SecureRandom paramSecureRandom) throws CRLException, IllegalStateException, NoSuchProviderException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
    byte[] arrayOfByte;
    TBSCertList tBSCertList = generateCertList();
    try {
      arrayOfByte = X509Util.calculateSignature(this.sigOID, this.signatureAlgorithm, paramString, paramPrivateKey, paramSecureRandom, (ASN1Encodable)tBSCertList);
    } catch (IOException iOException) {
      throw new ExtCRLException("cannot generate CRL encoding", iOException);
    } 
    return generateJcaObject(tBSCertList, arrayOfByte);
  }
  
  private TBSCertList generateCertList() {
    if (!this.extGenerator.isEmpty())
      this.tbsGen.setExtensions(this.extGenerator.generate()); 
    return this.tbsGen.generateTBSCertList();
  }
  
  private X509CRL generateJcaObject(TBSCertList paramTBSCertList, byte[] paramArrayOfbyte) throws CRLException {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)paramTBSCertList);
    aSN1EncodableVector.add((ASN1Encodable)this.sigAlgId);
    aSN1EncodableVector.add((ASN1Encodable)new DERBitString(paramArrayOfbyte));
    return (X509CRL)new X509CRLObject(new CertificateList((ASN1Sequence)new DERSequence(aSN1EncodableVector)));
  }
  
  public Iterator getSignatureAlgNames() {
    return X509Util.getAlgNames();
  }
  
  private static class ExtCRLException extends CRLException {
    Throwable cause;
    
    ExtCRLException(String param1String, Throwable param1Throwable) {
      super(param1String);
      this.cause = param1Throwable;
    }
    
    public Throwable getCause() {
      return this.cause;
    }
  }
}
