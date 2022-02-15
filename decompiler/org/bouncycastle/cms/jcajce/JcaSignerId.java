package org.bouncycastle.cms.jcajce;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cms.SignerId;

public class JcaSignerId extends SignerId {
  public JcaSignerId(X509Certificate paramX509Certificate) {
    super(convertPrincipal(paramX509Certificate.getIssuerX500Principal()), paramX509Certificate.getSerialNumber(), CMSUtils.getSubjectKeyId(paramX509Certificate));
  }
  
  public JcaSignerId(X500Principal paramX500Principal, BigInteger paramBigInteger) {
    super(convertPrincipal(paramX500Principal), paramBigInteger);
  }
  
  public JcaSignerId(X500Principal paramX500Principal, BigInteger paramBigInteger, byte[] paramArrayOfbyte) {
    super(convertPrincipal(paramX500Principal), paramBigInteger, paramArrayOfbyte);
  }
  
  private static X500Name convertPrincipal(X500Principal paramX500Principal) {
    return (paramX500Principal == null) ? null : X500Name.getInstance(paramX500Principal.getEncoded());
  }
}
