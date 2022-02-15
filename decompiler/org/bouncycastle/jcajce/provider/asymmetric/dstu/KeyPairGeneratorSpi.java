package org.bouncycastle.jcajce.provider.asymmetric.dstu;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ua.DSTU4145NamedCurves;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.generators.DSTU4145KeyPairGenerator;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class KeyPairGeneratorSpi extends KeyPairGenerator {
  Object ecParams = null;
  
  ECKeyPairGenerator engine = (ECKeyPairGenerator)new DSTU4145KeyPairGenerator();
  
  String algorithm = "DSTU4145";
  
  ECKeyGenerationParameters param;
  
  SecureRandom random = null;
  
  boolean initialised = false;
  
  public KeyPairGeneratorSpi() {
    super("DSTU4145");
  }
  
  public void initialize(int paramInt, SecureRandom paramSecureRandom) {
    this.random = paramSecureRandom;
    if (this.ecParams != null) {
      try {
        initialize((ECGenParameterSpec)this.ecParams, paramSecureRandom);
      } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
        throw new InvalidParameterException("key size not configurable.");
      } 
    } else {
      throw new InvalidParameterException("unknown key size.");
    } 
  }
  
  public void initialize(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidAlgorithmParameterException {
    if (paramAlgorithmParameterSpec instanceof ECParameterSpec) {
      ECParameterSpec eCParameterSpec = (ECParameterSpec)paramAlgorithmParameterSpec;
      this.ecParams = paramAlgorithmParameterSpec;
      this.param = new ECKeyGenerationParameters(new ECDomainParameters(eCParameterSpec.getCurve(), eCParameterSpec.getG(), eCParameterSpec.getN(), eCParameterSpec.getH()), paramSecureRandom);
      this.engine.init((KeyGenerationParameters)this.param);
      this.initialised = true;
    } else if (paramAlgorithmParameterSpec instanceof ECParameterSpec) {
      ECParameterSpec eCParameterSpec = (ECParameterSpec)paramAlgorithmParameterSpec;
      this.ecParams = paramAlgorithmParameterSpec;
      ECCurve eCCurve = EC5Util.convertCurve(eCParameterSpec.getCurve());
      ECPoint eCPoint = EC5Util.convertPoint(eCCurve, eCParameterSpec.getGenerator(), false);
      this.param = new ECKeyGenerationParameters(new ECDomainParameters(eCCurve, eCPoint, eCParameterSpec.getOrder(), BigInteger.valueOf(eCParameterSpec.getCofactor())), paramSecureRandom);
      this.engine.init((KeyGenerationParameters)this.param);
      this.initialised = true;
    } else if (paramAlgorithmParameterSpec instanceof ECGenParameterSpec || paramAlgorithmParameterSpec instanceof ECNamedCurveGenParameterSpec) {
      String str;
      if (paramAlgorithmParameterSpec instanceof ECGenParameterSpec) {
        str = ((ECGenParameterSpec)paramAlgorithmParameterSpec).getName();
      } else {
        str = ((ECNamedCurveGenParameterSpec)paramAlgorithmParameterSpec).getName();
      } 
      ECDomainParameters eCDomainParameters = DSTU4145NamedCurves.getByOID(new ASN1ObjectIdentifier(str));
      if (eCDomainParameters == null)
        throw new InvalidAlgorithmParameterException("unknown curve name: " + str); 
      this.ecParams = new ECNamedCurveSpec(str, eCDomainParameters.getCurve(), eCDomainParameters.getG(), eCDomainParameters.getN(), eCDomainParameters.getH(), eCDomainParameters.getSeed());
      ECParameterSpec eCParameterSpec = (ECParameterSpec)this.ecParams;
      ECCurve eCCurve = EC5Util.convertCurve(eCParameterSpec.getCurve());
      ECPoint eCPoint = EC5Util.convertPoint(eCCurve, eCParameterSpec.getGenerator(), false);
      this.param = new ECKeyGenerationParameters(new ECDomainParameters(eCCurve, eCPoint, eCParameterSpec.getOrder(), BigInteger.valueOf(eCParameterSpec.getCofactor())), paramSecureRandom);
      this.engine.init((KeyGenerationParameters)this.param);
      this.initialised = true;
    } else if (paramAlgorithmParameterSpec == null && BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa() != null) {
      ECParameterSpec eCParameterSpec = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
      this.ecParams = paramAlgorithmParameterSpec;
      this.param = new ECKeyGenerationParameters(new ECDomainParameters(eCParameterSpec.getCurve(), eCParameterSpec.getG(), eCParameterSpec.getN(), eCParameterSpec.getH()), paramSecureRandom);
      this.engine.init((KeyGenerationParameters)this.param);
      this.initialised = true;
    } else {
      if (paramAlgorithmParameterSpec == null && BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa() == null)
        throw new InvalidAlgorithmParameterException("null parameter passed but no implicitCA set"); 
      throw new InvalidAlgorithmParameterException("parameter object not a ECParameterSpec: " + paramAlgorithmParameterSpec.getClass().getName());
    } 
  }
  
  public KeyPair generateKeyPair() {
    if (!this.initialised)
      throw new IllegalStateException("DSTU Key Pair Generator not initialised"); 
    AsymmetricCipherKeyPair asymmetricCipherKeyPair = this.engine.generateKeyPair();
    ECPublicKeyParameters eCPublicKeyParameters = (ECPublicKeyParameters)asymmetricCipherKeyPair.getPublic();
    ECPrivateKeyParameters eCPrivateKeyParameters = (ECPrivateKeyParameters)asymmetricCipherKeyPair.getPrivate();
    if (this.ecParams instanceof ECParameterSpec) {
      ECParameterSpec eCParameterSpec1 = (ECParameterSpec)this.ecParams;
      BCDSTU4145PublicKey bCDSTU4145PublicKey1 = new BCDSTU4145PublicKey(this.algorithm, eCPublicKeyParameters, eCParameterSpec1);
      return new KeyPair(bCDSTU4145PublicKey1, new BCDSTU4145PrivateKey(this.algorithm, eCPrivateKeyParameters, bCDSTU4145PublicKey1, eCParameterSpec1));
    } 
    if (this.ecParams == null)
      return new KeyPair(new BCDSTU4145PublicKey(this.algorithm, eCPublicKeyParameters), new BCDSTU4145PrivateKey(this.algorithm, eCPrivateKeyParameters)); 
    ECParameterSpec eCParameterSpec = (ECParameterSpec)this.ecParams;
    BCDSTU4145PublicKey bCDSTU4145PublicKey = new BCDSTU4145PublicKey(this.algorithm, eCPublicKeyParameters, eCParameterSpec);
    return new KeyPair(bCDSTU4145PublicKey, new BCDSTU4145PrivateKey(this.algorithm, eCPrivateKeyParameters, bCDSTU4145PublicKey, eCParameterSpec));
  }
}
