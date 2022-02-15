package org.bouncycastle.cms.jcajce;

import java.security.PrivateKey;
import java.security.Provider;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.SymmetricKeyUnwrapper;
import org.bouncycastle.operator.jcajce.JceAsymmetricKeyUnwrapper;
import org.bouncycastle.operator.jcajce.JceKTSKeyUnwrapper;
import org.bouncycastle.operator.jcajce.JceSymmetricKeyUnwrapper;

class ProviderJcaJceExtHelper extends ProviderJcaJceHelper implements JcaJceExtHelper {
  public ProviderJcaJceExtHelper(Provider paramProvider) {
    super(paramProvider);
  }
  
  public JceAsymmetricKeyUnwrapper createAsymmetricUnwrapper(AlgorithmIdentifier paramAlgorithmIdentifier, PrivateKey paramPrivateKey) {
    return (new JceAsymmetricKeyUnwrapper(paramAlgorithmIdentifier, paramPrivateKey)).setProvider(this.provider);
  }
  
  public JceKTSKeyUnwrapper createAsymmetricUnwrapper(AlgorithmIdentifier paramAlgorithmIdentifier, PrivateKey paramPrivateKey, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    return (new JceKTSKeyUnwrapper(paramAlgorithmIdentifier, paramPrivateKey, paramArrayOfbyte1, paramArrayOfbyte2)).setProvider(this.provider);
  }
  
  public SymmetricKeyUnwrapper createSymmetricUnwrapper(AlgorithmIdentifier paramAlgorithmIdentifier, SecretKey paramSecretKey) {
    return (SymmetricKeyUnwrapper)(new JceSymmetricKeyUnwrapper(paramAlgorithmIdentifier, paramSecretKey)).setProvider(this.provider);
  }
}
