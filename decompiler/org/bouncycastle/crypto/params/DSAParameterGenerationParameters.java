package org.bouncycastle.crypto.params;

import java.security.SecureRandom;

public class DSAParameterGenerationParameters {
  public static final int DIGITAL_SIGNATURE_USAGE = 1;
  
  public static final int KEY_ESTABLISHMENT_USAGE = 2;
  
  private final int l;
  
  private final int n;
  
  private final int usageIndex;
  
  private final int certainty;
  
  private final SecureRandom random;
  
  public DSAParameterGenerationParameters(int paramInt1, int paramInt2, int paramInt3, SecureRandom paramSecureRandom) {
    this(paramInt1, paramInt2, paramInt3, paramSecureRandom, -1);
  }
  
  public DSAParameterGenerationParameters(int paramInt1, int paramInt2, int paramInt3, SecureRandom paramSecureRandom, int paramInt4) {
    this.l = paramInt1;
    this.n = paramInt2;
    this.certainty = paramInt3;
    this.usageIndex = paramInt4;
    this.random = paramSecureRandom;
  }
  
  public int getL() {
    return this.l;
  }
  
  public int getN() {
    return this.n;
  }
  
  public int getCertainty() {
    return this.certainty;
  }
  
  public SecureRandom getRandom() {
    return this.random;
  }
  
  public int getUsageIndex() {
    return this.usageIndex;
  }
}
