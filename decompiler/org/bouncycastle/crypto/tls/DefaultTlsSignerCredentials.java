package org.bouncycastle.crypto.tls;

import java.io.IOException;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class DefaultTlsSignerCredentials extends AbstractTlsSignerCredentials {
  protected TlsContext context;
  
  protected Certificate certificate;
  
  protected AsymmetricKeyParameter privateKey;
  
  protected SignatureAndHashAlgorithm signatureAndHashAlgorithm;
  
  protected TlsSigner signer;
  
  public DefaultTlsSignerCredentials(TlsContext paramTlsContext, Certificate paramCertificate, AsymmetricKeyParameter paramAsymmetricKeyParameter) {
    this(paramTlsContext, paramCertificate, paramAsymmetricKeyParameter, null);
  }
  
  public DefaultTlsSignerCredentials(TlsContext paramTlsContext, Certificate paramCertificate, AsymmetricKeyParameter paramAsymmetricKeyParameter, SignatureAndHashAlgorithm paramSignatureAndHashAlgorithm) {
    if (paramCertificate == null)
      throw new IllegalArgumentException("'certificate' cannot be null"); 
    if (paramCertificate.isEmpty())
      throw new IllegalArgumentException("'certificate' cannot be empty"); 
    if (paramAsymmetricKeyParameter == null)
      throw new IllegalArgumentException("'privateKey' cannot be null"); 
    if (!paramAsymmetricKeyParameter.isPrivate())
      throw new IllegalArgumentException("'privateKey' must be private"); 
    if (TlsUtils.isTLSv12(paramTlsContext) && paramSignatureAndHashAlgorithm == null)
      throw new IllegalArgumentException("'signatureAndHashAlgorithm' cannot be null for (D)TLS 1.2+"); 
    if (paramAsymmetricKeyParameter instanceof org.bouncycastle.crypto.params.RSAKeyParameters) {
      this.signer = new TlsRSASigner();
    } else if (paramAsymmetricKeyParameter instanceof org.bouncycastle.crypto.params.DSAPrivateKeyParameters) {
      this.signer = new TlsDSSSigner();
    } else if (paramAsymmetricKeyParameter instanceof org.bouncycastle.crypto.params.ECPrivateKeyParameters) {
      this.signer = new TlsECDSASigner();
    } else {
      throw new IllegalArgumentException("'privateKey' type not supported: " + paramAsymmetricKeyParameter.getClass().getName());
    } 
    this.signer.init(paramTlsContext);
    this.context = paramTlsContext;
    this.certificate = paramCertificate;
    this.privateKey = paramAsymmetricKeyParameter;
    this.signatureAndHashAlgorithm = paramSignatureAndHashAlgorithm;
  }
  
  public Certificate getCertificate() {
    return this.certificate;
  }
  
  public byte[] generateCertificateSignature(byte[] paramArrayOfbyte) throws IOException {
    try {
      return TlsUtils.isTLSv12(this.context) ? this.signer.generateRawSignature(this.signatureAndHashAlgorithm, this.privateKey, paramArrayOfbyte) : this.signer.generateRawSignature(this.privateKey, paramArrayOfbyte);
    } catch (CryptoException cryptoException) {
      throw new TlsFatalAlert((short)80, cryptoException);
    } 
  }
  
  public SignatureAndHashAlgorithm getSignatureAndHashAlgorithm() {
    return this.signatureAndHashAlgorithm;
  }
}
