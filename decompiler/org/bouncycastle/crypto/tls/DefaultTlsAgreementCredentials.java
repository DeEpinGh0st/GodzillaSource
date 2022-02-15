package org.bouncycastle.crypto.tls;

import java.math.BigInteger;
import org.bouncycastle.crypto.BasicAgreement;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.agreement.DHBasicAgreement;
import org.bouncycastle.crypto.agreement.ECDHBasicAgreement;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.util.BigIntegers;

public class DefaultTlsAgreementCredentials extends AbstractTlsAgreementCredentials {
  protected Certificate certificate;
  
  protected AsymmetricKeyParameter privateKey;
  
  protected BasicAgreement basicAgreement;
  
  protected boolean truncateAgreement;
  
  public DefaultTlsAgreementCredentials(Certificate paramCertificate, AsymmetricKeyParameter paramAsymmetricKeyParameter) {
    if (paramCertificate == null)
      throw new IllegalArgumentException("'certificate' cannot be null"); 
    if (paramCertificate.isEmpty())
      throw new IllegalArgumentException("'certificate' cannot be empty"); 
    if (paramAsymmetricKeyParameter == null)
      throw new IllegalArgumentException("'privateKey' cannot be null"); 
    if (!paramAsymmetricKeyParameter.isPrivate())
      throw new IllegalArgumentException("'privateKey' must be private"); 
    if (paramAsymmetricKeyParameter instanceof org.bouncycastle.crypto.params.DHPrivateKeyParameters) {
      this.basicAgreement = (BasicAgreement)new DHBasicAgreement();
      this.truncateAgreement = true;
    } else if (paramAsymmetricKeyParameter instanceof org.bouncycastle.crypto.params.ECPrivateKeyParameters) {
      this.basicAgreement = (BasicAgreement)new ECDHBasicAgreement();
      this.truncateAgreement = false;
    } else {
      throw new IllegalArgumentException("'privateKey' type not supported: " + paramAsymmetricKeyParameter.getClass().getName());
    } 
    this.certificate = paramCertificate;
    this.privateKey = paramAsymmetricKeyParameter;
  }
  
  public Certificate getCertificate() {
    return this.certificate;
  }
  
  public byte[] generateAgreement(AsymmetricKeyParameter paramAsymmetricKeyParameter) {
    this.basicAgreement.init((CipherParameters)this.privateKey);
    BigInteger bigInteger = this.basicAgreement.calculateAgreement((CipherParameters)paramAsymmetricKeyParameter);
    return this.truncateAgreement ? BigIntegers.asUnsignedByteArray(bigInteger) : BigIntegers.asUnsignedByteArray(this.basicAgreement.getFieldSize(), bigInteger);
  }
}
