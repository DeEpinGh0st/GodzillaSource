package org.bouncycastle.jcajce.provider.symmetric;

import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.crypto.generators.OpenSSLPBEParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.util.Strings;

public final class OpenSSLPBKDF {
  public static class Mappings extends AlgorithmProvider {
    private static final String PREFIX = OpenSSLPBKDF.class.getName();
    
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("SecretKeyFactory.PBKDF-OPENSSL", PREFIX + "$PBKDF");
    }
  }
  
  public static class PBKDF extends BaseSecretKeyFactory {
    public PBKDF() {
      super("PBKDF-OpenSSL", null);
    }
    
    protected SecretKey engineGenerateSecret(KeySpec param1KeySpec) throws InvalidKeySpecException {
      if (param1KeySpec instanceof PBEKeySpec) {
        PBEKeySpec pBEKeySpec = (PBEKeySpec)param1KeySpec;
        if (pBEKeySpec.getSalt() == null)
          throw new InvalidKeySpecException("missing required salt"); 
        if (pBEKeySpec.getIterationCount() <= 0)
          throw new InvalidKeySpecException("positive iteration count required: " + pBEKeySpec.getIterationCount()); 
        if (pBEKeySpec.getKeyLength() <= 0)
          throw new InvalidKeySpecException("positive key length required: " + pBEKeySpec.getKeyLength()); 
        if ((pBEKeySpec.getPassword()).length == 0)
          throw new IllegalArgumentException("password empty"); 
        OpenSSLPBEParametersGenerator openSSLPBEParametersGenerator = new OpenSSLPBEParametersGenerator();
        openSSLPBEParametersGenerator.init(Strings.toByteArray(pBEKeySpec.getPassword()), pBEKeySpec.getSalt());
        return new SecretKeySpec(((KeyParameter)openSSLPBEParametersGenerator.generateDerivedParameters(pBEKeySpec.getKeyLength())).getKey(), "OpenSSLPBKDF");
      } 
      throw new InvalidKeySpecException("Invalid KeySpec");
    }
  }
}
