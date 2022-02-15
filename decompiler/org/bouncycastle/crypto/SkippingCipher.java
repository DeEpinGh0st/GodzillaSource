package org.bouncycastle.crypto;

public interface SkippingCipher {
  long skip(long paramLong);
  
  long seekTo(long paramLong);
  
  long getPosition();
}
