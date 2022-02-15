package org.bouncycastle.pqc.jcajce.provider.newhope;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.ShortBufferException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseAgreementSpi;
import org.bouncycastle.pqc.crypto.ExchangePair;
import org.bouncycastle.pqc.crypto.newhope.NHAgreement;
import org.bouncycastle.pqc.crypto.newhope.NHExchangePairGenerator;
import org.bouncycastle.pqc.crypto.newhope.NHPublicKeyParameters;
import org.bouncycastle.util.Arrays;

public class KeyAgreementSpi extends BaseAgreementSpi {
  private NHAgreement agreement;
  
  private BCNHPublicKey otherPartyKey;
  
  private NHExchangePairGenerator exchangePairGenerator;
  
  private byte[] shared;
  
  public KeyAgreementSpi() {
    super("NH", null);
  }
  
  protected void engineInit(Key paramKey, SecureRandom paramSecureRandom) throws InvalidKeyException {
    if (paramKey != null) {
      this.agreement = new NHAgreement();
      this.agreement.init(((BCNHPrivateKey)paramKey).getKeyParams());
    } else {
      this.exchangePairGenerator = new NHExchangePairGenerator(paramSecureRandom);
    } 
  }
  
  protected void engineInit(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
    throw new InvalidAlgorithmParameterException("NewHope does not require parameters");
  }
  
  protected Key engineDoPhase(Key paramKey, boolean paramBoolean) throws InvalidKeyException, IllegalStateException {
    if (!paramBoolean)
      throw new IllegalStateException("NewHope can only be between two parties."); 
    this.otherPartyKey = (BCNHPublicKey)paramKey;
    if (this.exchangePairGenerator != null) {
      ExchangePair exchangePair = this.exchangePairGenerator.generateExchange((AsymmetricKeyParameter)this.otherPartyKey.getKeyParams());
      this.shared = exchangePair.getSharedValue();
      return (Key)new BCNHPublicKey((NHPublicKeyParameters)exchangePair.getPublicKey());
    } 
    this.shared = this.agreement.calculateAgreement(this.otherPartyKey.getKeyParams());
    return null;
  }
  
  protected byte[] engineGenerateSecret() throws IllegalStateException {
    byte[] arrayOfByte = Arrays.clone(this.shared);
    Arrays.fill(this.shared, (byte)0);
    return arrayOfByte;
  }
  
  protected int engineGenerateSecret(byte[] paramArrayOfbyte, int paramInt) throws IllegalStateException, ShortBufferException {
    System.arraycopy(this.shared, 0, paramArrayOfbyte, paramInt, this.shared.length);
    Arrays.fill(this.shared, (byte)0);
    return this.shared.length;
  }
  
  protected byte[] calcSecret() {
    return engineGenerateSecret();
  }
}
