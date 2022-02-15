package org.bouncycastle.jcajce.provider.asymmetric.elgamal;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.DHGenParameterSpec;
import javax.crypto.spec.DHParameterSpec;
import org.bouncycastle.crypto.generators.ElGamalParametersGenerator;
import org.bouncycastle.crypto.params.ElGamalParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseAlgorithmParameterGeneratorSpi;

public class AlgorithmParameterGeneratorSpi extends BaseAlgorithmParameterGeneratorSpi {
  protected SecureRandom random;
  
  protected int strength = 1024;
  
  private int l = 0;
  
  protected void engineInit(int paramInt, SecureRandom paramSecureRandom) {
    this.strength = paramInt;
    this.random = paramSecureRandom;
  }
  
  protected void engineInit(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidAlgorithmParameterException {
    if (!(paramAlgorithmParameterSpec instanceof DHGenParameterSpec))
      throw new InvalidAlgorithmParameterException("DH parameter generator requires a DHGenParameterSpec for initialisation"); 
    DHGenParameterSpec dHGenParameterSpec = (DHGenParameterSpec)paramAlgorithmParameterSpec;
    this.strength = dHGenParameterSpec.getPrimeSize();
    this.l = dHGenParameterSpec.getExponentSize();
    this.random = paramSecureRandom;
  }
  
  protected AlgorithmParameters engineGenerateParameters() {
    AlgorithmParameters algorithmParameters;
    ElGamalParametersGenerator elGamalParametersGenerator = new ElGamalParametersGenerator();
    if (this.random != null) {
      elGamalParametersGenerator.init(this.strength, 20, this.random);
    } else {
      elGamalParametersGenerator.init(this.strength, 20, new SecureRandom());
    } 
    ElGamalParameters elGamalParameters = elGamalParametersGenerator.generateParameters();
    try {
      algorithmParameters = createParametersInstance("ElGamal");
      algorithmParameters.init(new DHParameterSpec(elGamalParameters.getP(), elGamalParameters.getG(), this.l));
    } catch (Exception exception) {
      throw new RuntimeException(exception.getMessage());
    } 
    return algorithmParameters;
  }
}
