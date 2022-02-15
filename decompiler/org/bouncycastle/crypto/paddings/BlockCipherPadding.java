package org.bouncycastle.crypto.paddings;

import java.security.SecureRandom;
import org.bouncycastle.crypto.InvalidCipherTextException;

public interface BlockCipherPadding {
  void init(SecureRandom paramSecureRandom) throws IllegalArgumentException;
  
  String getPaddingName();
  
  int addPadding(byte[] paramArrayOfbyte, int paramInt);
  
  int padCount(byte[] paramArrayOfbyte) throws InvalidCipherTextException;
}
