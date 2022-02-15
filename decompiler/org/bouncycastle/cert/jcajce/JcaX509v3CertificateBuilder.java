package org.bouncycastle.cert.jcajce;

import java.math.BigInteger;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.cert.X509v3CertificateBuilder;

public class JcaX509v3CertificateBuilder extends X509v3CertificateBuilder {
  public JcaX509v3CertificateBuilder(X500Name paramX500Name1, BigInteger paramBigInteger, Date paramDate1, Date paramDate2, X500Name paramX500Name2, PublicKey paramPublicKey) {
    super(paramX500Name1, paramBigInteger, paramDate1, paramDate2, paramX500Name2, SubjectPublicKeyInfo.getInstance(paramPublicKey.getEncoded()));
  }
  
  public JcaX509v3CertificateBuilder(X500Name paramX500Name1, BigInteger paramBigInteger, Time paramTime1, Time paramTime2, X500Name paramX500Name2, PublicKey paramPublicKey) {
    super(paramX500Name1, paramBigInteger, paramTime1, paramTime2, paramX500Name2, SubjectPublicKeyInfo.getInstance(paramPublicKey.getEncoded()));
  }
  
  public JcaX509v3CertificateBuilder(X500Principal paramX500Principal1, BigInteger paramBigInteger, Date paramDate1, Date paramDate2, X500Principal paramX500Principal2, PublicKey paramPublicKey) {
    super(X500Name.getInstance(paramX500Principal1.getEncoded()), paramBigInteger, paramDate1, paramDate2, X500Name.getInstance(paramX500Principal2.getEncoded()), SubjectPublicKeyInfo.getInstance(paramPublicKey.getEncoded()));
  }
  
  public JcaX509v3CertificateBuilder(X509Certificate paramX509Certificate, BigInteger paramBigInteger, Date paramDate1, Date paramDate2, X500Principal paramX500Principal, PublicKey paramPublicKey) {
    this(paramX509Certificate.getSubjectX500Principal(), paramBigInteger, paramDate1, paramDate2, paramX500Principal, paramPublicKey);
  }
  
  public JcaX509v3CertificateBuilder(X509Certificate paramX509Certificate, BigInteger paramBigInteger, Date paramDate1, Date paramDate2, X500Name paramX500Name, PublicKey paramPublicKey) {
    this(X500Name.getInstance(paramX509Certificate.getSubjectX500Principal().getEncoded()), paramBigInteger, paramDate1, paramDate2, paramX500Name, paramPublicKey);
  }
  
  public JcaX509v3CertificateBuilder copyAndAddExtension(ASN1ObjectIdentifier paramASN1ObjectIdentifier, boolean paramBoolean, X509Certificate paramX509Certificate) throws CertificateEncodingException {
    copyAndAddExtension(paramASN1ObjectIdentifier, paramBoolean, new JcaX509CertificateHolder(paramX509Certificate));
    return this;
  }
}
