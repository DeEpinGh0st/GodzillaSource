package org.bouncycastle.jcajce.provider.asymmetric.util;

public class PrimeCertaintyCalculator {
  public static int getDefaultCertainty(int paramInt) {
    return (paramInt <= 1024) ? 80 : (96 + 16 * (paramInt - 1) / 1024);
  }
}
