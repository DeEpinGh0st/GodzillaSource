package org.bouncycastle.jcajce.provider.asymmetric.dsa;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAParameterSpec;
import java.util.Hashtable;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.DSAKeyPairGenerator;
import org.bouncycastle.crypto.generators.DSAParametersGenerator;
import org.bouncycastle.crypto.params.DSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.DSAParameterGenerationParameters;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.PrimeCertaintyCalculator;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Properties;

public class KeyPairGeneratorSpi extends KeyPairGenerator {
  private static Hashtable params = new Hashtable<Object, Object>();
  
  private static Object lock = new Object();
  
  DSAKeyGenerationParameters param;
  
  DSAKeyPairGenerator engine = new DSAKeyPairGenerator();
  
  int strength = 2048;
  
  SecureRandom random = new SecureRandom();
  
  boolean initialised = false;
  
  public KeyPairGeneratorSpi() {
    super("DSA");
  }
  
  public void initialize(int paramInt, SecureRandom paramSecureRandom) {
    if (paramInt < 512 || paramInt > 4096 || (paramInt < 1024 && paramInt % 64 != 0) || (paramInt >= 1024 && paramInt % 1024 != 0))
      throw new InvalidParameterException("strength must be from 512 - 4096 and a multiple of 1024 above 1024"); 
    this.strength = paramInt;
    this.random = paramSecureRandom;
    this.initialised = false;
  }
  
  public void initialize(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidAlgorithmParameterException {
    if (!(paramAlgorithmParameterSpec instanceof DSAParameterSpec))
      throw new InvalidAlgorithmParameterException("parameter object not a DSAParameterSpec"); 
    DSAParameterSpec dSAParameterSpec = (DSAParameterSpec)paramAlgorithmParameterSpec;
    this.param = new DSAKeyGenerationParameters(paramSecureRandom, new DSAParameters(dSAParameterSpec.getP(), dSAParameterSpec.getQ(), dSAParameterSpec.getG()));
    this.engine.init((KeyGenerationParameters)this.param);
    this.initialised = true;
  }
  
  public KeyPair generateKeyPair() {
    if (!this.initialised) {
      Integer integer = Integers.valueOf(this.strength);
      if (params.containsKey(integer)) {
        this.param = (DSAKeyGenerationParameters)params.get(integer);
      } else {
        synchronized (lock) {
          if (params.containsKey(integer)) {
            this.param = (DSAKeyGenerationParameters)params.get(integer);
          } else {
            DSAParametersGenerator dSAParametersGenerator;
            int i = PrimeCertaintyCalculator.getDefaultCertainty(this.strength);
            if (this.strength == 1024) {
              dSAParametersGenerator = new DSAParametersGenerator();
              if (Properties.isOverrideSet("org.bouncycastle.dsa.FIPS186-2for1024bits")) {
                dSAParametersGenerator.init(this.strength, i, this.random);
              } else {
                DSAParameterGenerationParameters dSAParameterGenerationParameters = new DSAParameterGenerationParameters(1024, 160, i, this.random);
                dSAParametersGenerator.init(dSAParameterGenerationParameters);
              } 
            } else if (this.strength > 1024) {
              DSAParameterGenerationParameters dSAParameterGenerationParameters = new DSAParameterGenerationParameters(this.strength, 256, i, this.random);
              dSAParametersGenerator = new DSAParametersGenerator((Digest)new SHA256Digest());
              dSAParametersGenerator.init(dSAParameterGenerationParameters);
            } else {
              dSAParametersGenerator = new DSAParametersGenerator();
              dSAParametersGenerator.init(this.strength, i, this.random);
            } 
            this.param = new DSAKeyGenerationParameters(this.random, dSAParametersGenerator.generateParameters());
            params.put(integer, this.param);
          } 
        } 
      } 
      this.engine.init((KeyGenerationParameters)this.param);
      this.initialised = true;
    } 
    AsymmetricCipherKeyPair asymmetricCipherKeyPair = this.engine.generateKeyPair();
    DSAPublicKeyParameters dSAPublicKeyParameters = (DSAPublicKeyParameters)asymmetricCipherKeyPair.getPublic();
    DSAPrivateKeyParameters dSAPrivateKeyParameters = (DSAPrivateKeyParameters)asymmetricCipherKeyPair.getPrivate();
    return new KeyPair(new BCDSAPublicKey(dSAPublicKeyParameters), new BCDSAPrivateKey(dSAPrivateKeyParameters));
  }
}
