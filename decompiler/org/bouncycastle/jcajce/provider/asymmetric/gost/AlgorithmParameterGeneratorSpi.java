package org.bouncycastle.jcajce.provider.asymmetric.gost;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.generators.GOST3410ParametersGenerator;
import org.bouncycastle.crypto.params.GOST3410Parameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseAlgorithmParameterGeneratorSpi;
import org.bouncycastle.jce.spec.GOST3410ParameterSpec;
import org.bouncycastle.jce.spec.GOST3410PublicKeyParameterSetSpec;

public abstract class AlgorithmParameterGeneratorSpi extends BaseAlgorithmParameterGeneratorSpi {
  protected SecureRandom random;
  
  protected int strength = 1024;
  
  protected void engineInit(int paramInt, SecureRandom paramSecureRandom) {
    this.strength = paramInt;
    this.random = paramSecureRandom;
  }
  
  protected void engineInit(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidAlgorithmParameterException {
    throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for GOST3410 parameter generation.");
  }
  
  protected AlgorithmParameters engineGenerateParameters() {
    AlgorithmParameters algorithmParameters;
    GOST3410ParametersGenerator gOST3410ParametersGenerator = new GOST3410ParametersGenerator();
    if (this.random != null) {
      gOST3410ParametersGenerator.init(this.strength, 2, this.random);
    } else {
      gOST3410ParametersGenerator.init(this.strength, 2, new SecureRandom());
    } 
    GOST3410Parameters gOST3410Parameters = gOST3410ParametersGenerator.generateParameters();
    try {
      algorithmParameters = createParametersInstance("GOST3410");
      algorithmParameters.init((AlgorithmParameterSpec)new GOST3410ParameterSpec(new GOST3410PublicKeyParameterSetSpec(gOST3410Parameters.getP(), gOST3410Parameters.getQ(), gOST3410Parameters.getA())));
    } catch (Exception exception) {
      throw new RuntimeException(exception.getMessage());
    } 
    return algorithmParameters;
  }
}
