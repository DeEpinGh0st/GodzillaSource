package org.bouncycastle.crypto.tls;

import java.io.IOException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.RSAKeyParameters;

public class DefaultTlsEncryptionCredentials extends AbstractTlsEncryptionCredentials {
  protected TlsContext context;
  
  protected Certificate certificate;
  
  protected AsymmetricKeyParameter privateKey;
  
  public DefaultTlsEncryptionCredentials(TlsContext paramTlsContext, Certificate paramCertificate, AsymmetricKeyParameter paramAsymmetricKeyParameter) {
    if (paramCertificate == null)
      throw new IllegalArgumentException("'certificate' cannot be null"); 
    if (paramCertificate.isEmpty())
      throw new IllegalArgumentException("'certificate' cannot be empty"); 
    if (paramAsymmetricKeyParameter == null)
      throw new IllegalArgumentException("'privateKey' cannot be null"); 
    if (!paramAsymmetricKeyParameter.isPrivate())
      throw new IllegalArgumentException("'privateKey' must be private"); 
    if (paramAsymmetricKeyParameter instanceof RSAKeyParameters) {
      this.context = paramTlsContext;
      this.certificate = paramCertificate;
      this.privateKey = paramAsymmetricKeyParameter;
      return;
    } 
    throw new IllegalArgumentException("'privateKey' type not supported: " + paramAsymmetricKeyParameter.getClass().getName());
  }
  
  public Certificate getCertificate() {
    return this.certificate;
  }
  
  public byte[] decryptPreMasterSecret(byte[] paramArrayOfbyte) throws IOException {
    return TlsRSAUtils.safeDecryptPreMasterSecret(this.context, (RSAKeyParameters)this.privateKey, paramArrayOfbyte);
  }
}
