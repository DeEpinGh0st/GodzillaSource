package org.bouncycastle.cert.jcajce;

import java.math.BigInteger;
import java.security.PublicKey;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v1CertificateBuilder;

public class JcaX509v1CertificateBuilder extends X509v1CertificateBuilder {
  public JcaX509v1CertificateBuilder(X500Name paramX500Name1, BigInteger paramBigInteger, Date paramDate1, Date paramDate2, X500Name paramX500Name2, PublicKey paramPublicKey) {
    super(paramX500Name1, paramBigInteger, paramDate1, paramDate2, paramX500Name2, SubjectPublicKeyInfo.getInstance(paramPublicKey.getEncoded()));
  }
  
  public JcaX509v1CertificateBuilder(X500Principal paramX500Principal1, BigInteger paramBigInteger, Date paramDate1, Date paramDate2, X500Principal paramX500Principal2, PublicKey paramPublicKey) {
    super(X500Name.getInstance(paramX500Principal1.getEncoded()), paramBigInteger, paramDate1, paramDate2, X500Name.getInstance(paramX500Principal2.getEncoded()), SubjectPublicKeyInfo.getInstance(paramPublicKey.getEncoded()));
  }
}
