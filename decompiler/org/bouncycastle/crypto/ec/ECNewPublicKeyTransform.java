package org.bouncycastle.crypto.ec;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;

public class ECNewPublicKeyTransform implements ECPairTransform {
  private ECPublicKeyParameters key;
  
  private SecureRandom random;
  
  public void init(CipherParameters paramCipherParameters) {
    if (paramCipherParameters instanceof ParametersWithRandom) {
      ParametersWithRandom parametersWithRandom = (ParametersWithRandom)paramCipherParameters;
      if (!(parametersWithRandom.getParameters() instanceof ECPublicKeyParameters))
        throw new IllegalArgumentException("ECPublicKeyParameters are required for new public key transform."); 
      this.key = (ECPublicKeyParameters)parametersWithRandom.getParameters();
      this.random = parametersWithRandom.getRandom();
    } else {
      if (!(paramCipherParameters instanceof ECPublicKeyParameters))
        throw new IllegalArgumentException("ECPublicKeyParameters are required for new public key transform."); 
      this.key = (ECPublicKeyParameters)paramCipherParameters;
      this.random = new SecureRandom();
    } 
  }
  
  public ECPair transform(ECPair paramECPair) {
    if (this.key == null)
      throw new IllegalStateException("ECNewPublicKeyTransform not initialised"); 
    ECDomainParameters eCDomainParameters = this.key.getParameters();
    BigInteger bigInteger1 = eCDomainParameters.getN();
    ECMultiplier eCMultiplier = createBasePointMultiplier();
    BigInteger bigInteger2 = ECUtil.generateK(bigInteger1, this.random);
    ECPoint[] arrayOfECPoint = { eCMultiplier.multiply(eCDomainParameters.getG(), bigInteger2), this.key.getQ().multiply(bigInteger2).add(paramECPair.getY()) };
    eCDomainParameters.getCurve().normalizeAll(arrayOfECPoint);
    return new ECPair(arrayOfECPoint[0], arrayOfECPoint[1]);
  }
  
  protected ECMultiplier createBasePointMultiplier() {
    return (ECMultiplier)new FixedPointCombMultiplier();
  }
}
