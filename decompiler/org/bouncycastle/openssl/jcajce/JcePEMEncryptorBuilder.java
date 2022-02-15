package org.bouncycastle.openssl.jcajce;

import java.security.Provider;
import java.security.SecureRandom;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.openssl.PEMEncryptor;
import org.bouncycastle.openssl.PEMException;

public class JcePEMEncryptorBuilder {
  private final String algorithm;
  
  private JcaJceHelper helper = (JcaJceHelper)new DefaultJcaJceHelper();
  
  private SecureRandom random;
  
  public JcePEMEncryptorBuilder(String paramString) {
    this.algorithm = paramString;
  }
  
  public JcePEMEncryptorBuilder setProvider(Provider paramProvider) {
    this.helper = (JcaJceHelper)new ProviderJcaJceHelper(paramProvider);
    return this;
  }
  
  public JcePEMEncryptorBuilder setProvider(String paramString) {
    this.helper = (JcaJceHelper)new NamedJcaJceHelper(paramString);
    return this;
  }
  
  public JcePEMEncryptorBuilder setSecureRandom(SecureRandom paramSecureRandom) {
    this.random = paramSecureRandom;
    return this;
  }
  
  public PEMEncryptor build(final char[] password) {
    if (this.random == null)
      this.random = new SecureRandom(); 
    byte b = this.algorithm.startsWith("AES-") ? 16 : 8;
    final byte[] iv = new byte[b];
    this.random.nextBytes(arrayOfByte);
    return new PEMEncryptor() {
        public String getAlgorithm() {
          return JcePEMEncryptorBuilder.this.algorithm;
        }
        
        public byte[] getIV() {
          return iv;
        }
        
        public byte[] encrypt(byte[] param1ArrayOfbyte) throws PEMException {
          return PEMUtilities.crypt(true, JcePEMEncryptorBuilder.this.helper, param1ArrayOfbyte, password, JcePEMEncryptorBuilder.this.algorithm, iv);
        }
      };
  }
}
