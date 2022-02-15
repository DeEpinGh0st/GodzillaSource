package org.bouncycastle.operator.jcajce;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.SymmetricKeyUnwrapper;

public class JceSymmetricKeyUnwrapper extends SymmetricKeyUnwrapper {
  private OperatorHelper helper = new OperatorHelper((JcaJceHelper)new DefaultJcaJceHelper());
  
  private SecretKey secretKey;
  
  public JceSymmetricKeyUnwrapper(AlgorithmIdentifier paramAlgorithmIdentifier, SecretKey paramSecretKey) {
    super(paramAlgorithmIdentifier);
    this.secretKey = paramSecretKey;
  }
  
  public JceSymmetricKeyUnwrapper setProvider(Provider paramProvider) {
    this.helper = new OperatorHelper((JcaJceHelper)new ProviderJcaJceHelper(paramProvider));
    return this;
  }
  
  public JceSymmetricKeyUnwrapper setProvider(String paramString) {
    this.helper = new OperatorHelper((JcaJceHelper)new NamedJcaJceHelper(paramString));
    return this;
  }
  
  public GenericKey generateUnwrappedKey(AlgorithmIdentifier paramAlgorithmIdentifier, byte[] paramArrayOfbyte) throws OperatorException {
    try {
      Cipher cipher = this.helper.createSymmetricWrapper(getAlgorithmIdentifier().getAlgorithm());
      cipher.init(4, this.secretKey);
      return new JceGenericKey(paramAlgorithmIdentifier, cipher.unwrap(paramArrayOfbyte, this.helper.getKeyAlgorithmName(paramAlgorithmIdentifier.getAlgorithm()), 3));
    } catch (InvalidKeyException invalidKeyException) {
      throw new OperatorException("key invalid in message.", invalidKeyException);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new OperatorException("can't find algorithm.", noSuchAlgorithmException);
    } 
  }
}
