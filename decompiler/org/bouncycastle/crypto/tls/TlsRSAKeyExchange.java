package org.bouncycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.util.io.Streams;

public class TlsRSAKeyExchange extends AbstractTlsKeyExchange {
  protected AsymmetricKeyParameter serverPublicKey = null;
  
  protected RSAKeyParameters rsaServerPublicKey = null;
  
  protected TlsEncryptionCredentials serverCredentials = null;
  
  protected byte[] premasterSecret;
  
  public TlsRSAKeyExchange(Vector paramVector) {
    super(1, paramVector);
  }
  
  public void skipServerCredentials() throws IOException {
    throw new TlsFatalAlert((short)10);
  }
  
  public void processServerCredentials(TlsCredentials paramTlsCredentials) throws IOException {
    if (!(paramTlsCredentials instanceof TlsEncryptionCredentials))
      throw new TlsFatalAlert((short)80); 
    processServerCertificate(paramTlsCredentials.getCertificate());
    this.serverCredentials = (TlsEncryptionCredentials)paramTlsCredentials;
  }
  
  public void processServerCertificate(Certificate paramCertificate) throws IOException {
    if (paramCertificate.isEmpty())
      throw new TlsFatalAlert((short)42); 
    Certificate certificate = paramCertificate.getCertificateAt(0);
    SubjectPublicKeyInfo subjectPublicKeyInfo = certificate.getSubjectPublicKeyInfo();
    try {
      this.serverPublicKey = PublicKeyFactory.createKey(subjectPublicKeyInfo);
    } catch (RuntimeException runtimeException) {
      throw new TlsFatalAlert((short)43, runtimeException);
    } 
    if (this.serverPublicKey.isPrivate())
      throw new TlsFatalAlert((short)80); 
    this.rsaServerPublicKey = validateRSAPublicKey((RSAKeyParameters)this.serverPublicKey);
    TlsUtils.validateKeyUsage(certificate, 32);
    super.processServerCertificate(paramCertificate);
  }
  
  public void validateCertificateRequest(CertificateRequest paramCertificateRequest) throws IOException {
    short[] arrayOfShort = paramCertificateRequest.getCertificateTypes();
    for (byte b = 0; b < arrayOfShort.length; b++) {
      switch (arrayOfShort[b]) {
        case 1:
        case 2:
        case 64:
          break;
        default:
          throw new TlsFatalAlert((short)47);
      } 
    } 
  }
  
  public void processClientCredentials(TlsCredentials paramTlsCredentials) throws IOException {
    if (!(paramTlsCredentials instanceof TlsSignerCredentials))
      throw new TlsFatalAlert((short)80); 
  }
  
  public void generateClientKeyExchange(OutputStream paramOutputStream) throws IOException {
    this.premasterSecret = TlsRSAUtils.generateEncryptedPreMasterSecret(this.context, this.rsaServerPublicKey, paramOutputStream);
  }
  
  public void processClientKeyExchange(InputStream paramInputStream) throws IOException {
    byte[] arrayOfByte;
    if (TlsUtils.isSSL(this.context)) {
      arrayOfByte = Streams.readAll(paramInputStream);
    } else {
      arrayOfByte = TlsUtils.readOpaque16(paramInputStream);
    } 
    this.premasterSecret = this.serverCredentials.decryptPreMasterSecret(arrayOfByte);
  }
  
  public byte[] generatePremasterSecret() throws IOException {
    if (this.premasterSecret == null)
      throw new TlsFatalAlert((short)80); 
    byte[] arrayOfByte = this.premasterSecret;
    this.premasterSecret = null;
    return arrayOfByte;
  }
  
  protected RSAKeyParameters validateRSAPublicKey(RSAKeyParameters paramRSAKeyParameters) throws IOException {
    if (!paramRSAKeyParameters.getExponent().isProbablePrime(2))
      throw new TlsFatalAlert((short)47); 
    return paramRSAKeyParameters;
  }
}
