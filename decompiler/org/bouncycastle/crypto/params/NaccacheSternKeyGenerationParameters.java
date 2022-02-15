package org.bouncycastle.crypto.params;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class NaccacheSternKeyGenerationParameters extends KeyGenerationParameters {
  private int certainty;
  
  private int cntSmallPrimes;
  
  private boolean debug = false;
  
  public NaccacheSternKeyGenerationParameters(SecureRandom paramSecureRandom, int paramInt1, int paramInt2, int paramInt3) {
    this(paramSecureRandom, paramInt1, paramInt2, paramInt3, false);
  }
  
  public NaccacheSternKeyGenerationParameters(SecureRandom paramSecureRandom, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean) {
    super(paramSecureRandom, paramInt1);
    this.certainty = paramInt2;
    if (paramInt3 % 2 == 1)
      throw new IllegalArgumentException("cntSmallPrimes must be a multiple of 2"); 
    if (paramInt3 < 30)
      throw new IllegalArgumentException("cntSmallPrimes must be >= 30 for security reasons"); 
    this.cntSmallPrimes = paramInt3;
    this.debug = paramBoolean;
  }
  
  public int getCertainty() {
    return this.certainty;
  }
  
  public int getCntSmallPrimes() {
    return this.cntSmallPrimes;
  }
  
  public boolean isDebug() {
    return this.debug;
  }
}
