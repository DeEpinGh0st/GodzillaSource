package org.bouncycastle.openssl.bc;

import org.bouncycastle.openssl.PEMDecryptor;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PasswordException;

public class BcPEMDecryptorProvider implements PEMDecryptorProvider {
  private final char[] password;
  
  public BcPEMDecryptorProvider(char[] paramArrayOfchar) {
    this.password = paramArrayOfchar;
  }
  
  public PEMDecryptor get(final String dekAlgName) {
    return new PEMDecryptor() {
        public byte[] decrypt(byte[] param1ArrayOfbyte1, byte[] param1ArrayOfbyte2) throws PEMException {
          if (BcPEMDecryptorProvider.this.password == null)
            throw new PasswordException("Password is null, but a password is required"); 
          return PEMUtilities.crypt(false, param1ArrayOfbyte1, BcPEMDecryptorProvider.this.password, dekAlgName, param1ArrayOfbyte2);
        }
      };
  }
}
