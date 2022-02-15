package org.bouncycastle.cert.selector.jcajce;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.selector.X509CertificateHolderSelector;

public class JcaX509CertificateHolderSelector extends X509CertificateHolderSelector {
  public JcaX509CertificateHolderSelector(X509Certificate paramX509Certificate) {
    super(convertPrincipal(paramX509Certificate.getIssuerX500Principal()), paramX509Certificate.getSerialNumber(), getSubjectKeyId(paramX509Certificate));
  }
  
  public JcaX509CertificateHolderSelector(X500Principal paramX500Principal, BigInteger paramBigInteger) {
    super(convertPrincipal(paramX500Principal), paramBigInteger);
  }
  
  public JcaX509CertificateHolderSelector(X500Principal paramX500Principal, BigInteger paramBigInteger, byte[] paramArrayOfbyte) {
    super(convertPrincipal(paramX500Principal), paramBigInteger, paramArrayOfbyte);
  }
  
  private static X500Name convertPrincipal(X500Principal paramX500Principal) {
    return (paramX500Principal == null) ? null : X500Name.getInstance(paramX500Principal.getEncoded());
  }
  
  private static byte[] getSubjectKeyId(X509Certificate paramX509Certificate) {
    byte[] arrayOfByte = paramX509Certificate.getExtensionValue(Extension.subjectKeyIdentifier.getId());
    return (arrayOfByte != null) ? ASN1OctetString.getInstance(ASN1OctetString.getInstance(arrayOfByte).getOctets()).getOctets() : null;
  }
}
