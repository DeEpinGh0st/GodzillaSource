package org.bouncycastle.pqc.crypto;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public interface ExchangePairGenerator {
  ExchangePair GenerateExchange(AsymmetricKeyParameter paramAsymmetricKeyParameter);
  
  ExchangePair generateExchange(AsymmetricKeyParameter paramAsymmetricKeyParameter);
}
