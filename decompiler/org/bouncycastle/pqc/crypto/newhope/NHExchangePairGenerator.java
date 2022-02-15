package org.bouncycastle.pqc.crypto.newhope;

import java.security.SecureRandom;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.ExchangePair;
import org.bouncycastle.pqc.crypto.ExchangePairGenerator;

public class NHExchangePairGenerator implements ExchangePairGenerator {
  private final SecureRandom random;
  
  public NHExchangePairGenerator(SecureRandom paramSecureRandom) {
    this.random = paramSecureRandom;
  }
  
  public ExchangePair GenerateExchange(AsymmetricKeyParameter paramAsymmetricKeyParameter) {
    return generateExchange(paramAsymmetricKeyParameter);
  }
  
  public ExchangePair generateExchange(AsymmetricKeyParameter paramAsymmetricKeyParameter) {
    NHPublicKeyParameters nHPublicKeyParameters = (NHPublicKeyParameters)paramAsymmetricKeyParameter;
    byte[] arrayOfByte1 = new byte[32];
    byte[] arrayOfByte2 = new byte[2048];
    NewHope.sharedB(this.random, arrayOfByte1, arrayOfByte2, nHPublicKeyParameters.pubData);
    return new ExchangePair(new NHPublicKeyParameters(arrayOfByte2), arrayOfByte1);
  }
}
