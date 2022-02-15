package org.bouncycastle.jcajce.provider.asymmetric.ec;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.crypto.BasicAgreement;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.agreement.ECDHBasicAgreement;
import org.bouncycastle.crypto.agreement.ECDHCBasicAgreement;
import org.bouncycastle.crypto.agreement.ECMQVBasicAgreement;
import org.bouncycastle.crypto.agreement.kdf.ConcatenationKDFGenerator;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.MQVPrivateParameters;
import org.bouncycastle.crypto.params.MQVPublicParameters;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseAgreementSpi;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.spec.MQVParameterSpec;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.interfaces.MQVPrivateKey;
import org.bouncycastle.jce.interfaces.MQVPublicKey;

public class KeyAgreementSpi extends BaseAgreementSpi {
  private static final X9IntegerConverter converter = new X9IntegerConverter();
  
  private String kaAlgorithm;
  
  private ECDomainParameters parameters;
  
  private BasicAgreement agreement;
  
  private MQVParameterSpec mqvParameters;
  
  private BigInteger result;
  
  protected KeyAgreementSpi(String paramString, BasicAgreement paramBasicAgreement, DerivationFunction paramDerivationFunction) {
    super(paramString, paramDerivationFunction);
    this.kaAlgorithm = paramString;
    this.agreement = paramBasicAgreement;
  }
  
  protected byte[] bigIntToBytes(BigInteger paramBigInteger) {
    return converter.integerToBytes(paramBigInteger, converter.getByteLength(this.parameters.getCurve()));
  }
  
  protected Key engineDoPhase(Key paramKey, boolean paramBoolean) throws InvalidKeyException, IllegalStateException {
    AsymmetricKeyParameter asymmetricKeyParameter;
    if (this.parameters == null)
      throw new IllegalStateException(this.kaAlgorithm + " not initialised."); 
    if (!paramBoolean)
      throw new IllegalStateException(this.kaAlgorithm + " can only be between two parties."); 
    if (this.agreement instanceof ECMQVBasicAgreement) {
      if (!(paramKey instanceof MQVPublicKey)) {
        ECPublicKeyParameters eCPublicKeyParameters1 = (ECPublicKeyParameters)ECUtils.generatePublicKeyParameter((PublicKey)paramKey);
        ECPublicKeyParameters eCPublicKeyParameters2 = (ECPublicKeyParameters)ECUtils.generatePublicKeyParameter(this.mqvParameters.getOtherPartyEphemeralKey());
        MQVPublicParameters mQVPublicParameters = new MQVPublicParameters(eCPublicKeyParameters1, eCPublicKeyParameters2);
      } else {
        MQVPublicKey mQVPublicKey = (MQVPublicKey)paramKey;
        ECPublicKeyParameters eCPublicKeyParameters1 = (ECPublicKeyParameters)ECUtils.generatePublicKeyParameter(mQVPublicKey.getStaticKey());
        ECPublicKeyParameters eCPublicKeyParameters2 = (ECPublicKeyParameters)ECUtils.generatePublicKeyParameter(mQVPublicKey.getEphemeralKey());
        MQVPublicParameters mQVPublicParameters = new MQVPublicParameters(eCPublicKeyParameters1, eCPublicKeyParameters2);
      } 
    } else {
      if (!(paramKey instanceof PublicKey))
        throw new InvalidKeyException(this.kaAlgorithm + " key agreement requires " + getSimpleName(ECPublicKey.class) + " for doPhase"); 
      asymmetricKeyParameter = ECUtils.generatePublicKeyParameter((PublicKey)paramKey);
    } 
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
    if (paramAlgorithmParameterSpec != null && !(paramAlgorithmParameterSpec instanceof MQVParameterSpec) && !(paramAlgorithmParameterSpec instanceof UserKeyingMaterialSpec))
      throw new InvalidAlgorithmParameterException("No algorithm parameters supported"); 
    initFromKey(paramKey, paramAlgorithmParameterSpec);
  }
  
  protected void engineInit(Key paramKey, SecureRandom paramSecureRandom) throws InvalidKeyException {
    initFromKey(paramKey, null);
  }
  
