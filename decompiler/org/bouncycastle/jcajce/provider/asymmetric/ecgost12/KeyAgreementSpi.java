package org.bouncycastle.jcajce.provider.asymmetric.ecgost12;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.agreement.ECVKOAgreement;
import org.bouncycastle.crypto.digests.GOST3411_2012_256Digest;
import org.bouncycastle.crypto.digests.GOST3411_2012_512Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithUKM;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseAgreementSpi;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;

public class KeyAgreementSpi extends BaseAgreementSpi {
  private static final X9IntegerConverter converter = new X9IntegerConverter();
  
  private String kaAlgorithm;
  
  private ECDomainParameters parameters;
  
  private ECVKOAgreement agreement;
  
  private byte[] result;
  
  protected KeyAgreementSpi(String paramString, ECVKOAgreement paramECVKOAgreement, DerivationFunction paramDerivationFunction) {
    super(paramString, paramDerivationFunction);
    this.kaAlgorithm = paramString;
    this.agreement = paramECVKOAgreement;
  }
  
  protected Key engineDoPhase(Key paramKey, boolean paramBoolean) throws InvalidKeyException, IllegalStateException {
    if (this.parameters == null)
      throw new IllegalStateException(this.kaAlgorithm + " not initialised."); 
    if (!paramBoolean)
      throw new IllegalStateException(this.kaAlgorithm + " can only be between two parties."); 
    if (!(paramKey instanceof PublicKey))
      throw new InvalidKeyException(this.kaAlgorithm + " key agreement requires " + getSimpleName(ECPublicKey.class) + " for doPhase"); 
    AsymmetricKeyParameter asymmetricKeyParameter = generatePublicKeyParameter((PublicKey)paramKey);
    try {
      this.result = this.agreement.calculateAgreement((CipherParameters)asymmetricKeyParameter);
    } catch (Exception exception) {
      throw new InvalidKeyException("calculation failed: " + exception.getMessage()) {
          public Throwable getCause() {
            return e;
          }
        };
    } 
    return null;
  }
  
  protected void engineInit(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
    if (paramAlgorithmParameterSpec != null && !(paramAlgorithmParameterSpec instanceof UserKeyingMaterialSpec))
      throw new InvalidAlgorithmParameterException("No algorithm parameters supported"); 
    initFromKey(paramKey, paramAlgorithmParameterSpec);
  }
  
  protected void engineInit(Key paramKey, SecureRandom paramSecureRandom) throws InvalidKeyException {
    initFromKey(paramKey, null);
  }
  
  private void initFromKey(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidKeyException {
    if (!(paramKey instanceof PrivateKey))
      throw new InvalidKeyException(this.kaAlgorithm + " key agreement requires " + getSimpleName(ECPrivateKey.class) + " for initialisation"); 
    ECPrivateKeyParameters eCPrivateKeyParameters = (ECPrivateKeyParameters)ECUtil.generatePrivateKeyParameter((PrivateKey)paramKey);
    this.parameters = eCPrivateKeyParameters.getParameters();
    this.ukmParameters = (paramAlgorithmParameterSpec instanceof UserKeyingMaterialSpec) ? ((UserKeyingMaterialSpec)paramAlgorithmParameterSpec).getUserKeyingMaterial() : null;
    this.agreement.init((CipherParameters)new ParametersWithUKM((CipherParameters)eCPrivateKeyParameters, this.ukmParameters));
  }
  
  private static String getSimpleName(Class paramClass) {
    String str = paramClass.getName();
    return str.substring(str.lastIndexOf('.') + 1);
  }
  
  static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey paramPublicKey) throws InvalidKeyException {
    return (paramPublicKey instanceof BCECGOST3410_2012PublicKey) ? (AsymmetricKeyParameter)((BCECGOST3410_2012PublicKey)paramPublicKey).engineGetKeyParameters() : ECUtil.generatePublicKeyParameter(paramPublicKey);
  }
  
  protected byte[] calcSecret() {
    return this.result;
  }
  
  public static class ECVKO256 extends KeyAgreementSpi {
    public ECVKO256() {
      super("ECGOST3410-2012-256", new ECVKOAgreement((Digest)new GOST3411_2012_256Digest()), null);
    }
  }
  
  public static class ECVKO512 extends KeyAgreementSpi {
    public ECVKO512() {
      super("ECGOST3410-2012-512", new ECVKOAgreement((Digest)new GOST3411_2012_512Digest()), null);
    }
  }
}
