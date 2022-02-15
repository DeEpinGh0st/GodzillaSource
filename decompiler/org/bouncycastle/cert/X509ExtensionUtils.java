package org.bouncycastle.cert;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.DigestCalculator;

public class X509ExtensionUtils {
  private DigestCalculator calculator;
  
  public X509ExtensionUtils(DigestCalculator paramDigestCalculator) {
    this.calculator = paramDigestCalculator;
  }
  
  public AuthorityKeyIdentifier createAuthorityKeyIdentifier(X509CertificateHolder paramX509CertificateHolder) {
    GeneralName generalName = new GeneralName(paramX509CertificateHolder.getIssuer());
    return new AuthorityKeyIdentifier(getSubjectKeyIdentifier(paramX509CertificateHolder), new GeneralNames(generalName), paramX509CertificateHolder.getSerialNumber());
  }
  
  public AuthorityKeyIdentifier createAuthorityKeyIdentifier(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
    return new AuthorityKeyIdentifier(calculateIdentifier(paramSubjectPublicKeyInfo));
  }
  
  public AuthorityKeyIdentifier createAuthorityKeyIdentifier(SubjectPublicKeyInfo paramSubjectPublicKeyInfo, GeneralNames paramGeneralNames, BigInteger paramBigInteger) {
    return new AuthorityKeyIdentifier(calculateIdentifier(paramSubjectPublicKeyInfo), paramGeneralNames, paramBigInteger);
  }
  
  public SubjectKeyIdentifier createSubjectKeyIdentifier(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
    return new SubjectKeyIdentifier(calculateIdentifier(paramSubjectPublicKeyInfo));
  }
  
  public SubjectKeyIdentifier createTruncatedSubjectKeyIdentifier(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
    byte[] arrayOfByte1 = calculateIdentifier(paramSubjectPublicKeyInfo);
    byte[] arrayOfByte2 = new byte[8];
    System.arraycopy(arrayOfByte1, arrayOfByte1.length - 8, arrayOfByte2, 0, arrayOfByte2.length);
    arrayOfByte2[0] = (byte)(arrayOfByte2[0] & 0xF);
    arrayOfByte2[0] = (byte)(arrayOfByte2[0] | 0x40);
    return new SubjectKeyIdentifier(arrayOfByte2);
  }
  
  private byte[] getSubjectKeyIdentifier(X509CertificateHolder paramX509CertificateHolder) {
    if (paramX509CertificateHolder.getVersionNumber() != 3)
      return calculateIdentifier(paramX509CertificateHolder.getSubjectPublicKeyInfo()); 
    Extension extension = paramX509CertificateHolder.getExtension(Extension.subjectKeyIdentifier);
    return (extension != null) ? ASN1OctetString.getInstance(extension.getParsedValue()).getOctets() : calculateIdentifier(paramX509CertificateHolder.getSubjectPublicKeyInfo());
  }
  
  private byte[] calculateIdentifier(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
    byte[] arrayOfByte = paramSubjectPublicKeyInfo.getPublicKeyData().getBytes();
    OutputStream outputStream = this.calculator.getOutputStream();
    try {
      outputStream.write(arrayOfByte);
      outputStream.close();
    } catch (IOException iOException) {
      throw new CertRuntimeException("unable to calculate identifier: " + iOException.getMessage(), iOException);
    } 
    return this.calculator.getDigest();
  }
}