  private void initFromKey(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidKeyException {
    if (this.agreement instanceof ECMQVBasicAgreement) {
      ECPrivateKeyParameters eCPrivateKeyParameters1;
      ECPrivateKeyParameters eCPrivateKeyParameters2;
      ECPublicKeyParameters eCPublicKeyParameters;
      this.mqvParameters = null;
      if (!(paramKey instanceof MQVPrivateKey) && !(paramAlgorithmParameterSpec instanceof MQVParameterSpec))
        throw new InvalidKeyException(this.kaAlgorithm + " key agreement requires " + getSimpleName(MQVParameterSpec.class) + " for initialisation"); 
      if (paramKey instanceof MQVPrivateKey) {
        MQVPrivateKey mQVPrivateKey = (MQVPrivateKey)paramKey;
        eCPrivateKeyParameters1 = (ECPrivateKeyParameters)ECUtil.generatePrivateKeyParameter(mQVPrivateKey.getStaticPrivateKey());
        eCPrivateKeyParameters2 = (ECPrivateKeyParameters)ECUtil.generatePrivateKeyParameter(mQVPrivateKey.getEphemeralPrivateKey());
        eCPublicKeyParameters = null;
        if (mQVPrivateKey.getEphemeralPublicKey() != null)
          eCPublicKeyParameters = (ECPublicKeyParameters)ECUtils.generatePublicKeyParameter(mQVPrivateKey.getEphemeralPublicKey()); 
      } else {
        MQVParameterSpec mQVParameterSpec = (MQVParameterSpec)paramAlgorithmParameterSpec;
        eCPrivateKeyParameters1 = (ECPrivateKeyParameters)ECUtil.generatePrivateKeyParameter((PrivateKey)paramKey);
        eCPrivateKeyParameters2 = (ECPrivateKeyParameters)ECUtil.generatePrivateKeyParameter(mQVParameterSpec.getEphemeralPrivateKey());
        eCPublicKeyParameters = null;
        if (mQVParameterSpec.getEphemeralPublicKey() != null)
          eCPublicKeyParameters = (ECPublicKeyParameters)ECUtils.generatePublicKeyParameter(mQVParameterSpec.getEphemeralPublicKey()); 
        this.mqvParameters = mQVParameterSpec;
        this.ukmParameters = mQVParameterSpec.getUserKeyingMaterial();
      } 
      MQVPrivateParameters mQVPrivateParameters = new MQVPrivateParameters(eCPrivateKeyParameters1, eCPrivateKeyParameters2, eCPublicKeyParameters);
      this.parameters = eCPrivateKeyParameters1.getParameters();
      this.agreement.init((CipherParameters)mQVPrivateParameters);
    } else {
      if (!(paramKey instanceof PrivateKey))
        throw new InvalidKeyException(this.kaAlgorithm + " key agreement requires " + getSimpleName(ECPrivateKey.class) + " for initialisation"); 
      ECPrivateKeyParameters eCPrivateKeyParameters = (ECPrivateKeyParameters)ECUtil.generatePrivateKeyParameter((PrivateKey)paramKey);
      this.parameters = eCPrivateKeyParameters.getParameters();
      this.ukmParameters = (paramAlgorithmParameterSpec instanceof UserKeyingMaterialSpec) ? ((UserKeyingMaterialSpec)paramAlgorithmParameterSpec).getUserKeyingMaterial() : null;
      this.agreement.init((CipherParameters)eCPrivateKeyParameters);
    } 
  }
  
  private static String getSimpleName(Class paramClass) {
    String str = paramClass.getName();
    return str.substring(str.lastIndexOf('.') + 1);
  }
  
  protected byte[] calcSecret() {
    return bigIntToBytes(this.result);
  }
  
  public static class CDHwithSHA1KDFAndSharedInfo extends KeyAgreementSpi {
    public CDHwithSHA1KDFAndSharedInfo() {
      super("ECCDHwithSHA1KDF", (BasicAgreement)new ECDHCBasicAgreement(), (DerivationFunction)new KDF2BytesGenerator(DigestFactory.createSHA1()));
    }
  }
  
  public static class CDHwithSHA224KDFAndSharedInfo extends KeyAgreementSpi {
    public CDHwithSHA224KDFAndSharedInfo() {
      super("ECCDHwithSHA224KDF", (BasicAgreement)new ECDHCBasicAgreement(), (DerivationFunction)new KDF2BytesGenerator(DigestFactory.createSHA224()));
    }
  }
  
  public static class CDHwithSHA256KDFAndSharedInfo extends KeyAgreementSpi {
    public CDHwithSHA256KDFAndSharedInfo() {
      super("ECCDHwithSHA256KDF", (BasicAgreement)new ECDHCBasicAgreement(), (DerivationFunction)new KDF2BytesGenerator(DigestFactory.createSHA256()));
    }
  }
  
  public static class CDHwithSHA384KDFAndSharedInfo extends KeyAgreementSpi {
    public CDHwithSHA384KDFAndSharedInfo() {
      super("ECCDHwithSHA384KDF", (BasicAgreement)new ECDHCBasicAgreement(), (DerivationFunction)new KDF2BytesGenerator(DigestFactory.createSHA384()));
    }
  }
  
  public static class CDHwithSHA512KDFAndSharedInfo extends KeyAgreementSpi {
    public CDHwithSHA512KDFAndSharedInfo() {
      super("ECCDHwithSHA512KDF", (BasicAgreement)new ECDHCBasicAgreement(), (DerivationFunction)new KDF2BytesGenerator(DigestFactory.createSHA512()));
    }
  }
  
  public static class DH extends KeyAgreementSpi {
    public DH() {
      super("ECDH", (BasicAgreement)new ECDHBasicAgreement(), null);
    }
  }
  
  public static class DHC extends KeyAgreementSpi {
    public DHC() {
      super("ECDHC", (BasicAgreement)new ECDHCBasicAgreement(), null);
    }
  }
  
  public static class DHwithSHA1CKDF extends KeyAgreementSpi {
    public DHwithSHA1CKDF() {
      super("ECDHwithSHA1CKDF", (BasicAgreement)new ECDHCBasicAgreement(), (DerivationFunction)new ConcatenationKDFGenerator(DigestFactory.createSHA1()));
    }
  }
  
  public static class DHwithSHA1KDF extends KeyAgreementSpi {
    public DHwithSHA1KDF() {
      super("ECDHwithSHA1KDF", (BasicAgreement)new ECDHBasicAgreement(), (DerivationFunction)new KDF2BytesGenerator(DigestFactory.createSHA1()));
    }
  }
  
  public static class DHwithSHA1KDFAndSharedInfo extends KeyAgreementSpi {
    public DHwithSHA1KDFAndSharedInfo() {
      super("ECDHwithSHA1KDF", (BasicAgreement)new ECDHBasicAgreement(), (DerivationFunction)new KDF2BytesGenerator(DigestFactory.createSHA1()));
    }
  }
  
  public static class DHwithSHA224KDFAndSharedInfo extends KeyAgreementSpi {
    public DHwithSHA224KDFAndSharedInfo() {
      super("ECDHwithSHA224KDF", (BasicAgreement)new ECDHBasicAgreement(), (DerivationFunction)new KDF2BytesGenerator(DigestFactory.createSHA224()));
    }
  }
  
  public static class DHwithSHA256CKDF extends KeyAgreementSpi {
    public DHwithSHA256CKDF() {
      super("ECDHwithSHA256CKDF", (BasicAgreement)new ECDHCBasicAgreement(), (DerivationFunction)new ConcatenationKDFGenerator(DigestFactory.createSHA256()));
    }
  }
  
  public static class DHwithSHA256KDFAndSharedInfo extends KeyAgreementSpi {
    public DHwithSHA256KDFAndSharedInfo() {
      super("ECDHwithSHA256KDF", (BasicAgreement)new ECDHBasicAgreement(), (DerivationFunction)new KDF2BytesGenerator(DigestFactory.createSHA256()));
    }
  }
  
  public static class DHwithSHA384CKDF extends KeyAgreementSpi {
    public DHwithSHA384CKDF() {
      super("ECDHwithSHA384CKDF", (BasicAgreement)new ECDHCBasicAgreement(), (DerivationFunction)new ConcatenationKDFGenerator(DigestFactory.createSHA384()));
    }
  }
  
  public static class DHwithSHA384KDFAndSharedInfo extends KeyAgreementSpi {
    public DHwithSHA384KDFAndSharedInfo() {
      super("ECDHwithSHA384KDF", (BasicAgreement)new ECDHBasicAgreement(), (DerivationFunction)new KDF2BytesGenerator(DigestFactory.createSHA384()));
    }
  }
  
  public static class DHwithSHA512CKDF extends KeyAgreementSpi {
    public DHwithSHA512CKDF() {
      super("ECDHwithSHA512CKDF", (BasicAgreement)new ECDHCBasicAgreement(), (DerivationFunction)new ConcatenationKDFGenerator(DigestFactory.createSHA512()));
    }
  }
  
  public static class DHwithSHA512KDFAndSharedInfo extends KeyAgreementSpi {
    public DHwithSHA512KDFAndSharedInfo() {
      super("ECDHwithSHA512KDF", (BasicAgreement)new ECDHBasicAgreement(), (DerivationFunction)new KDF2BytesGenerator(DigestFactory.createSHA512()));
    }
  }
  
  public static class MQV extends KeyAgreementSpi {
    public MQV() {
      super("ECMQV", (BasicAgreement)new ECMQVBasicAgreement(), null);
    }
  }
  
  public static class MQVwithSHA1CKDF extends KeyAgreementSpi {
    public MQVwithSHA1CKDF() {
      super("ECMQVwithSHA1CKDF", (BasicAgreement)new ECMQVBasicAgreement(), (DerivationFunction)new ConcatenationKDFGenerator(DigestFactory.createSHA1()));
    }
  }
  
  public static class MQVwithSHA1KDFAndSharedInfo extends KeyAgreementSpi {
    public MQVwithSHA1KDFAndSharedInfo() {
      super("ECMQVwithSHA1KDF", (BasicAgreement)new ECMQVBasicAgreement(), (DerivationFunction)new KDF2BytesGenerator(DigestFactory.createSHA1()));
    }
  }
  
  public static class MQVwithSHA224CKDF extends KeyAgreementSpi {
    public MQVwithSHA224CKDF() {
      super("ECMQVwithSHA224CKDF", (BasicAgreement)new ECMQVBasicAgreement(), (DerivationFunction)new ConcatenationKDFGenerator(DigestFactory.createSHA224()));
    }
  }
  
  public static class MQVwithSHA224KDFAndSharedInfo extends KeyAgreementSpi {
    public MQVwithSHA224KDFAndSharedInfo() {
      super("ECMQVwithSHA224KDF", (BasicAgreement)new ECMQVBasicAgreement(), (DerivationFunction)new KDF2BytesGenerator(DigestFactory.createSHA224()));
    }
  }
  
  public static class MQVwithSHA256CKDF extends KeyAgreementSpi {
    public MQVwithSHA256CKDF() {
      super("ECMQVwithSHA256CKDF", (BasicAgreement)new ECMQVBasicAgreement(), (DerivationFunction)new ConcatenationKDFGenerator(DigestFactory.createSHA256()));
    }
  }
  
  public static class MQVwithSHA256KDFAndSharedInfo extends KeyAgreementSpi {
    public MQVwithSHA256KDFAndSharedInfo() {
      super("ECMQVwithSHA256KDF", (BasicAgreement)new ECMQVBasicAgreement(), (DerivationFunction)new KDF2BytesGenerator(DigestFactory.createSHA256()));
    }
  }
  
  public static class MQVwithSHA384CKDF extends KeyAgreementSpi {
    public MQVwithSHA384CKDF() {
      super("ECMQVwithSHA384CKDF", (BasicAgreement)new ECMQVBasicAgreement(), (DerivationFunction)new ConcatenationKDFGenerator(DigestFactory.createSHA384()));
    }
  }
  
  public static class MQVwithSHA384KDFAndSharedInfo extends KeyAgreementSpi {
    public MQVwithSHA384KDFAndSharedInfo() {
      super("ECMQVwithSHA384KDF", (BasicAgreement)new ECMQVBasicAgreement(), (DerivationFunction)new KDF2BytesGenerator(DigestFactory.createSHA384()));
    }
  }
  
  public static class MQVwithSHA512CKDF extends KeyAgreementSpi {
    public MQVwithSHA512CKDF() {
      super("ECMQVwithSHA512CKDF", (BasicAgreement)new ECMQVBasicAgreement(), (DerivationFunction)new ConcatenationKDFGenerator(DigestFactory.createSHA512()));
    }
  }
  
  public static class MQVwithSHA512KDFAndSharedInfo extends KeyAgreementSpi {
    public MQVwithSHA512KDFAndSharedInfo() {
      super("ECMQVwithSHA512KDF", (BasicAgreement)new ECMQVBasicAgreement(), (DerivationFunction)new KDF2BytesGenerator(DigestFactory.createSHA512()));
    }
  }
}
