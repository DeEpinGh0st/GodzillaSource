package org.bouncycastle.jcajce.provider.asymmetric.dsa;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAParameterSpec;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.DSAParametersGenerator;
import org.bouncycastle.crypto.params.DSAParameterGenerationParameters;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseAlgorithmParameterGeneratorSpi;
import org.bouncycastle.jcajce.provider.asymmetric.util.PrimeCertaintyCalculator;

public class AlgorithmParameterGeneratorSpi extends BaseAlgorithmParameterGeneratorSpi {
  protected SecureRandom random;
  
  protected int strength = 2048;
  
  protected DSAParameterGenerationParameters params;
  
  protected void engineInit(int paramInt, SecureRandom paramSecureRandom) {
    if (paramInt < 512 || paramInt > 3072)
      throw new InvalidParameterException("strength must be from 512 - 3072"); 
    if (paramInt <= 1024 && paramInt % 64 != 0)
      throw new InvalidParameterException("strength must be a multiple of 64 below 1024 bits."); 
    if (paramInt > 1024 && paramInt % 1024 != 0)
      throw new InvalidParameterException("strength must be a multiple of 1024 above 1024 bits."); 
    this.strength = paramInt;
    this.random = paramSecureRandom;
  }
  
  protected void engineInit(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidAlgorithmParameterException {
    throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for DSA parameter generation.");
  }
  
  protected AlgorithmParameters engineGenerateParameters() {
    DSAParametersGenerator dSAParametersGenerator;
    AlgorithmParameters algorithmParameters;
    if (this.strength <= 1024) {
      dSAParametersGenerator = new DSAParametersGenerator();
    } else {
      dSAParametersGenerator = new DSAParametersGenerator((Digest)new SHA256Digest());
    } 
    if (this.random == null)
      this.random = new SecureRandom(); 
    int i = PrimeCertaintyCalculator.getDefaultCertainty(this.strength);
    if (this.strength == 1024) {
      this.params = new DSAParameterGenerationParameters(1024, 160, i, this.random);
      dSAParametersGenerator.init(this.params);
    } else if (this.strength > 1024) {
      this.params = new DSAParameterGenerationParameters(this.strength, 256, i, this.random);
      dSAParametersGenerator.init(this.params);
    } else {
      dSAParametersGenerator.init(this.strength, i, this.random);
    } 
    DSAParameters dSAParameters = dSAParametersGenerator.generateParameters();
    try {
      algorithmParameters = createParametersInstance("DSA");
      algorithmParameters.init(new DSAParameterSpec(dSAParameters.getP(), dSAParameters.getQ(), dSAParameters.getG()));
    } catch (Exception exception) {
      throw new RuntimeException(exception.getMessage());
    } 
    return algorithmParameters;
  }
}
