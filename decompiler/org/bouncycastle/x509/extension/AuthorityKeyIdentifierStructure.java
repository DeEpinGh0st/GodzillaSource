package org.bouncycastle.x509.extension;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.PrincipalUtil;

public class AuthorityKeyIdentifierStructure extends AuthorityKeyIdentifier {
  public AuthorityKeyIdentifierStructure(byte[] paramArrayOfbyte) throws IOException {
    super((ASN1Sequence)X509ExtensionUtil.fromExtensionValue(paramArrayOfbyte));
  }
  
  public AuthorityKeyIdentifierStructure(X509Extension paramX509Extension) {
    super((ASN1Sequence)paramX509Extension.getParsedValue());
  }
  
  public AuthorityKeyIdentifierStructure(Extension paramExtension) {
    super((ASN1Sequence)paramExtension.getParsedValue());
  }
  
  private static ASN1Sequence fromCertificate(X509Certificate paramX509Certificate) throws CertificateParsingException {
    try {
      if (paramX509Certificate.getVersion() != 3) {
        GeneralName generalName1 = new GeneralName((X509Name)PrincipalUtil.getIssuerX509Principal(paramX509Certificate));
        SubjectPublicKeyInfo subjectPublicKeyInfo1 = SubjectPublicKeyInfo.getInstance(paramX509Certificate.getPublicKey().getEncoded());
        return (ASN1Sequence)(new AuthorityKeyIdentifier(subjectPublicKeyInfo1, new GeneralNames(generalName1), paramX509Certificate.getSerialNumber())).toASN1Primitive();
      } 
      GeneralName generalName = new GeneralName((X509Name)PrincipalUtil.getIssuerX509Principal(paramX509Certificate));
      byte[] arrayOfByte = paramX509Certificate.getExtensionValue(Extension.subjectKeyIdentifier.getId());
      if (arrayOfByte != null) {
        ASN1OctetString aSN1OctetString = (ASN1OctetString)X509ExtensionUtil.fromExtensionValue(arrayOfByte);
        return (ASN1Sequence)(new AuthorityKeyIdentifier(aSN1OctetString.getOctets(), new GeneralNames(generalName), paramX509Certificate.getSerialNumber())).toASN1Primitive();
      } 
      SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(paramX509Certificate.getPublicKey().getEncoded());
      return (ASN1Sequence)(new AuthorityKeyIdentifier(subjectPublicKeyInfo, new GeneralNames(generalName), paramX509Certificate.getSerialNumber())).toASN1Primitive();
    } catch (Exception exception) {
      throw new CertificateParsingException("Exception extracting certificate details: " + exception.toString());
    } 
  }
  
  private static ASN1Sequence fromKey(PublicKey paramPublicKey) throws InvalidKeyException {
    try {
      SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(paramPublicKey.getEncoded());
      return (ASN1Sequence)(new AuthorityKeyIdentifier(subjectPublicKeyInfo)).toASN1Primitive();
    } catch (Exception exception) {
      throw new InvalidKeyException("can't process key: " + exception);
    } 
  }
  
  public AuthorityKeyIdentifierStructure(X509Certificate paramX509Certificate) throws CertificateParsingException {
    super(fromCertificate(paramX509Certificate));
  }
  
  public AuthorityKeyIdentifierStructure(PublicKey paramPublicKey) throws InvalidKeyException {
    super(fromKey(paramPublicKey));
  }
}
