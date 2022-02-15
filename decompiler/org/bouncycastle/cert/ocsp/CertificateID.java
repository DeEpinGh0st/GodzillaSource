package org.bouncycastle.cert.ocsp;

import java.io.OutputStream;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ocsp.CertID;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;

public class CertificateID {
  public static final AlgorithmIdentifier HASH_SHA1 = new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, (ASN1Encodable)DERNull.INSTANCE);
  
  private final CertID id;
  
  public CertificateID(CertID paramCertID) {
    if (paramCertID == null)
      throw new IllegalArgumentException("'id' cannot be null"); 
    this.id = paramCertID;
  }
  
  public CertificateID(DigestCalculator paramDigestCalculator, X509CertificateHolder paramX509CertificateHolder, BigInteger paramBigInteger) throws OCSPException {
    this.id = createCertID(paramDigestCalculator, paramX509CertificateHolder, new ASN1Integer(paramBigInteger));
  }
  
  public ASN1ObjectIdentifier getHashAlgOID() {
    return this.id.getHashAlgorithm().getAlgorithm();
  }
  
  public byte[] getIssuerNameHash() {
    return this.id.getIssuerNameHash().getOctets();
  }
  
  public byte[] getIssuerKeyHash() {
    return this.id.getIssuerKeyHash().getOctets();
  }
  
  public BigInteger getSerialNumber() {
    return this.id.getSerialNumber().getValue();
  }
  
  public boolean matchesIssuer(X509CertificateHolder paramX509CertificateHolder, DigestCalculatorProvider paramDigestCalculatorProvider) throws OCSPException {
    try {
      return createCertID(paramDigestCalculatorProvider.get(this.id.getHashAlgorithm()), paramX509CertificateHolder, this.id.getSerialNumber()).equals(this.id);
    } catch (OperatorCreationException operatorCreationException) {
      throw new OCSPException("unable to create digest calculator: " + operatorCreationException.getMessage(), operatorCreationException);
    } 
  }
  
  public CertID toASN1Primitive() {
    return this.id;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof CertificateID))
      return false; 
    CertificateID certificateID = (CertificateID)paramObject;
    return this.id.toASN1Primitive().equals(certificateID.id.toASN1Primitive());
  }
  
  public int hashCode() {
    return this.id.toASN1Primitive().hashCode();
  }
  
  public static CertificateID deriveCertificateID(CertificateID paramCertificateID, BigInteger paramBigInteger) {
    return new CertificateID(new CertID(paramCertificateID.id.getHashAlgorithm(), paramCertificateID.id.getIssuerNameHash(), paramCertificateID.id.getIssuerKeyHash(), new ASN1Integer(paramBigInteger)));
  }
  
  private static CertID createCertID(DigestCalculator paramDigestCalculator, X509CertificateHolder paramX509CertificateHolder, ASN1Integer paramASN1Integer) throws OCSPException {
    try {
      OutputStream outputStream = paramDigestCalculator.getOutputStream();
      outputStream.write(paramX509CertificateHolder.toASN1Structure().getSubject().getEncoded("DER"));
      outputStream.close();
      DEROctetString dEROctetString1 = new DEROctetString(paramDigestCalculator.getDigest());
      SubjectPublicKeyInfo subjectPublicKeyInfo = paramX509CertificateHolder.getSubjectPublicKeyInfo();
      outputStream = paramDigestCalculator.getOutputStream();
      outputStream.write(subjectPublicKeyInfo.getPublicKeyData().getBytes());
      outputStream.close();
      DEROctetString dEROctetString2 = new DEROctetString(paramDigestCalculator.getDigest());
      return new CertID(paramDigestCalculator.getAlgorithmIdentifier(), (ASN1OctetString)dEROctetString1, (ASN1OctetString)dEROctetString2, paramASN1Integer);
    } catch (Exception exception) {
      throw new OCSPException("problem creating ID: " + exception, exception);
    } 
  }
}
