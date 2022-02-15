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

public class ECElGamalEncryptor implements ECEncryptor {
  private ECPublicKeyParameters key;
  
  private SecureRandom random;
  
  public void init(CipherParameters paramCipherParameters) {
    if (paramCipherParameters instanceof ParametersWithRandom) {
      ParametersWithRandom parametersWithRandom = (ParametersWithRandom)paramCipherParameters;
      if (!(parametersWithRandom.getParameters() instanceof ECPublicKeyParameters))
        throw new IllegalArgumentException("ECPublicKeyParameters are required for encryption."); 
      this.key = (ECPublicKeyParameters)parametersWithRandom.getParameters();
      this.random = parametersWithRandom.getRandom();
    } else {
      if (!(paramCipherParameters instanceof ECPublicKeyParameters))
        throw new IllegalArgumentException("ECPublicKeyParameters are required for encryption."); 
      this.key = (ECPublicKeyParameters)paramCipherParameters;
      this.random = new SecureRandom();
    } 
  }
  
  public ECPair encrypt(ECPoint paramECPoint) {
    if (this.key == null)
      throw new IllegalStateException("ECElGamalEncryptor not initialised"); 
    ECDomainParameters eCDomainParameters = this.key.getParameters();
    BigInteger bigInteger = ECUtil.generateK(eCDomainParameters.getN(), this.random);
    ECMultiplier eCMultiplier = createBasePointMultiplier();
    ECPoint[] arrayOfECPoint = { eCMultiplier.multiply(eCDomainParameters.getG(), bigInteger), this.key.getQ().multiply(bigInteger).add(paramECPoint) };
    eCDomainParameters.getCurve().normalizeAll(arrayOfECPoint);
    return new ECPair(arrayOfECPoint[0], arrayOfECPoint[1]);
  }
  
  protected ECMultiplier createBasePointMultiplier() {
    return (ECMultiplier)new FixedPointCombMultiplier();
  }
}
