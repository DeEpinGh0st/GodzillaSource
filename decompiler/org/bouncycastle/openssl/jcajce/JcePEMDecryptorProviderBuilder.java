package org.bouncycastle.openssl.jcajce;

import java.security.Provider;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.openssl.PEMDecryptor;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PasswordException;

public class JcePEMDecryptorProviderBuilder {
  private JcaJceHelper helper = (JcaJceHelper)new DefaultJcaJceHelper();
  
  public JcePEMDecryptorProviderBuilder setProvider(Provider paramProvider) {
    this.helper = (JcaJceHelper)new ProviderJcaJceHelper(paramProvider);
    return this;
  }
  
  public JcePEMDecryptorProviderBuilder setProvider(String paramString) {
    this.helper = (JcaJceHelper)new NamedJcaJceHelper(paramString);
    return this;
  }
  
  public PEMDecryptorProvider build(final char[] password) {
    return new PEMDecryptorProvider() {
        public PEMDecryptor get(final String dekAlgName) {
          return new PEMDecryptor() {
              public byte[] decrypt(byte[] param2ArrayOfbyte1, byte[] param2ArrayOfbyte2) throws PEMException {
                if (password == null)
                  throw new PasswordException("Password is null, but a password is required"); 
                return PEMUtilities.crypt(false, JcePEMDecryptorProviderBuilder.this.helper, param2ArrayOfbyte1, password, dekAlgName, param2ArrayOfbyte2);
              }
            };
        }
      };
  }
}
