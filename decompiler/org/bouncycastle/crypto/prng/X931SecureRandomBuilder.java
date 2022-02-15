package org.bouncycastle.crypto.prng;

import java.security.SecureRandom;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Pack;

public class X931SecureRandomBuilder {
  private SecureRandom random;
  
  private EntropySourceProvider entropySourceProvider;
  
  private byte[] dateTimeVector;
  
  public X931SecureRandomBuilder() {
    this(new SecureRandom(), false);
  }
  
  public X931SecureRandomBuilder(SecureRandom paramSecureRandom, boolean paramBoolean) {
    this.random = paramSecureRandom;
    this.entropySourceProvider = new BasicEntropySourceProvider(this.random, paramBoolean);
  }
  
  public X931SecureRandomBuilder(EntropySourceProvider paramEntropySourceProvider) {
    this.random = null;
    this.entropySourceProvider = paramEntropySourceProvider;
  }
  
  public X931SecureRandomBuilder setDateTimeVector(byte[] paramArrayOfbyte) {
    this.dateTimeVector = paramArrayOfbyte;
    return this;
  }
  
  public X931SecureRandom build(BlockCipher paramBlockCipher, KeyParameter paramKeyParameter, boolean paramBoolean) {
    if (this.dateTimeVector == null) {
      this.dateTimeVector = new byte[paramBlockCipher.getBlockSize()];
      Pack.longToBigEndian(System.currentTimeMillis(), this.dateTimeVector, 0);
    } 
    paramBlockCipher.init(true, (CipherParameters)paramKeyParameter);
    return new X931SecureRandom(this.random, new X931RNG(paramBlockCipher, this.dateTimeVector, this.entropySourceProvider.get(paramBlockCipher.getBlockSize() * 8)), paramBoolean);
  }
}
