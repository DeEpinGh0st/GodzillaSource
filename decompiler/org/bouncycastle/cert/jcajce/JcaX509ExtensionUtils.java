package org.bouncycastle.cert.jcajce;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509ExtensionUtils;
import org.bouncycastle.operator.DigestCalculator;

public class JcaX509ExtensionUtils extends X509ExtensionUtils {
  public JcaX509ExtensionUtils() throws NoSuchAlgorithmException {
    super(new SHA1DigestCalculator(MessageDigest.getInstance("SHA1")));
  }
  
  public JcaX509ExtensionUtils(DigestCalculator paramDigestCalculator) {
    super(paramDigestCalculator);
  }
  
  public AuthorityKeyIdentifier createAuthorityKeyIdentifier(X509Certificate paramX509Certificate) throws CertificateEncodingException {
    return createAuthorityKeyIdentifier(new JcaX509CertificateHolder(paramX509Certificate));
  }
  
  public AuthorityKeyIdentifier createAuthorityKeyIdentifier(PublicKey paramPublicKey) {
    return createAuthorityKeyIdentifier(SubjectPublicKeyInfo.getInstance(paramPublicKey.getEncoded()));
  }
  
  public AuthorityKeyIdentifier createAuthorityKeyIdentifier(PublicKey paramPublicKey, X500Principal paramX500Principal, BigInteger paramBigInteger) {
    return createAuthorityKeyIdentifier(SubjectPublicKeyInfo.getInstance(paramPublicKey.getEncoded()), new GeneralNames(new GeneralName(X500Name.getInstance(paramX500Principal.getEncoded()))), paramBigInteger);
  }
  
  public AuthorityKeyIdentifier createAuthorityKeyIdentifier(PublicKey paramPublicKey, GeneralNames paramGeneralNames, BigInteger paramBigInteger) {
    return createAuthorityKeyIdentifier(SubjectPublicKeyInfo.getInstance(paramPublicKey.getEncoded()), paramGeneralNames, paramBigInteger);
  }
  
  public SubjectKeyIdentifier createSubjectKeyIdentifier(PublicKey paramPublicKey) {
    return createSubjectKeyIdentifier(SubjectPublicKeyInfo.getInstance(paramPublicKey.getEncoded()));
  }
  
  public SubjectKeyIdentifier createTruncatedSubjectKeyIdentifier(PublicKey paramPublicKey) {
    return createTruncatedSubjectKeyIdentifier(SubjectPublicKeyInfo.getInstance(paramPublicKey.getEncoded()));
  }
  
  public static ASN1Primitive parseExtensionValue(byte[] paramArrayOfbyte) throws IOException {
    return ASN1Primitive.fromByteArray(ASN1OctetString.getInstance(paramArrayOfbyte).getOctets());
  }
  
  private static class SHA1DigestCalculator implements DigestCalculator {
    private ByteArrayOutputStream bOut = new ByteArrayOutputStream();
    
    private MessageDigest digest;
    
    public SHA1DigestCalculator(MessageDigest param1MessageDigest) {
      this.digest = param1MessageDigest;
    }
    
    public AlgorithmIdentifier getAlgorithmIdentifier() {
      return new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1);
    }
    
    public OutputStream getOutputStream() {
      return this.bOut;
    }
    
    public byte[] getDigest() {
      byte[] arrayOfByte = this.digest.digest(this.bOut.toByteArray());
      this.bOut.reset();
      return arrayOfByte;
    }
  }
}
