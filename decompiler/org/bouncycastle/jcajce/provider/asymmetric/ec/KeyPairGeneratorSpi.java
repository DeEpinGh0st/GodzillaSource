package org.bouncycastle.jcajce.provider.asymmetric.ec;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.util.Hashtable;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Integers;

public abstract class KeyPairGeneratorSpi extends KeyPairGenerator {
  public KeyPairGeneratorSpi(String paramString) {
    super(paramString);
  }
  
  public static class EC extends KeyPairGeneratorSpi {
    ECKeyGenerationParameters param;
    
    ECKeyPairGenerator engine = new ECKeyPairGenerator();
    
    Object ecParams = null;
    
    int strength = 239;
    
    int certainty = 50;
    
    SecureRandom random = new SecureRandom();
    
    boolean initialised = false;
    
    String algorithm = "EC";
    
    ProviderConfiguration configuration = BouncyCastleProvider.CONFIGURATION;
    
    private static Hashtable ecParameters = new Hashtable<Object, Object>();
    
    public EC() {
      super("EC");
    }
    
    public EC(String param1String, ProviderConfiguration param1ProviderConfiguration) {
      super(param1String);
    }
    
    public void initialize(int param1Int, SecureRandom param1SecureRandom) {
      this.strength = param1Int;
      this.random = param1SecureRandom;
      ECGenParameterSpec eCGenParameterSpec = (ECGenParameterSpec)ecParameters.get(Integers.valueOf(param1Int));
      if (eCGenParameterSpec == null)
        throw new InvalidParameterException("unknown key size."); 
      try {
        initialize(eCGenParameterSpec, param1SecureRandom);
      } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
        throw new InvalidParameterException("key size not configurable.");
      } 
    }
    
    public void initialize(AlgorithmParameterSpec param1AlgorithmParameterSpec, SecureRandom param1SecureRandom) throws InvalidAlgorithmParameterException {
      if (param1AlgorithmParameterSpec == null) {
        ECParameterSpec eCParameterSpec = this.configuration.getEcImplicitlyCa();
        if (eCParameterSpec == null)
          throw new InvalidAlgorithmParameterException("null parameter passed but no implicitCA set"); 
        this.ecParams = null;
        this.param = createKeyGenParamsBC(eCParameterSpec, param1SecureRandom);
      } else if (param1AlgorithmParameterSpec instanceof ECParameterSpec) {
        this.ecParams = param1AlgorithmParameterSpec;
        this.param = createKeyGenParamsBC((ECParameterSpec)param1AlgorithmParameterSpec, param1SecureRandom);
      } else if (param1AlgorithmParameterSpec instanceof ECParameterSpec) {
        this.ecParams = param1AlgorithmParameterSpec;
        this.param = createKeyGenParamsJCE((ECParameterSpec)param1AlgorithmParameterSpec, param1SecureRandom);
      } else if (param1AlgorithmParameterSpec instanceof ECGenParameterSpec) {
        initializeNamedCurve(((ECGenParameterSpec)param1AlgorithmParameterSpec).getName(), param1SecureRandom);
      } else if (param1AlgorithmParameterSpec instanceof ECNamedCurveGenParameterSpec) {
        initializeNamedCurve(((ECNamedCurveGenParameterSpec)param1AlgorithmParameterSpec).getName(), param1SecureRandom);
      } else {
        throw new InvalidAlgorithmParameterException("parameter object not a ECParameterSpec");
      } 
      this.engine.init((KeyGenerationParameters)this.param);
      this.initialised = true;
    }
    
    public KeyPair generateKeyPair() {
      if (!this.initialised)
        initialize(this.strength, new SecureRandom()); 
      AsymmetricCipherKeyPair asymmetricCipherKeyPair = this.engine.generateKeyPair();
      ECPublicKeyParameters eCPublicKeyParameters = (ECPublicKeyParameters)asymmetricCipherKeyPair.getPublic();
      ECPrivateKeyParameters eCPrivateKeyParameters = (ECPrivateKeyParameters)asymmetricCipherKeyPair.getPrivate();
      if (this.ecParams instanceof ECParameterSpec) {
        ECParameterSpec eCParameterSpec1 = (ECParameterSpec)this.ecParams;
        BCECPublicKey bCECPublicKey1 = new BCECPublicKey(this.algorithm, eCPublicKeyParameters, eCParameterSpec1, this.configuration);
        return new KeyPair(bCECPublicKey1, new BCECPrivateKey(this.algorithm, eCPrivateKeyParameters, bCECPublicKey1, eCParameterSpec1, this.configuration));
      } 
      if (this.ecParams == null)
        return new KeyPair(new BCECPublicKey(this.algorithm, eCPublicKeyParameters, this.configuration), new BCECPrivateKey(this.algorithm, eCPrivateKeyParameters, this.configuration)); 
      ECParameterSpec eCParameterSpec = (ECParameterSpec)this.ecParams;
      BCECPublicKey bCECPublicKey = new BCECPublicKey(this.algorithm, eCPublicKeyParameters, eCParameterSpec, this.configuration);
      return new KeyPair(bCECPublicKey, new BCECPrivateKey(this.algorithm, eCPrivateKeyParameters, bCECPublicKey, eCParameterSpec, this.configuration));
    }
    
