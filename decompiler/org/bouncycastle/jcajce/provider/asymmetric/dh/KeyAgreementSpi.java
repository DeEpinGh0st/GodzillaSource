package org.bouncycastle.jcajce.provider.asymmetric.dh;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.agreement.kdf.DHKEKGenerator;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseAgreementSpi;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;

public class KeyAgreementSpi extends BaseAgreementSpi {
  private static final BigInteger ONE = BigInteger.valueOf(1L);
  
  private static final BigInteger TWO = BigInteger.valueOf(2L);
  
  private BigInteger x;
  
  private BigInteger p;
  
  private BigInteger g;
  
  private BigInteger result;
  
  public KeyAgreementSpi() {
    super("Diffie-Hellman", null);
  }
  
  public KeyAgreementSpi(String paramString, DerivationFunction paramDerivationFunction) {
    super(paramString, paramDerivationFunction);
  }
  
  protected byte[] bigIntToBytes(BigInteger paramBigInteger) {
    int i = (this.p.bitLength() + 7) / 8;
    byte[] arrayOfByte1 = paramBigInteger.toByteArray();
    if (arrayOfByte1.length == i)
      return arrayOfByte1; 
    if (arrayOfByte1[0] == 0 && arrayOfByte1.length == i + 1) {
      byte[] arrayOfByte = new byte[arrayOfByte1.length - 1];
      System.arraycopy(arrayOfByte1, 1, arrayOfByte, 0, arrayOfByte.length);
      return arrayOfByte;
    } 
    byte[] arrayOfByte2 = new byte[i];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, arrayOfByte2.length - arrayOfByte1.length, arrayOfByte1.length);
    return arrayOfByte2;
  }
  
  protected Key engineDoPhase(Key paramKey, boolean paramBoolean) throws InvalidKeyException, IllegalStateException {
    if (this.x == null)
      throw new IllegalStateException("Diffie-Hellman not initialised."); 
    if (!(paramKey instanceof DHPublicKey))
      throw new InvalidKeyException("DHKeyAgreement doPhase requires DHPublicKey"); 
    DHPublicKey dHPublicKey = (DHPublicKey)paramKey;
    if (!dHPublicKey.getParams().getG().equals(this.g) || !dHPublicKey.getParams().getP().equals(this.p))
      throw new InvalidKeyException("DHPublicKey not for this KeyAgreement!"); 
    BigInteger bigInteger = ((DHPublicKey)paramKey).getY();
    if (bigInteger == null || bigInteger.compareTo(TWO) < 0 || bigInteger.compareTo(this.p.subtract(ONE)) >= 0)
      throw new InvalidKeyException("Invalid DH PublicKey"); 
    this.result = bigInteger.modPow(this.x, this.p);
    if (this.result.compareTo(ONE) == 0)
      throw new InvalidKeyException("Shared key can't be 1"); 
    return paramBoolean ? null : new BCDHPublicKey(this.result, dHPublicKey.getParams());
  }
  
  protected byte[] engineGenerateSecret() throws IllegalStateException {
    if (this.x == null)
      throw new IllegalStateException("Diffie-Hellman not initialised."); 
    return super.engineGenerateSecret();
  }
  
  protected int engineGenerateSecret(byte[] paramArrayOfbyte, int paramInt) throws IllegalStateException, ShortBufferException {
    if (this.x == null)
      throw new IllegalStateException("Diffie-Hellman not initialised."); 
    return super.engineGenerateSecret(paramArrayOfbyte, paramInt);
  }
  
  protected SecretKey engineGenerateSecret(String paramString) throws NoSuchAlgorithmException {
    if (this.x == null)
      throw new IllegalStateException("Diffie-Hellman not initialised."); 
    byte[] arrayOfByte = bigIntToBytes(this.result);
    return paramString.equals("TlsPremasterSecret") ? new SecretKeySpec(trimZeroes(arrayOfByte), paramString) : super.engineGenerateSecret(paramString);
  }
  
  protected void engineInit(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
    if (!(paramKey instanceof DHPrivateKey))
      throw new InvalidKeyException("DHKeyAgreement requires DHPrivateKey for initialisation"); 
    DHPrivateKey dHPrivateKey = (DHPrivateKey)paramKey;
    if (paramAlgorithmParameterSpec != null) {
      if (paramAlgorithmParameterSpec instanceof DHParameterSpec) {
        DHParameterSpec dHParameterSpec = (DHParameterSpec)paramAlgorithmParameterSpec;
        this.p = dHParameterSpec.getP();
        this.g = dHParameterSpec.getG();
      } else if (paramAlgorithmParameterSpec instanceof UserKeyingMaterialSpec) {
        this.p = dHPrivateKey.getParams().getP();
        this.g = dHPrivateKey.getParams().getG();
        this.ukmParameters = ((UserKeyingMaterialSpec)paramAlgorithmParameterSpec).getUserKeyingMaterial();
      } else {
        throw new InvalidAlgorithmParameterException("DHKeyAgreement only accepts DHParameterSpec");
      } 
    } else {
      this.p = dHPrivateKey.getParams().getP();
      this.g = dHPrivateKey.getParams().getG();
    } 
    this.x = this.result = dHPrivateKey.getX();
  }
  
  protected void engineInit(Key paramKey, SecureRandom paramSecureRandom) throws InvalidKeyException {
    if (!(paramKey instanceof DHPrivateKey))
      throw new InvalidKeyException("DHKeyAgreement requires DHPrivateKey"); 
    DHPrivateKey dHPrivateKey = (DHPrivateKey)paramKey;
    this.p = dHPrivateKey.getParams().getP();
    this.g = dHPrivateKey.getParams().getG();
    this.x = this.result = dHPrivateKey.getX();
  }
  
  protected byte[] calcSecret() {
    return bigIntToBytes(this.result);
  }
  
  public static class DHwithRFC2631KDF extends KeyAgreementSpi {
    public DHwithRFC2631KDF() {
      super("DHwithRFC2631KDF", (DerivationFunction)new DHKEKGenerator(DigestFactory.createSHA1()));
    }
  }
}
