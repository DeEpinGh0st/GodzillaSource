package org.bouncycastle.cms.jcajce;

import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.cms.KEKIdentifier;
import org.bouncycastle.cms.KEKRecipientInfoGenerator;
import org.bouncycastle.operator.SymmetricKeyWrapper;
import org.bouncycastle.operator.jcajce.JceSymmetricKeyWrapper;

public class JceKEKRecipientInfoGenerator extends KEKRecipientInfoGenerator {
  public JceKEKRecipientInfoGenerator(KEKIdentifier paramKEKIdentifier, SecretKey paramSecretKey) {
    super(paramKEKIdentifier, (SymmetricKeyWrapper)new JceSymmetricKeyWrapper(paramSecretKey));
  }
  
  public JceKEKRecipientInfoGenerator(byte[] paramArrayOfbyte, SecretKey paramSecretKey) {
    this(new KEKIdentifier(paramArrayOfbyte, null, null), paramSecretKey);
  }
  
  public JceKEKRecipientInfoGenerator setProvider(Provider paramProvider) {
    ((JceSymmetricKeyWrapper)this.wrapper).setProvider(paramProvider);
    return this;
  }
  
  public JceKEKRecipientInfoGenerator setProvider(String paramString) {
    ((JceSymmetricKeyWrapper)this.wrapper).setProvider(paramString);
    return this;
  }
  
  public JceKEKRecipientInfoGenerator setSecureRandom(SecureRandom paramSecureRandom) {
    ((JceSymmetricKeyWrapper)this.wrapper).setSecureRandom(paramSecureRandom);
    return this;
  }
}
