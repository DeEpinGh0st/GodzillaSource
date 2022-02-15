package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cms.KeyTransRecipientInfoGenerator;
import org.bouncycastle.operator.AsymmetricKeyWrapper;
import org.bouncycastle.operator.jcajce.JceAsymmetricKeyWrapper;
import org.bouncycastle.operator.jcajce.JceKTSKeyWrapper;
import org.bouncycastle.util.encoders.Hex;

public class JceKTSKeyTransRecipientInfoGenerator extends KeyTransRecipientInfoGenerator {
  private static final byte[] ANONYMOUS_SENDER = Hex.decode("0c14416e6f6e796d6f75732053656e64657220202020");
  
  private JceKTSKeyTransRecipientInfoGenerator(X509Certificate paramX509Certificate, IssuerAndSerialNumber paramIssuerAndSerialNumber, String paramString, int paramInt) throws CertificateEncodingException {
    super(paramIssuerAndSerialNumber, (AsymmetricKeyWrapper)new JceKTSKeyWrapper(paramX509Certificate, paramString, paramInt, ANONYMOUS_SENDER, getEncodedRecipID(paramIssuerAndSerialNumber)));
  }
  
  public JceKTSKeyTransRecipientInfoGenerator(X509Certificate paramX509Certificate, String paramString, int paramInt) throws CertificateEncodingException {
    this(paramX509Certificate, new IssuerAndSerialNumber((new JcaX509CertificateHolder(paramX509Certificate)).toASN1Structure()), paramString, paramInt);
  }
  
  public JceKTSKeyTransRecipientInfoGenerator(byte[] paramArrayOfbyte, PublicKey paramPublicKey, String paramString, int paramInt) {
    super(paramArrayOfbyte, (AsymmetricKeyWrapper)new JceKTSKeyWrapper(paramPublicKey, paramString, paramInt, ANONYMOUS_SENDER, getEncodedSubKeyId(paramArrayOfbyte)));
  }
  
  private static byte[] getEncodedRecipID(IssuerAndSerialNumber paramIssuerAndSerialNumber) throws CertificateEncodingException {
    try {
      return paramIssuerAndSerialNumber.getEncoded("DER");
    } catch (IOException iOException) {
      throw new CertificateEncodingException("Cannot process extracted IssuerAndSerialNumber: " + iOException.getMessage()) {
          public Throwable getCause() {
            return e;
          }
        };
    } 
  }
  
  private static byte[] getEncodedSubKeyId(byte[] paramArrayOfbyte) {
    try {
      return (new DEROctetString(paramArrayOfbyte)).getEncoded();
    } catch (IOException iOException) {
      throw new IllegalArgumentException("Cannot process subject key identifier: " + iOException.getMessage()) {
          public Throwable getCause() {
            return e;
          }
        };
    } 
  }
  
  public JceKTSKeyTransRecipientInfoGenerator(X509Certificate paramX509Certificate, AlgorithmIdentifier paramAlgorithmIdentifier) throws CertificateEncodingException {
    super(new IssuerAndSerialNumber((new JcaX509CertificateHolder(paramX509Certificate)).toASN1Structure()), (AsymmetricKeyWrapper)new JceAsymmetricKeyWrapper(paramAlgorithmIdentifier, paramX509Certificate.getPublicKey()));
  }
  
  public JceKTSKeyTransRecipientInfoGenerator(byte[] paramArrayOfbyte, AlgorithmIdentifier paramAlgorithmIdentifier, PublicKey paramPublicKey) {
    super(paramArrayOfbyte, (AsymmetricKeyWrapper)new JceAsymmetricKeyWrapper(paramAlgorithmIdentifier, paramPublicKey));
  }
  
  public JceKTSKeyTransRecipientInfoGenerator setProvider(String paramString) {
    ((JceKTSKeyWrapper)this.wrapper).setProvider(paramString);
    return this;
  }
  
  public JceKTSKeyTransRecipientInfoGenerator setProvider(Provider paramProvider) {
    ((JceKTSKeyWrapper)this.wrapper).setProvider(paramProvider);
    return this;
  }
}