    protected ECKeyGenerationParameters createKeyGenParamsBC(ECParameterSpec param1ECParameterSpec, SecureRandom param1SecureRandom) {
      return new ECKeyGenerationParameters(new ECDomainParameters(param1ECParameterSpec.getCurve(), param1ECParameterSpec.getG(), param1ECParameterSpec.getN(), param1ECParameterSpec.getH()), param1SecureRandom);
    }
    
    protected ECKeyGenerationParameters createKeyGenParamsJCE(ECParameterSpec param1ECParameterSpec, SecureRandom param1SecureRandom) {
      ECCurve eCCurve = EC5Util.convertCurve(param1ECParameterSpec.getCurve());
      ECPoint eCPoint = EC5Util.convertPoint(eCCurve, param1ECParameterSpec.getGenerator(), false);
      BigInteger bigInteger1 = param1ECParameterSpec.getOrder();
      BigInteger bigInteger2 = BigInteger.valueOf(param1ECParameterSpec.getCofactor());
      ECDomainParameters eCDomainParameters = new ECDomainParameters(eCCurve, eCPoint, bigInteger1, bigInteger2);
      return new ECKeyGenerationParameters(eCDomainParameters, param1SecureRandom);
    }
    
    protected ECNamedCurveSpec createNamedCurveSpec(String param1String) throws InvalidAlgorithmParameterException {
      X9ECParameters x9ECParameters = ECUtils.getDomainParametersFromName(param1String);
      if (x9ECParameters == null)
        try {
          x9ECParameters = ECNamedCurveTable.getByOID(new ASN1ObjectIdentifier(param1String));
          if (x9ECParameters == null) {
            Map map = this.configuration.getAdditionalECParameters();
            x9ECParameters = (X9ECParameters)map.get(new ASN1ObjectIdentifier(param1String));
            if (x9ECParameters == null)
              throw new InvalidAlgorithmParameterException("unknown curve OID: " + param1String); 
          } 
        } catch (IllegalArgumentException illegalArgumentException) {
          throw new InvalidAlgorithmParameterException("unknown curve name: " + param1String);
        }  
      byte[] arrayOfByte = null;
      return new ECNamedCurveSpec(param1String, x9ECParameters.getCurve(), x9ECParameters.getG(), x9ECParameters.getN(), x9ECParameters.getH(), arrayOfByte);
    }
    
    protected void initializeNamedCurve(String param1String, SecureRandom param1SecureRandom) throws InvalidAlgorithmParameterException {
      ECNamedCurveSpec eCNamedCurveSpec = createNamedCurveSpec(param1String);
      this.ecParams = eCNamedCurveSpec;
      this.param = createKeyGenParamsJCE((ECParameterSpec)eCNamedCurveSpec, param1SecureRandom);
    }
    
    static {
      ecParameters.put(Integers.valueOf(192), new ECGenParameterSpec("prime192v1"));
      ecParameters.put(Integers.valueOf(239), new ECGenParameterSpec("prime239v1"));
      ecParameters.put(Integers.valueOf(256), new ECGenParameterSpec("prime256v1"));
      ecParameters.put(Integers.valueOf(224), new ECGenParameterSpec("P-224"));
      ecParameters.put(Integers.valueOf(384), new ECGenParameterSpec("P-384"));
      ecParameters.put(Integers.valueOf(521), new ECGenParameterSpec("P-521"));
    }
  }
  
  public static class ECDH extends EC {
    public ECDH() {
      super("ECDH", BouncyCastleProvider.CONFIGURATION);
    }
  }
  
  public static class ECDHC extends EC {
    public ECDHC() {
      super("ECDHC", BouncyCastleProvider.CONFIGURATION);
    }
  }
  
  public static class ECDSA extends EC {
    public ECDSA() {
      super("ECDSA", BouncyCastleProvider.CONFIGURATION);
    }
  }
  
  public static class ECMQV extends EC {
    public ECMQV() {
      super("ECMQV", BouncyCastleProvider.CONFIGURATION);
    }
  }
}
