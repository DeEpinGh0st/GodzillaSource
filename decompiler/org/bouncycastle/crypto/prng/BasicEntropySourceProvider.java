package org.bouncycastle.crypto.prng;

import java.security.SecureRandom;

public class BasicEntropySourceProvider implements EntropySourceProvider {
  private final SecureRandom _sr;
  
  private final boolean _predictionResistant;
  
  public BasicEntropySourceProvider(SecureRandom paramSecureRandom, boolean paramBoolean) {
    this._sr = paramSecureRandom;
    this._predictionResistant = paramBoolean;
  }
  
  public EntropySource get(final int bitsRequired) {
    return new EntropySource() {
        public boolean isPredictionResistant() {
          return BasicEntropySourceProvider.this._predictionResistant;
        }
        
        public byte[] getEntropy() {
          if (BasicEntropySourceProvider.this._sr instanceof SP800SecureRandom || BasicEntropySourceProvider.this._sr instanceof X931SecureRandom) {
            byte[] arrayOfByte = new byte[(bitsRequired + 7) / 8];
            BasicEntropySourceProvider.this._sr.nextBytes(arrayOfByte);
            return arrayOfByte;
          } 
          return BasicEntropySourceProvider.this._sr.generateSeed((bitsRequired + 7) / 8);
        }
        
        public int entropySize() {
          return bitsRequired;
        }
      };
  }
}
